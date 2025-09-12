package com.vic.textvalidator.BloomFilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.vic.textvalidator.Repository.UserAccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


@Component
public class UsernameBloomFilter {

    private BloomFilter<String> bloomFilter;
    private final UserAccountRepository userAccountRepository;

    public UsernameBloomFilter(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @PostConstruct
    public void init() {
        long count = userAccountRepository.count();
                bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8),
                        count == 0 ? 1000 : count * 2, // capacity (scale factor for growth)
                        0.01); // 1% false positive rate

        userAccountRepository.findAll().forEach(user -> bloomFilter.put(user.getUsername()));

        System.out.println("⚡ Bloom filter initialized with " + count + " usernames");
    }


    public boolean mightContain(String username) {
        return bloomFilter.mightContain(username);
    }

    public void add(String username){
        bloomFilter.put(username);
    }
}
