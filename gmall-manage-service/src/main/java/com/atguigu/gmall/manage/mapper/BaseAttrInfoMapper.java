package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by 刘江楠 on 2019/10/26
 */
@Repository
public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {

    List<BaseAttrInfo> selectBaseAttrInfoListByCatalog3Id(String catalog3Id);

    List<BaseAttrInfo> selectAttrInfoListByIds(@Param("valueIds") String valueIds);
}
