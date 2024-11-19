package com.interswittch.johnbosco.loans.repositories;

import com.interswittch.johnbosco.loans.models.LoanPaymentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanPaymentRepository extends CrudRepository<LoanPaymentEntity, Long> {
}
