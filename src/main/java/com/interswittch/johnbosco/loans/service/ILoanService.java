package com.interswittch.johnbosco.loans.service;

import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.LoanRequestDto;
import com.interswittch.johnbosco.common.enums.LoanStatus;
import com.interswittch.johnbosco.loans.models.LoanEntity;

public interface ILoanService {

    CustomApiResponse<LoanEntity> requestLoan(LoanRequestDto loanRequestDto);

    CustomApiResponse<LoanEntity> approveOrRejectLoan(String loanId, LoanStatus loanStatus);
}
