package com.example.foodordering.controller.admin;

import com.example.foodordering.config.CustomUserDetails;
import com.example.foodordering.dto.response.CustomerSummary;
import com.example.foodordering.dto.response.MonthlySalesData;
import com.example.foodordering.entity.*;
import com.example.foodordering.repository.OrderMenuItemRepository;
import com.example.foodordering.repository.FoodOrderRepository;
import com.example.foodordering.repository.FoodRepository;
import com.example.foodordering.repository.intf.AccountRepository;
import com.example.foodordering.repository.intf.CustomerRepository;
import com.example.foodordering.service.CustomerService;
import com.example.foodordering.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private FoodOrderRepository foodOrderRepository;
    @Autowired
    private OrderMenuItemRepository orderMenuItemRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        // Fetch real data from the database
        List<Customer> customers = customerRepository.findAll();
        List<FoodOrder> orders = foodOrderRepository.findAll();
        List<Food> foods = foodRepository.findAll();
        String adminName = "Admin";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            adminName = userDetails.getUsername();
        }
        // Create a map to store food IDs and their corresponding order count
        Map<Food, Long> foodOrderCountMap = new HashMap<>();
        
        // Count how many times each food item has been ordered
        for (FoodOrder order : orders) {
            // Filter out processing orders and only consider completed orders
            if (order.isOrderStatus()) {
                for (OrderMenuItem orderMenuItem : order.getOrderMenuItems()) {
                    Food food = orderMenuItem.getFood(); // Assuming there's a `Food` object in OrderMenuItem
                    foodOrderCountMap.put(food, foodOrderCountMap.getOrDefault(food, 0L) + 1);
                }
            }
        }

        // Sort the foods by order count in descending order and take top 5
        List<Food> topSellingFoods = foodOrderCountMap.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue())) // Sort by count
                .map(Map.Entry::getKey) // Get the Food object
                .limit(5) // Get top 5
                .collect(Collectors.toList());

        // Simulate order status counts
        long newOrders = orders.stream().filter(order -> !order.isOrderStatus()).count(); // false = giỏ hàng
        long orderProcessed = orders.stream().filter(order -> order.isOrderStatus()).count(); // true = đã đặt hàng
        long allCustomers = customers.size();

        // Prepare sales data for the chart
        List<MonthlySalesData> salesData = orderService.getMonthlySalesData();
        // Calculate the total revenue (sum of all revenues from the monthly sales data)
        double totalRevenue = salesData.stream()
                .mapToDouble(MonthlySalesData::getRevenue)  // Sum of all revenues
                .sum();

        // Add all necessary attributes to the model

        model.addAttribute("username", adminName);
        model.addAttribute("revenue", totalRevenue);
        model.addAttribute("salesData", salesData);
        model.addAttribute("newOrders", newOrders);
        model.addAttribute("orderProcessed", orderProcessed);
        model.addAttribute("allCustomers", allCustomers);
        model.addAttribute("topSellingFoods", topSellingFoods);
        model.addAttribute("orders", orders);
        model.addAttribute("customers", customers);
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("pagePath", "Main Menu / Dashboard");
        model.addAttribute("activePage", "dashboard");

        return "adminPage/index"; // This maps to the templates/admin/dashboard.html
    }

    @GetMapping("/product-list")
    public String productsListPage(Model model,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "5") int rowsPerPage,

                                   @RequestParam(required = false) Boolean available,
                                   @RequestParam(required = false) String search) {
        // Pageable uses 0-based index
        Pageable pageable = PageRequest.of(page - 1, rowsPerPage);
        Page<Food> foodPage;

        if (search != null && !search.isEmpty() && available != null) {
            // Search for both name and availability
            foodPage = foodRepository.findByNameContainingIgnoreCaseAndIsAvailable(search, available, pageable);
        } else if (search != null && !search.isEmpty()) {
            // Search only by name
            foodPage = foodRepository.findByNameContainingIgnoreCase(search, pageable);
        } else if (available != null) {
            // Filter only by availability
            foodPage = foodRepository.findByIsAvailable(available, pageable);
        } else {
            // No filters, return all products
            foodPage = foodRepository.findAll(pageable);
        }

        List<Food> pagedFoods = foodPage.getContent();
        List<String> filters = List.of("Availability");

        int totalPages = foodPage.getTotalPages();
        model.addAttribute("products", pagedFoods);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("rowsPerPage", rowsPerPage);
        model.addAttribute("filters", filters);
        model.addAttribute("search", search);
        model.addAttribute("available", available);
        model.addAttribute("path", "/admin/product-list");
        model.addAttribute("pageTitle", "Product List");
        model.addAttribute("pagePath", "Master Data / Product");
        model.addAttribute("activePage", "productList");
        return "adminPage/index"; // maps to templates/admin/products.html
    }

    @GetMapping("/account")
    public String accountsPage(Model model,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int rowsPerPage,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) String role) {
        // Pageable uses 0-based index
        Pageable pageable = PageRequest.of(page - 1, rowsPerPage);
        Page<Account> accountPage;

        // Use repository with custom filtering
        if (search != null && !search.isBlank()) {
            // Search accounts by username (case-insensitive)
            accountPage = accountRepository.findByUsernameContainingIgnoreCase(search, pageable);
        } else {
            // Get all accounts if no filters are applied
            accountPage = accountRepository.findAll(pageable);
        }
        // Fetch accounts filtered by role if provided
        if (role != null && !role.isEmpty()) {
            accountPage = accountRepository.findByRole(role, pageable);  // Assuming you have a method in your repository to fetch accounts by role
        } else {
            accountPage = accountRepository.findAll(pageable);  // If no role is selected, return all accounts
        }
        // Get the content of the current page
        List<Account> accounts = accountPage.getContent();

        // Calculate total pages for pagination
        int totalPages = accountPage.getTotalPages();

        model.addAttribute("role", role);
        model.addAttribute("accounts", accounts);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("rowsPerPage", rowsPerPage);
        model.addAttribute("search", search);
        model.addAttribute("pageTitle", "Users");
        model.addAttribute("path", "/admin/account");
        model.addAttribute("activePage", "userAccount");
        model.addAttribute("pagePath", "Master Data / Users");

        return "adminPage/index";
    }

    @GetMapping("/account/{accountId}")
    public String userDetailPage(@PathVariable Long accountId, Model model) {
        // Find the Account by the accountId
        Account account = accountRepository.findByAccountId(accountId);

        if (account == null) {
            model.addAttribute("error", "User not found.");
            return "error";  // Return to an error page or display a message if not found
        }

        // Add the Account details to the model
        model.addAttribute("accounts", account);
        model.addAttribute("pageTitle", "User Detail");
        model.addAttribute("activePage", "userDetail");
        model.addAttribute("pagePath", "Master Data / User / Detail");

        return "adminPage/index";  // Return to the user detail page
    }

    @GetMapping("/customers")
    public String customerPage(Model model,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int rowsPerPage,
                               @RequestParam(defaultValue = "ASC") String sortOrder,
                               @RequestParam(required = false) String searchTerm) {
        Pageable pageable = PageRequest.of(page - 1, rowsPerPage);
        Page<Customer> customersPage;

        // Apply sorting based on 'sortOrder'
        // if (sortOrder.equalsIgnoreCase("ASC")) {
        //     customersPage = customerRepository.findCustomersSortedByTotalSpentAsc(pageable);
        // } else {
        //     customersPage = customerRepository.findCustomersSortedByTotalSpentDesc(pageable);
        // }
        customersPage = customerRepository.findAllCustomers(pageable);

        // If a search term is provided, filter customers based on firstname or lastname
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            customersPage = customerRepository.findByFirstnameContainingOrLastnameContaining(searchTerm, pageable);
        }
        // Fetch the summary (totalItems and totalSpent) for each customer
        List<CustomerSummary> customerSummaries = customersPage.getContent().stream()
                .map(customer -> new CustomerSummary(customer.getFoodOrders()))  // Create DTO to calculate totals
                .collect(Collectors.toList());

        model.addAttribute("customerSummaries", customerSummaries);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("customers", customersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customersPage.getTotalPages());
        model.addAttribute("rowsPerPage", rowsPerPage);
        model.addAttribute("pageTitle", "Customers");
        model.addAttribute("pagePath", "Main Menu / Customers");
        model.addAttribute("path", "/admin/customers");
        model.addAttribute("activePage", "customers");

        return "adminPage/index"; // This will map to admin/customers.html
    }

    @GetMapping("/orders")
    public String showOrderPage(Model model,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int rowsPerPage,
                                @RequestParam(required = false) String searchTerm) {
        // Pagination logic using Spring Data pagination
        Pageable pageable = PageRequest.of(page - 1, rowsPerPage);

        Page<FoodOrder> ordersPage;

        // Apply search if a search term is provided
        if (searchTerm != null && !searchTerm.isEmpty()) {
            ordersPage = foodOrderRepository.findByCustomerNameContainingIgnoreCase(searchTerm, pageable);
        } else {
            ordersPage = foodOrderRepository.findAll(pageable);  // If no search term, retrieve all orders
        }

        List<FoodOrder> orders = ordersPage.getContent();  // Get paged orders
        int totalPages = ordersPage.getTotalPages();  // Total pages
        long totalOrders = ordersPage.getTotalElements();  // Total orders

        // Calculate total items in each order and add it to the order
        orders.forEach(order -> {
            int totalItems = order.getOrderMenuItems().stream()
                    .mapToInt(item -> item.getQuantityOrdered())  // Sum quantities
                    .sum();
            order.setTotalItems(totalItems);  // Store the total items count in the order
        });

        // Pass attributes to the model
        model.addAttribute("orders", orders);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("rowsPerPage", rowsPerPage);
        model.addAttribute("path", "/admin/orders");
        model.addAttribute("pageTitle", "Orders");
        model.addAttribute("pagePath", "Main Menu / Orders");
        model.addAttribute("activePage", "orders");
        return "adminPage/index"; // (optional)
    }
    @GetMapping("/orders/{id}")
    public String showOrderDetails(@PathVariable("id") int id, Model model, HttpServletRequest request, HttpSession session) {
        FoodOrder order = foodOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        session.setAttribute("previousPage", request.getHeader("Referer"));
        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Order Details");
        model.addAttribute("pagePath", "Main Menu / Order / Details");
        model.addAttribute("activePage", "orderDetail");
        return "adminPage/index";  // Template for the detailed order view
    }
    @GetMapping("/check-order/{orderId}")
    public ResponseEntity<String> checkOrderExists(@PathVariable int orderId) {
        // Check if the order exists using the OrderService
        boolean orderExists = orderService.orderExists(orderId);

        if (orderExists) {
            // If the order exists, return "found"
            return ResponseEntity.ok("found");
        } else {
            // If order doesn't exist, return "notFound"
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("notFound");
        }
    }

    @GetMapping("/announcement")
    public String showAnnouncementForm(Model model) {
        // You may want to add other attributes, like a list of customers if needed
        model.addAttribute("pageTitle", "Create Announcement");
        model.addAttribute("pagePath", "Main Menu / Announcement");
        model.addAttribute("activePage", "announcement");


        return "adminPage/index";  // Return the view for the email form
    }


    // Add more mappings as needed
}
