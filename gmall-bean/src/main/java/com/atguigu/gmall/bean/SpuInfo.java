package com.atguigu.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 刘江楠 on 2019/10/28
 */
@Data
public class SpuInfo implements Serializable {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column
    private String spuName;

    @Column
    private String description;

    @Column
    private  String catalog3Id;

    @Transient
    private List<SpuSaleAttr> spuSaleAttrList;
    @Transient
    private List<SpuImage> spuImageList;

}
