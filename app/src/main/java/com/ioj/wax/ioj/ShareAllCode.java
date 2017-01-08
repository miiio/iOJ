package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import thereisnospon.codeview.Code;

public class ShareAllCode {
    private Activity activity;
    private View view;
    private List<String> acnum = new ArrayList<>();
    private String cookies;
    private String notes;
    private LoadingDialog ld;
    private int prog;
    private String username;
    private int succ,fail;
    public ShareAllCode(Activity activity, View view, List<String> acnum, String cookies, String username) {
        this.activity = activity;
        this.view = view;
        this.acnum = acnum;
        this.cookies = cookies;
        this.username = username;
    }
    public void share(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("一键分享");
        builder.setMessage("您确定将所有代码分享吗？这将花费较长时间。");
        builder.setNegativeButton("No",null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(activity);
                final EditText mEditText = new EditText(activity);
                mEditText.setSingleLine(false);
                builder2.setTitle("一键分享");
                builder2.setMessage("您可以输入一些说明(可空):");
                builder2.setView(mEditText,60,0,60,0);
                builder2.setNegativeButton("No",null);
                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notes = mEditText.getText().toString();
                        ld = new LoadingDialog(activity);
                        ld.setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                                .setLoadingText("提交中...0/"+acnum.size()).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sharey();
                            }
                        }).start();
                    }
                }).show();

            }
        }).show();
    }
    private void sharey(){
        succ=0;fail=0;
        prog=0;
        for(int i=0;i<acnum.size();i++){
            String acid,code;
            title mtitle;
            try {
                acid = LoadData.getAcId(acnum.get(i), username);
                mtitle = new title("");
                code = LoadData.getMyCode(acid, cookies, username, acnum.get(i), mtitle);
                code = code.replace("\\","\\\\");
                code = code.replace("'","\\'");
                OkHttpUtils.post().url(CodeShare.host+"sharecode.php")
                        .addParams("acid",acid)
                        .addParams("prbid",acnum.get(i))
                        .addParams("prbtitle",mtitle.getTitle())
                        .addParams("code", code)
                        .addParams("contributor",username)
                        .addParams("notes",notes)
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        fail++;
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if(response.equals("succeed ")) {
                            succ++;
                        }
                        else{
                            fail++;
                        }
                    }
                });
            } catch (Exception e) {
                fail++;
                e.printStackTrace();
            }
            prog++;
            mHandler.obtainMessage(1).sendToTarget();
        }
        mHandler.obtainMessage(2).sendToTarget();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ld.setLoadingText("提交中..."+prog+"/"+acnum.size());
                    break;
                case 2:
                    ld.close();
                    Snackbar sBar = Snackbar.make(view,"一键分享完成，"+fail+"个失败",Snackbar.LENGTH_SHORT);
                    View sv = sBar.getView();
                    ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                    sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                    sBar.show();
                    break;
            }
        }
    };
}
