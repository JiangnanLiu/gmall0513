package com.atguigu.gmall.manage.comtroller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 刘江楠 on 2019/10/28
 */
@RestController
@CrossOrigin
public class SupManageController {

    @Reference
    private ManageService manageService;

    @RequestMapping("spuList")
    public List<SpuInfo> getSpuList(SpuInfo spuInfo){
        return manageService.getSpuInfoList(spuInfo);
    }

    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttrList(){
        return manageService.getBaseSaleAttrList();
    }

    @RequestMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
    }

}
