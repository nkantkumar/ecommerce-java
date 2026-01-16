package com.tablebooking;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RestaurantReservationSystem {

    // Thread-safe storage
    private final ConcurrentHashMap<String, Table> tables;
    private final ConcurrentHashMap<String, Reservation> reservations;

    // Fine-grained locking - one lock per table
    private final ConcurrentHashMap<String, ReentrantLock> tableLocks;

    // Lock for reservation ID generation and reservation map updates
    private final ReentrantReadWriteLock reservationLock;

    // Configuration
    private final int minPartySize = 1;
    private final int maxPartySize = 20;
    private final Duration minReservationDuration = Duration.ofMinutes(30);
    private final Duration maxReservationDuration = Duration.ofHours(4);
    private final Duration bufferTime = Duration.ofMinutes(15); // Cleanup buffer between reservations

    public RestaurantReservationSystem() {
        this.tables = new ConcurrentHashMap<>();
        this.reservations = new ConcurrentHashMap<>();
        this.tableLocks = new ConcurrentHashMap<>();
        this.reservationLock = new ReentrantReadWriteLock();

        initializeTables();
    }

    private void initializeTables() {
        // Add sample tables
        addTable(new Table("T001", 2, TableType.INDOOR));
        addTable(new Table("T002", 2, TableType.INDOOR));
        addTable(new Table("T003", 4, TableType.INDOOR));
        addTable(new Table("T004", 4, TableType.INDOOR));
        addTable(new Table("T005", 6, TableType.INDOOR));
        addTable(new Table("T006", 8, TableType.INDOOR));
        addTable(new Table("T007", 4, TableType.OUTDOOR));
        addTable(new Table("T008", 4, TableType.OUTDOOR));
        addTable(new Table("T009", 10, TableType.PRIVATE));
        addTable(new Table("T010", 4, TableType.BAR));
    }

    private void addTable(Table table) {
        tables.put(table.getTableId(), table);
        tableLocks.put(table.getTableId(), new ReentrantLock());
    }

    // ============= Core Booking Logic =============

    public Reservation makeReservation(ReservationRequest request) throws ReservationException {
        validateReservationRequest(request);

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();
        int partySize = request.getPartySize();

        // Find available tables
        List<Table> availableTables = findAvailableTables(startTime, endTime, partySize,
                request.getPreferredTableType());

        if (availableTables.isEmpty()) {
            throw new NoAvailableTableException(
                    String.format("No available tables for party of %d at %s", partySize, startTime)
            );
        }

        // Select best table (smallest that fits)
        Table selectedTable = selectBestTable(availableTables, partySize);

        // Try to book the table with fine-grained locking
        ReentrantLock tableLock = tableLocks.get(selectedTable.getTableId());
        tableLock.lock();
        try {
            // Double-check availability after acquiring lock
            if (!isTableAvailable(selectedTable, startTime, endTime)) {
                throw new NoAvailableTableException("Table became unavailable during booking process");
            }

            // Create reservation
            String reservationId = generateReservationId();
            Reservation reservation = new Reservation(
                    reservationId,
                    request.getCustomerName(),
                    request.getCustomerPhone(),
                    request.getCustomerEmail(),
                    startTime,
                    endTime,
                    partySize,
                    selectedTable
            );

            // Store reservation
            reservationLock.writeLock().lock();
            try {
                reservations.put(reservationId, reservation);
            } finally {
                reservationLock.writeLock().unlock();
            }

            System.out.println("✓ Reservation created: " + reservation);
            return reservation;

        } finally {
            tableLock.unlock();
        }
    }

    public boolean cancelReservation(String reservationId) throws ReservationException {
        reservationLock.writeLock().lock();
        try {
            Reservation reservation = reservations.get(reservationId);

            if (reservation == null) {
                throw new ReservationNotFoundException("Reservation not found: " + reservationId);
            }

            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                throw new InvalidReservationException("Reservation already cancelled");
            }

            if (reservation.getStatus() == ReservationStatus.COMPLETED) {
                throw new InvalidReservationException("Cannot cancel completed reservation");
            }

            // Check if cancellation is allowed (e.g., not too close to reservation time)
            if (LocalDateTime.now().isAfter(reservation.getStartTime().minusHours(2))) {
                throw new InvalidReservationException(
                        "Cannot cancel reservation less than 2 hours before start time"
                );
            }

            reservation.setStatus(ReservationStatus.CANCELLED);
            System.out.println("✓ Reservation cancelled: " + reservationId);
            return true;

        } finally {
            reservationLock.writeLock().unlock();
        }
    }

    // ============= Availability Checking =============

    private List<Table> findAvailableTables(LocalDateTime startTime, LocalDateTime endTime,
                                            int partySize, TableType preferredType) {
        return tables.values().stream()
                .filter(table -> table.getCapacity() >= partySize)
                .filter(table -> preferredType == null || table.getType() == preferredType)
                .filter(table -> isTableAvailable(table, startTime, endTime))
                .collect(Collectors.toList());
    }

    private boolean isTableAvailable(Table table, LocalDateTime startTime, LocalDateTime endTime) {
        // Add buffer time for cleanup
        LocalDateTime bufferedStart = startTime.minus(bufferTime);
        LocalDateTime bufferedEnd = endTime.plus(bufferTime);

        reservationLock.readLock().lock();
        try {
            return reservations.values().stream()
                    .filter(Reservation::isActive)
                    .filter(r -> r.getTable().equals(table))
                    .noneMatch(r -> r.overlaps(bufferedStart, bufferedEnd));
        } finally {
            reservationLock.readLock().unlock();
        }
    }

    private Table selectBestTable(List<Table> availableTables, int partySize) {
        return availableTables.stream()
                .min(Comparator.comparingInt(Table::getCapacity))
                .orElseThrow(() -> new RuntimeException("No tables available"));
    }

    // ============= Query Methods =============

    public List<Reservation> getReservationsByCustomer(String customerPhone) {
        reservationLock.readLock().lock();
        try {
            return reservations.values().stream()
                    .filter(r -> r.getCustomerPhone().equals(customerPhone))
                    .filter(Reservation::isActive)
                    .sorted(Comparator.comparing(Reservation::getStartTime))
                    .collect(Collectors.toList());
        } finally {
            reservationLock.readLock().unlock();
        }
    }

    public List<Reservation> getReservationsByDate(LocalDate date) {
        reservationLock.readLock().lock();
        try {
            return reservations.values().stream()
                    .filter(Reservation::isActive)
                    .filter(r -> r.getStartTime().toLocalDate().equals(date))
                    .sorted(Comparator.comparing(Reservation::getStartTime))
                    .collect(Collectors.toList());
        } finally {
            reservationLock.readLock().unlock();
        }
    }

    public Map<String, List<Reservation>> getReservationsByTable(LocalDate date) {
        reservationLock.readLock().lock();
        try {
            return reservations.values().stream()
                    .filter(Reservation::isActive)
                    .filter(r -> r.getStartTime().toLocalDate().equals(date))
                    .collect(Collectors.groupingBy(
                            r -> r.getTable().getTableId(),
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    list -> list.stream()
                                            .sorted(Comparator.comparing(Reservation::getStartTime))
                                            .collect(Collectors.toList())
                            )
                    ));
        } finally {
            reservationLock.readLock().unlock();
        }
    }

    public List<TimeSlot> getAvailableTimeSlots(LocalDate date, int partySize,
                                                Duration duration, TableType preferredType) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalDateTime startOfDay = date.atTime(11, 0); // Restaurant opens at 11 AM
        LocalDateTime endOfDay = date.atTime(22, 0);   // Last reservation at 10 PM

        LocalDateTime currentTime = startOfDay;
        Duration slotIncrement = Duration.ofMinutes(30);

        while (currentTime.plus(duration).isBefore(endOfDay) ||
                currentTime.plus(duration).equals(endOfDay)) {
            LocalDateTime slotEnd = currentTime.plus(duration);

            List<Table> available = findAvailableTables(currentTime, slotEnd, partySize, preferredType);

            if (!available.isEmpty()) {
                availableSlots.add(new TimeSlot(currentTime, slotEnd, available.size()));
            }

            currentTime = currentTime.plus(slotIncrement);
        }

        return availableSlots;
    }

    // ============= Validation =============

    private void validateReservationRequest(ReservationRequest request) throws InvalidReservationException {
        if (request.getPartySize() < minPartySize || request.getPartySize() > maxPartySize) {
            throw new InvalidReservationException(
                    String.format("Party size must be between %d and %d", minPartySize, maxPartySize)
            );
        }

        if (request.getDuration().compareTo(minReservationDuration) < 0 ||
                request.getDuration().compareTo(maxReservationDuration) > 0) {
            throw new InvalidReservationException(
                    String.format("Reservation duration must be between %d minutes and %d hours",
                            minReservationDuration.toMinutes(), maxReservationDuration.toHours())
            );
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidReservationException("Cannot make reservations in the past");
        }

        if (request.getStartTime().isAfter(LocalDateTime.now().plusMonths(3))) {
            throw new InvalidReservationException("Cannot make reservations more than 3 months in advance");
        }
    }

    // ============= Utility Methods =============

    private String generateReservationId() {
        return "RES-" + System.currentTimeMillis() + "-" +
                ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    public Reservation getReservation(String reservationId) {
        reservationLock.readLock().lock();
        try {
            return reservations.get(reservationId);
        } finally {
            reservationLock.readLock().unlock();
        }
    }

    public int getActiveReservationCount() {
        reservationLock.readLock().lock();
        try {
            return (int) reservations.values().stream()
                    .filter(Reservation::isActive)
                    .count();
        } finally {
            reservationLock.readLock().unlock();
        }
    }

    // ============= Supporting Classes =============

    static class TimeSlot {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
        private final int availableTables;

        public TimeSlot(LocalDateTime startTime, LocalDateTime endTime, int availableTables) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.availableTables = availableTables;
        }

        @Override
        public String toString() {
            return String.format("%s - %s (%d tables available)",
                    startTime.toLocalTime(), endTime.toLocalTime(), availableTables);
        }
    }

    // ============= Main Method for Testing =============

    public static void main(String[] args) {
        RestaurantReservationSystem system = new RestaurantReservationSystem();

        System.out.println("=== Restaurant Reservation System Test ===\n");

        // Test 1: Simple reservation
        try {
            ReservationRequest request1 = new ReservationRequest(
                    "John Doe",
                    "+1234567890",
                    "john@example.com",
                    LocalDateTime.now().plusDays(1).withHour(19).withMinute(0),
                    Duration.ofHours(2),
                    4,
                    TableType.INDOOR
            );

            Reservation res1 = system.makeReservation(request1);
            System.out.println("Reservation 1 created: " + res1.getReservationId() + "\n");

        } catch (ReservationException e) {
            System.err.println("Error: " + e.getMessage());
        }

        // Test 2: Concurrent bookings simulation
        System.out.println("=== Testing Concurrent Bookings ===\n");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(10);

        LocalDateTime bookingTime = LocalDateTime.now().plusDays(2).withHour(18).withMinute(0);

        for (int i = 0; i < 10; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    ReservationRequest request = new ReservationRequest(
                            "Customer " + index,
                            "+123456" + String.format("%04d", index),
                            "customer" + index + "@example.com",
                            bookingTime,
                            Duration.ofHours(2),
                            2,
                            null
                    );

                    Reservation res = system.makeReservation(request);
                    System.out.println("Thread " + index + " booked: " + res.getTable().getTableId());

                } catch (ReservationException e) {
                    System.out.println("Thread " + index + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nTotal active reservations: " + system.getActiveReservationCount());

        // Test 3: Check availability
        System.out.println("\n=== Available Time Slots for Tomorrow ===");
        List<TimeSlot> slots = system.getAvailableTimeSlots(
                LocalDate.now().plusDays(1),
                4,
                Duration.ofHours(2),
                TableType.INDOOR
        );

        slots.forEach(System.out::println);

        // Test 4: View reservations by date
        System.out.println("\n=== Reservations by Table for Day After Tomorrow ===");
        Map<String, List<Reservation>> byTable = system.getReservationsByTable(
                LocalDate.now().plusDays(2)
        );

        byTable.forEach((tableId, reservations) -> {
            System.out.println("\nTable " + tableId + ":");
            reservations.forEach(r -> System.out.println("  " + r.getStartTime().toLocalTime() +
                    " - " + r.getCustomerName()));
        });
    }
}
