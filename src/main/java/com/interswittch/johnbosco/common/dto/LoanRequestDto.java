package com.interswittch.johnbosco.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interswittch.johnbosco.common.enums.LoanStatus;
import com.interswittch.johnbosco.validation.IsNotZero;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record LoanRequestDto(@NotNull(message = "amount is required")
                             @IsNotZero
                             @Positive
                             BigDecimal amount,

                             @NotBlank(message = "account_number is required")
                             @Pattern(regexp = "\\d+", message = "account_number must be numeric")
                             @Size(min = 10, max = 10, message = "account_number must be 10 digits long")
                             @JsonProperty("account_number")
                             String accountNumber,

                             @NotNull(message = "status is required")
                             LoanStatus status) {
}
