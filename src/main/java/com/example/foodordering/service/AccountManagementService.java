package com.example.foodordering.service;

import com.example.foodordering.dto.request.AccountUpdateRequest;
import com.example.foodordering.dto.response.AccountResponse;
import com.example.foodordering.entity.Account;
import com.example.foodordering.exception.AppException;
import com.example.foodordering.exception.ErrorCode;
import com.example.foodordering.mapper.AccountMapper;
import com.example.foodordering.repository.intf.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountManagementService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private AccountMapper accountMapper;

    /**
     * Updates an existing account.
     *
     * @param accountId the ID of the account to update.
     * @param request   the account update request containing updated details.
     * @return the updated account as an {@link AccountResponse}.
     * @throws AppException if the account does not exist.
     */
    public AccountResponse updateAccount(Long accountId, AccountUpdateRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));

        // Cập nhật thông tin cá nhân
        account.getCustomer().setFirstname(request.getFirstname());
        account.getCustomer().setLastname(request.getLastname());
        account.setEmail(request.getEmail());
        account.getCustomer().setPhoneNumber(request.getPhoneNumber());

        // Nếu có mật khẩu mới thì encode và cập nhật
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            account.setPassword(encodedPassword);
        }

        accountRepository.save(account);

        return accountMapper.toAccountResponse(account);
    }


    /**
     * Retrieves an account by its ID.
     *
     * @param accountId the ID of the account to retrieve.
     * @return the retrieved account as an {@link AccountResponse}.
     * @throws RuntimeException if the account is not found.
     */
    public AccountResponse getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setFirstname(account.getCustomer().getFirstname());
        accountResponse.setLastname(account.getCustomer().getLastname());
        accountResponse.setPhoneNumber(account.getCustomer().getPhoneNumber());
        accountResponse.setEmail(account.getEmail());

        return accountResponse;
    }

    /**
     * Deletes an account by its ID.
     *
     * @param accountId the ID of the account to delete.
     */
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    /**
     * Retrieves all accounts.
     *
     * @return a list of all accounts as {@link AccountResponse}.
     */
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        List<AccountResponse> accountResponses = new ArrayList<>();
        accounts.forEach(account -> accountResponses.add(accountMapper.toAccountResponse(account)));
        return accountResponses;
    }
}
