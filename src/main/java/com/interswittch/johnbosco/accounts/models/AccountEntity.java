package com.interswittch.johnbosco.accounts.models;

import com.fasterxml.jackson.annotation.*;
import com.interswittch.johnbosco.common.models.AbstractEntity;
import com.interswittch.johnbosco.customers.models.CustomerEntity;
import com.interswittch.johnbosco.loans.models.LoanEntity;
import com.interswittch.johnbosco.transactions.models.TransactionEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Table(name = "accounts")
@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountEntity extends AbstractEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @Column(unique = true, name = "account_number")
    @JsonProperty("account_number")
    private String accountNumber;

    private BigDecimal balance;

    @JsonIgnore
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<TransactionEntity> transactions;

    @JsonIgnore
    @OneToOne(mappedBy = "account")
    private LoanEntity loan;
}
