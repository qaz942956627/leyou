package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 小卢
 */
public interface BrandMapper extends Mapper<Brand> {

    /**
     * 查询出对应分类id所有的品牌
     * @param cid 分类id
     * @return 品牌集合
     */
    @Select("SELECT a.* FROM tb_brand a INNER JOIN tb_category_brand b ON a.id = b.brand_id WHERE b.category_id = #{cid} ORDER BY a.ID DESC")
    List<Brand> queryBrandsByCid(@Param("cid") Long cid);

    /**
     * 插入分类商品中间表数据
     * @param id
     * @param cid
     */
    @Select("INSERT INTO tb_category_brand(category_id,brand_id) VALUES (#{id},#{cid})")
    void insertCategoryBrand(@Param("id") Long id, @Param("cid") Long cid);
}
