package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Spu;
import com.leyou.pojo.PageResult;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 小卢
 */
@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    /***
     * 根据分页条件查询spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuByPage(String key, boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //添加查询条件
        criteria.andLike("title","%"+key+"%");

        //添加上下架过滤条件
        criteria.andEqualTo("saleable",saleable);

        //添加分页条件
        PageHelper.startPage(page,rows);
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);

        //spu集合转化成spuBo结果集
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            //获取品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spuBo.getBrandId());
            spuBo.setBname(brand.getName());
            //获取品类名称
            List<String> bNames = this.categoryService.queryNamesByIds(Arrays.asList(spuBo.getCid1(), spuBo.getCid2(), spuBo.getCid3()));
            spuBo.setCname(StringUtils.join(bNames, "-"));
            return spuBo;
        }).collect(Collectors.toList());

        //返回PageResult<SpuBo>
        return new PageResult<>(spuPageInfo.getTotal(),spuBos);
    }
}
