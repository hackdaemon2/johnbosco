package com.interswittch.johnbosco.schedulers;

import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.accounts.repositories.AccountRepository;
import com.interswittch.johnbosco.common.enums.LoanStatus;
import com.interswittch.johnbosco.common.enums.TransactionType;
import com.interswittch.johnbosco.common.util.TransactionMapper;
import com.interswittch.johnbosco.loans.models.LoanEntity;
import com.interswittch.johnbosco.loans.models.LoanPaymentEntity;
import com.interswittch.johnbosco.loans.repositories.LoanPaymentRepository;
import com.interswittch.johnbosco.loans.repositories.LoanRepository;
import com.interswittch.johnbosco.transactions.models.TransactionEntity;
import com.interswittch.johnbosco.transactions.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanPaymentScheduler {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;

    private static final int PAGE_SIZE = 100;

    @Scheduled(cron = "0 0 0 3 * ?") // Executes on the 3rd day of every month
    public void processLoanRepayments() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        Page<LoanEntity> activeLoans;

        do {
            activeLoans = loanRepository.findByStatus(LoanStatus.APPROVED, pageable);
            activeLoans.forEach(this::processSingleLoanRepayment);
            pageable = pageable.next();
        } while (activeLoans.hasNext());
    }

    private void processSingleLoanRepayment(LoanEntity loan) {
        AccountEntity account = loan.getAccount();
        BigDecimal repaymentAmount = loan.getMonthlyRepaymentAmount();

        if (account.getBalance().compareTo(repaymentAmount) >= 0) {
            account.setBalance(account.getBalance().subtract(repaymentAmount));
            loan.setLoanRepaidAmount(loan.getLoanRepaidAmount().add(repaymentAmount));

            if (loan.getLoanRepaidAmount().compareTo(loan.getLoanAmount()) >= 0) {
                loan.setStatus(LoanStatus.COMPLETED);
                log.info("loan successfully completed for {}", loan.getLoanId());
            }

            accountRepository.save(account);
            loanRepository.save(loan);
            LoanPaymentEntity loanPaymentEntity = formulateLoanPaymentEntity(loan, repaymentAmount);
            loanPaymentRepository.save(loanPaymentEntity);
            saveTransaction(loan, account);
        }

        log.warn("insufficient balance for account {}", account.getAccountNumber());
    }

    private static LoanPaymentEntity formulateLoanPaymentEntity(LoanEntity loan, BigDecimal repaymentAmount) {
        return LoanPaymentEntity
                .builder()
                .loan(loan)
                .amount(repaymentAmount)
                .date(LocalDateTime.now())
                .build();
    }

    private void saveTransaction(LoanEntity loan, AccountEntity account) {
        TransactionEntity transactionEntity = TransactionMapper.formulateTransaction(
                account,
                loan,
                "loan payment",
                TransactionType.DEBIT);
        transactionRepository.save(transactionEntity);
    }
}
