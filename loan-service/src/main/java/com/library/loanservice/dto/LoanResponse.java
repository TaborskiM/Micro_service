package com.library.loanservice.dto;

import com.library.loanservice.model.Loan;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LoanResponse {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;

    public static LoanResponse fromLoan(Loan loan) {
        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setUserId(loan.getUserId());
        response.setBookId(loan.getBookId());
        response.setLoanDate(loan.getLoanDate());
        response.setDueDate(loan.getDueDate());
        response.setReturnDate(loan.getReturnDate());
        response.setStatus(loan.getStatus().toString());
        return response;
    }
}
