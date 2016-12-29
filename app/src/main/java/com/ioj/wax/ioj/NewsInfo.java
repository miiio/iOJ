package com.ioj.wax.ioj;

public class NewsInfo {
    private String newstitle;
    private String newsid;
    private String name;
    private String date;

    public String getNewstitle() {
        return newstitle;
    }

    public void setNewstitle(String newstitle) {
        this.newstitle = newstitle;
    }

    public String getNewsid() {
        return newsid;
    }

    public void setNewsid(String newsid) {
        this.newsid = newsid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public NewsInfo(String newstitle, String newsid, String name, String date) {
        this.newstitle = newstitle;
        this.newsid = newsid;
        this.name = name;
        this.date = date;
    }
}
