package com.ioj.wax.ioj;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;

import java.io.IOException;

import thereisnospon.codeview.CodeView;
import thereisnospon.codeview.CodeViewTheme;

public class CodeViewActivity extends AppCompatActivity {
    private String username;
    private String acid;
    private String cookies;
    private String Code;

    private CodeView mCodeView;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codeview);
        //获取intent传入信息
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        acid = intent.getStringExtra("acid");
        cookies = intent.getStringExtra("cookies");
        //初始化CodeView控件
        mCodeView = (CodeView)findViewById(R.id.CodeView_mainview);
        mCodeView.setTheme(CodeViewTheme.ARDUINO_LIGHT).fillColor();
        //设置标题
        mToolbar = (Toolbar)findViewById(R.id.CodeView_toolbar);
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        mToolbar.setTitle("CodeView-"+acid);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new LoadCode().start();
    }
    class LoadCode extends Thread{
        @Override
        public void run() {
            try {
                Code = LoadData.getMyCode(acid,cookies,username,"1",new title("1"));
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
                    Code = Code.replace("\r","\r\n");
                    Code = Code.replace("&lt;","<");
                    Code = Code.replace("&gt;",">");
                    Code = Code.replace("&nbsp;"," ");
                    Code = Code.replace("&amp;"," ");
                    mCodeView.showCode(Code);
                }else{
                    mCodeView.showCode("获取失败");
                }

            }
        }
    };
}
