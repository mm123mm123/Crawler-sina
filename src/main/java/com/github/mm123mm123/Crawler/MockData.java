package com.github.mm123mm123.Crawler;

import org.apache.http.HttpHost;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockData extends Thread {
    @Override
    public void run() {
        try {
            String resource = "db/mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                for (int i = 0; i < 1000; i++) {
                    List<News> list = session.selectList("db.mybatis.ElasticMapper.xml.selectNews");
                    storeDataIntoElasticSreach(list);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void storeDataIntoElasticSreach(List<News> list) throws IOException {
        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")))) {
            BulkRequest request = new BulkRequest();
            for (News news : list) {
                Map<String, Object> data = new HashMap<>();
                data.put("title", news.getTitle());
                data.put("content", news.getContent().length() > 10 ? news.getContent().substring(0, 10) : news.getContent());
                data.put("link", news.getLink());
                data.put("created_at", news.getCreated_at());
                data.put("modified_at", news.getModified_at());
                IndexRequest indexRequest = new IndexRequest("news").source(data, XContentType.JSON);
                request.add(indexRequest);
            }
            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
            System.out.println(bulkResponse.status().getStatus());
        }
    }
}
