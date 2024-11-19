package com.interswittch.johnbosco.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerDto(@NotBlank(message = "first_name is required")
                          @JsonProperty("first_name")
                          String firstName,

                          @NotBlank(message = "last_name is required")
                          @JsonProperty("last_name")
                          String lastName,

                          @Email(message = "enter a valid email", regexp = ".+@.+")
                          @NotBlank(message = "email is required")
                          String email,

                          @Pattern(regexp = "^(\\+)?\\d{11,15}$", message = "invalid phone_number passed")
                          @Size(min = 11, max = 15, message = "invalid phone length passed")
                          @NotBlank(message = "phone_number is required")
                          @JsonProperty("phone_number")
                          String phoneNumber) {
}
