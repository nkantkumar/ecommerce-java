package com.jpa.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private CustomerTier tier;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    private LocalDateTime createdAt;

    // Constructors, getters, setters
    public Customer() {
        this.createdAt = LocalDateTime.now();
    }

    public Customer(String name, String email, CustomerTier tier) {
        this.name = name;
        this.email = email;
        this.tier = tier;
        this.createdAt = LocalDateTime.now();
    }
}

