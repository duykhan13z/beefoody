package com.example.foodordering.repository;

import com.example.foodordering.entity.FoodOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodOrderRepository extends JpaRepository<FoodOrder, Integer> {
    /**
     * Tìm danh sách đơn hàng của một khách hàng dựa trên trạng thái đơn hàng.
     * SELECT * FROM food_order fo
     * WHERE fo.customer_id = ? AND fo.order_status = ?;
     * @param customerId  ID của khách hàng.
     * @param statusValue Giá trị trạng thái của đơn hàng (true: đã đặt hàng, false: trong giỏ hàng).
     * @return Danh sách đơn hàng thỏa mãn điều kiện.
     */
    List<FoodOrder> findByCustomer_IdAndOrderStatus(int customerId, boolean statusValue);
    List<FoodOrder> findByOrderStatus(boolean statusValue);
    // Custom query to fetch FoodOrder and Customer together
    @Query("SELECT o FROM FoodOrder o JOIN FETCH o.customer WHERE o.id = :id")
    FoodOrder findByIdWithCustomer(@Param("id") int id);

    // If you want to fetch all food orders with customers (you may use pagination)
    @Query("SELECT o FROM FoodOrder o JOIN FETCH o.customer")
    List<FoodOrder> findAllWithCustomers();

    // Custom query to find orders by customer firstname or lastname, ignoring case
    @Query("SELECT o FROM FoodOrder o WHERE (o.customer.firstname LIKE %?1% OR o.customer.lastname LIKE %?1%)")
    Page<FoodOrder> findByCustomerNameContainingIgnoreCase(String searchTerm, Pageable pageable);
}
