package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
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
    @Select("INSERT INTO tb_category_brand(category_id,brand_id) VALUES (#{cid},#{id})")
    void insertCategoryBrand(@Param("id") Long id, @Param("cid") Long cid);

    /**
     * 通过品牌id查询对应的所有分类
     * @param bid 品牌id
     * @return 分类id集合
     */
    @Select("SELECT category_id FROM `tb_category_brand` WHERE brand_id = #{bid}")
    List<Long> queryCidsByBid(@Param("bid")Long bid);

    /***
     * 删除品牌的时候关联删除品牌分类中间表
     * @param bid
     * @return
     */
    @Delete("DELETE FROM tb_category_brand WHERE brand_id = #{bid}")
    Integer deleteCategoryBrandByBid(@Param("bid") Long bid);
}
