package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 刘江楠 on 2019/10/25
 */
@RestController
public class OrderController {

    @Reference
    private UserInfoService userInfoService;

    @GetMapping("trad")
    public List<UserAddress> getUserAddressByUserId(String userId){
        return userInfoService.findAll(userId);
    }
}
