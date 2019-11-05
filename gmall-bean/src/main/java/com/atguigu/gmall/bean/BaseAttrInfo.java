package com.atguigu.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 刘江楠 on 2019/10/26
 */
@Data
public class BaseAttrInfo implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String attrName;
    @Column
    private String catalog3Id;

    @Transient
    private List<BaseAttrValue> attrValueList;

}
