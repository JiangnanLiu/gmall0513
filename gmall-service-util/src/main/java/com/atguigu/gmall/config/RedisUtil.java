package com.atguigu.gmall.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by 刘江楠 on 2019/11/1
 */
public class RedisUtil {

    private JedisPool jedisPool;

    public void init(String host, int port, int timeOut){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //设置最大连接数
        jedisPoolConfig.setMaxTotal(200);
        //设置最小保留数
        jedisPoolConfig.setMinIdle(10);
        //设置自动监测是否可用
        jedisPoolConfig.setTestOnBorrow(true);
        //设置等待时间
        jedisPoolConfig.setMaxWaitMillis(10*1000);
        //排队等候
        jedisPoolConfig.setBlockWhenExhausted(true);
        jedisPool = new JedisPool(jedisPoolConfig, host, port, timeOut);
    }

    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
}
