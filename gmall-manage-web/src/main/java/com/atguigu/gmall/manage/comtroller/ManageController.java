package com.atguigu.gmall.manage.comtroller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 刘江楠 on 2019/10/26
 */
@RestController
@CrossOrigin
public class ManageController {

    @Reference
    private ManageService manageService;

    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1(){
        return manageService.getCatalog1();
    }

    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        return manageService.getCatalog2(catalog1Id);
    }

    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        return manageService.getCatalog3(catalog2Id);
    }

    @RequestMapping("attrInfoList")
    public List<BaseAttrInfo> attrInfoList(String catalog3Id){
        return manageService.getAttrList(catalog3Id);
    }

    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
    }

    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){
        BaseAttrInfo baseAttrInfo = manageService.getBaseAttrInfo(attrId);
        return baseAttrInfo.getAttrValueList();
    }
}
