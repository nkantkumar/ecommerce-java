package com.fix.ecommerce.jpa;

public class OrderService {

    @TrackExecution(value = "createOrder", enabled = true)
    public void createOrder() {
        // business logic
    }
}
