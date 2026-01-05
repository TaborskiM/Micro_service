package com.library.loanservice.controller;

import com.library.loanservice.dto.LoanRequest;
import com.library.loanservice.dto.LoanResponse;
import com.library.loanservice.model.Loan;
import com.library.loanservice.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    // Borrow a book
    @PostMapping
    public ResponseEntity<LoanResponse> borrowBook(
            @RequestBody @Valid LoanRequest request
    ) {
        Loan loan = loanService.borrowBook(
                request.getUserId(),
                request.getBookId()
        );

        return new ResponseEntity<>(
                LoanResponse.fromLoan(loan),
                HttpStatus.CREATED
        );
    }

    // Return a book
    @PutMapping("/{loanId}/return")
    public ResponseEntity<LoanResponse> returnBook(
            @PathVariable Long loanId
    ) {
        Loan loan = loanService.returnBook(loanId);
        return ResponseEntity.ok(LoanResponse.fromLoan(loan));
    }

    // Get all loans of a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponse>> getUserLoans(
            @PathVariable Long userId
    ) {
        List<LoanResponse> response = loanService.getUserLoans(userId)
                .stream()
                .map(LoanResponse::fromLoan)
                .toList();

        return ResponseEntity.ok(response);
    }

    // Get active loans of a user
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<LoanResponse>> getActiveUserLoans(
            @PathVariable Long userId
    ) {
        List<LoanResponse> response = loanService.getActiveUserLoans(userId)
                .stream()
                .map(LoanResponse::fromLoan)
                .toList();

        return ResponseEntity.ok(response);
    }

    // Get all loans (admin/debug)
    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        List<LoanResponse> response = loanService.getAllLoans()
                .stream()
                .map(LoanResponse::fromLoan)
                .toList();

        return ResponseEntity.ok(response);
    }
}
