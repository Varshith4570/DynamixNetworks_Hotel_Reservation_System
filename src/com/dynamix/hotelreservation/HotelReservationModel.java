package com.dynamix.hotelreservation;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class HotelReservationModel {
    private List<Room> rooms;
    private List<Customer> customers;
    private List<Reservation> reservations;
    private final String ROOMS_FILE = "rooms.dat";
    private final String CUSTOMERS_FILE = "customers.dat";
    private final String RESERVATIONS_FILE = "reservations.dat";

    public HotelReservationModel() {
        this.rooms = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.reservations = new ArrayList<>();
        loadAllData();
        if (rooms.isEmpty()) {
            initializeSampleRooms();
        }
    }

    // ==================== ROOM OPERATIONS ====================

    public void addRoom(String roomType, double pricePerNight, int capacity) {
        Room room = new Room(roomType, pricePerNight, capacity);
        rooms.add(room);
        saveRooms();
    }

    public void removeRoom(String roomId) {
        Room room = findRoomById(roomId);
        if (room != null && room.isAvailable()) {
            rooms.remove(room);
            saveRooms();
        }
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public List<Room> getAvailableRooms() {
        return rooms.stream()
                .filter(Room::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut) {
        return rooms.stream()
                .filter(room -> room.isAvailable() && isRoomAvailable(room, checkIn, checkOut))
                .collect(Collectors.toList());
    }

    private Room findRoomById(String roomId) {
        return rooms.stream()
                .filter(r -> r.getRoomId().equals(roomId))
                .findFirst()
                .orElse(null);
    }

    private boolean isRoomAvailable(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        return reservations.stream()
                .filter(r -> r.getRoom().getRoomId().equals(room.getRoomId()))
                .filter(r -> r.getStatus().equals("Confirmed"))
                .noneMatch(r -> !(checkOutDate.isBefore(r.getCheckInDate()) || 
                                 checkInDate.isAfter(r.getCheckOutDate())));
    }

    // ==================== CUSTOMER OPERATIONS ====================

    public Customer addCustomer(String name, String email, String phoneNumber, String address) {
        Customer existing = customers.stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (existing != null) return existing;

        Customer customer = new Customer(name, email, phoneNumber, address);
        customers.add(customer);
        saveCustomers();
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public Customer findCustomerById(String customerId) {
        return customers.stream()
                .filter(c -> c.getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);
    }

    public Customer findCustomerByEmail(String email) {
        return customers.stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    // ==================== RESERVATION OPERATIONS ====================

    public Reservation bookReservation(String customerId, String roomId, 
                                       LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            return null;
        }

        Customer customer = findCustomerById(customerId);
        Room room = findRoomById(roomId);

        if (customer == null || room == null) return null;
        if (!isRoomAvailable(room, checkInDate, checkOutDate)) return null;

        Reservation reservation = new Reservation(customer, room, checkInDate, checkOutDate);
        reservations.add(reservation);
        room.setAvailable(false);
        saveAll();

        return reservation;
    }

    public void cancelReservation(String reservationId) {
        Reservation reservation = findReservationById(reservationId);
        if (reservation != null && reservation.getStatus().equals("Confirmed")) {
            reservation.setStatus("Cancelled");
            reservation.getRoom().setAvailable(true);
            saveAll();
        }
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public List<Reservation> getReservationsByCustomer(String customerId) {
        return reservations.stream()
                .filter(r -> r.getCustomer().getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public Reservation findReservationById(String reservationId) {
        return reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst()
                .orElse(null);
    }

    // ==================== REPORTING ====================

    public Map<String, Object> getBookingStats() {
        long totalReservations = reservations.stream()
                .filter(r -> r.getStatus().equals("Confirmed"))
                .count();

        double totalRevenue = reservations.stream()
                .filter(r -> r.getStatus().equals("Confirmed"))
                .mapToDouble(Reservation::getTotalCost)
                .sum();

        long cancelledCount = reservations.stream()
                .filter(r -> r.getStatus().equals("Cancelled"))
                .count();

        Map<String, Long> roomTypeBookings = reservations.stream()
                .filter(r -> r.getStatus().equals("Confirmed"))
                .collect(Collectors.groupingBy(
                    r -> r.getRoom().getRoomType(),
                    Collectors.counting()
                ));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReservations", totalReservations);
        stats.put("totalRevenue", totalRevenue);
        stats.put("cancelledCount", cancelledCount);
        stats.put("roomTypeBookings", roomTypeBookings);

        return stats;
    }

    // ==================== FILE OPERATIONS ====================

    public void saveRooms() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(CUSTOMERS_FILE))) {
            oos.writeObject(customers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveReservations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(RESERVATIONS_FILE))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAll() {
        saveRooms();
        saveCustomers();
        saveReservations();
    }

    public void loadAllData() {
        loadRooms();
        loadCustomers();
        loadReservations();
    }

    @SuppressWarnings("unchecked")
    private void loadRooms() {
        File file = new File(ROOMS_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(ROOMS_FILE))) {
            rooms = (List<Room>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            rooms = new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadCustomers() {
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(CUSTOMERS_FILE))) {
            customers = (List<Customer>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            customers = new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadReservations() {
        File file = new File(RESERVATIONS_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(RESERVATIONS_FILE))) {
            reservations = (List<Reservation>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            reservations = new ArrayList<>();
        }
    }

    private void initializeSampleRooms() {
        addRoom("Single", 1500, 1);
        addRoom("Double", 2500, 2);
        addRoom("Suite", 4000, 3);
        addRoom("Deluxe", 5500, 4);
        addRoom("Single", 1500, 1);
        addRoom("Double", 2500, 2);
    }
}
