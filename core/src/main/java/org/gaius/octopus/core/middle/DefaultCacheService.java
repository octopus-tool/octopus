package org.gaius.octopus.core.middle;

import org.gaius.octopus.common.middle.CacheService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaobo
 * @program octopus
 * @description 默认缓存服务
 * @date 2024/6/7
 */
@Service
public class DefaultCacheService implements CacheService<String> {
    
    @Override
    public String ping() {
        return null;
    }
    
    @Override
    public Set<String> scan(String matchKey, Long limit, boolean... flag) {
        return null;
    }
    
    @Override
    public boolean expire(String key, long time, TimeUnit timeUnit, boolean... flag) {
        return false;
    }
    
    @Override
    public long getExpire(String key, boolean... flag) {
        return 0;
    }
    
    @Override
    public boolean hasKey(String key, boolean... flag) {
        return false;
    }
    
    @Override
    public long del(List<String> keys, boolean... flag) {
        return 0;
    }
    
    @Override
    public boolean del(String key, boolean... flag) {
        return false;
    }
    
    @Override
    public String get(String key, boolean... flag) {
        return null;
    }
    
    @Override
    public boolean setnx(String key, String value, long time, TimeUnit timeUnit, boolean... flag) {
        return false;
    }
    
    @Override
    public boolean setnx(String key, String value, Duration timeout, boolean... flag) {
        return false;
    }
    
    @Override
    public void set(String key, String value, long time, TimeUnit timeUnit, boolean... flag) {
    
    }
    
    @Override
    public void set(String key, String value, boolean... flag) {
    
    }
    
    @Override
    public void batchSet(Map<String, String> map, long seconds, boolean... flag) {
    
    }
    
    @Override
    public void batchSet(Map<String, String> map, boolean... flag) {
    
    }
    
    @Override
    public List<String> batchGet(List<String> list, boolean... flag) {
        return null;
    }
    
    @Override
    public Map<String, String> batchMapGet(List<String> list, boolean... flag) {
        return null;
    }
    
    @Override
    public void batchDelete(List<String> list, boolean... flag) {
    
    }
    
    @Override
    public Object hget(String key, String item, boolean... flag) {
        return null;
    }
    
    @Override
    public Map<String, Object> hmget(String key, boolean... flag) {
        return null;
    }
    
    @Override
    public Set<Object> hkeys(String key, boolean... flag) {
        return null;
    }
    
    @Override
    public void hmset(String key, Map<String, Object> map, long time, TimeUnit timeUnit, boolean... flag) {
    
    }
    
    @Override
    public void hmset(String key, Map<String, Object> map, boolean... flag) {
    
    }
    
    @Override
    public void hset(String key, String item, Object value, long time, TimeUnit timeUnit, boolean... flag) {
    
    }
    
    @Override
    public void hset(String key, String item, Object value, boolean... flag) {
    
    }
    
    @Override
    public Long hdel(String key, List<String> item, boolean... flag) {
        return null;
    }
    
    @Override
    public boolean hHasKey(String key, String item, boolean... flag) {
        return false;
    }
    
    @Override
    public List<Object> hmultiGet(String key, List<Object> keys, boolean... flag) {
        return null;
    }
    
    @Override
    public Set<String> sGet(String key, boolean... flag) {
        return null;
    }
    
    @Override
    public boolean sHasKey(String key, String value, boolean... flag) {
        return false;
    }
    
    @Override
    public Long sSetAndTime(String key, long time, TimeUnit timeUnit, String[] values, boolean... flag) {
        return null;
    }
    
    @Override
    public Long sSet(String key, String[] values, boolean... flag) {
        return null;
    }
    
    @Override
    public Long sGetSetSize(String key, boolean... flag) {
        return null;
    }
    
    @Override
    public Long setRemove(String key, String[] values, boolean... flag) {
        return null;
    }
    
    @Override
    public List<String> lGet(String key, long start, long end, boolean... flag) {
        return null;
    }
    
    @Override
    public long lGetListSize(String key, boolean... flag) {
        return 0;
    }
    
    @Override
    public String lGetIndex(String key, long index, boolean... flag) {
        return null;
    }
    
    @Override
    public void lSet(String key, String value, long time, TimeUnit timeUnit, boolean... flag) {
    
    }
    
    @Override
    public Long lSet(String key, String value, boolean... flag) {
        return null;
    }
    
    @Override
    public void lSet(String key, List<String> value, long time, TimeUnit timeUnit, boolean... flag) {
    
    }
    
    @Override
    public Long lSet(String key, List<String> value, boolean... flag) {
        return null;
    }
    
    @Override
    public void lUpdateIndex(String key, long index, String value, boolean... flag) {
    
    }
    
    @Override
    public Long lRemove(String key, long count, String value, boolean... flag) {
        return null;
    }
    
    @Override
    public Long lRightPushAll(String key, Collection<String> values, boolean... flag) {
        return null;
    }
    
    @Override
    public Long lLeftPushAll(String key, Collection<String> values, boolean... flag) {
        return null;
    }
    
    @Override
    public Long lRightPush(String key, String value, boolean... flag) {
        return null;
    }
    
    @Override
    public Long lLeftPush(String key, String value, boolean... flag) {
        return null;
    }
    
    @Override
    public String lLeftPop(String key, boolean... flag) {
        return null;
    }
    
    @Override
    public String lRightPop(String key, boolean... flag) {
        return null;
    }
    
    @Override
    public long incr(String key, long delta, boolean... flag) {
        return 0;
    }
    
    @Override
    public long decr(String key, long delta, boolean... flag) {
        return 0;
    }
    
    @Override
    public double hincr(String key, Object item, double by, boolean... flag) {
        return 0;
    }
    
    @Override
    public long incrRedisAtomic(String key, long liveTime, boolean... flag) {
        return 0;
    }
}
