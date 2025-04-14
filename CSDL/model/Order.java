package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    private int id;
    private int customerId;
    private String orderDate;
    private String status;
    private double totalAmount;

    public Order() {

    }

    public Order(int id, int customerId, String orderDate, String status) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Order(int customerId) {
        this();
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getOderDate() {
        return orderDate;
    }

    public void setOderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}