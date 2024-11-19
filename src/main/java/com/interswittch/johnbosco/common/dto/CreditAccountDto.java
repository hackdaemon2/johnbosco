package com.interswittch.johnbosco.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interswittch.johnbosco.validation.IsNotZero;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreditAccountDto(@NotBlank(message = "account_number is required")
                               @JsonProperty("account_number")
                               @Pattern(regexp = "\\d+", message = "account_number must be numeric")
                               @Size(min = 10, max = 10, message = "account_number must be 10 digits long")
                               String accountNumber,

                               @NotNull(message = "amount is required")
                               @IsNotZero
                               @Positive
                               BigDecimal amount) {
}
