package com.interswittch.johnbosco.customers.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.CustomerDto;
import com.interswittch.johnbosco.common.exception.ResourceConflictException;
import com.interswittch.johnbosco.customers.models.CustomerEntity;
import com.interswittch.johnbosco.customers.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer_Success() throws JsonProcessingException {
        // Arrange
        CustomerDto customerDto = new CustomerDto("John", "Doe", "john.doe@example.com", "+12345678901");
        CustomerEntity savedCustomer = CustomerEntity.builder()
                                                     .firstName("John")
                                                     .lastName("Doe")
                                                     .email("john.doe@example.com")
                                                     .phoneNumber("+12345678901")
                                                     .build();

        when(customerRepository.existsByEmailOrPhoneNumber(anyString(), anyString())).thenReturn(false);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(savedCustomer);
        when(mapper.writeValueAsString(any())).thenReturn("mocked JSON");

        // Act
        CustomApiResponse<CustomerEntity> response = customerService.createCustomer(customerDto);

        // Assert
        assertNotNull(response);
        assertFalse(response.error());
        assertEquals("John", response.data().getFirstName());
        assertEquals("Doe", response.data().getLastName());
        assertEquals("john.doe@example.com", response.data().getEmail());
        assertEquals("+12345678901", response.data().getPhoneNumber());

        verify(customerRepository, times(1)).existsByEmailOrPhoneNumber("john.doe@example.com", "+12345678901");
        verify(customerRepository, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    void createCustomer_Conflict() {
        // Arrange
        CustomerDto customerDto = new CustomerDto("John", "Doe", "john.doe@example.com", "+12345678901");
        when(customerRepository.existsByEmailOrPhoneNumber(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class, () -> customerService.createCustomer(customerDto));

        assertEquals("Customer already exists", exception.getMessage());
        verify(customerRepository, times(1)).existsByEmailOrPhoneNumber("john.doe@example.com", "+12345678901");
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }
}

