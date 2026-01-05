package com.library.loanservice.service;

import com.library.loanservice.model.Loan;
import com.library.loanservice.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;

    @Transactional
    public Loan borrowBook(Long userId, Long bookId) {
        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setBookId(bookId);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14)); // 14 days loan period
        loan.setStatus(Loan.LoanStatus.ACTIVE);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(Loan.LoanStatus.RETURNED);

        return loanRepository.save(loan);
    }

    public List<Loan> getUserLoans(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> getActiveUserLoans(Long userId) {
        return loanRepository.findByUserIdAndStatus(userId, Loan.LoanStatus.ACTIVE);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}
