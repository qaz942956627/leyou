package com.leyou.item.bo;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 小卢
 */
@Getter
@Setter
@ToString
public class SpuBo extends Spu {

    private String cname;

    private String bname;

    private List<Sku> skus;

    private SpuDetail spuDetail;

}
