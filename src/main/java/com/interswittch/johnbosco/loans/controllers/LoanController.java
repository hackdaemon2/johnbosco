package com.interswittch.johnbosco.loans.controllers;

import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.LoanRequestDto;
import com.interswittch.johnbosco.common.enums.LoanStatus;
import com.interswittch.johnbosco.loans.models.LoanEntity;
import com.interswittch.johnbosco.loans.service.ILoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final ILoanService loanService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<LoanEntity>> requestLoan(@RequestBody @Valid LoanRequestDto loanRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.requestLoan(loanRequest));
    }

    @PatchMapping("/{loanId}/{loanStatus}")
    public ResponseEntity<CustomApiResponse<LoanEntity>> approveOrRejectLoan(@PathVariable String loanId,
                                                                             @PathVariable LoanStatus loanStatus) {
        return ResponseEntity.ok(loanService.approveOrRejectLoan(loanId, loanStatus));
    }
}
