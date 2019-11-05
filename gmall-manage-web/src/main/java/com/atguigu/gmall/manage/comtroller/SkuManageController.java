package com.atguigu.gmall.manage.comtroller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuImage;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.entity.SkuLsInfo;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 刘江楠 on 2019/10/29
 */
@RestController
@CrossOrigin
public class SkuManageController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;

    @RequestMapping("spuImageList")
    public List<SpuImage> spuImageList(SpuImage spuImage){
        return manageService.getSpuImageList(spuImage);
    }

    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> spuSaleAttrList(String spuId){
        return manageService.getSpuSaleAttrList(spuId);
    }

    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
    }


    @RequestMapping("onSale")
    public void saveEsSkuInfo(String skuId){
        //申明es存储对象
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        //从数据库查询商品数据
        SkuInfo skuInfo = manageService.getSkuInfoById(skuId);
        //拷贝对象
        BeanUtils.copyProperties(skuInfo, skuLsInfo);
        listService.saveESSkuInfo(skuLsInfo);
    }

}
