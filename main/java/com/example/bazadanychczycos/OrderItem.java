package com.example.bazadanychczycos;

public class OrderItem {
    private long id;
    private String date;
    private String customerName;
    private String details;
    private float totalPrice;

    public OrderItem(long id, String date, String customerName, String details, float totalPrice) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
        this.details = details;
        this.totalPrice = totalPrice;
    }

    // Gettery
    public long getId() { return id; }
    public String getDate() { return date; }
    public String getCustomerName() { return customerName; }
    public String getDetails() { return details; }
    public float getTotalPrice() { return totalPrice; }
}
