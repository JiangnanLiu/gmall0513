package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.SpuSaleAttr;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by 刘江楠 on 2019/10/28
 */
@Repository
public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {

    List<SpuSaleAttr> selectSpuSaleAttrList(String spuId);

    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(String id, String spuId);
}
