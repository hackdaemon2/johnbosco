package com.interswittch.johnbosco.loans.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.interswittch.johnbosco.accounts.models.AccountEntity;
import com.interswittch.johnbosco.common.enums.LoanStatus;
import com.interswittch.johnbosco.common.models.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loans")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanEntity extends AbstractEntity {

    @ManyToOne
    private AccountEntity account;

    @Column(name = "loan_amount")
    private BigDecimal loanAmount;

    @Column(name = "loan_repaid_amount")
    private BigDecimal loanRepaidAmount;

    @Column(name = "total_repayment_amount")
    private BigDecimal totalRepaymentAmount;

    @Column(name = "monthly_repayment_amount")
    private BigDecimal monthlyRepaymentAmount;

    @Column(unique = true, name = "loan_id")
    @JsonProperty("loan_id")
    private String loanId;

    @Enumerated(EnumType.STRING)
    private LoanStatus status; // PENDING, APPROVED, REJECTED, COMPLETED
}
