package com.interswittch.johnbosco.accounts.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.accounts.repositories.AccountRepository;
import com.interswittch.johnbosco.common.dto.AccountDto;
import com.interswittch.johnbosco.common.dto.CreditAccountDto;
import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.TransactionPageDto;
import com.interswittch.johnbosco.common.enums.SortOrder;
import com.interswittch.johnbosco.common.exception.BusinessException;
import com.interswittch.johnbosco.common.exception.ResourceNotFoundException;
import com.interswittch.johnbosco.customers.models.CustomerEntity;
import com.interswittch.johnbosco.customers.repositories.CustomerRepository;
import com.interswittch.johnbosco.transactions.models.TransactionEntity;
import com.interswittch.johnbosco.transactions.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Happy Flow for Create Account Test")
    void testCreateAccount_Success() {
        // Arrange
        AccountDto accountDto = new AccountDto("1234567890", "customer@example.com");
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setEmail("customer@example.com");

        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customerEntity));
        when(accountRepository.existsByCustomerEmail(anyString())).thenReturn(false);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        AccountEntity savedAccount = AccountEntity
                .builder()
                .accountNumber("1234567890")
                .customer(customerEntity)
                .balance(BigDecimal.ZERO)
                .build();

        when(accountRepository.save(any(AccountEntity.class))).thenReturn(savedAccount);

        // Act
        CustomApiResponse<AccountEntity> response = accountService.createAccount(accountDto);

        // Assert
        assertNotNull(response);
        assertEquals("1234567890", response.data().getAccountNumber());
        verify(accountRepository, times(1)).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("Customer Not Found for Create Account Test")
    void testCreateAccount_CustomerNotFound() {
        // Arrange
        AccountDto accountDto = new AccountDto("1234567890", "nonexistent@example.com");

        when(customerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.createAccount(accountDto));
        verify(accountRepository, never()).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("Happy Flow for Get Account Statement Test")
    void testGetAccountStatements_Success() {
        // Arrange
        String accountNumber = "1234567890";
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountNumber(accountNumber);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(accountEntity));

        TransactionEntity transactionEntity = new TransactionEntity();
        Page<TransactionEntity> page = new PageImpl<>(Collections.singletonList(transactionEntity));

        when(transactionRepository.findByAccountAndDateBetween(
                eq(accountEntity),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(page);

        // Act
        TransactionPageDto result = accountService.getAccountStatements(accountNumber, 0, 10, SortOrder.ASC, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.page());
        assertEquals(1, result.totalPages());
        verify(transactionRepository, times(1))
                .findByAccountAndDateBetween(eq(accountEntity), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Get Account Statement with Start and End Dates")
    void testGetAccountStatements_StartDateAfterEndDate() {
        // Arrange
        String accountNumber = "1234567890";
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 31, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 1, 23, 59);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> accountService.getAccountStatements(accountNumber, 0, 10, SortOrder.ASC, startDate, endDate));
        verify(transactionRepository, never()).findByAccountAndDateBetween(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Get Account Statement with Account Not Found")
    void testGetAccountStatements_AccountNotFound() {
        // Arrange
        String accountNumber = "invalidAccount";
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountStatements(accountNumber, 0, 10, SortOrder.ASC, startDate, endDate));
    }

    @Test
    @DisplayName("Credit Account Test Successful")
    void creditAccount_Successful() {
        // Arrange
        String accountNumber = "1234567890";
        BigDecimal creditAmount = BigDecimal.valueOf(1000);

        CreditAccountDto creditAccountDto = new CreditAccountDto(accountNumber, creditAmount);
        AccountEntity account = new AccountEntity();
        account.setAccountNumber(accountNumber);
        account.setBalance(BigDecimal.valueOf(5000));

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(account);

        // Act
        CustomApiResponse<AccountEntity> response = accountService.creditAccount(creditAccountDto);

        // Assert
        assertNotNull(response);
        assertFalse(response.error());
        assertEquals(BigDecimal.valueOf(6000), response.data().getBalance());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    @DisplayName("Credit Account Test Failed - Account Not Found")
    void creditAccount_AccountNotFound() {
        // Arrange
        String accountNumber = "1234567890";
        BigDecimal creditAmount = BigDecimal.valueOf(1000);

        CreditAccountDto creditAccountDto = new CreditAccountDto(accountNumber, creditAmount);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> accountService.creditAccount(creditAccountDto));

        assertEquals("Account does not exist", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountRepository, never()).save(any(AccountEntity.class));
    }
}
