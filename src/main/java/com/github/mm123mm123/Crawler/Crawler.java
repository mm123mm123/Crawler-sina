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
    public Crawler() throws SQLException {
    }

    Database database = new Database();

    public void crawler() throws IOException, SQLException {
        String link;
        while ((link = database.linkPoolIsEmpty()) != null) {
            database.runSQL(link, "DELETE FROM LINK_POOL WHERE LINK=?");
            link = InterceptCoreURL(link);
            if (database.linkIsProcessed(link)) {
                continue;
            }
            database.runSQL(link, "INSERT INTO PROCESSED_LINK_POOL (LINK) VALUES (?)");
            if (filterLinksConditions(link)) {
                if (link.startsWith("//")) {
                    link = "https:" + link;
                }
                System.out.println(link);
                httpGetAndParse(link);
            }
        }
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
                database.storeDataToDatabase(title, content, link);
            }
        }
        ArrayList<Element> tags = doc.select("a");
        for (Element taga : tags) {
            String newlink = taga.attr("href");
            database.runSQL(newlink, "INSERT INTO LINK_POOL (LINK) VALUES (?)");
        }
    }

    private boolean filterLinksConditions(String link) {
        String[] interestedKeyWords = {"https://sina.cn/", "https://edu.sina.cn/", "https://finance.sina.cn/", "https://emil.sina.cn/", "https://tech.sina.cn/", "https://nba.sina.cn/", "https://edu.sina.cn/"};
        return StringUtils.containsAny(link, interestedKeyWords);
    }
}
