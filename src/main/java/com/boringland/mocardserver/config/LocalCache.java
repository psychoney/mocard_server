package com.boringland.mocardserver.config;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;

public class LocalCache {
    public static final long TIMEOUT;
    private static final long CLEAN_TIMEOUT;
    public static final TimedCache<String, Object> CACHE;

    public LocalCache() {
    }

    static {
        TIMEOUT = 10L * DateUnit.MINUTE.getMillis();
        CLEAN_TIMEOUT = 10L * DateUnit.MINUTE.getMillis();
        CACHE = CacheUtil.newTimedCache(TIMEOUT);
        CACHE.schedulePrune(CLEAN_TIMEOUT);
    }
}
