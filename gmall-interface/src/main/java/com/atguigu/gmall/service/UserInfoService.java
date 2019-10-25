package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;

import java.util.List;

/**
 * Created by 刘江楠 on 2019/10/24
 */
public interface UserInfoService {

    List<UserInfo> findAll();

    List<UserInfo> findAllByProperty(UserInfo userInfo);

    List<UserInfo> findAllByExample(UserInfo userInfo);

    UserInfo selectOne(UserInfo userInfo);

    UserInfo selectByPrimaryKey(String id);

    void addUser(UserInfo userInfo);

    void delUser(UserInfo userInfo);

    void updateUser(UserInfo userInfo);

    List<UserAddress> findAll(String userId);
}
