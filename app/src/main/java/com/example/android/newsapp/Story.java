package com.example.android.newsapp;

public class Story {

    private final String date;
    private final String section;
    private final String title;
    private final String author;
    private final String url;

    public Story(String date, String title, String section, String url, String author) {
        this.date = date;
        this.section = section;
        this.title = title;
        this.url = url;
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public String getSection() {
        return section;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }
}
