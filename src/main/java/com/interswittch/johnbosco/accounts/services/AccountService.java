package com.interswittch.johnbosco.accounts.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.accounts.repositories.AccountRepository;
import com.interswittch.johnbosco.common.dto.AccountDto;
import com.interswittch.johnbosco.common.dto.CreditAccountDto;
import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.TransactionPageDto;
import com.interswittch.johnbosco.common.enums.SortOrder;
import com.interswittch.johnbosco.common.enums.TransactionType;
import com.interswittch.johnbosco.common.exception.BusinessException;
import com.interswittch.johnbosco.common.exception.ResourceConflictException;
import com.interswittch.johnbosco.common.exception.ResourceNotFoundException;
import com.interswittch.johnbosco.common.util.EntityMapper;
import com.interswittch.johnbosco.customers.models.CustomerEntity;
import com.interswittch.johnbosco.customers.repositories.CustomerRepository;
import com.interswittch.johnbosco.transactions.models.TransactionEntity;
import com.interswittch.johnbosco.transactions.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final ObjectMapper mapper;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public CustomApiResponse<AccountEntity> creditAccount(CreditAccountDto creditAccountDto) {
        try {
            log.info("credit account request => {}", mapper.writeValueAsString(creditAccountDto));
            AccountEntity account = accountRepository.findByAccountNumber(creditAccountDto.accountNumber())
                                                     .orElseThrow(() -> new ResourceNotFoundException("Account does not exist"));

            account.setBalance(account.getBalance().add(creditAccountDto.amount()));
            AccountEntity updatedAccount = accountRepository.save(account);

            // create transaction
            TransactionEntity transactionEntity = TransactionEntity
                    .builder()
                    .account(account)
                    .amount(creditAccountDto.amount())
                    .type(TransactionType.CREDIT)
                    .date(LocalDateTime.now())
                    .narration("CR transactions")
                    .build();

            transactionRepository.save(transactionEntity);
            CustomApiResponse<AccountEntity> response = new CustomApiResponse<>(updatedAccount, false);
            log.info("credit account response => {}", mapper.writeValueAsString(response));
            return response;
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    @Transactional
    @Override
    public CustomApiResponse<AccountEntity> createAccount(AccountDto accountDto) {
        try {
            log.info("create account request => {}", mapper.writeValueAsString(accountDto));
            String customerEmail = accountDto.customerEmail();
            CustomerEntity customer = customerRepository.findByEmail(customerEmail)
                                                        .orElseThrow(() -> new ResourceNotFoundException("Customer does not exist"));

            if (accountRepository.existsByCustomerEmail(accountDto.customerEmail())) {
                String message = "Customer already has as account";
                log.warn(message);
                throw new ResourceConflictException(message);
            }

            accountRepository.findByAccountNumber(accountDto.accountNumber())
                             .ifPresent(account -> {
                                 throw new ResourceConflictException("account already exists");
                             });

            AccountEntity savedAccount = EntityMapper.mapToEntity(
                    accountDto,
                    customer,
                    (dto, customerEntity) -> accountRepository.save(AccountEntity
                            .builder()
                            .accountNumber(dto.accountNumber())
                            .balance(BigDecimal.ZERO)
                            .customer(customerEntity)
                            .build()));

            CustomApiResponse<AccountEntity> response = new CustomApiResponse<>(savedAccount, false);
            log.info("create account response => {}", mapper.writeValueAsString(response));
            return response;
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    @Override
    public TransactionPageDto getAccountStatements(String accountNumber, int page, int size, SortOrder sortOrder,
                                                   LocalDateTime startDate, LocalDateTime endDate) {
        try {
            if (startDate.isAfter(endDate)) {
                throw new BusinessException("startDate cannot be a date after endDate");
            }

            Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder.name()), "id");

            AccountEntity accountEntity = accountRepository.findByAccountNumber(accountNumber)
                                                           .orElseThrow(() -> new ResourceNotFoundException("account not found"));

            Page<TransactionEntity> transactions = transactionRepository.findByAccountAndDateBetween(
                    accountEntity,
                    startDate.toLocalDate().atStartOfDay(),
                    endDate.toLocalDate().atTime(LocalTime.MAX),
                    pageable);

            TransactionPageDto response = TransactionPageDto.builder()
                                                            .totalPages(transactions.getTotalPages())
                                                            .count(transactions.getTotalElements())
                                                            .size(size)
                                                            .page(page)
                                                            .data(transactions.getContent())
                                                            .build();

            log.info("account statement response => {}", mapper.writeValueAsString(response));
            return response;
        } catch (JsonProcessingException exception) {
            throw new BusinessException(exception.getMessage());
        }
    }
}
