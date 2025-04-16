package com.example.foodordering.service;


import com.example.foodordering.dto.response.CustomerSummary;
import com.example.foodordering.entity.Customer;
import com.example.foodordering.entity.FoodOrder;
import com.example.foodordering.repository.FoodOrderRepository;
import com.example.foodordering.repository.intf.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final FoodOrderRepository foodOrderRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(FoodOrderRepository foodOrderRepository, CustomerRepository customerRepository) {
        this.foodOrderRepository = foodOrderRepository;
        this.customerRepository = customerRepository;
    }

    // Method to get customer summary (total items and total spent)
    public CustomerSummary getCustomerSummary(Customer customer) {
        // Fetch completed orders for the customer
        List<FoodOrder> completedOrders = foodOrderRepository.findByCustomer_IdAndOrderStatus(customer.getId(), true);

        // Create CustomerSummaryDTO to calculate totals
        return new CustomerSummary(completedOrders);
    }
}
