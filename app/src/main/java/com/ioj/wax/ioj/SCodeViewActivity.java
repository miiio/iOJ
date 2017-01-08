package com.ioj.wax.ioj;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wx.goodview.GoodView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.Call;
import thereisnospon.codeview.CodeView;

public class SCodeViewActivity extends AppCompatActivity {
    private ShareCodeInfo mShareCodeInfo;
    private String username;
    private ImageView likeView;
    private boolean islike;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Pbv_Theme);
        setContentView(R.layout.activity_scodeview);
        islike=false;
        mShareCodeInfo = (ShareCodeInfo)(getIntent().getSerializableExtra("info"));
        username = getIntent().getStringExtra("username");
        ((TextView)findViewById(R.id.scode_title)).setText(mShareCodeInfo.getPrbtitle());
        findViewById(R.id.scode_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((CodeView)findViewById(R.id.scode_codeview)).showCode(mShareCodeInfo.getCode());
        final GoodView goodView = new GoodView(SCodeViewActivity.this);
        likeView = (ImageView) findViewById(R.id.scode_like);
        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(!islike) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpUtils.get().url(CodeShare.host+"like_it.php")
                                    .addParams("username",username).addParams("acid",mShareCodeInfo.getAcid())
                                    .build().execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Snackbar sBar = Snackbar.make(likeView,"连接服务器失败",Snackbar.LENGTH_SHORT);
                                    View sv = sBar.getView();
                                    ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                                    sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                                    sBar.show();
                                }
                                @Override
                                public void onResponse(String response, int id) {
                                    likeView.setImageResource(R.drawable.like_h);
                                    goodView.setDistance(150);
                                    goodView.setText("+1");
                                    goodView.show(v);
                                }
                            });
                        }
                    }).start();
                }else{
                    likeView.setClickable(false);
                    likeView.setImageResource(R.drawable.like_h);
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                initlike();
            }
        }).start();
    }
    private void initlike(){
        OkHttpUtils.get().url(CodeShare.host+"like_record.php")
                .addParams("username",username)
                .addParams("acid",mShareCodeInfo.getAcid())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        likeView.setClickable(false);
                        likeView.setImageResource(R.drawable.like_w);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        likeView.setClickable(true);
                        try {
                            JSONArray jsonArr = new JSONArray(response);
                            if(jsonArr.length()>0){
                                islike=true;
                                likeView.setClickable(false);
                                likeView.setImageResource(R.drawable.like_h);
                            }else{
                                islike=false;
                                likeView.setImageResource(R.drawable.like_w);
                            }
                        } catch (JSONException e) {
                            likeView.setClickable(false);
                            likeView.setImageResource(R.drawable.like_w);
                        }
                    }
                });
    }
}
