package com.ioj.wax.ioj;

public class StatusInfo {
    private String username;
    private String problemid;
    private String result;
    private String memory;
    private String time;
    private String compiler;
    private String submittime;
    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }

    public String getSubmittime() {
        return submittime;
    }

    public void setSubmittime(String submittime) {
        this.submittime = submittime;
    }

    public String getCompiler() {
        return compiler;

    }

    public StatusInfo(String username, String problemid, String result, String memory, String time, String lenght,String compiler,String submittime) {
        this.username = username;
        this.problemid = problemid;
        this.result = result;
        this.memory = memory;
        this.time = time;
        this.lenght = lenght;
        this.compiler = compiler;
        this.submittime = submittime;
    }

    public String getProblemid() {
        return problemid;
    }

    public void setProblemid(String problemid) {
        this.problemid = problemid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLenght() {
        return lenght;
    }

    public void setLenght(String lenght) {
        this.lenght = lenght;
    }

    private String lenght;
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
