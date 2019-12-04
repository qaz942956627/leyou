package com.leyou.item.controller;

import com.leyou.item.bo.SpuBo;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 小卢
 */
@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /***
     * 根据分页条件查询spu
      * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    ){
        PageResult<SpuBo> result = goodsService.querySpuByPage(key,saleable,page,rows);
        if (result == null || CollectionUtils.isEmpty(result.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
