package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public class CodeViewThread extends Thread{
    private String prbid;
    private String username;
    private String cookies;
    private View view;
    private Activity activity;
    CodeViewThread(String prbid, String username,String cookies, View view, Activity activity){
        this.prbid = prbid;
        this.cookies = cookies;
        this.username = username;
        this.view = view;
        this.activity = activity;
    }
    @Override
    public void run() {
        String acid = null;
        try {
            acid = LoadData.getAcId(prbid,username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(acid == null){
            Snackbar sBar = Snackbar.make(view,"找不到该题目的AC代码",Snackbar.LENGTH_SHORT);
            View sv = sBar.getView();
            ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
            sv.setBackgroundColor(Color.parseColor("#00BCD4"));
            sBar.show();
        }else {
            Intent intent = new Intent(activity, CodeViewActivity.class);
            intent.putExtra("acid", acid);
            intent.putExtra("cookies", cookies);
            intent.putExtra("username",username);
            activity.startActivity(intent);
        }
    }
}