package com.github.mm123mm123.Crawler;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class Mybatis implements CrawlerDAO {
    String resource = "db/mybatis/config.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    boolean autoCommit = true;

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
    public synchronized boolean linkIsProcessedAndInsert(String link) {
        try (SqlSession session = sqlSessionFactory.openSession(autoCommit)) {
            if (session.selectOne("db.mybatis.Mapper.xml.selectProcessedLink", link) == null) {
                session.insert("db.mybatis.Mapper.xml.insertLink", link);
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
        try (SqlSession session = sqlSessionFactory.openSession(autoCommit)) {
            session.insert("db.mybatis.Mapper.xml.insertNewLink", newLink);
        }
    }
}
