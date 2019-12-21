package com.leyou.item.bo;

import com.leyou.item.pojo.Brand;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 小卢
 */
@Getter
@Setter
public class BrandBo extends Brand {

    private List<Long> cids;
}
