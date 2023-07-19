package com.example.BackendBankingService.dao;

import com.example.BackendBankingService.model.Transaction;

import java.util.List;

public interface TransactionDao {

    List<Transaction> getAllTransactions();

    List<Transaction> getTransactionsByAccountId(Long accountId);

    String addTransaction(Transaction transaction);
}