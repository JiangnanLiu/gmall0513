package com.atguigu.gmall.service;

import com.atguigu.gmall.entity.SkuLsInfo;
import com.atguigu.gmall.entity.SkuLsParams;
import com.atguigu.gmall.entity.SkuLsResult;

/**
 * Created by 刘江楠 on 2019/11/2
 */
public interface ListService {

    public void saveESSkuInfo(SkuLsInfo skuLsInfo);

    public SkuLsResult search(SkuLsParams skuLsParams);

    public void incrHotScore(String skuId);
}
