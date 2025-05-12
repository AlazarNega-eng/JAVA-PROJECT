package com.tourism.model;

import java.math.BigDecimal;
import java.sql.Timestamp; // Use java.sql.Timestamp for easier JDBC mapping

public class Payment {
    private int paymentId;
    private int bookingId;
    private BigDecimal amount;
    private Timestamp paymentDate;
    private String paymentMethod;
    private String status;

    // Constructors
    public Payment() {}

    public Payment(int paymentId, int bookingId, BigDecimal amount, Timestamp paymentDate, String paymentMethod, String status) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

     @Override
    public String toString() {
        return "Payment ID: " + paymentId + ", Booking ID: " + bookingId + ", Amount: " + amount + ", Method: " + paymentMethod;
    }
}