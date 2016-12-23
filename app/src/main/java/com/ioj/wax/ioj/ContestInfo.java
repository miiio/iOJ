package com.ioj.wax.ioj;

public class ContestInfo {
    private String contestId;
    private String title;
    private String type;
    private String startTime;
    private String endTime;
    private String status;
    private String purview;
    private String holder;

    public ContestInfo(String type, String contestId, String title, String startTime, String endTime, String status, String purview, String holder) {
        this.type = type;
        this.contestId = contestId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.purview = purview;
        this.holder = holder;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPurview() {
        return purview;
    }

    public void setPurview(String purview) {
        this.purview = purview;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }
}
