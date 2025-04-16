package com.example.foodordering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class FoodResponse {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String description;
    private String image;
}
