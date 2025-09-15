package com.vic.textvalidator.Service;

import com.vic.textvalidator.DataInitialization.DataSeeder;
import com.vic.textvalidator.Models.UserAccount;
import com.vic.textvalidator.Repository.UserAccountRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UsernameService {

    private final UserAccountRepository userAccountRepository;


    private final CacheManager cacheManager;

    private final DataSeeder dataSeeder;

    private static final List<String> WORD_SUFFIXES = List.of(
            "dev", "pro", "x", "official", "hub", "code"
    );

    private final Random random = new Random();

    public UsernameService(UserAccountRepository userAccountRepository, CacheManager cacheManager, DataSeeder dataSeeder) {
        this.userAccountRepository = userAccountRepository;
        this.cacheManager = cacheManager;
        this.dataSeeder = dataSeeder;
    }


    public boolean checkUsername(String username) {
        // Bloom filter quick check
        if (dataSeeder.mightContain(username)) {
            System.out.println("🚫 Bloom filter: " + username + " is probably taken.");

                Cache cache = cacheManager.getCache("usernameCheck");


                if (cache == null) return true; // if no cache, assume available

               if (cache.get(username) != null){

                   System.out.println("❌ " + username + " is taken.");  // not null → in cache → taken
                   return false;
               } else {
                   System.out.println("✅" + username + " is available."); // null → not in cache → available
                   return true;
               }
        }

        // Step 2: If bloom filter says "definitely not present", then it's available
        System.out.println("✅ Bloom filter: " + username + " definitely not taken.");
        return true;

    }


    public UserAccount registerUser(String username) {
        if (!checkUsername(username)) {
            throw new IllegalArgumentException("Username already taken!");
        }

        UserAccount user = new UserAccount();
        user.setUsername(username);
        userAccountRepository.save(user);

        dataSeeder.addToBloom(username);

        Cache cache = cacheManager.getCache("usernameCheck");
        if (cache != null) {
            cache.put(username, Boolean.TRUE); // mark username as taken
        }

        return user;
    }


    public List<String> SuggestUsernames(String baseUsername, int limit) {
        List<String> suggestions = new ArrayList<>();
        int year = LocalDate.now().getYear();

        Cache cache = cacheManager.getCache("usernameCheck");
        if (cache == null) {
            throw new IllegalStateException("Cache 'usernameCheck' is not available");
        }

        // Helper function to check cache quickly
        java.util.function.Predicate<String> isAvailable = candidate ->
                cache.get(candidate) == null; // null = available, not null = taken

        // Try word-based suffixes
        for (String word : WORD_SUFFIXES) {
            if (suggestions.size() >= limit) break;
            String candidate = baseUsername + "_" + word;
            if (isAvailable.test(candidate)) {
                suggestions.add(candidate);
            }
        }

        // Try year-based
        if (suggestions.size() < limit) {
            String candidate = baseUsername + year;
            if (isAvailable.test(candidate)) {
                suggestions.add(candidate);
            }
        }

        // Try random numbers until filled
        while (suggestions.size() < limit) {
            String candidate = baseUsername + (random.nextInt(9000) + 1000); // 4-digit number
            if (isAvailable.test(candidate) && !suggestions.contains(candidate)) {
                suggestions.add(candidate);
            }
        }

        return suggestions;
    }

}
