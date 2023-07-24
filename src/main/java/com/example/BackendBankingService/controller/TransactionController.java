package com.example.BackendBankingService.controller;

import com.example.BackendBankingService.dao.TransactionDao;
import com.example.BackendBankingService.elasticConfig.ElasticsearchService;
import com.example.BackendBankingService.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Secured("ROLE_USER")
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }

    @GetMapping("/client/{clientId}")
    public List<Transaction> getTransactionsByClientId(@PathVariable Long clientId) {
        return transactionDao.getTransactionsByAccountId(clientId);
    }

    @PostMapping
    public ResponseEntity<?> addTransaction(@Valid @RequestBody Transaction transaction, BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            // If there are validation errors, create a custom error response
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        String result = transactionDao.addTransaction(transaction);
        if (result.equals("Transaction successful")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }


}