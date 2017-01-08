package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private final static int Login_REQUEST_CODE=1;
    private AppBarLayout appbar;
    private EditText input_user;
    private  EditText input_password;
    UserInfo info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar mToolbar = (Toolbar)findViewById(R.id.login_toolbar);
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        mToolbar.setTitle("登陆");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        appbar = (AppBarLayout)findViewById(R.id.login_appbar);
        Button btnlogin=(Button)findViewById(R.id.buttonLogin);
        input_user = (EditText) findViewById(R.id.editTextName);
        input_password = (EditText) findViewById(R.id.editTextPassword);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String ret = null;
                        ret = LoadData.Login(input_user.getText().toString(),input_password.getText().toString());
                        if(ret==null){
                            Snackbar sBar = Snackbar.make(appbar,"登陆失败,账号或密码错误!或服务器炸了(つд⊂)",Snackbar.LENGTH_SHORT);
                            View sv = sBar.getView();
                            ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                            sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                            sBar.show();
                        }else{
                            try {
                                //pic = getHttpBitmap(getPic(ret));
                                info = new UserInfo();
                                LoadData.GetUserInfo(ret,info);
                                info.setLogin(true);
                                info.setUsername( input_user.getText().toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Intent ResultIntent=new Intent();
                            //储存登陆账号密码。。
                            info.setPassword(input_password.getText().toString());
                            info.setUsername(input_user.getText().toString());
                            ObjectSaveUtils.saveObject(LoginActivity.this,"UserInfo",info);
                            ResultIntent.putExtra("info",info);
                            setResult(Login_REQUEST_CODE, ResultIntent);
                            finish();
                        }

                    }
                }).start();

            }

        });
    }


}
