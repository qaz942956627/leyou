package com.leyou.item.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author 小卢
 */
@Table(name="tb_spu_detail")
@Getter
@Setter
@ToString
public class SpuDetail {
    @Id
    /**
     * 对应的SPU的id
     */
    private Long spuId;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 通用规格参数数据
     */
    private String genericSpec;
    /**
     * 特有规格参数及可选值信息，json格式
     */
    private String specialSpec;
    /**
     * 包装清单
     */
    private String packingList;
    /**
     * 售后服务
     */
    private String afterService;
}