package com.vic.textvalidator.Repository;

import com.vic.textvalidator.Models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository  extends JpaRepository<UserAccount,Long> {
    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

}
