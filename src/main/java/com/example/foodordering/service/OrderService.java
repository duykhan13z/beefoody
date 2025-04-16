package com.example.foodordering.service;

import com.example.foodordering.dto.response.ApiResponse;
import com.example.foodordering.dto.response.MonthlySalesData;
import com.example.foodordering.entity.Customer;
import com.example.foodordering.entity.FoodOrder;
import com.example.foodordering.entity.OrderMenuItem;
import com.example.foodordering.repository.FoodOrderRepository;
import com.example.foodordering.repository.intf.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final FoodOrderRepository foodOrderRepository;

    public OrderService(FoodOrderRepository foodOrderRepository, CartService cartService, CustomerRepository customerRepository) {
        this.foodOrderRepository = foodOrderRepository;
    }
    /**
     * Check if an order exists by order ID.
     * @param orderId ID of the order.
     * @return true if the order exists, false otherwise.
     */
    public boolean orderExists(int orderId) {
        return foodOrderRepository.findById(orderId).isPresent();
    }

    /**
     * Creates a new order from the customer's cart.
     * @return ApiResponse containing the newly created FoodOrder.
     */
    public FoodOrder createOrder(int customerId) {
        // Lấy đơn hàng trong giỏ hàng (orderStatus = false) của khách hàng
        Optional<FoodOrder> cartOrder = foodOrderRepository.findByCustomer_IdAndOrderStatus(customerId, false).stream().findFirst();

        // Kiểm tra nếu tìm thấy giỏ hàng
        if (cartOrder.isPresent()) {
            // Cập nhật orderStatus thành true để đánh dấu đơn hàng đã được tạo
            FoodOrder order = cartOrder.get();
            order.setOrderStatus(true);
            // Get and set the current date
            order.setOrderDate(LocalDate.now());
            // Lưu lại đơn hàng với status đã cập nhật
            foodOrderRepository.save(order);
            // Trả về đơn hàng đã được cập nhật
            return order;
        }

        // Nếu không tìm thấy giỏ hàng, trả về lỗi
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cart found for customer.");
    }


    /**
     * Retrieves a list of orders placed by a specific customer.
     * @return ApiResponse containing a list of FoodOrder.
     */
    public ApiResponse<List<FoodOrder>> getOrderList(int customerId) {
        List<FoodOrder> orders = foodOrderRepository.findByCustomer_IdAndOrderStatus(customerId, true);
        return ApiResponse.build(1000, "Success", orders);
    }

    /**
     * Retrieves details of a specific order.
     *
     * @param orderId ID of the order.
     * @param customerId ID of the logged-in customer.
     * @return ApiResponse containing the FoodOrder details.
     */
    public ApiResponse<FoodOrder> getOrderDetails(int orderId, int customerId) {
        Optional<FoodOrder> order = foodOrderRepository.findById(orderId);
        if (order.isEmpty()) {
            return ApiResponse.build(1404, "Order not found", null);
        }
        if (!order.get().getCustomer().getId().equals(customerId)) {
            return ApiResponse.build(1403, "Access denied. Order does not belong to this customer.", null);
        }
        return ApiResponse.build(1000, "Success", order.get());
    }

    public List<MonthlySalesData> getMonthlySalesData() {
        // Fetch all placed orders
        List<FoodOrder> placedOrders = foodOrderRepository.findByOrderStatus(true);

        // Group orders by year and month
        Map<String, List<FoodOrder>> ordersByMonth = placedOrders.stream()
                .collect(Collectors.groupingBy(order -> {
                    LocalDate orderDate = order.getOrderDate();
                    return orderDate.getYear() + "-" + String.format("%02d", orderDate.getMonthValue());
                }));

        List<MonthlySalesData> monthlySalesDataList = new ArrayList<>();

        // Calculate total sales amount and revenue for each month
        for (Map.Entry<String, List<FoodOrder>> entry : ordersByMonth.entrySet()) {
            String yearMonth = entry.getKey();
            List<FoodOrder> ordersInMonth = entry.getValue();

            double totalRevenue = ordersInMonth.stream()
                    .map(FoodOrder::getPrice)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum();

            // Extract month name from yearMonth string
            String[] parts = yearMonth.split("-");
            int monthValue = Integer.parseInt(parts[1]);
            Month month = Month.of(monthValue);
            String monthName = month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault());

            MonthlySalesData monthlySalesData = new MonthlySalesData();
            monthlySalesData.setMonth(monthName);
            monthlySalesData.setRevenue(totalRevenue);

            monthlySalesDataList.add(monthlySalesData);
        }

        return monthlySalesDataList;
    }
}