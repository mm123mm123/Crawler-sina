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

public class Crawler {
    public void crawler() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        List<String> linkPool = new ArrayList<>();
        Set<String> processedLinkPool = new HashSet<>();
        linkPool.add("https://sina.cn/");
        while (true) {
            if (linkPool.isEmpty()) {
                break;
            }
            String link = linkPool.remove(linkPool.size() - 1);
            if (processedLinkPool.contains(link)) {
                continue;
            }
            if (link.contains("https://news.sina.cn/") || link.equals("https://sina.cn/")) {
                System.out.println(link);
                processedLinkPool.add(link);
                HttpGet httpGet = new HttpGet(link);
                httpGet.addHeader("User_Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36");

                try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
//                    System.out.println(response1.getStatusLine());
                    HttpEntity entity1 = response1.getEntity();
                    String html = EntityUtils.toString(entity1);
                    Document doc = Jsoup.parse(html);
                    String title = doc.body().select("h1").text();
                    System.out.println(title);
                    ArrayList<Element> tags = doc.select("a");
                    for (Element taga : tags) {
                        String newlink = taga.attr("href");
                        linkPool.add(newlink);
                    }
                    EntityUtils.consume(entity1);
                }

            } else {
                continue;
            }

        }

    }
}
