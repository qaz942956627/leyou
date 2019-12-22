package com.leyou.item.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_stock")
@Getter
@Setter
@ToString
public class Stock {

    @Id
    private Long skuId;
    /**
     * 秒杀可用库存
     */
    private Integer seckillStock;
    /**
     * 已秒杀数量
     */
    private Integer seckillTotal;
    /**
     * 正常库存
     */
    private Long stock;
}