package com.example.foodordering.controller;

import com.example.foodordering.config.CustomUserDetails;
import com.example.foodordering.dto.request.SearchFoodRequest;
import com.example.foodordering.dto.response.FoodResponse;
import com.example.foodordering.entity.Food;
import com.example.foodordering.entity.FoodOrder;
import com.example.foodordering.entity.OrderMenuItem;
import com.example.foodordering.service.CartService;
import com.example.foodordering.service.GuestService;
import com.example.foodordering.utils.UserDetailsHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller xử lý các yêu cầu liên quan đến khách, bao gồm hiển thị danh sách món ăn,
 * tìm kiếm và xem chi tiết món ăn.
 */
@Controller
@RequestMapping("/")
public class GuestController {

    private final GuestService guestService;
    private final CartService cartService;
    public GuestController(GuestService guestService, CartService cartService) {
        this.guestService = guestService;
        this.cartService = cartService;
    }

    /**
     * Hiển thị trang chủ.
     *
     * @return tên view của trang chủ.
     */

    @GetMapping
    public String getRoot(Model model)
    {
        Optional<CustomUserDetails> userDetails = UserDetailsHelper.getUserDetails();
        if (userDetails.isPresent()) {
            if (Objects.equals(userDetails.get().getRole(), "customer")) {
                return "redirect:/home"; // Trả về home.html
            }

            if (Objects.equals(userDetails.get().getRole(), "manager") || Objects.equals(userDetails.get().getRole(), "admin")) {
                model.addAttribute("username", userDetails.get().getUsername());
                model.addAttribute("password", userDetails.get().getPassword());
                return "redirect:/admin/dashboard";
            }

        }
        List<Food> dishes = guestService.getAvailableDishes();
        model.addAttribute("dishes", dishes);

        // Nếu chưa đăng nhập, chuyển hướng về trang login
        return "guest";

    }

    @GetMapping({"/home"})
    public String homepage(Model model) {
        Optional<CustomUserDetails> userDetails = UserDetailsHelper.getUserDetails();
        if (userDetails.isPresent()) {
            model.addAttribute("username", userDetails.get().getUsername());
            model.addAttribute("password", userDetails.get().getPassword());

            // Thêm dòng này để lấy danh sách món ăn
            List<Food> dishes = guestService.getAvailableDishes();
            List<OrderMenuItem> cartItems = cartService.getCart(userDetails.get().getAccountId());

            model.addAttribute("dishes", dishes);
            model.addAttribute("cartItems", cartItems);
            return "home";
        } else {
            return "redirect:/login";
        }
    }
    @GetMapping("/dishes")
    public String getAvailableDishes(Model model) {
        List<Food> dishes = guestService.getAvailableDishes();
        model.addAttribute("dishes", dishes);
        return "dish/list";
    }

    /**
     * Tìm kiếm các món ăn dựa trên từ khóa, sắp xếp, danh mục và số lượng kết quả tối đa.
     *
     * @param keyword    từ khóa tìm kiếm (tùy chọn).
     * @param model      đối tượng Model để truyền dữ liệu tới view.
     * @return tên view hiển thị kết quả tìm kiếm.
     */
    // Xử lý tìm kiếm món ăn
    @GetMapping("/search-results")
    public String searchDishes(@RequestParam(required = false) String keyword, Model model) {
        // Tạo đối tượng SearchFoodRequest từ từ khóa tìm kiếm
        SearchFoodRequest request = new SearchFoodRequest(keyword, null);  // Không sử dụng sortBy
        List<Food> results = guestService.searchDishes(request);

        // Thêm kết quả tìm kiếm vào mô hình để hiển thị
        model.addAttribute("searchResults", results);
        model.addAttribute("searchKeyword", keyword);

        // Trả về trang kết quả tìm kiếm
        return "search-results";
    }

    // Trang tìm kiếm để nhập từ khóa
    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }

    /**
     * Lấy thông tin chi tiết của một món ăn cụ thể.
     *
     * @param foodId ID của món ăn.
     * @param model  đối tượng Model để truyền dữ liệu tới view.
     * @return tên view hiển thị chi tiết món ăn.
     */
    @GetMapping("/dish/{foodId}")
    public String getFoodDetails(@PathVariable int foodId, Model model) {
        Food response = guestService.getFoodDetails(foodId);
        if (response != null) {
            model.addAttribute("food", response);
            Optional<CustomUserDetails> userDetails = UserDetailsHelper.getUserDetails();
            userDetails.ifPresent(details -> model.addAttribute("userDetails", details));
            return "dish/details";
        } else {
            model.addAttribute("foodNotFound", true);
            return "dish/details";
        }
    }
}
