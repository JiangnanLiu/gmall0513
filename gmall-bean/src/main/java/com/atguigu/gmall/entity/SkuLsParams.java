package com.atguigu.gmall.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by 刘江楠 on 2019/11/2
 */
@Data
public class SkuLsParams implements Serializable {

    String  keyword;

    String catalog3Id;

    String[] valueId;

    int pageNo=1;

    int pageSize=20;

}
