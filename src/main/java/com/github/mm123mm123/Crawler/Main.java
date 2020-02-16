package com.github.mm123mm123.Crawler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Mybatis database = new Mybatis();
        for (int num = 0; num < 100; num++) {
            Crawler crawler = new Crawler(database);
            crawler.start();
        }
    }
}
