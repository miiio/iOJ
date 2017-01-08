package com.ioj.wax.ioj;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import thereisnospon.codeview.CodeViewTheme;

public class NewsViewActivity extends AppCompatActivity{
    private Toolbar mToolbar;
    private String title;
    private String id;
    private TextView tv_title;
    private TextView tv_publisher;
    private TextView tv_date;
    private TextView tv_pv;
    private TextView tv_main;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsview);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        id = intent.getStringExtra("id");
        mToolbar = (Toolbar)findViewById(R.id.NewsView_toolbar);
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        mToolbar.setTitle("News");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //获取控件
        tv_publisher = (TextView)findViewById(R.id.newsview_publisher);
        tv_date = (TextView)findViewById(R.id.newsview_publishdate);
        tv_pv = (TextView)findViewById(R.id.newsview_publishpv);
        tv_main = (TextView)findViewById(R.id.newsview_c);
        tv_title = (TextView)findViewById(R.id.newsview_title);
        tv_title.setText(title);
        new LoadNews().start();
    }
    class LoadNews extends Thread{
        @Override
        public void run() {
            OkHttpUtils.get()
                    .url("http://acm.swust.edu.cn/news/article/"+id+"/")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            tv_publisher.setText("Publisher : "+LoadData.getStrMid(response,"Publisher : ","</p>"));
                            tv_pv.setText("PV : "+LoadData.getStrMid(response,"PV : ","</p>"));
                            tv_date.setText("Time : "+LoadData.getStrMid(response,"Time : ","</p>"));
                            tv_main.setText(Html.fromHtml(LoadData.getStrMid(response,"<div class=\"news-content\">","</div>")));
                        }
                    });
        }
    }

}
