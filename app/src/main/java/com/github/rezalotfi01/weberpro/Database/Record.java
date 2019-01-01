package com.github.rezalotfi01.weberpro.Database;

public class Record {
    private String title;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    private String url;
    public String getURL() {
        return url;
    }
    public void setURL(String url) {
        this.url = url;
    }

    private String path;
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    private String fileName;

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private long time;
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public Record() {
        this.title = null;
        this.url = null;
        this.path = null;
        this.fileName = null;
        this.time = 0L;
    }

    public Record(String title, String url, long time) {
        this.title = title;
        this.url = url;
        this.time = time;
    }

    public Record(String title, String url, String path, String fileName, long time) {
        this.title = title;
        this.url = url;
        this.path = path;
        this.fileName = fileName;
        this.time = time;
    }
}
