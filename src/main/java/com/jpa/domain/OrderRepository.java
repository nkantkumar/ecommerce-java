package com.jpa.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o " +
            "WHERE o.customer.id = :customerId " +
            "AND o.orderDate >= :startDate " +
            "AND o.orderDate <= :endDate")
    List<Order> findCustomerOrdersInDateRange(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.items " +
            "WHERE o.status = :status")
    List<Order> findByStatusWithItems(@Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o " +
            "WHERE o.customer.id = :customerId " +
            "AND o.status = 'DELIVERED'")
    BigDecimal calculateTotalSpent(@Param("customerId") Long customerId);

    @Modifying
    @Query("UPDATE Order o SET o.status = :newStatus " +
            "WHERE o.status = :oldStatus " +
            "AND o.orderDate < :cutoffDate")
    int updateOldOrderStatus(
            @Param("oldStatus") OrderStatus oldStatus,
            @Param("newStatus") OrderStatus newStatus,
            @Param("cutoffDate") LocalDateTime cutoffDate
    );
}
