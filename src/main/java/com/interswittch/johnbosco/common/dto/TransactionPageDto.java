package com.interswittch.johnbosco.common.dto;

import com.interswittch.johnbosco.transactions.models.TransactionEntity;
import lombok.Builder;

import java.util.List;

@Builder
public record TransactionPageDto(long page,
                                 long size,
                                 long totalPages,
                                 long count,
                                 List<TransactionEntity> data) {
}
