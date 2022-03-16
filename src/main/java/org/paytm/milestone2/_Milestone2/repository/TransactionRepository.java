package org.paytm.milestone2._Milestone2.repository;

import org.paytm.milestone2._Milestone2.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    List<Transaction> findByPayerMobileNumberOrPayeeMobileNumber(String payerNum, String payeeNum, Pageable pageable);
}
