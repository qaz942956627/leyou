package com.leyou.item.controller;

import com.leyou.item.bo.BrandBo;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.BrandService;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 小卢
 */
@Controller
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    private BrandService brandService;

    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam("pid")Long pid){
            if (pid == null || pid < 0) {
                // 400 参数不合法
                //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                return ResponseEntity.badRequest().build();
            }
            List<Category> categories = this.categoryService.queryCategoriesByPid(pid);
            if (CollectionUtils.isEmpty(categories)) {
                //404 资源未找到
                return ResponseEntity.notFound().build();
            }
            //200 查询成功返回数据
            return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<Void> addCategoryByPid(Category category){
        this.categoryService.addCategoryByPid(category);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateCategory(Category category){
        System.out.println("进入put");
        this.categoryService.updateCategory(category);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id){
        this.categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("bid/{bid}")
    public ResponseEntity<BrandBo> editBrand(@PathVariable("bid")Long bid){
        if (bid == null || bid < 0) {
            return ResponseEntity.badRequest().build();
        }
        BrandBo brandBo = this.brandService.queryBrandsAndCidsByBid(bid);
        if (brandBo==null) {
            //404 资源未找到
            return ResponseEntity.notFound().build();
        }
        //200 查询成功返回数据
        return ResponseEntity.ok(brandBo);
    }
    @DeleteMapping("bid/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid")Long bid){
        if (bid == null || bid < 0) {
            return ResponseEntity.badRequest().build();
        }
        Integer count = this.brandService.deleteBrandsAndCidsByBid(bid);
        if (count==0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

}
