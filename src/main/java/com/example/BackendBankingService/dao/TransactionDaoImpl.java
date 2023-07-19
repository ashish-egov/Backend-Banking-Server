package com.example.BackendBankingService.dao;

import com.example.BackendBankingService.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                + "from_account_id BIGINT,"
                + "to_account_id BIGINT,"
                + "date_time TIMESTAMP NOT NULL,"
                + "FOREIGN KEY (from_account_id) REFERENCES clients(account_id),"
                + "FOREIGN KEY (to_account_id) REFERENCES clients(account_id)"
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
            transaction.setFromAccountId(rs.getLong("from_account_id"));
            transaction.setToAccountId(rs.getLong("to_account_id"));
            transaction.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
            transaction.setType(rs.getString("type").charAt(0));
            return transaction;
        });
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        String sql = "SELECT * FROM transactions WHERE from_account_id = ? OR to_account_id = ?";
        return jdbcTemplate.query(sql, new Object[] { accountId, accountId }, (rs, rowNum) -> {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(rs.getLong("id"));
            transaction.setAmount(rs.getBigDecimal("amount"));
            transaction.setFromAccountId(rs.getLong("from_account_id"));
            transaction.setToAccountId(rs.getLong("to_account_id"));
            transaction.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
            transaction.setType(rs.getString("type").charAt(0));
            return transaction;
        });
    }

    @Transactional
    @Override
    public String addTransaction(Transaction transaction) {
        transaction.setDateTime(LocalDateTime.now());

        if (transaction.getType() == 'W' && transaction.getFromAccountId() == null) {
            return "From account number is required for a withdrawal transaction";
        }

        if (transaction.getType() == 'D' && transaction.getToAccountId() == null) {
            return "To account number is required for a deposit transaction";
        }
        // Check if the transaction type is 'D' and the balance will be negative after the transaction
        if ((transaction.getType() == 'W' || transaction.getType() == 'T') && getBalance(transaction.getFromAccountId()).compareTo(transaction.getAmount()) < 0) {
            return "Insufficient balance";
        }

        if (transaction.getType() == 'T' && transaction.getFromAccountId().equals(transaction.getToAccountId())) {
            return "Cannot transfer money to the same account";
        }

        if (transaction.getType() == 'T' && (transaction.getFromAccountId() == null || transaction.getToAccountId() == null)) {
            return "Both account numbers are required for a transfer transaction";
        }

        String sql;
        Object[] params;
        if (transaction.getType() == 'D') {
            // Deposit transaction, the fromAccountId should be null
            sql = "INSERT INTO transactions (type, amount, to_account_id, date_time) VALUES (?, ?, ?, ?)";
            params = new Object[]{transaction.getType(), transaction.getAmount(), transaction.getToAccountId(), transaction.getDateTime()};
        } else {
            // Credit or withdrawal transaction
            sql = "INSERT INTO transactions (type, amount, from_account_id, to_account_id, date_time) VALUES (?, ?, ?, ?, ?)";
            params = new Object[]{transaction.getType(), transaction.getAmount(), transaction.getFromAccountId(), transaction.getToAccountId(), transaction.getDateTime()};
        }
        jdbcTemplate.update(sql, params);

        if (transaction.getType() == 'T') {
            // Update the balances of both accounts for a transfer transaction
            BigDecimal amount = transaction.getAmount();
            Long fromAccountId = transaction.getFromAccountId();
            Long toAccountId = transaction.getToAccountId();

            String updateFromSql = "UPDATE clients SET balance = balance - ? WHERE account_id = ?";
            String updateToSql = "UPDATE clients SET balance = balance + ? WHERE account_id = ?";
            jdbcTemplate.update(updateFromSql, amount, fromAccountId);
            jdbcTemplate.update(updateToSql, amount, toAccountId);
        } else {
            // Update the balance of the account for a credit or debit transaction
            BigDecimal amount = transaction.getAmount();
            Long accountId = transaction.getType() == 'W' ?transaction.getFromAccountId() : transaction.getToAccountId();
            String updateSql = "UPDATE clients SET balance = balance " + (transaction.getType() == 'W' ? "- ?" : "+ ?") + " WHERE account_id = ?";
            jdbcTemplate.update(updateSql, amount, accountId);
        }

        return "Transaction successful";
    }

    private BigDecimal getBalance(Long accountId) {
        String sql = "SELECT balance FROM clients WHERE account_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{accountId}, BigDecimal.class);
    }
}