package com.example.foodordering.controller;

import com.example.foodordering.dto.response.AccountResponse;
import com.example.foodordering.dto.response.ApiResponse;
import com.example.foodordering.entity.Customer;
import com.example.foodordering.entity.Food;
import com.example.foodordering.entity.FoodOrder;
import com.example.foodordering.repository.FoodOrderRepository;
import com.example.foodordering.repository.FoodRepository;
import com.example.foodordering.service.ManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for managing administrative operations such as users, orders, and food items.
 */
@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerService managerService;
    private final FoodRepository foodRepository;
    private final FoodOrderRepository foodOrderRepository;
    public ManagerController(ManagerService managerService, FoodRepository foodRepository, FoodOrderRepository foodOrderRepository) {
        this.managerService = managerService;
        this.foodRepository = foodRepository;
        this.foodOrderRepository = foodOrderRepository;
    }

    /**
     * Retrieves a list of all registered users.
     *
     * @param model Model to add user data.
     * @return Thymeleaf template for user list.
     */
    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<Customer> users = managerService.getAllUsers().getData();
        model.addAttribute("users", users);
        return "manager/user-list";
    }

    @GetMapping("/users/{userId}")
    public String getCurrentUser(@PathVariable int userId, Model model) {
        Customer user = managerService.getCurrentCustomer(userId).getData();
        model.addAttribute("user", user);
        return "manager/user-details";
    }

    /**
     * Retrieves a list of all placed orders.
     *
     * @param model Model to add order data.
     * @return Thymeleaf template for order list.
     */
    @GetMapping("/orders")
    public String getAllOrders(Model model) {
        List<FoodOrder> orders = managerService.getAllOrders().getData();
        model.addAttribute("orders", orders);
        return "manager/order-list";
    }

    @GetMapping("/orders/{id}")
    public String getOrderDetails(@PathVariable int id, Model model) {
        FoodOrder order = foodOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        model.addAttribute("order", order);
        return "manager/order-details"; // This corresponds to the order-details.html
    }
    /**
     * Displays the form for adding a new food item.
     *
     * @param model Model to add food data.
     * @return Thymeleaf template for adding food.
     */
    @GetMapping("/food/add")
    public String showAddFoodForm(Model model) {
        model.addAttribute("food", new Food());
        return "manager/food-form";
    }

    /**
     * List all the available foods
     *
     * @param model Model to add food data.
     * @return Thymeleaf template for adding food.
     */
    @GetMapping("/food")
    public String getAllFoods(Model model) {
        List<Food> foods = foodRepository.findAll();
        model.addAttribute("foods", foods);
        return "manager/food-list";
    }


    /**
     * Adds a new food item.
     *
     * @param food The Food entity to be added.
     * @return Redirect to food list page.
     */
    @PostMapping("/food/add")
    public String addFood(@ModelAttribute Food food) {
        managerService.addFood(food);
        return "redirect:/manager/food";
    }

    /**
     * Displays the form for editing an existing food item.
     *
     * @param foodId ID of the food item to update.
     * @param model  Model to add food data.
     * @return Thymeleaf template for editing food.
     */
    @GetMapping("/food/update/{foodId}")
    public String showEditFoodForm(@PathVariable int foodId, Model model) {
        Food food = managerService.getFoodById(foodId).getData();
        model.addAttribute("food", food);
        return "manager/food-form";
    }

    /**
     * Updates an existing food item.
     *
     * @param foodId      The ID of the food item to update.
     * @param updatedFood The Food entity containing updated information.
     * @return Redirect to food list page.
     */
    @PostMapping("/food/update/{foodId}")
    public String updateFood(@PathVariable int foodId, @ModelAttribute Food updatedFood) {
        managerService.updateFood(foodId, updatedFood);
        return "redirect:/manager/food";
    }

    /**
     * Deletes a food item.
     *
     * @param foodId The ID of the food item to delete.
     * @return Redirect to food list page.
     */
    @PostMapping("/food/delete/{foodId}")
    public String deleteFood(@PathVariable int foodId) {
        managerService.deleteFood(foodId);
        return "redirect:/manager/food";
    }
}
