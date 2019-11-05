package com.atguigu.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by 刘江楠 on 2019/10/28
 */
@Data
public class BaseSaleAttr implements Serializable {

    @Id
    @Column
    String id ;

    @Column
    String name;

}
