package com.vic.textvalidator.DataInitialization;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.vic.textvalidator.Models.UserAccount;
import com.vic.textvalidator.Repository.UserAccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder {

    private final UserAccountRepository repository;
    private final CacheManager cacheManager;

    private BloomFilter<String> bloomFilter;

    public DataSeeder(UserAccountRepository repository, CacheManager cacheManager) {
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void init() {
        long count = repository.count();

        // 1. Seed if empty
        if (count == 0) {
            List<UserAccount> users = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                UserAccount user = new UserAccount();
                user.setUsername("user" + String.format("%04d", i));
                users.add(user);
            }
            repository.saveAll(users);
            count = 1000;
            System.out.println("✅ Seeded 1000 usernames into the database.");
        } else {
            System.out.println("ℹ️ Database already has data, skipping seeding.");
        }

        // 2. Init Bloom filter
        bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                count * 2, // capacity with growth factor
                0.01       // 1% false positive rate
        );

        // 3. Fetch usernames once
        List<String> usernames = repository.findAllUsernames();

        usernames.forEach(bloomFilter::put);
        System.out.println("⚡ Bloom filter initialized with " + usernames.size() + " usernames");

        // 4. Warm up cache
        Cache cache = cacheManager.getCache("usernameCheck");
        if (cache != null) {
            usernames.forEach(u -> cache.put(u, Boolean.TRUE));
            System.out.println("✅ Warm-up complete: " + usernames.size() + " usernames cached.");
        } else {
            System.err.println("⚠️ Cache 'usernameCheck' not configured!");
        }
    }

    // expose bloom filter for service use
    public boolean mightContain(String username) {
        return bloomFilter.mightContain(username);
    }

    public void addToBloom(String username) {
        bloomFilter.put(username);
    }
}
