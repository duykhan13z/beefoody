package com.example.foodordering.mapper;

import com.example.foodordering.dto.response.FoodResponse;
import com.example.foodordering.entity.Food;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FoodResponseMapper {
    FoodResponse toFoodResponse(Food food);
}
