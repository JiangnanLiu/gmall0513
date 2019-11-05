package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.entity.SkuLsInfo;
import com.atguigu.gmall.entity.SkuLsParams;
import com.atguigu.gmall.entity.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import javax.swing.text.Highlighter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘江楠 on 2019/11/2
 */
@Service
public class ListServiceImpl implements ListService {

    @Autowired
    private JestClient jestClient;

    @Autowired
    private RedisUtil redisUtil;

    public static final String ES_INDEX = "gmall";

    public static final String ES_TYPE = "SkuInfo";

    @Override
    public void saveESSkuInfo(SkuLsInfo skuLsInfo) {
        Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
        try {
            DocumentResult execute = jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        String query = makeQueryStringForSearch(skuLsParams);

        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult result = null;
        try {
            result = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SkuLsResult skuLsResult = makeResultForSearch(skuLsParams, result);

        return skuLsResult;
    }

    @Override
    public void incrHotScore(String skuId) {
        // 获取redis
        Jedis jedis = redisUtil.getJedis();
        // 定义key
        String hotKey = "hotScore";

        // zSort
        Double hotScore = jedis.zincrby(hotKey, 1, "skuId:" + skuId);
        if (hotScore % 10 == 0) {
            // 更新一次es  Math.round(12.5) = 13  Math.round(-12.5) = -12
            updateHotScore(skuId, Math.round(hotScore));
        }
    }

    private void updateHotScore(String skuId, long hotScore) {
        /*
        1.  定义dsl 语句
        2.  定义动作
        3.  执行
         */
        String updateQuery = "{\n" +
                "  \"doc\": {\n" +
                "      \"hotScore\": " + hotScore + "\n" +
                "  }\n" +
                "}";
        Update update = new Update.Builder(updateQuery).index(ES_INDEX).type(ES_TYPE).id(skuId).build();
        try {
            jestClient.execute(update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SkuLsResult makeResultForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
        SkuLsResult skuLsResult = new SkuLsResult();

        List<SkuLsInfo> skuLsInfoList=new ArrayList<>(skuLsParams.getPageSize());

        //获取sku列表
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
            SkuLsInfo skuLsInfo = hit.source;
            if(hit.highlight!=null&&hit.highlight.size()>0){
                List<String> list = hit.highlight.get("skuName");
                //把带有高亮标签的字符串替换skuName
                String skuNameHl = list.get(0);
                skuLsInfo.setSkuName(skuNameHl);
            }
            skuLsInfoList.add(skuLsInfo);
        }
        skuLsResult.setSkuLsInfoList(skuLsInfoList);
        skuLsResult.setTotal(searchResult.getTotal());

        //取记录个数并计算出总页数
        long totalPage= (searchResult.getTotal() + skuLsParams.getPageSize() -1) / skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPage);

        //取出涉及的属性值id
        List<String> attrValueIdList=new ArrayList<>();
        MetricAggregation aggregations = searchResult.getAggregations();
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");
        if(groupby_attr!=null){
            List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                attrValueIdList.add( bucket.getKey()) ;
            }
            skuLsResult.setAttrValueIdList(attrValueIdList);
        }
        return skuLsResult;
    }

    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        //声明查询器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //申明bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //设置skuName查询
        if (skuLsParams.getKeyword() != null && skuLsParams.getKeyword().length() > 0){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            boolQueryBuilder.must(matchQueryBuilder);

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            // 设置高亮字段
            highlightBuilder.field("skuName");
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            // 将高亮结果放入查询器中
            searchSourceBuilder.highlight(highlightBuilder);
        }

        //设置三级分类id
        if (skuLsParams.getCatalog3Id() != null && skuLsParams.getCatalog3Id().length() > 0){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }
        //设置属性值
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0){
            for (String valueId : skuLsParams.getValueId()) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        searchSourceBuilder.query(boolQueryBuilder);
        // 设置分页
        int form = (skuLsParams.getPageNo() - 1) * skuLsParams.getPageSize();
        searchSourceBuilder.from(form);
        searchSourceBuilder.size(skuLsParams.getPageSize());
        // 设置按照热度
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);

        // 设置聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_attr);

        String query = searchSourceBuilder.toString();
        System.out.println("query=" + query);

        return query;
    }
}
