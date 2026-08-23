
package com.RentFlow.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;

import com.RentFlow.enums.PaymentMethod;
import com.RentFlow.enums.PaymentStatus;

public class UpdatePaymentRequestDTO {

    @DecimalMin(
        value = "0.01",
        message = "Amount must be greater than zero"
    )
    private BigDecimal amount;

    private LocalDate paymentDate;

    private LocalDate dueDate;


    private PaymentMethod paymentMethod;

    @Size(
        max = 100,
        message = "Transaction reference cannot exceed 100 characters"
    )
    private String transactionReference;

    public UpdatePaymentRequestDTO() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }
}

