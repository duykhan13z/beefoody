package com.example.foodordering.repository.intf;

import com.example.foodordering.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);

    Optional<Account> findByUsername(String username);


    Page<Account> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<Account> findByRole(String role, Pageable pageable);
    Account findByAccountId(Long accountId);
}
