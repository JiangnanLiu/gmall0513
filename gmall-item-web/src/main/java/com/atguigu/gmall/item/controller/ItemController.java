package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuAttrValue;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 刘江楠 on 2019/10/30
 */
@Controller
public class ItemController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, Model model){
        //获取商品详情
        SkuInfo skuInfo = manageService.getSkuInfoById(skuId);
        //查询销售属性-销售属性值
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrBySkuId(skuInfo);
        //销售属性-销售属性值与skuId的关系查询
        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());

        /*//把列表变换成 valueid1|valueid2|valueid3 ：skuId  的 哈希表 用于在页面中定位查询
        String valueIdsKey = "";

        Map<String, String> valuesSkuMap = new HashMap<>();

        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
            if (valueIdsKey.length() != 0) {
                valueIdsKey = valueIdsKey + "|";
            }
            valueIdsKey = valueIdsKey + skuSaleAttrValue.getSaleAttrValueId();
            if ((i + 1) == skuSaleAttrValueList.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i + 1).getSkuId())) {
                valuesSkuMap.put(valueIdsKey, skuSaleAttrValue.getSkuId());
                valueIdsKey = "";
            }
        }
        //把map变成json串
        String valuesSkuJson = JSON.toJSONString(valuesSkuMap);*/
        Map skuValueIdsMap = manageService.getSkuValueIdsMap(skuInfo.getSpuId());

        model.addAttribute("valuesSkuJson", JSON.toJSONString(skuValueIdsMap));


        model.addAttribute("spuSaleAttrList", spuSaleAttrList);
        model.addAttribute("skuInfo", skuInfo);

        listService.incrHotScore(skuId);

        return "item";
    }
}
