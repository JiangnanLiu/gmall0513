package com.atguigu.gmall.manage.service.impl;

import ch.qos.logback.core.util.TimeUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.manage.constant.ManageConst;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.StringUtils.*;

/**
 * Created by 刘江楠 on 2019/10/26
 */
@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    //注入jedisPool
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if(!isEmpty(baseAttrInfo.getId())){
            //根据平台属性id更新平台属性
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);

            //根据平台属性id删除平台属性值列表
            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.delete(baseAttrValue);
        }else {
            //新增平台属性
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }

        List<BaseAttrValue> baseAttrValueList = baseAttrInfo.getAttrValueList();
        if(baseAttrValueList != null && baseAttrValueList.size() > 0){
            for (BaseAttrValue baseAttrValue: baseAttrValueList) {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(baseAttrValue);
            }
        }
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
//        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
//        baseAttrInfo.setCatalog3Id(catalog3Id);
//        return baseAttrInfoMapper.select(baseAttrInfo);
        return baseAttrInfoMapper.selectBaseAttrInfoListByCatalog3Id(catalog3Id);
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        return baseAttrValueMapper.select(baseAttrValue);
    }

    @Override
    public BaseAttrInfo getBaseAttrInfo(String attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        baseAttrInfo.setAttrValueList(getAttrValueList(attrId));
        return baseAttrInfo;
    }

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {

        if (isEmpty(spuInfo.getId())) {
            //保存商品SPUInfo
            spuInfoMapper.insertSelective(spuInfo);
        } else {
            //根据SPUInfo id 更新商品SPUInfo
            spuInfoMapper.updateByPrimaryKeySelective(spuInfo);
        }

        //据SPUInfo id 删除商品SPUImage
        SpuImage image = new SpuImage();
        image.setSpuId(spuInfo.getId());
        spuImageMapper.delete(image);

        //保存商品SPUImage
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList != null && spuImageList.size() > 0){
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }

        //据SPUInfo id 删除商品SPUSaleAttr
        SpuSaleAttr saleAttr = new SpuSaleAttr();
        saleAttr.setSpuId(spuInfo.getId());
        spuSaleAttrMapper.delete(saleAttr);

        //据SPUInfo id 删除商品SPUSaleAttrValue
        SpuSaleAttrValue saleAttrValue = new SpuSaleAttrValue();
        saleAttrValue.setSpuId(spuInfo.getId());
        spuSaleAttrValueMapper.delete(saleAttrValue);

        //保存商品SPUSaleAttr
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);
                //保存商品SPUSaleAttrValue
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList != null && spuSaleAttrValueList.size() > 0){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    @Override
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {
        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insertSelective(skuInfo);

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size() > 0){
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(skuImage);
            }
        }

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList != null && skuAttrValueList.size() > 0){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(skuAttrValue);
            }
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValueList != null && skuSaleAttrValueList.size() > 0){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
            }
        }
    }

    @Override
    public SkuInfo getSkuInfoById(String skuId) {
        return getSkuInfoRedisson(skuId);
    }

    private SkuInfo getSkuInfoRedisson(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String skuInfoKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            if(!jedis.exists(skuInfoKey)){
                String skuInfoJson = jedis.get(skuInfoKey);
                if (skuInfoJson == null || skuInfoJson.length() == 0){
                    //缓存中没有数据
                    Config config = new Config();
                    config.useSingleServer().setAddress("redis://192.168.119.128:6379");

                    //创建redisson
                    RedissonClient redisson = Redisson.create(config);
                    RLock lock = redisson.getLock("my-lock");
                    // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
                    boolean res = false;
                    try {
                        res = lock.tryLock(100, 10, TimeUnit.SECONDS);
                        if (res) {
                            // 从数据库中获取数据，并放入到缓存！
                            skuInfo = getSkuInfoDB(skuId);
                            // 将是数据放入缓存 // 将对象转换成字符串
                            String skuRedisStr = JSON.toJSONString(skuInfo);
                            jedis.setex(skuInfoKey, ManageConst.SKUKEY_TIMEOUT, skuRedisStr);
                            return skuInfo;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }else {
                    // 缓存有数据
                    skuInfo = JSON.parseObject(skuInfoJson, SkuInfo.class);
                    return skuInfo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoRedis(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String skuInfoKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            if(!jedis.exists(skuInfoKey)){
                String skuInfoJson = jedis.get(skuInfoKey);
                if (skuInfoJson == null || skuInfoJson.length() == 0){
                    //缓存中没有数据
                    String skuKeyLock = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKULOCK_SUFFIX;

                    String token = UUID.randomUUID().toString().replace("-", "");
                    String result = jedis.set(skuKeyLock, token, "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                    if ("OK".equals(result)){
                        // 从数据库中获取数据，并放入到缓存！
                        skuInfo = getSkuInfoDB(skuId);
                        // 将是数据放入缓存 // 将对象转换成字符串
                        String skuRedisStr = JSON.toJSONString(skuInfo);
                        jedis.setex(skuInfoKey, ManageConst.SKUKEY_TIMEOUT, skuRedisStr);
                        // 删除key // jedis.del(skuLockKey);
                        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                        // 如果key 与value 相等，则删除！
                        jedis.eval(script, Collections.singletonList(skuKeyLock), Collections.singletonList(token));

                        return  skuInfo;
                    }else {
                        // 等待
                        try {
                            Thread.sleep(1000);
                            return getSkuInfoDB(skuId);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    // 缓存有数据
                    skuInfo = JSON.parseObject(skuInfoJson, SkuInfo.class);
                    return skuInfo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        skuInfo.setSkuImageList(getSkuImageBySkuId(skuId));

        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
        skuInfo.setSkuAttrValueList(skuAttrValueList);

        return skuInfo;
    }

    @Override
    public List<SkuImage> getSkuImageBySkuId(String skuId) {
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        return skuImageMapper.select(skuImage);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrBySkuId(SkuInfo skuInfo) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.getSkuSaleAttrValueListBySpu(spuId);
    }

    @Override
    public Map getSkuValueIdsMap(String spuId) {
        List<Map<Object, Object>> mapList = skuSaleAttrValueMapper.getSkuValueIdsMap(spuId);
        HashMap<Object, Object> map = new HashMap<>();
        for (Map<Object, Object> objectObjectMap : mapList) {
            map.put(objectObjectMap.get("value_ids"),objectObjectMap.get("sku_id"));
        }
        return map;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        String valueIds = org.apache.commons.lang3.StringUtils.join(attrValueIdList.toArray(), ",");
        return baseAttrInfoMapper.selectAttrInfoListByIds(valueIds);
    }
}
