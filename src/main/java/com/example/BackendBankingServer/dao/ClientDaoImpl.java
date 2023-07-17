package com.example.BackendBankingServer.dao;

import com.example.BackendBankingServer.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Repository
public class ClientDaoImpl implements ClientDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS clients ("
                + "account_id BIGINT PRIMARY KEY,"
                + "name VARCHAR(50) NOT NULL,"
                + "phone VARCHAR(20) NOT NULL,"
                + "address VARCHAR(100) NOT NULL,"
                + "balance DECIMAL(10,2) NOT NULL"
                + ")";
        jdbcTemplate.execute(sql);
    }

    @Override
    public List<Client> getAllClients() {
        String sql = "SELECT * FROM clients";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Client client = new Client();
            client.setAccountId(rs.getLong("account_id"));
            client.setName(rs.getString("name"));
            client.setPhone(rs.getString("phone"));
            client.setAddress(rs.getString("address"));
            client.setBalance(rs.getBigDecimal("balance"));
            return client;
        });
    }

    @Override
    public Client getClientById(Long id) {
        String sql = "SELECT * FROM clients WHERE account_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { id }, (rs, rowNum) -> {
            Client client = new Client();
            client.setAccountId(rs.getLong("account_id"));
            client.setName(rs.getString("name"));
            client.setPhone(rs.getString("phone"));
            client.setAddress(rs.getString("address"));
            client.setBalance(rs.getBigDecimal("balance"));
            return client;
        });
    }

    @Override
    public String addClient(Client client) {
        String sql = "INSERT INTO clients (account_id, name, phone, address, balance) VALUES (?, ?, ?, ?, ?)";
        String accountId;
        boolean accountIdExists;
        do {
            // Generate a random 11-digit account ID
            accountId = generateRandomAccountId();
            // Check if the account ID already exists in the database
            accountIdExists = accountIdExistsInDatabase(accountId);
        } while (accountIdExists);
        int numRowsAffected = jdbcTemplate.update(sql, Long.parseLong(accountId), client.getName(), client.getPhone(), client.getAddress(), client.getBalance());
        if (numRowsAffected == 1) {
            return "Client added successfully with account ID: " + accountId;
        } else {
            return "Failed to add client";
        }
    }

    private String generateRandomAccountId() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000000) + 100000000; // Generate a random 9-digit number
        int randomDigitFirst = random.nextInt(9) + 1; // Generate a random non-zero digit
        int randomDigitSecond=random.nextInt(9)+1; // Generate a random non-zero digit
        return randomDigitFirst+""+randomDigitSecond+"" + randomNumber;
    }

    private boolean accountIdExistsInDatabase(String accountId) {
        String sql = "SELECT COUNT(*) FROM clients WHERE account_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, Long.parseLong(accountId));
        return count > 0;
    }

    @Override
    public String updateClient(Client client) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE clients SET");
        List<Object> args = new ArrayList<>();

        if (client.getName() != null) {
            sqlBuilder.append(" name = ?,");
            args.add(client.getName());
        }
        if (client.getPhone() != null) {
            sqlBuilder.append(" phone = ?,");
            args.add(client.getPhone());
        }
        if (client.getAddress() != null) {
            sqlBuilder.append(" address = ?,");
            args.add(client.getAddress());
        }
        if (client.getBalance() != null) {
            sqlBuilder.append(" balance = ?,");
            args.add(client.getBalance());
        }

        // Check if any fields were updated
        if (args.isEmpty()) {
            // No fields were updated, so return a message indicating that nothing was changed
            throw new RuntimeException("No fields were updated.");
        }

        // Remove the trailing comma from the SQL statement
        sqlBuilder.setLength(sqlBuilder.length() - 1);

        sqlBuilder.append(" WHERE account_id = ?");
        args.add(client.getAccountId());

        String sql = sqlBuilder.toString();
        Object[] argArray = args.toArray();

        int rowsUpdated = jdbcTemplate.update(sql, argArray);

        if (rowsUpdated > 0) {
            return "Client with ID " + client.getAccountId() + " has been updated.";
        } else {
            throw new RuntimeException("Failed to update client with ID " + client.getAccountId());
        }
    }

    @Override
    public String deleteClient(Long id) {
        String sql = "DELETE FROM clients WHERE account_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        if (rowsAffected > 0) {
            return "Client deleted";
        } else {
            return "Client not found";
        }
    }
}