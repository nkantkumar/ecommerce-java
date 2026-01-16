package com.tablebooking;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;

// ============= Core Domain Models =============

class Table {
    private final String tableId;
    private final int capacity;
    private final TableType type;

    public Table(String tableId, int capacity, TableType type) {
        this.tableId = tableId;
        this.capacity = capacity;
        this.type = type;
    }

    public String getTableId() { return tableId; }
    public int getCapacity() { return capacity; }
    public TableType getType() { return type; }

    @Override
    public String toString() {
        return String.format("Table[id=%s, capacity=%d, type=%s]", tableId, capacity, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Table)) return false;
        Table table = (Table) o;
        return Objects.equals(tableId, table.tableId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableId);
    }
}

enum TableType {
    INDOOR, OUTDOOR, PRIVATE, BAR
}

enum ReservationStatus {
    CONFIRMED, CANCELLED, COMPLETED, NO_SHOW
}

class Reservation {
    private final String reservationId;
    private final String customerName;
    private final String customerPhone;
    private final String customerEmail;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final int partySize;
    private final Table table;
    private ReservationStatus status;
    private final LocalDateTime createdAt;

    public Reservation(String reservationId, String customerName, String customerPhone,
                       String customerEmail, LocalDateTime startTime, LocalDateTime endTime,
                       int partySize, Table table) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.partySize = partySize;
        this.table = table;
        this.status = ReservationStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public String getReservationId() { return reservationId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getPartySize() { return partySize; }
    public Table getTable() { return table; }
    public ReservationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return status == ReservationStatus.CONFIRMED;
    }

    public boolean overlaps(LocalDateTime start, LocalDateTime end) {
        return !this.endTime.isBefore(start) && !this.startTime.isAfter(end);
    }

    @Override
    public String toString() {
        return String.format("Reservation[id=%s, customer=%s, time=%s, partySize=%d, table=%s, status=%s]",
                reservationId, customerName, startTime, partySize, table.getTableId(), status);
    }
}

class ReservationRequest {
    private final String customerName;
    private final String customerPhone;
    private final String customerEmail;
    private final LocalDateTime startTime;
    private final Duration duration;
    private final int partySize;
    private final TableType preferredTableType;

    public ReservationRequest(String customerName, String customerPhone, String customerEmail,
                              LocalDateTime startTime, Duration duration, int partySize,
                              TableType preferredTableType) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.startTime = startTime;
        this.duration = duration;
        this.partySize = partySize;
        this.preferredTableType = preferredTableType;
    }

    // Getters
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public LocalDateTime getStartTime() { return startTime; }
    public Duration getDuration() { return duration; }
    public int getPartySize() { return partySize; }
    public TableType getPreferredTableType() { return preferredTableType; }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }
}

// ============= Custom Exceptions =============

class ReservationException extends Exception {
    public ReservationException(String message) {
        super(message);
    }
}

class NoAvailableTableException extends ReservationException {
    public NoAvailableTableException(String message) {
        super(message);
    }
}

class ReservationNotFoundException extends ReservationException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}

class InvalidReservationException extends ReservationException {
    public InvalidReservationException(String message) {
        super(message);
    }
}

// ============= Main Reservation System =============

