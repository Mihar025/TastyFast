package com.misha.tastyfast.config;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncode());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncode() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuditorAware<Integer> auditorAware() {
        return new ApplicationAuditAware();
    }


    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCacheSpecification(
                "store:byId: maximumSize=500, expireAfterWrite=60m; " +
                        "store:allProducts: maximumSize=100, expireAfterWrite=30m; " +
                        "store:product:update: maximumSize=200, expireAfterWrite=15m; " +
                        "store:drink:byId: maximumSize=300, expireAfterWrite=20m; " +
                        "store:allDrinks: maximumSize=200, expireAfterWrite=30m; " +
                        "store:drinks:update: maximumSize=300, expireAfterWrite=30m; " +
                        "store:update: maximumSize=200, expireAfterWrite=15m; " +
                        "store:allStores: maximumSize=200, expireAfterWrite=15m; " +
                        "store:allStoresWithoutDelivery: maximumSize=200, expireAfterWrite=15m; " +

                        "restaurant:byId: maximumSize=300, expireAfterWrite=20m; " +
                         "restaurant:allRestaurants: maximumSize=500, expireAfterWrite=30m; " +
                        "restaurant:dish:byId: maximumSize=600, expireAfterWrite=30m; " +
                        "restaurant:allDish: maximumSize=600, expireAfterWrite=30m; " +
                        "restaurant:dish:update: maximumSize=300, expireAfterWrite=30m; " +

                        "restaurant:drink:byId: maximumSize=300, expireAfterWrite=20m; " +
                       "restaurant:allDrinks: maximumSize=200, expireAfterWrite=30m; " +
                        "restaurant:drinks:update: maximumSize=300, expireAfterWrite=30m; " +
                        "restaurant:allRestaurantsNoDelivery: maximumSize=300, expireAfterWrite=30m"
        );

        List<String> cacheNames = new ArrayList<>(Arrays.asList(
                "store:byId", "store:allProducts", "store:product:update",
                "store:drink:byId", "store:allDrinks", "store:drinks:update","store:allStores", "store:store:byId","store:update",
                "restaurant:byId", "restaurant:allRestaurants", "restaurant:dish:byId",
                "restaurant:allDish", "restaurant:dish:update",
                "restaurant:drink:byId", "restaurant:allDrinks", "restaurant:drinks:update", "restaurant:allRestaurantsNoDelivery"
        ));
        cacheManager.setCacheNames(cacheNames);

        return cacheManager;
    }

















}