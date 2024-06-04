package com.probashiincltd.probashilive.cache;

public class CacheHelper {
    static CacheManager cacheManager;
    public static void setCacheManager(CacheManager cacheManager){
        CacheHelper.cacheManager = cacheManager;
    }

    public static CacheManager getCacheManager(){
        return cacheManager;
    }
}
