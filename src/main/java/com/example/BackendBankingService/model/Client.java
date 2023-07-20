package com.example.BackendBankingService.model;

import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class Client {

    private Long accountId;
    @NotBlank(message = "Name cannot be null")
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;

    @NotBlank(message = "Phone number cannot be null")
    @Pattern(regexp = "[0-9]{10}", message = "Phone number must be a 10-digit number")
    private String phone;

    @NotBlank(message = "Address cannot be null")
    @Size(min = 1, max = 100, message = "Address must be between 1 and 100 characters")
    private String address;


    @NotNull(message = "Balance shouldn't be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @DecimalMax(value = "9999999999999999999.0", inclusive = true, message = "Balance cannot exceed 9999999999999999999.0")
    private BigDecimal balance;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}