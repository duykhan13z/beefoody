package com.example.foodordering.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.foodordering.entity.Food;
import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

    /**
     * Tìm các món ăn có tên chứa một chuỗi nhất định, case insensitive.
     * SELECT * FROM food f
     * WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%'));
     * @param name Chuỗi tìm kiếm.
     * @return Danh sách các món ăn phù hợp.
     */
    List<Food> findByNameContainingIgnoreCase(String name);

    /**
     * Tìm tất cả các món ăn có sẵn và sắp xếp theo giá tăng dần hoặc giảm dần.
     * Các lệnh SQL tương ứng với các hàm này:
     * Phần ASC - Tăng dần
     * SELECT * FROM food f
     * WHERE f.is_available = true
     * ORDER BY f.price ASC;
     * hoặc cho DESC - Giảm dần
     * SELECT * FROM food f
     * WHERE f.is_available = true
     * ORDER BY f.price DESC;
     * @return Danh sách các món ăn có sẵn, sắp xếp theo giá tăng dần hoặc giảm dần.
     */
    List<Food> findByIsAvailableTrueOrderByPriceAsc();
    List<Food> findByIsAvailableTrueOrderByPriceDesc();

    /**
     * Tìm tất cả các món ăn đang có sẵn.
     * SELECT * FROM food f
     * WHERE f.is_available = true;
     * @return Danh sách các món ăn có sẵn.
     */
    List<Food> findByIsAvailableTrue();


    Page<Food> findByIsAvailable(Boolean isAvailable, Pageable pageable);
    Page<Food> findByNameContainingIgnoreCaseAndIsAvailable(String name, Boolean isAvailable, Pageable pageable);
    Page<Food> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
