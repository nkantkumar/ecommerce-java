package com.jpa.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    List<Customer> findByTier(CustomerTier tier);

    @Query("SELECT c FROM Customer c WHERE c.createdAt >= :since")
    List<Customer> findRecentCustomers(@Param("since") LocalDateTime since);

    @Query("SELECT c FROM Customer c JOIN c.orders o " +
            "WHERE o.status = :status " +
            "GROUP BY c " +
            "HAVING COUNT(o) >= :minOrders")
    List<Customer> findCustomersWithMinimumOrders(
            @Param("status") OrderStatus status,
            @Param("minOrders") Long minOrders
    );

    boolean existsByEmail(String email);
}
