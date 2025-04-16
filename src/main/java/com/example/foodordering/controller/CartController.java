package com.example.foodordering.controller;

import com.example.foodordering.config.CustomUserDetails;
import com.example.foodordering.dto.request.CartItemRequest;
import com.example.foodordering.entity.OrderMenuItem;
import com.example.foodordering.service.CartService;
import com.example.foodordering.utils.UserDetailsHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing cart operations using Thymeleaf.
 * Supports retrieving cart items, adding, updating, and removing items.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Retrieves the cart items of a specific customer.
     * @param model      Model to add cart items.
     * @return Thymeleaf template for the cart page.
     */
    @GetMapping
    public String getCart(Model model) {
        Optional<CustomUserDetails> userDetails = UserDetailsHelper.getUserDetails();
        if (userDetails.isPresent()) {
            List<OrderMenuItem> cartItems = cartService.getCart(userDetails.get().getAccountId());
            model.addAttribute("cartItems", cartItems);
            return "cart/list";
        }
        return "redirect:/login"; // Hoặc xử lý trường hợp chưa đăng nhập khác
    }

    /**
     * Adds an item to the cart.
     *
     * @return Redirect to cart page after adding.
     */
    @PostMapping("/items/add")
    public String addItemToCart(@RequestParam("foodId") int foodId,
                                @RequestParam("quantity") int quantity) {
        Optional<CustomUserDetails> userDetails = UserDetailsHelper.getUserDetails();
        if (userDetails.isPresent()) {CartItemRequest request = new CartItemRequest();
            request.setFoodId(foodId);
            request.setQuantity(quantity);
            request.setCustomerId(userDetails.get().getAccountId());
            cartService.addItemToCart(request);
            return "redirect:/cart";
        }
        return "redirect:/login";
    }


    /**
     * Updates the quantity of a cart item.
     *
     * @param cartItemId  ID of the cart item.
     * @param newQuantity New quantity to update.
     * @param model       Model to add updated cart item.
     * @return Thymeleaf template for updated cart item.
     */
    @PutMapping("/items/{cartItemId}")
    public String updateItemQuantity(@PathVariable int cartItemId, @RequestParam int newQuantity, Model model) {
        Optional<CustomUserDetails> userDetails = UserDetailsHelper.getUserDetails();
        if (userDetails.isPresent()) {
            OrderMenuItem item = cartService.updateItemQuantity(userDetails.get().getAccountId(), cartItemId, newQuantity).getData();
            model.addAttribute("item", item);
            return "cart/item";
        }
        return "redirect:/login"; // Hoặc xử lý trường hợp chưa đăng nhập khác
    }

    /**
     * Removes an item from the cart.
     *
     * @param cartItemId ID of the cart item to remove.
     * @return Redirect to cart page after removal.
     */
    @PostMapping("/items/delete/{cartItemId}")
    public String removeItemFromCart(@PathVariable int cartItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            int customerId = (int) userDetails.getAccountId();
            cartService.removeItemFromCart(customerId, cartItemId);
            return "redirect:/cart"; // Redirect to cart page
        }
        return "redirect:/login"; // Hoặc xử lý trường hợp chưa đăng nhập khác
    }

    /**
     * Clears the entire cart of a customer.
     *
     * @return Redirect to cart page after clearing.
     */
    @PostMapping("/items/clear")
    public String clearCart() {
        Optional<CustomUserDetails> userDetails = UserDetailsHelper.getUserDetails();
        if (userDetails.isPresent()) {
            cartService.clearCart(userDetails.get().getAccountId());
            return "redirect:/home"; // Redirect to cart page
        }
        return "redirect:/login"; // Hoặc xử lý trường hợp chưa đăng nhập khác
    }
}