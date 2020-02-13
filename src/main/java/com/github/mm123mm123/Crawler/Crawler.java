package com.github.mm123mm123.Crawler;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class Crawler {
    Connection connection = DriverManager.getConnection("jdbc:h2:file:E:/IdeaProjects/31/Crawler-sina/news", "mm123mm123", "mm123mm123");

    public Crawler() throws SQLException {
    }

    public void crawler() throws IOException, SQLException {
        String link;
        while ((link = linkPoolIsEmpty()) != null) {
            runSQL(link, "DELETE FROM LINK_POOL WHERE LINK=?");
            link = InterceptCoreURL(link);
            if (linkIsProcessed(link)) {
                continue;
            }
            runSQL(link, "INSERT INTO PROCESSED_LINK_POOL (LINK) VALUES (?)");
            if (filterLinksConditions(link)) {
                if (link.startsWith("//")) {
                    link = "https:" + link;
                }
                System.out.println(link);
                httpGetAndParse(link);
            }
        }
    }

    private void runSQL(String link, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            int ignored = statement.executeUpdate();
        }
    }

    private boolean linkIsProcessed(String link) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT LINK FROM PROCESSED_LINK_POOL WHERE LINK = ?");
            statement.setString(1, link);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    private String linkPoolIsEmpty() throws SQLException {
        String link = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT LINK FROM LINK_POOL"); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                link = resultSet.getString(1);
            }
        }
        return link;
    }

    private String InterceptCoreURL(String link) {
        //将“.d.html?”之后的字符去掉，可以避免重复的新闻
        if (link.contains(".d.html?")) {
            link = link.substring(0, link.indexOf(".d.html?"));
        }
        return link;
    }

    private void httpGetAndParse(String link) throws IOException, SQLException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(link);
        httpGet.addHeader("User_Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36");
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            HttpEntity entity1 = response1.getEntity();
            String html = EntityUtils.toString(entity1);
            parseHtml(html, link);
            EntityUtils.consume(entity1);
        }
    }

    private void parseHtml(String html, String link) throws SQLException {
        Document doc = Jsoup.parse(html);
        ArrayList<Element> aTags = doc.select("article");
        if (!aTags.isEmpty()) {
            for (Element aTag : aTags) {
                String title = aTag.select("h1").text();
                System.out.println(title);
                String content = doc.select("p").text();
                storeDataToDatabase(title, content, link);
            }
        }
        ArrayList<Element> tags = doc.select("a");
        for (Element taga : tags) {
            String newlink = taga.attr("href");
            runSQL(newlink, "INSERT INTO LINK_POOL (LINK) VALUES (?)");
        }
    }

    private void storeDataToDatabase(String title, String content, String link) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO NEWS (TITLE,CONTENT,URL,CREATED_AT,MODIFIED_AT) VALUES(?,?,?,NOW(),NOW())")) {
            statement.setString(1, title);
            statement.setString(2, content);
            statement.setString(3, link);
            int ignored = statement.executeUpdate();
        }
    }

    private boolean filterLinksConditions(String link) {
        String[] interestedKeyWords = {"https://sina.cn/", "https://edu.sina.cn/", "https://finance.sina.cn/", "https://emil.sina.cn/", "https://tech.sina.cn/", "https://nba.sina.cn/", "https://edu.sina.cn/"};
        return StringUtils.containsAny(link, interestedKeyWords);
    }
}
