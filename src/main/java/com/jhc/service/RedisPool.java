package com.jhc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;

/**
 * @author jhc on 2019/2/21
 */

@Service("redisPool")
@Slf4j
public class RedisPool {
    @Resource(name="shardedJedisPool")
    private ShardedJedisPool shardedJedisPool;

    public ShardedJedis instance(){
        return shardedJedisPool.getResource();
    }

    public void safeClose(ShardedJedis shardedJedis){
        try{
            if(shardedJedis != null){
                shardedJedis.close();
            }
        }catch (Exception e){
            log.error("close redis happens Exception {}",e);
        }
    }
}
