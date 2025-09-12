package com.vic.textvalidator.DataInitialization;


import com.vic.textvalidator.Models.UserAccount;
import com.vic.textvalidator.Repository.UserAccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder {

    private final UserAccountRepository repository;

    public DataSeeder(UserAccountRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void seedData() {
        if (repository.count() == 0) {
            List<UserAccount> users = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                UserAccount user = new UserAccount();
                user.setUsername("user" + String.format("%04d", i));
                // user0001, user0002, ..., user1000
                users.add(user);
            }
            repository.saveAll(users);
            System.out.println("✅ Seeded 1000 usernames into the database.");
        } else {
            System.out.println("ℹ️ Database already has data, skipping seeding.");
        }
    }
}

