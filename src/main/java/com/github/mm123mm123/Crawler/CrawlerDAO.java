package com.github.mm123mm123.Crawler;

import java.io.IOException;
import java.sql.SQLException;

public interface CrawlerDAO {
    String getLinkAndDelete() throws SQLException, IOException;

    boolean linkIsProcessedAndInsert(String link) throws SQLException;

    void storeNewsToDatabase(String title, String content, String link) throws SQLException;

    void storeNewLinkToDatabase(String link) throws SQLException;
}
