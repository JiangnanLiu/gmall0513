package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;
import java.util.Map;

/**
 * Created by 刘江楠 on 2019/10/26
 */
public interface ManageService {

    /**
     * 获取所有一级分类
     * @return
     */
    public List<BaseCatalog1> getCatalog1();

    /**
     * 根据一级分类id获取二级分类数据
     * @param catalog1Id
     * @return
     */
    public List<BaseCatalog2> getCatalog2(String catalog1Id);

    /**
     * 根据二级分类id获取三级分类数据
     * @param catalog2Id
     * @return
     */
    public List<BaseCatalog3> getCatalog3(String catalog2Id);

    /**
     * 新增/保存平台属性和平台属性值
     * @param baseAttrInfo
     * @return
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据三级分类id获取平台属性集合
     * @param catalog3Id
     * @return
     */
    public List<BaseAttrInfo> getAttrList(String catalog3Id);

    /**
     * 根据平台属性id查询平台属性值集合
     * @param attrId
     * @return
     */
    public List<BaseAttrValue> getAttrValueList(String attrId);

    /**
     * 根据平台属性id查询平台属性数据
     * @param attrId
     * @return
     */
    public BaseAttrInfo getBaseAttrInfo(String attrId);

    /**
     * 查询所有的Spu商品信息
     * @param spuInfo
     * @return
     */
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);

    /**
     * 查询所有销售属性列表
     * @return
     */
    public List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 保存商品SPU
     * @param spuInfo
     */
    public void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 根据spuImage属性查询spuImage列表
     * @param spuImage
     * @return
     */
    List<SpuImage> getSpuImageList(SpuImage spuImage);

    /**
     * 根据SPUID查询销售属性集合
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    /**
     * 保存商品skuInfo
     * @param skuInfo
     * @return
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 根据 skuId 查询 SkuInfo 对象
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfoById(String skuId);

    /**
     * 根据 skuId 查询 skuImage
     * @param skuId
     * @return
     */
    List<SkuImage> getSkuImageBySkuId(String skuId);

    /**
     * 根据 skuId 查询销售属性-销售属性值集合
     * @param skuInfo
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrBySkuId(SkuInfo skuInfo);

    /**
     * 根据 spuId 查询销售属性-销售属性值与skuId之间的关系
     * @param spuId
     * @return
     */
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);

    /**
     * 根据 spuId 查询销售属性-销售属性值与skuId之间的关系
     * @param spuId
     * @return
     */
    Map getSkuValueIdsMap(String spuId);

    /**
     * 查询检索平台属性列表
     * @param attrValueIdList
     * @return
     */
    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);
}
