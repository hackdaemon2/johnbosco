package com.interswittch.johnbosco.accounts.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.accounts.services.IAccountService;
import com.interswittch.johnbosco.common.dto.AccountDto;
import com.interswittch.johnbosco.common.dto.AccountStatementParameterDto;
import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.TransactionPageDto;
import com.interswittch.johnbosco.common.enums.SortOrder;
import com.interswittch.johnbosco.transactions.models.TransactionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create Account Test")
    void testCreateAccount() throws Exception {
        // Arrange
        AccountDto accountDto = new AccountDto("johndoe@example.com", "3101106398");
        AccountEntity accountEntity = AccountEntity
                .builder()
                .accountNumber("3101106398")
                .balance(BigDecimal.valueOf(100))
                .build();
        CustomApiResponse<AccountEntity> response = new CustomApiResponse<>(accountEntity, false);
        Mockito.when(accountService.createAccount(any(AccountDto.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(accountDto)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.data.account_number").value("3101106398"))
               .andExpect(jsonPath("$.error").value(false));

        Mockito.verify(accountService, Mockito.times(1)).createAccount(any(AccountDto.class));
    }

    @Test
    @DisplayName("Get Account Statement Test")
    void testGetAccountStatements() throws Exception {
        // Arrange
        String accountNumber = "123456";
        AccountStatementParameterDto params = new AccountStatementParameterDto(0, 10, LocalDateTime.now().minusDays(10), LocalDateTime.now(), SortOrder.ASC);
        TransactionEntity transaction = new TransactionEntity();
        transaction.setNarration("Test Transaction");
        Page<TransactionEntity> page = new PageImpl<>(Collections.singletonList(transaction), PageRequest.of(0, 10), 1);

        Mockito.when(accountService.getAccountStatements(
                eq(accountNumber),
                eq(params.getPage()),
                eq(params.getSize()),
                eq(params.getSortOrder()),
                eq(params.getStartDate()),
                eq(params.getEndDate())
        )).thenReturn(TransactionPageDto
                .builder()
                .count(page.getTotalElements())
                .data(page.getContent())
                .totalPages(page.getTotalPages())
                .page(0L)
                .size(10L)
                .build());

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/{accountNumber}/statements", accountNumber)
                       .param("page", String.valueOf(params.getPage()))
                       .param("size", String.valueOf(params.getSize()))
                       .param("startDate", params.getStartDate().toString())
                       .param("endDate", params.getEndDate().toString())
                       .param("sortOrder", params.getSortOrder().toString())
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data[0].narration").value("Test Transaction"));

        Mockito.verify(accountService, Mockito.times(1)).getAccountStatements(
                eq(accountNumber),
                eq(params.getPage()),
                eq(params.getSize()),
                eq(params.getSortOrder()),
                eq(params.getStartDate()),
                eq(params.getEndDate())
        );
    }
}

