package com.interswittch.johnbosco.transactions.repositories;

import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.transactions.models.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    Page<TransactionEntity> findByAccountAndDateBetween(AccountEntity accountEntity,
                                                        LocalDateTime startDate,
                                                        LocalDateTime endDate,
                                                        Pageable pageable);
}
