package com.atguigu.gmall.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 刘江楠 on 2019/11/2
 */
@Data
public class SkuLsInfo implements Serializable {

    String id;

    BigDecimal price;

    String skuName;

    String catalog3Id;

    String skuDefaultImg;

    Long hotScore=0L;

    List<SkuLsAttrValue> skuAttrValueList;

}
