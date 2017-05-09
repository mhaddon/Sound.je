// todo phase in support of this, instead of xml file

///*
// *  NestedBird  Copyright (C) 2016-2017  Michael Haddon
// *
// *  This program is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU Affero General Public License version 3
// *  as published by the Free Software Foundation.
// *
// *  This program is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU Affero General Public License for more details.
// *
// *  You should have received a copy of the GNU Affero General Public License
// *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package com.nestedbird.config;
//
//import net.sf.ehcache.config.CacheConfiguration;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CachingConfigurer;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.ehcache.EhCacheCacheManager;
//import org.springframework.cache.interceptor.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @source http://stackoverflow.com/a/21944585/1507692
// */
//@Configuration
//@EnableCaching
//public class EhCacheConfig implements CachingConfigurer {
//    @Bean
//    @Override
//    public CacheManager cacheManager() {
//        return new EhCacheCacheManager(ehCacheManager());
//    }
//
//    @Bean(destroyMethod = "shutdown")
//    public net.sf.ehcache.CacheManager ehCacheManager() {
//        final net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
//        config.addCache(defaultCache());
//        config.addCache(standardQueryCache());
//        config.addCache(updateTimestampsCache());
//
//        return net.sf.ehcache.CacheManager.newInstance(config);
//    }
//
//    private CacheConfiguration defaultCache() {
//        final CacheConfiguration cacheConfiguration = new CacheConfiguration();
//        cacheConfiguration.setName("defaultCache");
//        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
//        cacheConfiguration.setMaxEntriesLocalHeap(10000);
//        cacheConfiguration.setDiskExpiryThreadIntervalSeconds(120);
//        cacheConfiguration.setMaxEntriesLocalDisk(10000000);
//        cacheConfiguration.setDiskSpoolBufferSizeMB(30);
//        cacheConfiguration.setTimeToLiveSeconds(120);
//        cacheConfiguration.setTimeToIdleSeconds(120);
//        cacheConfiguration.setEternal(false);
//
//        return cacheConfiguration;
//    }
//
//    private CacheConfiguration updateTimestampsCache() {
//        final CacheConfiguration cacheConfiguration = new CacheConfiguration();
//        cacheConfiguration.setName("org.hibernate.cache.spi.UpdateTimestampsCache");
//        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
//        cacheConfiguration.setMaxEntriesLocalHeap(1000);
//        cacheConfiguration.setDiskExpiryThreadIntervalSeconds(120);
//        cacheConfiguration.setMaxEntriesLocalDisk(5000);
//        cacheConfiguration.setDiskSpoolBufferSizeMB(30);
//        cacheConfiguration.setTimeToLiveSeconds(120);
//        cacheConfiguration.setTimeToIdleSeconds(120);
//        cacheConfiguration.setEternal(false);
//
//        return cacheConfiguration;
//    }
//
//    private CacheConfiguration standardQueryCache() {
//        final CacheConfiguration cacheConfiguration = new CacheConfiguration();
//        cacheConfiguration.setName("org.hibernate.cache.internal.StandardQueryCache");
//        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
//        cacheConfiguration.setMaxEntriesLocalHeap(15);
//        cacheConfiguration.setDiskExpiryThreadIntervalSeconds(120);
//        cacheConfiguration.setMaxEntriesLocalDisk(10000000);
//        cacheConfiguration.setDiskSpoolBufferSizeMB(30);
//        cacheConfiguration.setTimeToLiveSeconds(120);
//        cacheConfiguration.setTimeToIdleSeconds(120);
//        cacheConfiguration.setEternal(false);
//
//        return cacheConfiguration;
//    }
//
//    @Bean
//    @Override
//    public CacheResolver cacheResolver() {
//        return new SimpleCacheResolver();
//    }
//
//    @Bean
//    @Override
//    public KeyGenerator keyGenerator() {
//        return new SimpleKeyGenerator();
//    }
//
//    @Bean
//    @Override
//    public CacheErrorHandler errorHandler() {
//        return new SimpleCacheErrorHandler();
//    }
//}