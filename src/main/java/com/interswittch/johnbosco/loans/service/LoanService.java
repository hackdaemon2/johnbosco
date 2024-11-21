package com.interswittch.johnbosco.loans.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.accounts.repositories.AccountRepository;
import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.LoanRequestDto;
import com.interswittch.johnbosco.common.enums.LoanStatus;
import com.interswittch.johnbosco.common.enums.TransactionType;
import com.interswittch.johnbosco.common.exception.BusinessException;
import com.interswittch.johnbosco.common.exception.ResourceNotFoundException;
import com.interswittch.johnbosco.common.util.EntityMapper;
import com.interswittch.johnbosco.common.util.TransactionMapper;
import com.interswittch.johnbosco.loans.models.LoanEntity;
import com.interswittch.johnbosco.loans.repositories.LoanRepository;
import com.interswittch.johnbosco.transactions.models.TransactionEntity;
import com.interswittch.johnbosco.transactions.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService implements ILoanService {

    private final ObjectMapper mapper;
    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public CustomApiResponse<LoanEntity> requestLoan(LoanRequestDto loanRequestDto) {
        try {
            log.info("loan acquisition request => {}", mapper.writeValueAsString(loanRequestDto));

            AccountEntity account;
            account = accountRepository.findByAccountNumber(loanRequestDto.accountNumber()).orElseThrow(() -> new BusinessException("Account not found"));

            Optional<LoanEntity> activeLoan = loanRepository.findExistingLoan(account, LoanStatus.APPROVED);

            if (activeLoan.isPresent()) {
                String message = "Existing loan must be repaid first";
                log.warn(message);
                throw new BusinessException(message);
            }

            LoanEntity savedLoan = saveLoanEntity(loanRequestDto, account);
            CustomApiResponse<LoanEntity> response = new CustomApiResponse<>(savedLoan, false);
            log.info("loan acquisition response => {}", mapper.writeValueAsString(response));
            return response;
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    @Transactional
    @Override
    public CustomApiResponse<LoanEntity> approveOrRejectLoan(String loanId, LoanStatus loanStatus) {
        try {
            log.info("loan approval request => {}", loanId);
            LoanEntity loanEntity = loanRepository.findByLoanId(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

            if (loanStatus == LoanStatus.PENDING || loanStatus == LoanStatus.COMPLETED) {
                throw new BusinessException("invalid loan status passed");
            }

            loanEntity.setStatus(loanStatus);
            LoanEntity updatedLoanEntity = loanRepository.save(loanEntity);

            // disburse loan
            AccountEntity accountEntity = loanEntity.getAccount();
            accountEntity.setBalance(accountEntity.getBalance().add(updatedLoanEntity.getLoanAmount()));
            accountRepository.save(accountEntity);

            // create transaction
            TransactionEntity transactionEntity = TransactionMapper.formulateTransaction(
                    accountEntity,
                    updatedLoanEntity,
                    "loan disbursement",
                    TransactionType.CREDIT);

            transactionRepository.save(transactionEntity);
            CustomApiResponse<LoanEntity> response = new CustomApiResponse<>(updatedLoanEntity, false);
            log.info("loan approval response => {}", mapper.writeValueAsString(response));
            return response;
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    private LoanEntity saveLoanEntity(LoanRequestDto loanRequestDto, AccountEntity account) {
        BigDecimal totalRepaymentAmount = loanRequestDto.amount();
        BigDecimal monthlyRepaymentAmount = totalRepaymentAmount.multiply(BigDecimal.valueOf(0.1), MathContext.DECIMAL32); // 10% of loan amount
        return EntityMapper.mapToEntity(
                loanRequestDto,
                account,
                (dto, accountEntity) -> loanRepository.save(LoanEntity
                        .builder()
                        .account(accountEntity)
                        .loanAmount(dto.amount())
                        .loanId(UUID.randomUUID().toString())
                        .status(LoanStatus.PENDING)
                        .loanRepaidAmount(BigDecimal.ZERO)
                        .totalRepaymentAmount(totalRepaymentAmount)
                        .monthlyRepaymentAmount(monthlyRepaymentAmount)
                        .build()));
    }
}
