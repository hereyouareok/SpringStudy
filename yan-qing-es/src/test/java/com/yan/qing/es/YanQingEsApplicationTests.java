package com.yan.qing.es;

import com.alibaba.fastjson.JSON;
import com.yan.qing.es.entity.User;
import com.yan.qing.es.utils.ESconst;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class YanQingEsApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    //索引创建
    @Test
    void testCreateIndex() throws IOException {
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("yan_index");
        //客户端执行请求   indicesClient 请求后获得响应
        CreateIndexResponse indexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

        System.out.println(indexResponse);
    }

    //获取索引
    @Test
    void testExitIndex() throws IOException {
        GetIndexRequest yanIndex = new GetIndexRequest("yan_index");

        boolean exists = restHighLevelClient.indices().exists(yanIndex, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //删除索引
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest yanIndex = new DeleteIndexRequest("yan_index");

        AcknowledgedResponse delete = restHighLevelClient.indices().delete(yanIndex, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }


    //添加文档
    @Test
    void addDoc() throws IOException {
        User user = new User("颜倾", 18);

        IndexRequest yanIndex = new IndexRequest("yan_index");
        //put

        yanIndex.id("1");
        yanIndex.timeout(TimeValue.timeValueSeconds(1));
        yanIndex.timeout("1s");
        //将数据放入请求 json
        IndexRequest source = yanIndex.source(JSON.toJSONString(user), XContentType.JSON);

        //发送请求
        IndexResponse index = restHighLevelClient.index(yanIndex, RequestOptions.DEFAULT);

        //响应的结果
        System.out.println(index.toString());
        System.out.println(index.status());


    }

    //获取文档 判断是否存在
    void testIsExit() throws IOException {
        GetRequest getRequest = new GetRequest("yan_index", "1");
        //不获取返回的_source的上下文
        getRequest.fetchSourceContext(new FetchSourceContext(false));

        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);

        System.out.println(exists);
    }

    //获取文档信息
    @Test
    void testGetDoc() throws IOException {
        GetRequest getRequest = new GetRequest("yan_index", "1");
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        //文档内容
        System.out.println(getResponse.getSourceAsString());
        //返回的内容和命令一样
        System.out.println(getResponse);
    }

    //更新文档信息
    @Test
    void testUpdateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("yan_index", "1");
        updateRequest.timeout("1s");
        User user = new User("颜倾ak", 20);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }

    //删除文档信息
    @Test
    void testDeleteDoc() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("yan_index", "3");
        deleteRequest.timeout("1s");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }

    //批量插入文档信息
    @Test
    void testBulkDoc() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User("yanqing1", 15));
        arrayList.add(new User("yanqing2", 75));
        arrayList.add(new User("yanqing3", 55));
        arrayList.add(new User("yanqing4", 16));
        arrayList.add(new User("yanqing5", 15));
        for (int i = 0; i < arrayList.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("yan_index")
                            .id("" + (i + 1))
                            .source(JSON.toJSONString(arrayList.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());
    }

    //查询
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("yan_index");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询条件  使用QueryBuilders实现 term精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "yanqing");
        sourceBuilder.query(termQueryBuilder);
/*        sourceBuilder.from();
        sourceBuilder.size();*/
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);


        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(JSON.toJSONString(search.getHits()));

        System.out.println("-----------------------------");
        for (SearchHit documentHit : search.getHits().getHits()) {
            System.out.println(documentHit.getSourceAsMap());
        }
    }
}
