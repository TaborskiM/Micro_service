package com.library.loanservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Book ID is required")
    private Long bookId;
}
