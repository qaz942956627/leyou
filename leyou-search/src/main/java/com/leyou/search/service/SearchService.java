package com.leyou.search.service;

import com.alibaba.fastjson.JSONObject;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 小卢
 */
@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    public Goods buildGoods(Spu spu) {
        Goods goods = new Goods();

        //根据分类的id查询分类名称
        List<String> cNames = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //根据品牌id查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //根据spuId查询所有的sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        //初始化一个价格集合,收集所有的sku的价格
        List<Long> prices = new ArrayList<>();
        //收集sku的必要字段信息
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            //获取spu下所有sku的价格
            prices.add(sku.getPrice());

            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            map.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);

            skuMapList.add(map);
        });

        //根据spu中的cid3查询出所有的搜索规格参数
        List<SpecParam> specParams = this.specificationClient.queryParams(null, spu.getCid3(), null, true);

        //把通用的规格参数值,进行反序列化
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        Map<String,Object> genericSpecMap = JSONObject.parseObject(spuDetail.getGenericSpec(), Map.class);
        //把特殊的规格参数值,进行反序列化
        Map<String,List<Object>> specialSpecMap = JSONObject.parseObject(spuDetail.getSpecialSpec(), Map.class);

        Map<String,Object> specs = new HashMap<>();
        specParams.forEach(specParam -> {
            //判断规格参数的类型,是否是通用的规格参数
            if (specParam.getGeneric()) {
                //如果是通用类型的参数,从genericSpecMap获取规格参数的值
                String value = genericSpecMap.get(specParam.getId().toString()).toString();
                //判断是否是数值类型,如果是数值类型,应该返回一个区间
                if (specParam.getNumeric()) {
                    value = chooseSegment(value,specParam);
                }
                specs.put(specParam.getName(),value);
            }else {
                //如果是特殊的规格参数,从specialSpecMap中获取值
                List<Object> value = specialSpecMap.get(specParam.getId().toString());
                specs.put(specParam.getName(),value);
            }
        });

        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());

        //拼接all字段,需要分类名称以及品牌名称
        goods.setAll(spu.getTitle() + " " + StringUtils.join(cNames, ",") + " " + brand.getName());

        goods.setPrice(prices);

        //获取spu下所有sku,并转化成json串
        goods.setSkus(JSONObject.toJSONString(skuMapList));

        //获取所有查询的规格参数{name:value}
        goods.setSpecs(specs);

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }



    public SearchResult search(SearchRequest request) {
        String key = request.getKey();
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // 1、对key进行全文检索查询
        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);
        queryBuilder.withQuery(basicQuery);

        // 2、通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(
                new String[]{"id","skus","subTitle"}, null));

        // 3、分页
        // 准备分页参数
        int page = request.getPage();
        int size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        //添加分类和品牌的聚合
        String categoryAggName = "categories";
        String brandAggName = "brands";

        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 4、查询，获取结果
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        List<Map<String,Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        //3.4 处理规格参数
        List<Map<String,Object>> specs = null;
        if (!CollectionUtils.isEmpty(categories) || categories.size() == 1){
            //如果商品分类只有一个进行聚合，并根据分类与基本查询条件聚合
            Long cid = (Long) categories.get(0).get("id");
            specs = getParamAggResult(cid,basicQuery);
        }

        // 封装结果并返回
        return new SearchResult(goodsPage.getTotalElements(), goodsPage.getTotalPages(), goodsPage.getContent(),categories,brands,null);
    }

    /***
     * 根据查询条件来聚合规格参数
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {
        //自定义查询条件构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本查询条件
        queryBuilder.withQuery(basicQuery);

        //查询要聚合的规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, cid, null, null);

        //添加规格参数的聚合
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs."+param.getName()+".keyword"));
        });

        //添加结果集的过滤  不需要普通结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));

        //执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        //解析聚合结果集 key-聚合名称(规格参数名称),value-聚合对象
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();

        List<Map<String,Object>> specs = new ArrayList<>();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            //初始化一个规格参数map key规格参数名,options规格参数值
            Map<String,Object> map = new HashMap<>();
            map.put("key",entry.getKey());
            //初始化一个options数组
            List<String> options = new ArrayList<>();
            Aggregation aggregation = entry.getValue();
            StringTerms terms = (StringTerms) aggregation;
            terms.getBuckets().forEach(bucket -> {
                options.add(bucket.getKeyAsString());
            });
            map.put("options",options);
            specs.add(map);
        }

        return specs;
    }

    /***
     * 解析品牌的聚合结果集
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        //通过词条获取每一个桶
        List<Brand> brands = terms.getBuckets().stream().map(bucket -> {
            //遍历每一个桶获取到品牌id
            long brandId = bucket.getKeyAsNumber().longValue();
            //调用品牌接口获取每个品牌对象
            Brand brand = this.brandClient.queryBrandById(brandId);
            return brand;
        }).collect(Collectors.toList());
        return brands;
    }

    /***
     * 解析分类的聚合结果集
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        List<Map<String, Object>> maps = terms.getBuckets().stream().map(bucket -> {
            Map<String, Object> map = new HashMap<>();
            Long id = bucket.getKeyAsNumber().longValue();
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(id));
            map.put("id", id);
            map.put("name", names.get(0));
            return map;
        }).collect(Collectors.toList());
        return maps;
    }
}
