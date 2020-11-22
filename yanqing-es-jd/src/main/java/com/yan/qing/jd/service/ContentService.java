package com.yan.qing.jd.service;

import com.alibaba.fastjson.JSON;
import com.yan.qing.jd.pojo.Content;
import com.yan.qing.jd.utils.HtmlParseUtil;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author YanQin
 * @version v1.0.0
 * @Description :
 * @Create on : 2020/11/22 21:22
 **/
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    public Boolean parseContent(String keyWords) throws IOException {
        List<Content> contentList = new HtmlParseUtil().parse(keyWords);

        BulkRequest bulkRequest = new BulkRequest();

        bulkRequest.timeout("2m");
        for (int i = 0; i < contentList.size(); i++) {

            bulkRequest.add(new IndexRequest("jd_goods")
                    .source(JSON.toJSONString(contentList.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();

    }

    //搜索功能
    public List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException {

        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = searchRequest.source();
        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //构建高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            //解析高亮的字段,获取高亮的字段

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //解析高亮字段 将原来的字段替换为我们高亮的字段
            if (title != null) {
                Text[] fragments = title.getFragments();
                String newTilt = "";
                for (Text text : fragments) {
                    newTilt += text;
                }
                sourceAsMap.put("title", newTilt);
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}
