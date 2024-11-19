package com.interswittch.johnbosco.common.util;

import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.common.enums.TransactionType;
import com.interswittch.johnbosco.loans.models.LoanEntity;
import com.interswittch.johnbosco.transactions.models.TransactionEntity;

import java.time.LocalDateTime;

public final class TransactionMapper {

    private TransactionMapper() {
        throw new IllegalStateException(TransactionMapper.class.getName());
    }

    public static TransactionEntity formulateTransaction(AccountEntity accountEntity, LoanEntity loanEntity,
                                                         String narration, TransactionType transactionType) {
        return EntityMapper.mapToEntity(
                accountEntity,
                loanEntity,
                (account, loan) -> TransactionEntity
                        .builder()
                        .account(account)
                        .amount(loan.getLoanAmount())
                        .type(transactionType)
                        .date(LocalDateTime.now())
                        .narration(narration)
                        .build());

    }
}
