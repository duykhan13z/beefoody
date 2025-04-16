package com.example.foodordering.controller;

import com.example.foodordering.config.CustomUserDetails;
import com.example.foodordering.dto.request.AccountUpdateRequest;
import com.example.foodordering.dto.response.AccountResponse;
import com.example.foodordering.service.AccountManagementService;
import com.example.foodordering.utils.UserDetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountManagementController {

    @Autowired
    private AccountManagementService accountManagementService;

    // Hiển thị chi tiết tài khoản của người dùng hiện tại
    @GetMapping
    public String getAccount(Model model) {
        Optional<CustomUserDetails> userDetails = UserDetailsHelper.getUserDetails();
        if (userDetails.isPresent()) {
            Long accountId = (long) userDetails.get().getAccountId();
            AccountResponse account = accountManagementService.getAccount(accountId);

            model.addAttribute("account", account);
            model.addAttribute("updateRequest", new AccountUpdateRequest());
            return "account/detail";
        } else {
            return "redirect:/login";
        }
    }

    // Cập nhật thông tin tài khoản của người dùng hiện tại
    @PostMapping
    public String updateAccount(@ModelAttribute("updateRequest") AccountUpdateRequest request) {
        Optional<CustomUserDetails> userDetails = UserDetailsHelper.getUserDetails();
        if (userDetails.isPresent()) {
            Long accountId = (long) userDetails.get().getAccountId();
            AccountResponse updatedAccount = accountManagementService.updateAccount(accountId, request);
            return "redirect:/account/success";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/success")
    public String updateSuccessPage() {
        return "account/update";
    }
}
