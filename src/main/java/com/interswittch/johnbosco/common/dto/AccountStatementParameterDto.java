package com.interswittch.johnbosco.common.dto;

import com.interswittch.johnbosco.common.enums.SortOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatementParameterDto {

    @Min(value = 0, message = "minimum value for page must be 0")
    private int page = 0;

    private int size = 50;

    @NotNull(message = "startDate is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "endDate is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    private SortOrder sortOrder = SortOrder.DESC;
}
