package com.ioj.wax.ioj;

import android.graphics.Bitmap;

public class Ranklist_p{
    String name;
    Bitmap pic;
    String maxim;
    String solved,submit,rank;
    public Ranklist_p(String name, Bitmap pic, String maxim, String solved, String submit, String rank) {
        this.name = name;
        this.pic = pic;
        this.maxim = maxim;
        this.solved = solved;
        this.submit = submit;
        this.rank ="No."+ rank;
        //if(maxim.isEmpty()) {this.maxim="我不想说话.";}
    }
}