package com.example.foodordering.controller;

import com.example.foodordering.config.CustomUserDetails;
import com.example.foodordering.entity.FoodOrder;
import com.example.foodordering.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing customer orders.
 * Supports creating, listing, and retrieving order details.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order for a specific customer.
     *
     * @param model      Model to add order data.
     * @return Thymeleaf template for order success page.
     */
    @PostMapping
    public String createOrder(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            int customerId = userDetails.getAccountId();
            FoodOrder order = orderService.createOrder(customerId);
            model.addAttribute("order", order);
            return "orders/success"; // Thymeleaf template: orders/order-success.html
        }
        return "redirect:/login";
    }

    /**
     * Retrieves a list of orders for a specific customer.
     *
     * @param model      Model to add order list data.
     * @return Thymeleaf template for order list page.
     */
    @GetMapping
    public String getOrderList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            int customerId = (int) userDetails.getAccountId();
            List<FoodOrder> orders = orderService.getOrderList(customerId).getData();
            model.addAttribute("orders", orders);
            return "orders/list"; // Thymeleaf template: orders/order-list.html
        }
        return "redirect:/login";
    }

    /**
     * Retrieves details of a specific order for a customer.
     *
     * @param orderId    ID of the order.
     * @param model      Model to add order detail data.
     * @return Thymeleaf template for order detail page.
     */
    @GetMapping("/{orderId}")
    public String getOrderDetails(@PathVariable int orderId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            int customerId = (int) userDetails.getAccountId();
            FoodOrder order = orderService.getOrderDetails(orderId, customerId).getData();
            model.addAttribute("order", order);
            return "orders/detail"; // Thymeleaf template: orders/order-detail.html
        }
        return "redirect:/login";
    }
}