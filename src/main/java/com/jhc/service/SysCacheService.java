package com.jhc.service;

import com.google.common.base.Joiner;
import com.jhc.beans.CacheKeyConstants;
import com.jhc.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

/**
 * @author jhc on 2019/2/21
 */
@Service
@Slf4j
public class SysCacheService {

    @Resource(name="redisPool")
    public RedisPool redisPool;

    public void saveCache(String saveValue, int timeoutSeconds, CacheKeyConstants prefix){
        saveCache(saveValue,timeoutSeconds,prefix,null);
    }
    public void saveCache(String saveValue,int timeoutSeconds,CacheKeyConstants prefix,String... keys){
        if(saveValue == null){
            return ;
        }
        ShardedJedis shardedJedis = null;
        try{
            String cacheKey = generateCacheKey(prefix,keys);
            shardedJedis = redisPool.instance();
            shardedJedis.setex(cacheKey,600,saveValue);
        }catch (Exception e){
            log.error("prefix:{} ,keys:{},exception:{} ",prefix.name(), JsonMapper.obj2String(keys),e);
        }finally {
            redisPool.safeClose(shardedJedis);
        }
    }
    public String getFromCache(CacheKeyConstants prefix,String... keys){
        ShardedJedis shardedJedis = null;
        String cacheKey = generateCacheKey(prefix,keys);
        try{
            shardedJedis = redisPool.instance();
            String value = shardedJedis.get(cacheKey);
            return value;
        }catch (Exception e){
            log.error("prefix:{} ,keys:{},exception:{} ",prefix.name(), JsonMapper.obj2String(keys),e);
            return null;
        }finally {
            redisPool.safeClose(shardedJedis);
        }
    }

    private String generateCacheKey(CacheKeyConstants prefix,String... keys){
        String cacheKey = prefix.name();
        if(keys != null && keys.length>0){
            cacheKey += "_"+ Joiner.on("_").join(keys);
        }
        return cacheKey;
    }
}
