package com.ioj.wax.ioj;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import thereisnospon.codeview.CodeView;
import thereisnospon.codeview.CodeViewTheme;

public class CodeViewActivity extends AppCompatActivity implements View.OnClickListener {
    private String username;
    private String acid;
    private String cookies;
    private String Code;
    private StatusInfo mStatusInfo;
    private CodeView mCodeView;
    private Toolbar mToolbar;
    private title mTitle;
    private FloatingActionButton share_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置toolbar样式
        setTheme(R.style.Pbv_Theme);
        setContentView(R.layout.activity_codeview);
        //获取intent传入信息
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        acid = intent.getStringExtra("acid");
        cookies = intent.getStringExtra("cookies");
        mStatusInfo = (StatusInfo) intent.getSerializableExtra("statusInfo");
        //初始化CodeView控件
        mCodeView = (CodeView)findViewById(R.id.CodeView_mainview);
        mCodeView.setTheme(CodeViewTheme.ARDUINO_LIGHT).fillColor();
        //设置标题
        mToolbar = (Toolbar)findViewById(R.id.CodeView_toolbar);
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        mToolbar.setTitle("...");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置分享按钮
        share_btn = (FloatingActionButton)findViewById(R.id.floatbtn_sharemycode);
        share_btn.setOnClickListener(this);
        new LoadCode().start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.floatbtn_sharemycode:
                if(!mStatusInfo.getResult().equals("0")){
                    Snackbar sBar = Snackbar.make(mToolbar,"请分享accept后的代码！",Snackbar.LENGTH_SHORT);
                    View sv = sBar.getView();
                    ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                    sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                    sBar.show();
                }else {
                    CodeShare cs = new CodeShare(CodeViewActivity.this, mToolbar,username);
                    cs.shareCode(acid, mStatusInfo.getProblemid(), mTitle.getTitle(), Code, username);
                }
                break;
        }
    }

    class LoadCode extends Thread{
        @Override
        public void run() {
            try {
                mTitle = new title("null");
                Code = LoadData.getMyCode(acid,cookies,username,mStatusInfo.getProblemid(),mTitle);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHandler.obtainMessage(1).sendToTarget();
        }
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                if(Code!=null){
                    mCodeView.showCode(Code);
                    getSupportActionBar().setTitle(mTitle.getTitle());
                }else{
                    mCodeView.showCode("获取失败");
                }

            }
        }
    };
}
