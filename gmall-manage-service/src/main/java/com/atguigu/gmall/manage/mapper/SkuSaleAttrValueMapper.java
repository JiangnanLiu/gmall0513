package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.SkuSaleAttrValue;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by 刘江楠 on 2019/10/29
 */
@Repository
public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {

    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);

    List<Map<Object, Object>> getSkuValueIdsMap(String spuId);
}
