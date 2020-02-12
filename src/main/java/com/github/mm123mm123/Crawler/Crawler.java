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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class Crawler {
    public void crawler() throws IOException {
        List<String> linkPool = new ArrayList<>();
        Set<String> processedLinkPool = new HashSet<>();
        linkPool.add("https://sina.cn/");
        while (!linkPool.isEmpty()) {
            String link = linkPool.remove(linkPool.size() - 1);
            link = InterceptCoreURL(link);
            if (processedLinkPool.contains(link)) {
                continue;
            }
            processedLinkPool.add(link);
            if (filterLinksConditions(link)) {
                if (link.startsWith("//")) {
                    link = "https:" + link;
                }
                System.out.println(link);
                httpGetAndParse(linkPool, link);
            } else {
                continue;
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

    private void httpGetAndParse(List<String> linkPool, String link) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(link);
        httpGet.addHeader("User_Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36");

        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            HttpEntity entity1 = response1.getEntity();
            String html = EntityUtils.toString(entity1);
            prseHtml(html, linkPool);
            EntityUtils.consume(entity1);
        }
    }

    private void prseHtml(String html, List<String> linkPool) {
        Document doc = Jsoup.parse(html);
        ArrayList<Element> aTags = doc.select("article");
        if (!aTags.isEmpty()) {
            for (Element aTag : aTags) {
                String title = aTag.select("h1").text();
                System.out.println(title);
            }
        }
        ArrayList<Element> tags = doc.select("a");
        for (Element taga : tags) {
            String newlink = taga.attr("href");
            linkPool.add(newlink);
        }
    }

    private boolean filterLinksConditions(String link) {
        String[] interestedKeyWords = {"https://sina.cn/", "https://edu.sina.cn/", "https://finance.sina.cn/", "https://emil.sina.cn/", "https://tech.sina.cn/", "https://nba.sina.cn/", "https://edu.sina.cn/"};
        return StringUtils.containsAny(link, interestedKeyWords);
    }
}
