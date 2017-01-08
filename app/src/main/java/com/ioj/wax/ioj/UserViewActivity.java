package com.ioj.wax.ioj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.next.tagview.TagCloudView;
import okhttp3.Call;

public class UserViewActivity extends AppCompatActivity{
    Toolbar mToolbar;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    UserInfo mUserInfo;
    List<String> acnum = new ArrayList<>();
    List<String> chnum = new ArrayList<>();
    TagCloudView acView;
    TagCloudView chView;
    CircleImageView pic;
    Bitmap pic_bitmap;
    String cookies;
    String username;
    String rk;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mUserInfo = (UserInfo)(getIntent().getSerializableExtra("userinfo"));//要显示的
        cookies = getIntent().getStringExtra("cookies");
        username = getIntent().getStringExtra("username");//这个username是登陆的账号
        rk = getIntent().getStringExtra("rank");
        mToolbar = (Toolbar)findViewById(R.id.user_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.user_collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setTitle("Profile");
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#FFFFFF"));
        //设置头像
        pic = (CircleImageView)findViewById(R.id.user_pic);
        ((TextView)findViewById(R.id.user_username)).setText(mUserInfo.getUsername());
        new Thread(new Runnable() {
            @Override
            public void run() {
                pic_bitmap = LoadData.getHttpBitmap(UserViewActivity.this,mUserInfo.getPicurl());
                mHandler.obtainMessage(2).sendToTarget();
            }
        }).start();
        findViewById(R.id.user_shareac).setVisibility(mUserInfo.isLogin()?View.VISIBLE:View.INVISIBLE);
        findViewById(R.id.user_shareac).setOnClickListener(shareall);
        if(rk!=null){
            ((TextView)findViewById(R.id.user_rk)).setText(rk+"\nRK");
        }else{
            new loadRank().start();
        }
        new loadMyAc().start();
    }
    private View.OnClickListener shareall = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new ShareAllCode(UserViewActivity.this,mToolbar,acnum,cookies,username).share();
        }
    };
    class loadRank extends Thread{
        @Override
        public void run() {
            // <span class = "des">OJ排名:</span><span class ="content">14</span>
            //http://acm.swust.edu.cn/problem/userprofile/wwhhff11/
            OkHttpUtils.get().url("http://acm.swust.edu.cn/problem/userprofile/"+mUserInfo.getUsername()+"/")
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    ((TextView)findViewById(R.id.user_rk)).setText("No."+
                            LoadData.getStrMid(response,"<span class = \"des\">OJ排名:</span><span class =\"content\">",
                                    "</span>")+"\nRK");
                }
            });
        }
    }
    class loadMyAc extends Thread{
        @Override
        public void run() {
            try {
                LoadData.getMyAc(acnum,chnum,mUserInfo.getUsername());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mHandler.obtainMessage(1).sendToTarget();
        }
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    TextView tv_acnum = (TextView)findViewById(R.id.user_acnum);
                    ((TextView)findViewById(R.id.user_ac)).setText(acnum.size()+"\nAC");
                    tv_acnum.setText("Accepted("+acnum.size()+"):");
                    TextView tv_chnum = (TextView)findViewById(R.id.user_chnum);
                    ((TextView)findViewById(R.id.user_ch)).setText(chnum.size()+"\nCH");
                    tv_chnum.setText("Challenging("+chnum.size()+"):");
                    acView = (TagCloudView) findViewById(R.id.user_tag_ac);
                    acView.setTags(acnum);
                    acView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
                        @Override
                        public void onTagClick(int position) {
                            Intent intent = new Intent(UserViewActivity.this,ProblemsView.class);
                            intent.putExtra("title","");
                            intent.putExtra("id",acnum.get(position));
                            intent.putExtra("cookies",cookies);
                            intent.putExtra("contestid","0");
                            intent.putExtra("username",username);
                            startActivityForResult(intent,2);
                        }
                    });
                    chView = (TagCloudView) findViewById(R.id.user_tag_ch);
                    chView.setTags(chnum);
                    chView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
                        @Override
                        public void onTagClick(int position) {
                            Intent intent = new Intent(UserViewActivity.this,ProblemsView.class);
                            intent.putExtra("title","");
                            intent.putExtra("id",chnum.get(position));
                            intent.putExtra("cookies",cookies);
                            intent.putExtra("contestid","0");
                            intent.putExtra("username",username);
                            startActivityForResult(intent,2);
                        }
                    });
                    break;
                case 2:
                    pic.setImageBitmap(pic_bitmap);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2){
            Intent ResultIntent = new Intent();
            setResult(2, ResultIntent);
            finish();
        }
    }
}
