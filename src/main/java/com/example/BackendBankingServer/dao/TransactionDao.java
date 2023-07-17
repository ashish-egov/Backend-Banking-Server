package com.example.BackendBankingServer.dao;

import com.example.BackendBankingServer.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionDao {

    List<Transaction> getAllTransactions();

    List<Transaction> getTransactionsByClientId(Long clientId);

    String addTransaction(Transaction transaction);
}