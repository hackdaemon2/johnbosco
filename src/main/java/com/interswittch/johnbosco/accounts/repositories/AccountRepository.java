package com.interswittch.johnbosco.accounts.repositories;

import com.interswittch.johnbosco.accounts.models.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Query("select case when exists (select 1 from AccountEntity a join a.customer c where c.email = :email) then true else false end")
    boolean existsByCustomerEmail(@Param("email") String email);

    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
