package com.vic.textvalidator.Repository;

import com.vic.textvalidator.Models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository  extends JpaRepository<UserAccount,Long> {
    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u.username FROM UserAccount u")
    List<String> findAllUsernames();

}
