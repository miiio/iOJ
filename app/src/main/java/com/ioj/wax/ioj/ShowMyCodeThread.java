package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.ioj.wax.ioj.LoadData.LoadStatusInfoData_o;

public class ShowMyCodeThread{
    private Activity activity;
    private View view;
    private String prbId;
    private String cookies;
    private String username;
    private List<StatusInfo> mStatusData = new ArrayList<StatusInfo>();
    private String[] item;
    ShowMyCodeThread(Activity activity,View view,String prbId, String cookies, String username){
        this.activity = activity;
        this.view = view;
        this.prbId = prbId;
        this.cookies = cookies;
        this.username = username;
    }
    public void selectView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LoadStatusInfoData_o(mStatusData,1,true,username,prbId,"");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.obtainMessage(111).sendToTarget();
            }
        }).start();
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==111){
                if(mStatusData.size()==0){
                    Snackbar sBar = Snackbar.make(view,"您未提交过该题！",Snackbar.LENGTH_SHORT);
                    View sv = sBar.getView();
                    ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                    sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                    sBar.show();
                }else{
                    item = new String[mStatusData.size()];
                    for(int i=0;i<mStatusData.size();i++){
                        StatusInfo mStatusInfo = mStatusData.get(i);
                        item[i] = mStatusInfo.getAcid()+" "+LoadData.getResult(mStatusInfo.getResult())
                                    +" "+mStatusInfo.getTime()+"MS "+mStatusInfo.getLenght()+"B";
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("找到"+mStatusData.size()+"条记录");
                    builder.setItems(item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(activity, CodeViewActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("statusInfo",mStatusData.get(which));
                            intent.putExtras(bundle);
                            intent.putExtra("acid", mStatusData.get(which).getAcid());
                            intent.putExtra("cookies", cookies);
                            intent.putExtra("username",username);
                            activity.startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancel",null);
                    builder.show();
                }
            }
            super.handleMessage(msg);
        }
    };
}
