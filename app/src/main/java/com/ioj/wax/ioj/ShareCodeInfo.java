package com.ioj.wax.ioj;


import java.io.Serializable;

public class ShareCodeInfo implements Serializable {
    private String id;
    private String acid;
    private String prbid;
    private String prbtitle;
    private String code;
    private String contributor;
    private String submittime;
    private String notes;

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    private String like;
    public ShareCodeInfo(String notes, String id, String acid, String prbid, String prbtitle, String code, String contributor, String submittime, String like) {
        this.notes = notes;
        this.id = id;
        this.acid = acid;
        this.prbid = prbid;
        this.prbtitle = prbtitle;
        this.code = code;
        this.contributor = contributor;
        this.submittime = submittime;
        this.like = like;
    }

    public String getNotes() {
        if(notes.equals("")){
            return "...";
        }
        else{
            return notes;
        }
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcid() {
        return acid;
    }

    public void setAcid(String acid) {
        this.acid = acid;
    }

    public String getPrbid() {
        return prbid;
    }

    public void setPrbid(String prbid) {
        this.prbid = prbid;
    }

    public String getPrbtitle() {
        return prbtitle;
    }

    public void setPrbtitle(String prbtitle) {
        this.prbtitle = prbtitle;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getSubmittime() {
        return submittime;
    }

    public void setSubmittime(String submittime) {
        this.submittime = submittime;
    }
}
