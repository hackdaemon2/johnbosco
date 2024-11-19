package com.interswittch.johnbosco.customers.repositories;

import com.interswittch.johnbosco.customers.models.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {

    boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

    Optional<CustomerEntity> findByEmail(String email);
}
