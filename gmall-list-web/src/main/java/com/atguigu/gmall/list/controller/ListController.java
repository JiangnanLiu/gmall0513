package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseAttrValue;
import com.atguigu.gmall.entity.SkuLsInfo;
import com.atguigu.gmall.entity.SkuLsParams;
import com.atguigu.gmall.entity.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 刘江楠 on 2019/11/2
 */
@Controller
public class ListController {

    @Reference
    private ListService listService;

    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")
    public String search(SkuLsParams skuLsParams, Model model){
        skuLsParams.setPageSize(2);
        //通过服务查询检索列表结果
        SkuLsResult skuLsResult = listService.search(skuLsParams);
        //取得SkuLsInfo
        List<SkuLsInfo> skuLsInfoList = skuLsResult.getSkuLsInfoList();

        //取得attrValueIdList
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrList(attrValueIdList);

        // 记录查询的参数条件
        String urlParam = makeUrlParam(skuLsParams);
        System.out.println("urlParam = " + urlParam);
        // 声明一个集合来存储面包屑
        ArrayList<BaseAttrValue> baseAttrValueArrayList = new ArrayList<>();

        for (Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator(); iterator.hasNext(); ) {
            BaseAttrInfo baseAttrInfo = iterator.next();
            //获取平台属性值集合
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            // 与参数上的valueId 进行匹配 是否需要判断参数是否有空？
            if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0){
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    for (String valueId : skuLsParams.getValueId()) {
                        if (valueId.equals(baseAttrValue.getId())){
                            // 删除平台属性对象
                            iterator.remove();

                            // 平台属性名称： 平台属性值名称
                            BaseAttrValue baseAttrValueed = new BaseAttrValue();
                            baseAttrValueed.setValueName(baseAttrInfo.getAttrName() + ":" + baseAttrValue.getValueName());

                            // 调用制作urlParam 参数的方法
                            String newParam = makeUrlParam(skuLsParams, valueId);
                            System.out.println("最新的：" + newParam);
                            baseAttrValueed.setUrlParam(newParam);
                            // 存储的面包屑
                            baseAttrValueArrayList.add(baseAttrValueed);
                        }
                    }
                }
            }
        }

        //放入作用域中
        model.addAttribute("skuLsInfoList", skuLsInfoList);
        model.addAttribute("baseAttrInfoList", baseAttrInfoList);
        model.addAttribute("keyword", skuLsParams.getKeyword());
        model.addAttribute("baseAttrValueArrayList", baseAttrValueArrayList);
        model.addAttribute("urlParam",urlParam);
        //返回分页数据
        model.addAttribute("pageNo",skuLsParams.getPageNo());
        model.addAttribute("totalPages",skuLsResult.getTotalPages());
        return "list";
    }

    private String makeUrlParam(SkuLsParams skuLsParams, String... excludeValueIds) {
        String paramUrl = "";
        //拼接检索关键字
        if (skuLsParams.getKeyword() != null && skuLsParams.getKeyword().length() > 0){
            paramUrl += "keyword=" + skuLsParams.getKeyword();
        }
        //拼接三级分类id
        if (skuLsParams.getCatalog3Id() != null && skuLsParams.getCatalog3Id().length() > 0){
            paramUrl += "catalog3Id=" + skuLsParams.getCatalog3Id();
        }
        //拼接平台属性id
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0){
            for (String valueId : skuLsParams.getValueId()) {
                if (excludeValueIds != null && excludeValueIds.length > 0){
                    String excludeValueId = excludeValueIds[0];
                    if (excludeValueId.equals(valueId)){
                        continue;
                    }
                }
                if (paramUrl.length() > 0){
                    paramUrl += "&";
                }
                paramUrl += "valueId=" + valueId;
                System.out.println("paramUrl =" + paramUrl);
            }
        }

        return paramUrl;
    }
}
