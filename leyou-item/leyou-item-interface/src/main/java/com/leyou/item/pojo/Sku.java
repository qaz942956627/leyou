package com.leyou.item.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 小卢
 */
@Table(name = "tb_sku")
@Getter
@Setter
@ToString
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long spuId;
    private String title;
    private String images;
    private Long price;
    /**
     * 商品特殊规格的下标
     */
    private String indexes;
    /**
     * 商品特殊规格的键值对
     */
    private String ownSpec;
    /**
     * 是否有效，逻辑删除用
     */
    private Boolean enable;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后修改时间
     */
    private Date lastUpdateTime;
    @Transient
    /**
     * @Transient 表示该属性并非一个到数据库表的字段的映射, ORM框架将忽略该属性.
     */
    private Long stock;
}