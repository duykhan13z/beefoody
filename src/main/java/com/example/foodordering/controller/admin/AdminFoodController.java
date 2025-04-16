package com.example.foodordering.controller.admin;

import com.example.foodordering.entity.Food;
import com.example.foodordering.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/food")
public class AdminFoodController {
    @Autowired
    private FoodRepository foodRepository;

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Food food = foodRepository.findById(id).orElse(null);
        if (food == null) {
            return "redirect:/admin/product-list"; // fallback if food not found
        }
        model.addAttribute("food", food);
        model.addAttribute("path", "/admin/product-list/edit");
        model.addAttribute("pagePath", "Master Data / Product / Edit");
        model.addAttribute("pageTitle", "Edit Food");
        model.addAttribute("activePage", "editFood");
        return "adminPage/index";
    }

    @PostMapping("/edit/{id}")
    public String updateFood(@PathVariable int id, @ModelAttribute Food food,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "5") int rowsPerPage) {
        food.setId(id); // ensure ID is set
        foodRepository.save(food); // update or insert
        return "redirect:/admin/product-list?page=" + page + "&rowsPerPage=" + rowsPerPage;
    }
    @GetMapping("/toggle/{id}")
    public String toggleFoodAvailability(@PathVariable int id,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "5") int rowsPerPage)  {
        Food food = foodRepository.findById(id).orElse(null);
        if (food != null) {
            food.setIsAvailable(!food.getIsAvailable()); // flip the boolean
            foodRepository.save(food); // update in DB
        }
        return "redirect:/admin/product-list?page=" + page + "&rowsPerPage=" + rowsPerPage;
    }

    @GetMapping("/delete/{id}")
    public String deleteFood(@PathVariable int id,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "5") int rowsPerPage) {
        foodRepository.deleteById(id);
        return "redirect:/admin/product-list?page=" + page + "&rowsPerPage=" + rowsPerPage;
    }

    @GetMapping("/add")
    public String showAddFoodForm(Model model) {
        model.addAttribute("food", new Food()); // Empty form object
        model.addAttribute("pageTitle", "Add New Food");
        model.addAttribute("pagePath", "Master Data / Product / Add");
        model.addAttribute("path", "/admin/product-list/add");
        model.addAttribute("activePage", "addNewFood");
        return "adminPage/index";
    }

    @PostMapping("/add")
    public String handleAddFoodForm(@ModelAttribute Food food) {
        foodRepository.save(food);
        return "redirect:/admin/product-list";
    }

}
