package com.example.foodordering.dto.response;

import com.example.foodordering.entity.FoodOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerSummary {
    private int totalItems;
    private BigDecimal totalSpent;

    // Constructor
    public CustomerSummary(List<FoodOrder> foodOrders) {
        calculateTotals(foodOrders);
    }

    // Method to calculate totalItems and totalSpent from completed orders only
    private void calculateTotals(List<FoodOrder> foodOrders) {
        // Filter only completed orders (orderStatus == true for completed orders)
        List<FoodOrder> completedOrders = foodOrders.stream()
                .filter(FoodOrder::isOrderStatus)  // Assuming 'isOrderStatus' returns 'true' for completed orders
                .collect(Collectors.toList());

        // Calculate totalItems and totalSpent for completed orders
        this.totalItems = completedOrders.stream().mapToInt(FoodOrder::getTotalItems).sum();
        this.totalSpent = completedOrders.stream()
                .map(FoodOrder::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters
    public int getTotalItems() {
        return totalItems;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }
}

