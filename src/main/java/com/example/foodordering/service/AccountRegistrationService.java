package com.example.foodordering.service;

import com.example.foodordering.dto.request.AccountRegistrationRequest;
import com.example.foodordering.dto.request.AccountUpdateRequest;
import com.example.foodordering.dto.response.AccountResponse;
import com.example.foodordering.entity.Account;
import com.example.foodordering.entity.Customer;
import com.example.foodordering.exception.AppException;
import com.example.foodordering.exception.ErrorCode;
import com.example.foodordering.mapper.AccountMapper;
import com.example.foodordering.mapper.AccountResponseMapper;
import com.example.foodordering.mapper.CustomerMapper;
import com.example.foodordering.repository.intf.AccountRepository;
import com.example.foodordering.repository.intf.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class AccountRegistrationService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    AccountResponseMapper accountResponseMapper;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Creates a new account.
     *
     * @param request the account creation request containing user details.
     * @return the created account as an {@link AccountResponse}.
     * @throws AppException if the username already exists.
     */
    public AccountResponse createAccount(AccountRegistrationRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Account account = accountMapper.toAccount(request);
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        Customer customer = new Customer();
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setFirstname(request.getFirstname());
        customer.setLastname(request.getLastname());

        customer.setAccount(account);
        account.setCustomer(customer);

        accountRepository.save(account);
        customerRepository.save(customer);

        return accountResponseMapper.toAccountResponse(request);
    }


}