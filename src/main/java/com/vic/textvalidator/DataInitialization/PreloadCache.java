//package com.vic.textvalidator.DataInitialization;
//
//
//import com.vic.textvalidator.Repository.UserAccountRepository;
//import jakarta.annotation.PostConstruct;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class PreloadCache {
//
//    private final UserAccountRepository userAccountRepository;
//
//    private final CacheManager cacheManager;
//
//    public PreloadCache(UserAccountRepository userAccountRepository, CacheManager cacheManager) {
//        this.userAccountRepository = userAccountRepository;
//        this.cacheManager = cacheManager;
//    }
//
//    @PostConstruct
//    public void preloadCache() {
//        Cache cache = cacheManager.getCache("usernameCheck");
//        if (cache == null) {
//            System.err.println("⚠️ Cache 'usernameCheck' not configured!");
//            return;
//        }
//
//        List<String> allUsernames = userAccountRepository.findAllUsernames();
//        for (String username : allUsernames) {
//            cache.put(username, Boolean.TRUE); // dummy value, key is what matters
//        }
//
//        System.out.println("✅ Warm-up complete: " + allUsernames.size() + " usernames cached.");
//    }
//
//}
