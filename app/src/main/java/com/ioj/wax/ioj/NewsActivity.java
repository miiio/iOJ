package com.ioj.wax.ioj;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class NewsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ArrayList<NewsInfo> mData = new ArrayList<>();
    private CommonAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newslist);
        //设置标题
        mToolbar = (Toolbar)findViewById(R.id.CodeView_toolbar);
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        mToolbar.setTitle("ALL OF THE OJ NEWS");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView = (RecyclerView)findViewById(R.id.news_recyclerview);
        adapter = new CommonAdapter<NewsInfo>(this,R.layout.newsshow,mData ){
            @Override
            protected void convert(ViewHolder holder, NewsInfo newsInfo, int position) {
                holder.setText(R.id.newslist_title,newsInfo.getNewstitle());
                holder.setText(R.id.newslist_date,newsInfo.getDate());
                holder.setText(R.id.newslist_name,newsInfo.getName());
            }
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        new LoadNewsData().start();
    }
    class LoadNewsData extends Thread{
        @Override
        public void run() {
            OkHttpUtils.get()
                    .url("http://acm.swust.edu.cn/news/list/")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            String reg1 = "<li><a href=\"/news/article/(.*)/\">(.*)</a></li>";
                            Pattern pattern = Pattern.compile(reg1);
                            Matcher matcher = pattern.matcher(response);
                            while (matcher.find()) {
                                String mtitle = matcher.group(2);
                                String mid = matcher.group(1);
                                String mname = LoadData.getStrMid(response, mtitle + "</a></li>\n" +
                                        "                        <li>", "</li>");
                                String mdate = LoadData.getStrMid(response, mname + "</li>\n" +
                                        "                        <li>", "</li>");
                                mData.add(new NewsInfo(mtitle, mid, mname, mdate));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

}
