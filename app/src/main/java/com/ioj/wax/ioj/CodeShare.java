package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class CodeShare {
    public static final String host = "http://laobo9.top/ioj/";
    private Activity activity;
    private View view;
    private String username;
    CodeShare(Activity activity,View view, String username){
        this.activity=activity;
        this.view=view;
        this.username = username;
    }
    public void shareCode(final String acid, final String prbid, final String prbTitle,String code, final String contributor){
        final String[] fcode = {code};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Share");
        final EditText mEditText = new EditText(activity);
        mEditText.setSingleLine(false);
        builder.setMessage("您可以输入一些说明(可空):");
        builder.setView(mEditText,60,0,60,0);
        builder.setNegativeButton("cancel",null);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final LoadingDialog ld = new LoadingDialog(activity);
                ld.setLoadingText("提交中")
                        .setSuccessText("分享成功")//显示加载成功时的文字
                        .setFailedText("分享失败")
                        .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                        .show();
                fcode[0] = fcode[0].replace("\\","\\\\");
                fcode[0] = fcode[0].replace("'","\\'");
                OkHttpUtils.post().url(host+"sharecode.php")
                        .addParams("acid",acid)
                        .addParams("prbid",prbid)
                        .addParams("prbtitle",prbTitle)
                        .addParams("code", fcode[0])
                        .addParams("contributor",contributor)
                        .addParams("notes",mEditText.getText().toString())
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ld.loadFailed();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if(response.equals("succeed ")) {
                            ld.loadSuccess();
                        }else{
                            ld.loadFailed();
                        }
                    }
                });
            }
        }).show();
    }
    public void findCode(String prbid){
        final LoadingDialog ld = new LoadingDialog(activity);
        ld.setLoadingText("加载中")
                .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                .show();
        OkHttpUtils.get().url(host+"findcode.php")
                .addParams("prbid",prbid)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Snackbar sBar = Snackbar.make(view,"连接服务器失败！",Snackbar.LENGTH_SHORT);
                View sv = sBar.getView();
                ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                sBar.show();
            }

            @Override
            public void onResponse(String response, int id) {
                ld.close();
                JSONArray jsonArr = null;
                try {
                    jsonArr = new JSONArray(response);
                    if(jsonArr.length()==0){
                        Snackbar sBar = Snackbar.make(view,"暂未有人分享此题...",Snackbar.LENGTH_SHORT);
                        View sv = sBar.getView();
                        ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                        sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                        sBar.show();
                    }else {
                        showShareList(jsonArr);
                    }
                } catch (JSONException e) {
                    Snackbar sBar = Snackbar.make(view,"加载失败！",Snackbar.LENGTH_SHORT);
                    View sv = sBar.getView();
                    ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                    sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                    sBar.show();
                    e.printStackTrace();
                }
            }
        });

    }
    private void showShareList(JSONArray jsonArr) throws JSONException {
        ArrayList<ShareCodeInfo> mShareCodeData = new ArrayList<>();
        for(int i = 0;i<jsonArr.length();i++) {
            JSONObject jsonobj2 = jsonArr.getJSONObject(i);
            mShareCodeData.add(new ShareCodeInfo(jsonobj2.getString("notes"),
                    jsonobj2.getString("id"),
                    jsonobj2.getString("acid"),
                    jsonobj2.getString("prbid"),
                    jsonobj2.getString("prbtitle"),
                    jsonobj2.getString("code"),
                    jsonobj2.getString("contributor"),
                    jsonobj2.getString("submittime"),
                    jsonobj2.getString("likenum")));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("找到"+jsonArr.length()+"条记录");
        final RecyclerView mRecyclerView = new RecyclerView(activity);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        CommonAdapter adapt = new CommonAdapter<ShareCodeInfo>(activity,R.layout.sharecodeshow,mShareCodeData){
            @Override
            protected void convert(ViewHolder holder, final ShareCodeInfo shareCodeInfo, int position) {
                holder.setText(R.id.sc_p,position+1+"");
                holder.setText(R.id.sc_contributor,shareCodeInfo.getContributor());
                holder.setText(R.id.sc_like,shareCodeInfo.getLike());
                holder.setText(R.id.sc_notes,shareCodeInfo.getNotes());
                holder.setText(R.id.sc_time,shareCodeInfo.getSubmittime());
                holder.setOnClickListener(R.id.sc_main, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, SCodeViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("info",shareCodeInfo);
                        intent.putExtras(bundle);
                        intent.putExtra("username",username);
                        activity.startActivity(intent);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(adapt);
        builder.setView(mRecyclerView);
        builder.setNegativeButton("cancel",null).show();
    }
}
