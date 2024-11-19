package com.interswittch.johnbosco.loans.repositories;

import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.common.enums.LoanStatus;
import com.interswittch.johnbosco.loans.models.LoanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanRepository extends CrudRepository<LoanEntity, Long> {

    @Query("select l from LoanEntity l where l.account = :account and l.status = :status")
    Optional<LoanEntity> findExistingLoan(AccountEntity account, LoanStatus status);

    Optional<LoanEntity> findByLoanId(String loanId);

    Page<LoanEntity> findByStatus(LoanStatus status, Pageable pageable);
}
