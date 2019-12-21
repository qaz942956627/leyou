package com.leyou.item.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

/**
 * @author 小卢
 */
@Table(name = "tb_category_brand")
@Getter
@Setter
public class CategoryBrand {
    private Long categoryId;
    private Long brandId;
}
