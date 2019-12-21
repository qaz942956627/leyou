package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.bo.BrandBo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.pojo.PageResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author 小卢
 */
@Service
public class BrandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandService.class);

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, boolean desc) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //根据姓名模糊查询或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        //添加分页条件
        PageHelper.startPage(page,rows);
        //添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy+" "+(desc?"desc":"asc"));
        }
        List<Brand> brands = this.brandMapper.selectByExample(example);
        //包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        System.out.println(pageInfo.getTotal());

        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBrand(Brand brand,Long[] cids){
        this.brandMapper.insert(brand);
        for (Long cid : cids) {
            this.brandMapper.insertCategoryBrand(brand.getId(),cid);
        }
    }

    public List<Brand> queryBrandsByCid(Long cid) {
        return this.brandMapper.queryBrandsByCid(cid);
    }

    public BrandBo queryBrandsAndCidsByBid(Long bid) {
        Brand brand = this.brandMapper.selectByPrimaryKey(bid);
        LOGGER.info("品牌数据:{}",brand);
        List<Long> cids = this.brandMapper.queryCidsByBid(brand.getId());
        LOGGER.info("cids:{}",cids);
        BrandBo brandBo = new BrandBo();
        BeanUtils.copyProperties(brand,brandBo);
        brandBo.setCids(cids);
        LOGGER.info("brandBo:{}",brandBo);
        return brandBo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer deleteBrandsAndCidsByBid(Long bid) {
        int counts = this.brandMapper.deleteCategoryBrandByBid(bid);
        LOGGER.info("中间表数据删除了{}条!",counts);
        int count = this.brandMapper.deleteByPrimaryKey(bid);
        LOGGER.info("品牌表删除了{}条数据!",count);
        return count;
    }
}
