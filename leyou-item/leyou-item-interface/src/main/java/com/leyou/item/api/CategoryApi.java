package com.leyou.item.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 小卢
 */
@RequestMapping("/category")
public interface CategoryApi {

    @GetMapping
    List<String> queryNamesByIds(@RequestParam("cids") List<Long> ids);
}
