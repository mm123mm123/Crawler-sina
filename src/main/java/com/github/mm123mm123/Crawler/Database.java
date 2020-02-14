package com.github.mm123mm123.Crawler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.*;

@SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
public class Database implements CrawlerDAO {

    Connection connection = DriverManager.getConnection("jdbc:h2:file:E:/IdeaProjects/31/Crawler-sina/news", "mm123mm123", "mm123mm123");

    public Database() throws SQLException {
    }

    @Override
    public String getLinkAndDelete() throws SQLException {
        String link = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT LINK FROM LINK_POOL"); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                link = resultSet.getString(1);
                runSQL(link, "DELETE FROM LINK_POOL WHERE LINK=?");
            }
        }
        return link;
    }

    void runSQL(String link, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            int ignored = statement.executeUpdate();
        }
    }
    @Override
    public boolean linkIsProcessedAndInsert(String link) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT LINK FROM PROCESSED_LINK_POOL WHERE LINK = ?");
            statement.setString(1, link);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                runSQL(link, "INSERT INTO PROCESSED_LINK_POOL (LINK) VALUES (?)");
                return false;
            } else {
                return true;
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    @Override
    public void storeNewsToDatabase(String title, String content, String link) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO NEWS (TITLE,CONTENT,URL,CREATED_AT,MODIFIED_AT) VALUES(?,?,?,NOW(),NOW())")) {
            statement.setString(1, title);
            statement.setString(2, content);
            statement.setString(3, link);
            int ignored = statement.executeUpdate();
        }
    }
    @Override
    public void storeNewLinkToDatabase(String newLink) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO LINK_POOL (LINK) VALUES (?)")) {
            statement.setString(1, newLink);
            int ignored = statement.executeUpdate();
        }
    }
}
