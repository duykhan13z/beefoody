package com.example.foodordering.dto.request;

import lombok.Data;

@Data
public class CartItemRequest {
    private int customerId;
    private int foodId;
    private int quantity;
}
