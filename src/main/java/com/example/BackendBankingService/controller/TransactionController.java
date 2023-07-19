package com.example.BackendBankingService.controller;

import com.example.BackendBankingService.dao.TransactionDao;
import com.example.BackendBankingService.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionDao transactionDao;

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }

    @GetMapping("/client/{clientId}")
    public List<Transaction> getTransactionsByClientId(@PathVariable Long clientId) {
        return transactionDao.getTransactionsByAccountId(clientId);
    }

    @PostMapping
    public ResponseEntity<String> addTransaction(@RequestBody Transaction transaction) {
        String result = transactionDao.addTransaction(transaction);
        if (result.equals("Transaction successful")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }


}