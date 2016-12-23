package com.ioj.wax.ioj;


public class Problems_p{
    String title;
    String id;
    String isAc;
    int diffcult;
    String contestId;
    String score;
    public Problems_p(String id, String title, String isAc, int diffcult,String contestId,String score) {
        this.id = id;
        this.title = title;
        this.isAc = isAc;
        this.diffcult = diffcult;
        this.contestId = contestId;
        this.score = score;
    }
    public Problems_p(String id, String title, String isAc, int diffcult,String contestId) {
        this.id = id;
        this.title = title;
        this.isAc = isAc;
        this.diffcult = diffcult;
        this.contestId = contestId;
    }
}