package com.atguigu.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by 刘江楠 on 2019/10/26
 */
@Data
public class BaseCatalog1 implements Serializable {

    @Id
    @Column
    private String id;
    @Column
    private String name;

}



