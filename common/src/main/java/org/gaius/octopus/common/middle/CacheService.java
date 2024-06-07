package org.gaius.octopus.common.middle;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务
 *
 * @author gaius.zhao
 * @date 2024/6/7
 */
public interface CacheService<T> {

    String ping();

    /**
     * 模糊搜索获取rediskey
     *
     * @param matchKey
     * @param limit
     * @return
     */
    Set<String> scan(String matchKey, Long limit, boolean... flag);

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    boolean expire(String key, long time, TimeUnit timeUnit, boolean... flag);

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    long getExpire(String key, boolean... flag);

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    boolean hasKey(String key, boolean... flag);

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    long del(List<String> keys, boolean... flag);

    /**
     * 删除缓存
     *
     * @param key 值
     */
    boolean del(String key, boolean... flag);

    // ===============================String=================================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    T get(String key, boolean... flag);

    /**
     * 如果键不存在则新增,存在则不改变已经有的值
     *
     * @param key      键
     * @param value    值
     * @param time     过期时间
     * @param timeUnit 指定时间类型
     * @return 值
     */
    boolean setnx(String key, T value, long time, TimeUnit timeUnit, boolean... flag);

    /**
     * 如果键不存在则新增,存在则不改变已经有的值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    boolean setnx(String key, T value, Duration timeout, boolean... flag);

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    void set(String key, T value, long time, TimeUnit timeUnit, boolean... flag);

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false 失败
     */
    void set(String key, T value, boolean... flag);

    //    void set(String key, T value, long seconds, int limitBytes);

    /**
     * 批量缓存放入并设置时间
     *
     * @param map
     * @param seconds 过期时间
     * @return true成功 false 失败
     */
    void batchSet(Map<String, T> map, long seconds, boolean... flag);

    /**
     * 批量缓存放入并设置时间
     *
     * @param map
     * @return true成功 false 失败
     */
    void batchSet(Map<String, T> map, boolean... flag);

    /**
     * 批量获取值
     *
     * @param list keys建
     * @return
     */
    List<T> batchGet(List<String> list, boolean... flag);

    Map<String, T> batchMapGet(List<String> list, boolean... flag);

    /**
     * 批量删除
     *
     * @return
     */
    @Deprecated
    void batchDelete(List<String> list, boolean... flag);

    // ===============================hash=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    Object hget(String key, String item, boolean... flag);

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    Map<Object, Object> hmget(String key, boolean... flag);

    Set<Object> hkeys(String key, boolean... flag);

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    void hmset(String key, Map<String, Object> map, long time, TimeUnit timeUnit, boolean... flag);

    void hmset(String key, Map<String, Object> map, boolean... flag);

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    void hset(String key, String item, Object value, long time, TimeUnit timeUnit, boolean... flag);

    void hset(String key, String item, Object value, boolean... flag);

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    Long hdel(String key, List<String> item, boolean... flag);

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    boolean hHasKey(String key, String item, boolean... flag);

    List<Object> hmultiGet(String key, List<Object> keys, boolean... flag);
    // ===============================set=================================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    Set<T> sGet(String key, boolean... flag);

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    boolean sHasKey(String key, T value, boolean... flag);

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    Long sSetAndTime(String key, long time, TimeUnit timeUnit, T[] values, boolean... flag);

    Long sSet(String key, T[] values, boolean... flag);

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    Long sGetSetSize(String key, boolean... flag);

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    Long setRemove(String key, T[] values, boolean... flag);

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    List<T> lGet(String key, long start, long end, boolean... flag);

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    long lGetListSize(String key, boolean... flag);

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    T lGetIndex(String key, long index, boolean... flag);

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    void lSet(String key, T value, long time, TimeUnit timeUnit, boolean... flag);

    Long lSet(String key, T value, boolean... flag);

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    void lSet(String key, List<T> value, long time, TimeUnit timeUnit, boolean... flag);

    Long lSet(String key, List<T> value, boolean... flag);

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    void lUpdateIndex(String key, long index, T value, boolean... flag);

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    Long lRemove(String key, long count, T value, boolean... flag);

    /**
     * list右边添加数据
     *
     * @param key    键
     * @param values values 值
     */
    Long lRightPushAll(String key, Collection<T> values, boolean... flag);

    Long lLeftPushAll(String key, Collection<T> values, boolean... flag);

    /**
     * list右边添加数据
     *
     * @param key 键
     */
    Long lRightPush(String key, T value, boolean... flag);

    /**
     * list左添加数据
     *
     * @param key 键
     */
    Long lLeftPush(String key, T value, boolean... flag);

    /**
     * list移除最左边的一个元素
     *
     * @param key
     * @return
     */
    T lLeftPop(String key, boolean... flag);

    /**
     * list移除最右边的一个元素
     *
     * @param key
     * @return
     */
    T lRightPop(String key, boolean... flag);

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    long incr(String key, long delta, boolean... flag);

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    long decr(String key, long delta, boolean... flag);

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    double hincr(String key, Object item, double by, boolean... flag);

    long incrRedisAtomic(String key, long liveTime, boolean... flag);
}
