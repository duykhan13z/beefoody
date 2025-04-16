package com.example.foodordering.service;

import com.example.foodordering.dto.request.SearchFoodRequest;
import com.example.foodordering.dto.response.ApiResponse;
import com.example.foodordering.dto.response.FoodResponse;
import com.example.foodordering.entity.Food;
import com.example.foodordering.mapper.FoodResponseMapper;
import com.example.foodordering.repository.FoodRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service to handle guest-related operations, such as retrieving available dishes,
 * searching food items, filtering by category, and providing recommendations.
 */
@Service
public class GuestService {

    private final FoodRepository foodRepository;

    public GuestService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    /**
     * Retrieves the list of available dishes (where isAvailable = true).
     *
     * @return ApiResponse containing available dishes or an error if none found.
     */
    public List<Food> getAvailableDishes() {
        return foodRepository.findByIsAvailableTrue();
    }

    public List<Food> searchDishes(SearchFoodRequest request) {
        // Nếu không có từ khóa, trả về tất cả món ăn
        if (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) {
            return foodRepository.findAll();  // Nếu không có từ khóa, lấy tất cả món ăn
        }
        // Tìm kiếm theo tên món ăn
        return foodRepository.findByNameContainingIgnoreCase(request.getKeyword());
    }

    /**
     * Retrieves details of a specific food item.
     *
     * @param foodId the ID of the food item
     * @return ApiResponse containing food details or an error if not found.
     */

    public Food getFoodDetails(int foodId) {
        if (foodRepository.findById(foodId).isPresent()) {
            return foodRepository.findById(foodId).get();
        }
        return null;
    }
}

