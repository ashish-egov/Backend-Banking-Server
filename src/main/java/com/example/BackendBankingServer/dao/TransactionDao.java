package com.example.BackendBankingServer.dao;

import com.example.BackendBankingServer.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionDao {

    List<Transaction> getAllTransactions();

    List<Transaction> getTransactionsByAccountId(Long accountId);

    String addTransaction(Transaction transaction);
}