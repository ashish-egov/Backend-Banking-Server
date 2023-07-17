package com.example.BackendBankingServer.dao;

import com.example.BackendBankingServer.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TransactionDaoImpl implements TransactionDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS transactions ("
                + "id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                + "type CHAR(1) NOT NULL,"
                + "amount DECIMAL(10,2) NOT NULL,"
                + "client_id BIGINT NOT NULL,"
                + "date_time TIMESTAMP NOT NULL,"
                + "FOREIGN KEY (client_id) REFERENCES clients(account_id)"
                + ")";
        jdbcTemplate.execute(sql);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        String sql = "SELECT * FROM transactions";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(rs.getLong("id"));
            transaction.setAmount(rs.getBigDecimal("amount"));
            transaction.setClientId(rs.getLong("client_id"));
            transaction.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
            transaction.setType(rs.getString("type").charAt(0));
            return transaction;
        });
    }

    @Override
    public List<Transaction> getTransactionsByClientId(Long clientId) {
        String sql = "SELECT * FROM transactions WHERE client_id = ?";
        return jdbcTemplate.query(sql, new Object[] { clientId }, (rs, rowNum) -> {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(rs.getLong("id"));
            transaction.setAmount(rs.getBigDecimal("amount"));
            transaction.setClientId(rs.getLong("client_id"));
            transaction.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
            transaction.setType(rs.getString("type").charAt(0));
            return transaction;
        });
    }

    @Override
    public String addTransaction(Transaction transaction) {
        transaction.setDateTime(LocalDateTime.now());

        // Check if the transaction type is 'D' and the balance will be negative after the transaction
        if (transaction.getType() == 'D' && getBalance(transaction.getClientId()).compareTo(transaction.getAmount()) < 0) {
            return "Insufficient balance";
        }

        String sql = "INSERT INTO transactions (type, amount, client_id, date_time) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, transaction.getType(), transaction.getAmount(), transaction.getClientId(), transaction.getDateTime());

        // Update the client's balance based on the transaction type
        BigDecimal amount = transaction.getAmount();
        Long clientId = transaction.getClientId();
        String updateSql = "UPDATE clients SET balance = balance " + (transaction.getType() == 'C' ? "+ ?" : "- ?") + " WHERE account_id = ?";
        jdbcTemplate.update(updateSql, amount, clientId);

        return "Transaction successful";
    }

    private BigDecimal getBalance(Long clientId) {
        String sql = "SELECT balance FROM clients WHERE account_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{clientId}, BigDecimal.class);
    }
}