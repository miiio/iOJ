package com.ioj.wax.ioj;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.toolbar_theme);
        setContentView(R.layout.activity_setting);
        Toolbar mToolbar = (Toolbar)findViewById(R.id.setting_toolbar);
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        mToolbar.setTitle("设置");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.setting_clear).setOnClickListener(this);
        findViewById(R.id.setting_mail).setOnClickListener(this);
        findViewById(R.id.setting_sourcecode).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_clear:
                LoadData.clearImageCache(SettingActivity.this);
                Snackbar sBar = Snackbar.make(v,"清除缓存成功！",Snackbar.LENGTH_SHORT);
                View sv = sBar.getView();
                ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                sBar.show();
                break;
            case R.id.setting_mail:
                Intent data=new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:laobo3515@gmail.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "Bug反馈");
                startActivity(data);
                break;
            case R.id.setting_sourcecode:
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("Open Source Licenses");
                builder.setMessage("thereisnospon.codeview:codeview:0.3.1\n" +
                        "com.youth.banner:banner:1.4.6\n" +
                        "com.github.bumptech.glide:glide:3.5.2\n" +
                        "com.squareup.okhttp3:okhttp:3.5.0\n" +
                        "com.zhy:okhttputils:2.6.2\n" +
                        "com.zhy:base-rvadapter:3.0.3\n" +
                        "com.github.ForgetAll:LoadingDialog:v1.0.4\n" +
                        "com.github.kingideayou:tagcloudview:1.0.2\n" +
                        "com.getbase:floatingactionbutton:1.10.1\n" +
                        "com.wx.goodview:goodview:1.0.0\n" +
                        "highLightText").show();
                break;
        }
    }
}
