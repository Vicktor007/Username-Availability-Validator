package com.vic.textvalidator.Service;


import com.vic.textvalidator.BloomFilter.UsernameBloomFilter;
import com.vic.textvalidator.Models.UserAccount;
import com.vic.textvalidator.Repository.UserAccountRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UsernameService {

    private final UserAccountRepository userAccountRepository;

    private final UsernameBloomFilter bloomFilter;

    private final CacheManager cacheManager;

    public UsernameService(UserAccountRepository userAccountRepository, UsernameBloomFilter bloomFilter, CacheManager cacheManager) {
        this.userAccountRepository = userAccountRepository;
        this.bloomFilter = bloomFilter;
        this.cacheManager = cacheManager;
    }


    public boolean checkUsername(String username) {
        // Step 1: Bloom filter quick check
        if (bloomFilter.mightContain(username)) {
            System.out.println("🚫 Bloom filter: " + username + " is probably taken.");

            // Step 2: Check Redis cache
            Cache cache = cacheManager.getCache("usernameCheck");
            Boolean cached = (cache != null) ? cache.get(username, Boolean.class) : null;

            if (cached != null) {
                System.out.println("⚡ Cache hit: " + username + " availability = " + cached);
                // If cache says taken -> trust it
                if (!cached) {
                    return false;
                }
                // If cache says available -> confirm with DB
            }

            // Step 3: Fallback to DB
            boolean available = !userAccountRepository.existsByUsername(username);
            Objects.requireNonNull(cacheManager.getCache("usernameCheck")).put(username, available);

            if (available) {
                System.out.println(username + " ✅ is available (after DB check).");
            } else {
                System.out.println(username + " ❌ is taken (after DB check).");
            }
            return available;
        }

        // Bloom says it's not seen before → likely available,
        // but double-check DB to avoid false negatives
        boolean available = !userAccountRepository.existsByUsername(username);
        Objects.requireNonNull(cacheManager.getCache("usernameCheck")).put(username, available);

        if (available) {
            System.out.println(username + " ✅ is available (Bloom filter miss + DB check).");
        } else {
            System.out.println(username + " ❌ is taken (Bloom filter miss + DB check).");
        }

        return available;
    }


    // Register user → add to DB, Bloom, and Redis cache
    public void registerUser(String username) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        userAccountRepository.save(user);

        bloomFilter.add(username);
        Objects.requireNonNull(cacheManager.getCache("usernameCheck")).put(username, false); // now taken
    }
}
