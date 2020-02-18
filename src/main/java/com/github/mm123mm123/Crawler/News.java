package com.github.mm123mm123.Crawler;

public class News {
    String title;
    String content;
    String link;
    String created_at;
    String modified_at;


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getModified_at() {
        return modified_at;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }



    public News(String title, String content, String link) {
        this.title=title;
        this.content=content;
        this.link=link;
    }

    public News() {
    }
}
