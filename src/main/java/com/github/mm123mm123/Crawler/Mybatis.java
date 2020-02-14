package com.github.mm123mm123.Crawler;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;

public class Mybatis implements CrawlerDAO {
    String resource = "db/mybatis/config.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    boolean autoCommit = true;
    HashMap<String, String> conditionMap = new HashMap<>();

    public Mybatis() throws IOException {
    }

    @Override
    public String getLinkAndDelete() {
        String link = null;
        try (SqlSession session = sqlSessionFactory.openSession(autoCommit)) {
            link = session.selectOne("db.mybatis.Mapper.xml.selectLink");
            if (link != null) {
                session.delete("db.mybatis.Mapper.xml.deleteLink", link);
            }
        }
        return link;
    }

    @Override
    public boolean linkIsProcessedAndInsert(String link) throws SQLException {
        conditionMap.put("tableName", "PROCESSED_LINK_POOL");
        conditionMap.put("link", link);
        try (SqlSession session = sqlSessionFactory.openSession(autoCommit)) {
            if (session.selectOne("db.mybatis.Mapper.xml.selectProcessedLink", link) == null) {
                session.insert("db.mybatis.Mapper.xml.insertLink", conditionMap);
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void storeNewsToDatabase(String title, String content, String link) {
        try (SqlSession session = sqlSessionFactory.openSession(autoCommit)) {
            session.insert("db.mybatis.Mapper.xml.insertNews", new News(title, content, link));
        }
    }

    @Override
    public void storeNewLinkToDatabase(String newLink) throws SQLException {
        conditionMap.put("tableName", "LINK_POOL");
        conditionMap.put("link", newLink);
        try (SqlSession session = sqlSessionFactory.openSession(autoCommit)) {
            session.insert("db.mybatis.Mapper.xml.insertLink", conditionMap);
        }
    }
}
