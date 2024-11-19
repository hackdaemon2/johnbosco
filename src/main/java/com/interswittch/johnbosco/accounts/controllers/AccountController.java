package com.interswittch.johnbosco.accounts.controllers;

import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.accounts.services.IAccountService;
import com.interswittch.johnbosco.common.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final IAccountService accountService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<AccountEntity>> createAccount(@RequestBody @Valid AccountDto accountDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(accountDto));
    }

    @PostMapping("/credit")
    public ResponseEntity<CustomApiResponse<AccountEntity>> creditAccount(@RequestBody @Valid CreditAccountDto creditAccountDto) {
        return ResponseEntity.ok(accountService.creditAccount(creditAccountDto));
    }

    @GetMapping("/{accountNumber}/statements")
    public ResponseEntity<TransactionPageDto> getAccountStatements(@PathVariable String accountNumber,
                                                                   @ModelAttribute @Valid
                                                                   AccountStatementParameterDto accountStatementParameterDto) {
        return ResponseEntity.ok(accountService.getAccountStatements(
                accountNumber,
                accountStatementParameterDto.getPage(),
                accountStatementParameterDto.getSize(),
                accountStatementParameterDto.getSortOrder(),
                accountStatementParameterDto.getStartDate(),
                accountStatementParameterDto.getEndDate()));
    }
}

