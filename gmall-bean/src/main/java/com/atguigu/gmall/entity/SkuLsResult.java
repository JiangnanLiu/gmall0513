package com.atguigu.gmall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 刘江楠 on 2019/11/2
 */
@Data
public class SkuLsResult implements Serializable {

    List<SkuLsInfo> skuLsInfoList;

    long total;

    long totalPages;

    List<String> attrValueIdList;

}
