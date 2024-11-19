package com.interswittch.johnbosco.customers.services;

import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.CustomerDto;
import com.interswittch.johnbosco.customers.models.CustomerEntity;

public interface ICustomerService {

    CustomApiResponse<CustomerEntity> createCustomer(CustomerDto customer);
}
