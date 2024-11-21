package com.interswittch.johnbosco.customers.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.CustomerDto;
import com.interswittch.johnbosco.common.exception.BusinessException;
import com.interswittch.johnbosco.common.exception.ResourceConflictException;
import com.interswittch.johnbosco.common.util.EntityMapper;
import com.interswittch.johnbosco.customers.models.CustomerEntity;
import com.interswittch.johnbosco.customers.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final ObjectMapper mapper;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomApiResponse<CustomerEntity> createCustomer(CustomerDto customerDto) {
        try {
            log.info("create customer request => {}", mapper.writeValueAsString(customerDto));

            if (customerRepository.existsByEmailOrPhoneNumber(customerDto.email(), customerDto.phoneNumber())) {
                String message = "Customer already exists";
                log.warn(message);
                throw new ResourceConflictException(message);
            }

            CustomerEntity savedCustomer = EntityMapper.mapToEntity(
                    customerDto,
                    dto -> customerRepository.save(CustomerEntity
                            .builder()
                            .email(dto.email())
                            .firstName(dto.firstName())
                            .lastName(dto.lastName())
                            .phoneNumber(dto.phoneNumber())
                            .build()));

            CustomApiResponse<CustomerEntity> response = new CustomApiResponse<>(savedCustomer, false);
            log.info("create customer response => {}", mapper.writeValueAsString(response));
            return response;
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ex.getMessage());
        }
    }
}

