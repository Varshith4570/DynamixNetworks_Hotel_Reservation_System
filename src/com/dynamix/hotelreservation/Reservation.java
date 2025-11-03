package com.dynamix.hotelreservation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String reservationId;
    private Customer customer;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalCost;
    private String status;
    private static int reservationCount = 0;

    public Reservation(Customer customer, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.reservationId = "RES" + (++reservationCount);
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = "Confirmed";
        calculateTotalCost();
    }

    private void calculateTotalCost() {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        this.totalCost = nights * room.getPricePerNight();
    }

    public String getReservationId() { return reservationId; }
    public Customer getCustomer() { return customer; }
    public Room getRoom() { return room; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public double getTotalCost() { return totalCost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFormattedDates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return checkInDate.format(formatter) + " to " + checkOutDate.format(formatter);
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
}
