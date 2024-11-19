package com.interswittch.johnbosco.customers.controllers;

import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.CustomerDto;
import com.interswittch.johnbosco.customers.models.CustomerEntity;
import com.interswittch.johnbosco.customers.services.ICustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CustomerControllerTest {

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private ICustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomer_Success() {
        // Arrange
        CustomerDto customerDto = new CustomerDto("John", "Doe", "john.doe@example.com", "+12345678901");
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName("John");
        customerEntity.setLastName("Doe");
        customerEntity.setEmail("john.doe@example.com");
        customerEntity.setPhoneNumber("+12345678901");

        CustomApiResponse<CustomerEntity> mockResponse = new CustomApiResponse<>(customerEntity, false);

        when(customerService.createCustomer(any(CustomerDto.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<CustomApiResponse<CustomerEntity>> response = customerController.createCustomer(customerDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("John", Objects.requireNonNull(response.getBody()).data().getFirstName());
        assertEquals("Doe", response.getBody().data().getLastName());
        assertEquals("john.doe@example.com", response.getBody().data().getEmail());
        assertEquals("+12345678901", response.getBody().data().getPhoneNumber());
    }
}

