package com.ioj.wax.ioj;


import java.io.Serializable;

public class UserInfo implements Serializable{
    private String cookie;
    private String username;
    private String picurl;
    private String maxin;
    private boolean isLogin;
    public String getCookie() {
        if(cookie==null)return "";
        return cookie;
    }
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
    public void setLogin(boolean login) {
        isLogin = login;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }
    public void setMaxin(String maxin) {
        this.maxin = maxin;
    }
    public String getUsername() {
        return username;
    }
    public String getPicurl() {
        return picurl;
    }
    public String getMaxin() {
        return maxin;
    }
    public boolean isLogin() {
        return isLogin;
    }
    public UserInfo()
    {
        this.username=null;
        this.picurl=null;
        this.maxin=null;
        this.isLogin=false;
    }

}
