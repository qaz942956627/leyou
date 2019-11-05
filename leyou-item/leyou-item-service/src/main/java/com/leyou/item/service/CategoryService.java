package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 小卢
 */
@Service
public class CategoryService {

    private CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 根据父节点查询子节点
     * @param pid 父节点id
     * @return 子节点品类集合
     */
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return this.categoryMapper.select(record);
    }

    public void addCategoryByPid(Category category) {
        System.out.println("category"+category.getId());
        //先插入一条新数据,先把id设置为null不然会是0
        category.setId(null);
        this.categoryMapper.insert(category);
        //插入之后把如果这个节点之前不是父节点更新为父节点
        Category parent = new Category();
        parent.setId(category.getParentId());
        parent.setIsParent(true);
        this.categoryMapper.updateByPrimaryKeySelective(category);
    }

    public void updateCategory(Category category) {
        int i = this.categoryMapper.updateByPrimaryKeySelective(category);
        System.out.println("成功更新"+i+"条数据!");
    }

    public void deleteCategory(Long id) {
        System.out.println("category.id="+id);
        int i = this.categoryMapper.deleteByPrimaryKey(id);
        System.out.println("成功删除"+i+"条数据!");
    }
}
