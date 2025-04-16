package com.example.foodordering.service;

import com.example.foodordering.dto.response.ApiResponse;
import com.example.foodordering.entity.Customer;
import com.example.foodordering.entity.Food;
import com.example.foodordering.entity.FoodOrder;
import com.example.foodordering.repository.intf.CustomerRepository;
import com.example.foodordering.repository.FoodOrderRepository;
import com.example.foodordering.repository.FoodRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
//@Transactional
public class ManagerService {

    private final CustomerRepository customerRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final FoodRepository foodRepository;

    public ManagerService(CustomerRepository customerRepository, FoodOrderRepository foodOrderRepository, FoodRepository foodRepository) {
        this.customerRepository = customerRepository;
        this.foodOrderRepository = foodOrderRepository;
        this.foodRepository = foodRepository;
    }

    /**
     * Retrieves a list of all registered users.
     *
     * @return ApiResponse containing a list of Customer entities.
     */
    public ApiResponse<List<Customer>> getAllUsers() {
        List<Customer> users = customerRepository.findAll();
        return ApiResponse.build(1000, "Success", users);
    }

    public ApiResponse<Customer> getCurrentCustomer(int userId) {
        Customer user = customerRepository.findById(userId).get();
        return ApiResponse.build(1000, "Success", user);
    }

    /**
     * Retrieves a list of all placed orders.
     *
     * @return ApiResponse containing a list of FoodOrder entities.
     */
    public ApiResponse<List<FoodOrder>> getAllOrders() {
        // Assuming 'true' in statusValue represents a placed order
        List<FoodOrder> orders = foodOrderRepository.findByOrderStatus(true);
        return ApiResponse.build(1000, "Success", orders);
    }

    /**
     * Adds a new food item to the menu.
     *
     * @param food The Food entity to be added.
     */
    public void addFood(Food food) {
        Food savedFood = foodRepository.save(food);
        ApiResponse.build(1201, "Food added successfully", savedFood);
    }

    /**
     * Updates an existing food item.
     *
     * @param foodId      The ID of the food item to update.
     * @param updatedFood The Food entity containing updated information.
     */
    public void updateFood(int foodId, Food updatedFood) {
        Optional<Food> existingFoodOptional = foodRepository.findById(foodId);
        if (existingFoodOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Food item not found with ID: " + foodId);
        }
        Food existingFood = existingFoodOptional.get();
        existingFood.setName(updatedFood.getName());
        existingFood.setDescription(updatedFood.getDescription());
        existingFood.setPrice(updatedFood.getPrice());
        existingFood.setImage(updatedFood.getImage());
        existingFood.setIsAvailable(updatedFood.getIsAvailable());
        foodRepository.save(existingFood);

    }

    /**
     * Deletes a food item by marking it as unavailable.
     *
     * @param foodId The ID of the food item to delete.
     */
    public void deleteFood(int foodId) {
        foodRepository.deleteById(foodId);
    }

    /**
     * Retrieves a food item by its ID.
     *
     * @param foodId The ID of the food item to retrieve.
     * @return ApiResponse containing the Food entity.
     */
    public ApiResponse<Food> getFoodById(int foodId) {
        Optional<Food> foodOptional = foodRepository.findById(foodId);
        if (foodOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Food item not found with ID: " + foodId);
        }
        return ApiResponse.build(1000, "Success", foodOptional.get());
    }

    /**
     * Retrieves sales statistics.
     *
     * @return ApiResponse containing sales statistics (for now, just a list of all placed orders).
     */
    public ApiResponse<List<FoodOrder>> getSalesStatistics() {
        // In a real application, you would perform calculations here.
        // For simplicity, we are just returning all placed orders.
        List<FoodOrder> placedOrders = foodOrderRepository.findByOrderStatus(true);
        return ApiResponse.build(1000, "Success", placedOrders);
    }
}
