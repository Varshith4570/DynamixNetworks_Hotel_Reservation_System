package com.dynamix.hotelreservation;

import java.io.Serializable;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private String roomId;
    private String roomType;
    private double pricePerNight;
    private boolean isAvailable;
    private int capacity;
    private static int roomCount = 0;

    public Room(String roomType, double pricePerNight, int capacity) {
        this.roomId = "ROOM" + (++roomCount);
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.isAvailable = true;
    }

    public String getRoomId() { return roomId; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() {
        return roomId + " - " + roomType + " (₹" + pricePerNight + "/night)";
    }
}
