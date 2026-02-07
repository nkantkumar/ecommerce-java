package com.mcp;

import java.sql.*;
import java.util.*;

public class DatabaseMCPServer {

    private Connection connection;

    public DatabaseMCPServer(String dbUrl) throws SQLException {
        // Connect to database
        this.connection = DriverManager.getConnection(dbUrl);
    }

    // Tool: Search customers
    public Map<String, Object> searchCustomers(String query) {
        List<Map<String, String>> results = new ArrayList<>();

        try {
            String sql = "SELECT id, name, email FROM customers WHERE name LIKE ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "%" + query + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(Map.of(
                        "id", String.valueOf(rs.getInt("id")),
                        "name", rs.getString("name"),
                        "email", rs.getString("email")
                ));
            }

        } catch (SQLException e) {
            return Map.of("error", e.getMessage());
        }

        return Map.of(
                "content", List.of(
                        Map.of(
                                "type", "text",
                                "text", "Found " + results.size() + " customers:\n" +
                                        formatResults(results)
                        )
                )
        );
    }

    // Tool: Get customer details
    public Map<String, Object> getCustomerDetails(int customerId) {
        try {
            String sql = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, customerId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, String> customer = Map.of(
                        "id", String.valueOf(rs.getInt("id")),
                        "name", rs.getString("name"),
                        "email", rs.getString("email"),
                        "phone", rs.getString("phone"),
                        "created_at", rs.getTimestamp("created_at").toString()
                );

                return Map.of(
                        "content", List.of(
                                Map.of("type", "text", "text", formatCustomer(customer))
                        )
                );
            }

        } catch (SQLException e) {
            return Map.of("error", e.getMessage());
        }

        return Map.of("error", "Customer not found");
    }

    // Tool: Create customer ticket
    public Map<String, Object> createTicket(int customerId, String issue) {
        try {
            String sql = "INSERT INTO tickets (customer_id, issue, status) VALUES (?, ?, 'open')";
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, customerId);
            stmt.setString(2, issue);

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int ticketId = rs.getInt(1);
                    return Map.of(
                            "content", List.of(
                                    Map.of("type", "text",
                                            "text", "✅ Ticket #" + ticketId + " created successfully")
                            )
                    );
                }
            }

        } catch (SQLException e) {
            return Map.of("error", e.getMessage());
        }

        return Map.of("error", "Failed to create ticket");
    }

    private String formatResults(List<Map<String, String>> results) {
        StringBuilder sb = new StringBuilder();
        for (Map<String, String> row : results) {
            sb.append("- ").append(row.get("name"))
                    .append(" (").append(row.get("email")).append(")\n");
        }
        return sb.toString();
    }

    private String formatCustomer(Map<String, String> customer) {
        return String.format(
                "Customer ID: %s\nName: %s\nEmail: %s\nPhone: %s\nCreated: %s",
                customer.get("id"),
                customer.get("name"),
                customer.get("email"),
                customer.get("phone"),
                customer.get("created_at")
        );
    }
}
