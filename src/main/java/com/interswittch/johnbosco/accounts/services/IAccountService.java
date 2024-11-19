package com.interswittch.johnbosco.accounts.services;

import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.common.dto.AccountDto;
import com.interswittch.johnbosco.common.dto.CreditAccountDto;
import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.TransactionPageDto;
import com.interswittch.johnbosco.common.enums.SortOrder;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

public interface IAccountService {

    CustomApiResponse<AccountEntity> createAccount(AccountDto accountDto);

    TransactionPageDto getAccountStatements(String accountNumber,
                                            int page,
                                            int size,
                                            SortOrder sortOrder,
                                            LocalDateTime startDate, LocalDateTime endDate);

    CustomApiResponse<AccountEntity> creditAccount(@Valid CreditAccountDto creditAccountDto);
}
