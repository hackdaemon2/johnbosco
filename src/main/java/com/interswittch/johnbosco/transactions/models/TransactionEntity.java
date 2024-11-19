package com.interswittch.johnbosco.transactions.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.common.enums.TransactionType;
import com.interswittch.johnbosco.common.models.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "transactions")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionEntity extends AbstractEntity {

    @ManyToOne
    private AccountEntity account;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // Debit or Credit

    private String narration;
    private BigDecimal amount;
    private LocalDateTime date;
}
