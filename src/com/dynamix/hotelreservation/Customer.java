package com.dynamix.hotelreservation;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private static int customerCount = 0;

    public Customer(String name, String email, String phoneNumber, String address) {
        this.customerId = "CUST" + (++customerCount);
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return customerId + " - " + name;
    }
}
