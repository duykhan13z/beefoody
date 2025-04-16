package com.example.foodordering.service;

import com.example.foodordering.dto.request.AuthenticationRequest;
import com.example.foodordering.dto.response.AuthenticationResponse;
import com.example.foodordering.entity.Account;
import com.example.foodordering.repository.intf.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthenticationService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

//    public UsernamePasswordAuthenticationToken authenticate(AuthenticationRequest authenticationRequest) {
//        Account account = accountRepository.findByUsername(authenticationRequest.getUsername())
//                .orElseThrow(() -> new RuntimeException("Account Not Found"));
//
//        System.out.println("[PW] Authentication = " + authenticationRequest.getPassword() + "\n Account = " + account.getPassword());
//
//        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), account.getPassword());
//        if (!authenticated) throw new RuntimeException("Invalid Password");
//
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(account, null, new ArrayList<>());
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            authenticationToken.setAuthenticated(authenticated);
//        return authenticationToken;
//    }
}
