package com.example.BackendBankingService.model;

import com.example.BackendBankingService.validators.ValidAccountId;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private Long transactionId;

    @NotNull(message = "Transaction type cannot be null")
    @Pattern(regexp = "[D|W|T]", message = "Transaction type must be 'D', 'W', or 'T'")
    private String type;

    @NotNull(message = "Balance shouldn't be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @DecimalMax(value = "9999999999999999999.0", inclusive = true, message = "Balance cannot exceed 9999999999999999999.0")
    private BigDecimal amount;
    @ValidAccountId
    private Long fromAccountId;

    @ValidAccountId
    private Long toAccountId;
    private LocalDateTime dateTime;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}