package com.example.foodordering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.foodordering.entity.OrderMenuItem;

import java.util.List;

/* Truy vấn OrderMenuItem dựa vào FoodOrder → Customer (Để không phải thay đổi cấu trúc của CSDL)
 * Spring Boot sẽ tự động chuyển đổi tên hàm thành SQL:
 * ===========================================================
 * SELECT * FROM order_menu_item omi
 * JOIN food_order fo ON omi.food_order_id = fo.food_order_id
 * WHERE fo.customer_id = ?;
 * ===========================================================
 * Tên Function khá quan trọng =)) Ý kiến cập nhật ping thằng @duy103zxc nhé.
 * */
@Repository
public interface OrderMenuItemRepository extends JpaRepository<OrderMenuItem, Integer> {
    /**
     * Retrieves all OrderMenuItems by a given FoodOrder ID.
     * <p>
     * Equivalent SQL:
     * {@code SELECT * FROM order_menu_item WHERE food_order_id = ?}
     * </p>
     *
     * @param foodOrderId the ID of the food order
     * @return list of order menu items associated with the given food order
     */
    List<OrderMenuItem> findByFoodOrder_Id(int foodOrderId);
    /**
     * Retrieves all OrderMenuItems by a given Customer ID via FoodOrder.
     * Equivalent SQL:
     * {@code SELECT omi.* FROM order_menu_item omi
     * JOIN food_order fo ON omi.food_order_id = fo.food_order_id
     * WHERE fo.customer_id = ?}
     * @param customerId the ID of the customer
     * @return list of order menu items associated with the given customer
     */
    List<OrderMenuItem> findByFoodOrder_Customer_Id(int customerId);

    /**
     * Deletes all OrderMenuItems related to a specific FoodOrder ID.
     * <p>
     * Equivalent SQL:
     * {@code DELETE FROM order_menu_item WHERE food_order_id = ?}
     * </p>
     *
     * @param foodOrderId the ID of the food order
     */
    void deleteByFoodOrder_Id(int foodOrderId);
}
