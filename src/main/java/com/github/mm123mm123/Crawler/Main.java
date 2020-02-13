package com.github.mm123mm123.Crawler;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        Crawler crawler = new Crawler();
        crawler.crawler();
    }
}
