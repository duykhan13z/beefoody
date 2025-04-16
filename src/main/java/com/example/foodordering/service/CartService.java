package com.example.foodordering.service;

import com.example.foodordering.dto.request.CartItemRequest;
import com.example.foodordering.dto.response.ApiResponse;
import com.example.foodordering.entity.*;
import com.example.foodordering.repository.FoodOrderRepository;
import com.example.foodordering.repository.intf.CustomerRepository;
import com.example.foodordering.repository.FoodRepository;
import com.example.foodordering.repository.OrderMenuItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service to manage cart operations including adding, updating, retrieving, and deleting cart items.
 */
@Service
public class CartService {

    private final OrderMenuItemRepository orderMenuItemRepository;
    private final FoodRepository foodRepository;
    private final CustomerRepository customerRepository;
    private final FoodOrderRepository foodOrderRepository; // Inject FoodOrderRepository

    public CartService(OrderMenuItemRepository orderMenuItemRepository, FoodRepository foodRepository,
                       CustomerRepository customerRepository, FoodOrderRepository foodOrderRepository) {
        this.orderMenuItemRepository = orderMenuItemRepository;
        this.foodRepository = foodRepository;
        this.customerRepository = customerRepository;
        this.foodOrderRepository = foodOrderRepository;
    }

    /**
     * Retrieves the cart items for a specific customer.
     *
     * @param customerId ID of the customer.
     * @return ApiResponse containing cart items or an empty list if the cart is empty.
     */
    public List<OrderMenuItem> getCart(int customerId) {
        List<FoodOrder> cartOrders = foodOrderRepository.findByCustomer_IdAndOrderStatus(
                customerId,
                false
        );

        if (!cartOrders.isEmpty()) {
            return orderMenuItemRepository.findByFoodOrder_Id(cartOrders.getFirst().getId());
        }
        return List.of();
    }


    /**
     * Adds an item to the cart.
     *
     * @param request the request containing customerId, foodId, and quantity.
     */
    public void addItemToCart(CartItemRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found"));

        // Find existing cart (orderStatus = false)
        List<FoodOrder> existingCartOrders = foodOrderRepository.findByCustomer_IdAndOrderStatus(
                request.getCustomerId(), false);
        FoodOrder cartOrder = existingCartOrders.isEmpty()
                ? createNewCart(customer)
                : existingCartOrders.getFirst();

        // Ensure cart item list is initialized
        if (cartOrder.getOrderMenuItems() == null) {
            cartOrder.setOrderMenuItems(new ArrayList<>());
        }

        // Check if food already exists in cart
        Optional<OrderMenuItem> existingItem = cartOrder.getOrderMenuItems().stream()
                .filter(item -> item.getFood().getId() == food.getId())
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            OrderMenuItem cartItem = existingItem.get();
            cartItem.setQuantityOrdered(cartItem.getQuantityOrdered() + request.getQuantity());
            orderMenuItemRepository.save(cartItem);
        } else {
            // Create new item
            OrderMenuItem cartItem = new OrderMenuItem();
            cartItem.setFood(food);
            cartItem.setQuantityOrdered(request.getQuantity());
            cartItem.setFoodOrder(cartOrder);
            orderMenuItemRepository.save(cartItem);
            cartOrder.getOrderMenuItems().add(cartItem);
        }

        // Update cart totals
        updateCartTotals(cartOrder);
    }

    private FoodOrder createNewCart(Customer customer) {
        FoodOrder cartOrder = new FoodOrder();
        cartOrder.setCustomer(customer);
        cartOrder.setOrderStatus(false); // false = still in cart
        cartOrder.setTotalItems(0);
        cartOrder.setPrice(BigDecimal.ZERO);
        return foodOrderRepository.save(cartOrder);
    }



    /**
     * Updates the quantity of an item in the cart.
     *
     * @param customerId  ID of the logged-in customer.
     * @param cartItemId  ID of the cart item.
     * @param newQuantity the new quantity.
     * @return ApiResponse containing the updated cart item.
     */
    public ApiResponse<OrderMenuItem> updateItemQuantity(int customerId, int cartItemId, int newQuantity) {
        OrderMenuItem cartItem = orderMenuItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        FoodOrder cartOrder = cartItem.getFoodOrder();
        if (cartOrder.getCustomer().getId() != customerId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cart item does not belong to the current customer's cart");
        }

        cartItem.setQuantityOrdered(newQuantity);
        orderMenuItemRepository.save(cartItem);

        // Cập nhật totalItems và price trong FoodOrder tương ứng
        updateCartTotals(cartOrder);

        return ApiResponse.build(1000, "Success", cartItem);
    }

    /**
     * Removes an item from the cart.
     *
     * @param customerId ID of the logged-in customer.
     * @param cartItemId ID of the cart item to be removed.
     */
    public void removeItemFromCart(int customerId, int cartItemId) {
        OrderMenuItem cartItem = orderMenuItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        FoodOrder cartOrder = cartItem.getFoodOrder();
        if (cartOrder.getCustomer().getId() != customerId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cart item does not belong to the current customer's cart");
        }

        orderMenuItemRepository.deleteById(cartItemId);

        // Cập nhật totalItems và price trong FoodOrder
        updateCartTotals(cartOrder);
    }

    /**
     * Clears the entire cart for a specific customer.
     *
     * @param customerId ID of the logged-in customer.
     */
    public void clearCart(int customerId) {
        // Find the customer's active cart (orderStatus = false)
        List<OrderMenuItem> orderMenuItems = orderMenuItemRepository.findAll();
        FoodOrder cartOrder = orderMenuItems.getFirst().getFoodOrder();
        if (cartOrder.getCustomer().getId() != customerId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cart item does not belong to the current customer's cart");
        }
        for (OrderMenuItem item : orderMenuItems) {
            orderMenuItemRepository.deleteById(item.getId());
        }
    }

    private void updateCartTotals(FoodOrder cartOrder) {
        int totalItems = cartOrder.getOrderMenuItems() == null ? 0 : cartOrder.getOrderMenuItems().stream().mapToInt(OrderMenuItem::getQuantityOrdered).sum();
        BigDecimal totalPrice = cartOrder.getOrderMenuItems() == null ? BigDecimal.ZERO : cartOrder.getOrderMenuItems().stream()
                .map(item -> item.getFood().getPrice().multiply(BigDecimal.valueOf(item.getQuantityOrdered())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cartOrder.setTotalItems(totalItems);
        cartOrder.setPrice(totalPrice);
        foodOrderRepository.save(cartOrder);
    }


}