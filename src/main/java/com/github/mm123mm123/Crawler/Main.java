package com.github.mm123mm123.Crawler;


import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Mybatis database = new Mybatis();
        for (int num = 0; num < 25; num++) {
            Crawler crawler = new Crawler(database);
            crawler.start();
        }
//       制造数据插入引擎
//        for(int i=0;i<25;i++){
//            MockData mockData=new MockData();
//            mockData.run();
//        }
//        通过引擎对输入的关键词进行搜索
//        while (true) {
//            if (System.in != null) {
//                Reader reader = new InputStreamReader(System.in);
//                BufferedReader bufferedReader = new BufferedReader(reader);
//                String keyWord = bufferedReader.readLine();
//                ElasticSearchEngine engine=new ElasticSearchEngine();
//                engine.search(keyWord);
//            }
//        }
    }
}
