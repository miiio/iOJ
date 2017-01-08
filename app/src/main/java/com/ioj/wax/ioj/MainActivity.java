package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import okhttp3.Call;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private long exitTime = 0;
    private final static int Login_REQUEST_CODE=1;
    private HomeFragment mHomeFragment;
    private RanklistFragment mRanklistFragment;
    private StatusFragment mStatusFragment;
    private ProblemsFragment mProblemsFragment;
    private ContestFragment mContestFragment;
    private NavigationView mNavigationView;
    private ImageView btn_login;
    private TextView TextView__username;
    private TextView TextView_maxin;
    private Bitmap pic_bitmap;
    public Toolbar mToolbar;
    public ActionBarDrawerToggle mDrawerToggle;
    public UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserInfo = new UserInfo();
        dl=(DrawerLayout)findViewById(R.id.id_drawer_layout);
        mToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        mToolbar.setTitle("Home");
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this,dl,mToolbar,R.string.open,R.string.close);
        mDrawerToggle.syncState();
        dl.setDrawerListener(mDrawerToggle);
        //设置点击事件

        setDefaultFragment();
        setshadow();
        //设置默认homeFragment 

        mNavigationView = (NavigationView)findViewById(R.id.id_nv_menu);
        mNavigationView.setNavigationItemSelectedListener(mNavigationItemSelectedListener);
        //设置抽屉选择事件
        View view = mNavigationView.getHeaderView(0);
        btn_login = (ImageView)view.findViewById(R.id.id_usericon);
        TextView__username = (TextView)view.findViewById(R.id.id_username);
        TextView_maxin = (TextView)view.findViewById(R.id.id_mix);
        //账号切换事件
        view.findViewById(R.id.switch_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, Login_REQUEST_CODE);
            }
        });
        //头像点击事件
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUserInfo.isLogin() == false) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, Login_REQUEST_CODE);
                }else{
                    Intent intent = new Intent(MainActivity.this, UserViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userinfo",mUserInfo);
                    intent.putExtras(bundle);
                    intent.putExtra("username",mUserInfo.getUsername());
                    intent.putExtra("cookies",mUserInfo.getCookie());
                    //startActivity(intent);
                    startActivityForResult(intent,2);
                }

            }
        });
        //从文件读取UserInfo信息
        mUserInfo.setLogin(false);
        mUserInfo = (UserInfo)ObjectSaveUtils.getObject(MainActivity.this,"UserInfo");
        if(mUserInfo!=null && mUserInfo.isLogin()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String t = LoadData.Login(mUserInfo.getUsername(),mUserInfo.getPassword());
                    if(t!=null){
                        mUserInfo.setCookie(t);
                        try {
                            LoadData.GetUserInfo(t,mUserInfo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mUserInfo.setLogin(true);
                        pic_bitmap = LoadData.getHttpBitmap(MainActivity.this,mUserInfo.getPicurl());
                        mHandler.obtainMessage(85).sendToTarget();
                    }else{
                        Snackbar sBar = Snackbar.make(dl,"登陆失败,账号或密码错误!或服务器炸了(つд⊂)",Snackbar.LENGTH_SHORT);
                        View sv = sBar.getView();
                        ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                        sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                        sBar.show();
                    }
                }
            }).start();
        }else{
            mUserInfo = new UserInfo();
        }
    }

    //负责更新UI
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==85)
                if(mUserInfo!=null && mUserInfo.isLogin()){
                    Snackbar sBar = Snackbar.make(dl,"欢迎回来，"+mUserInfo.getUsername()+"！",Snackbar.LENGTH_SHORT);
                    View sv = sBar.getView();
                    ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                    sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                    sBar.show();
                }
            btn_login.setImageBitmap(pic_bitmap);
            TextView__username.setText(mUserInfo.getUsername());
            TextView_maxin.setText(mUserInfo.getMaxin());
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Login_REQUEST_CODE) {
            mUserInfo = new UserInfo();
            mUserInfo =(UserInfo) data.getExtras().getSerializable("info");
            TextView__username.setText(mUserInfo.getUsername());
            TextView_maxin.setText(mUserInfo.getMaxin());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    pic_bitmap = LoadData.getHttpBitmap(MainActivity.this,mUserInfo.getPicurl());
                    mHandler.obtainMessage(85).sendToTarget();
                }
            }).start();

        }else if(resultCode == 2){
            openStatus();
        }
    }

    private void setDefaultFragment() {

        FragmentManager FragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = FragmentManager.beginTransaction();

        mHomeFragment = new HomeFragment();
        transaction.replace(R.id.id_content, mHomeFragment);
        transaction.commit();
    }
    public void openStatus(){
        FragmentManager FragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = FragmentManager.beginTransaction();
        if (mStatusFragment == null) {
            mStatusFragment = new StatusFragment();
        }
        // 使用当前Fragment的布局替代id_content的控件
        setBarTitle("Status");
        transaction.replace(R.id.id_content, mStatusFragment);
        transaction.commit();
        mNavigationView.setCheckedItem(R.id.nav_status);
    }
    private void setBarTitle(String title){
        mToolbar.setTitle(title);
    }
    private void unshadow()
    {
    }
    private void setshadow()
    {
        //
    }
    private NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener =new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager FragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = FragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.nav_ranklist:
                    if (mRanklistFragment == null) {
                        mRanklistFragment = new RanklistFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Ranklist");
                    unshadow();
                    transaction.replace(R.id.id_content, mRanklistFragment);
                    item.setChecked(true);
                    break;

                case R.id.nav_status:
                    if (mStatusFragment == null) {
                        mStatusFragment = new StatusFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Status");
                    setshadow();
                    transaction.replace(R.id.id_content, mStatusFragment);
                    item.setChecked(true);
                    break;
                case R.id.nav_problems:
                    if (mProblemsFragment == null) {
                        mProblemsFragment = new ProblemsFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Problems");
                    setshadow();
                    transaction.replace(R.id.id_content, mProblemsFragment);
                    item.setChecked(true);
                    break;

                case R.id.nav_contest:
                    if (mContestFragment == null) {
                        mContestFragment = new ContestFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Contest");
                    setshadow();
                    transaction.replace(R.id.id_content, mContestFragment);
                    item.setChecked(true);
                    break;
                case R.id.nav_home:
                    if (mHomeFragment == null) {
                        mHomeFragment = new HomeFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Home");
                    setshadow();
                    transaction.replace(R.id.id_content, mHomeFragment);
                    item.setChecked(true);
                    break;
                case R.id.nav_exit:
                    iojexit();
                    return false;
                case R.id.nav_about:
                    startActivity(new Intent(MainActivity.this,SettingActivity.class));
                    break;

            }
            transaction.commit();
            //事务提交
            dl.closeDrawers();
            return false;
        }
    };
    public void iojexit(){
        finish();
    }
    class LoadSourceCode extends Thread{
        @Override
        public void run() {
            String acid=null,id=null,code=null;
            title mtitle = null;
            String[] prbid={"0015", "0020", "0021", "0022", "0023", "0025", "0026", "0027", "0028", "0029", "0030", "0031", "0032", "0033", "0034", "0035", "0036", "0037", "0038", "0039", "0040", "0041", "0042", "0044", "0045", "0046", "0047", "0048", "0056", "0066", "0067", "0068", "0069", "0070", "0072", "0074", "0076", "0077", "0078", "0080", "0082", "0084", "0086", "0109", "0116", "0117", "0119", "0129", "0133", "0139", "0140", "0141", "0142", "0143", "0144", "0147", "0148", "0149", "0150", "0152", "0160", "0161", "0162", "0167", "0170", "0171", "0172", "0179", "0189", "0190", "0192", "0203", "0220", "0221", "0226", "0227", "0232", "0242", "0254", "0256", "0259", "0273", "0276", "0277", "0278", "0279", "0280", "0281", "0282", "0284", "0285", "0287", "0288", "0289", "0290", "0291", "0292", "0293", "0294", "0295", "0297", "0299", "0308", "0310", "0311", "0312", "0313", "0314", "0315", "0316", "0318", "0319", "0320", "0321", "0331", "0333", "0334", "0335", "0336", "0341", "0342", "0361", "0405", "0413", "0416", "0423", "0424", "0426", "0427", "0430", "0434", "0435", "0437", "0440", "0442", "0446", "0448", "0470", "0471", "0480", "0484", "0485", "0489", "0494", "0507", "0509", "0524", "0533", "0540", "0541", "0554", "0557", "0558", "0559", "0563", "0566", "0571", "0580", "0597", "0599", "0600", "0601", "0605", "0606", "0607", "0612", "0613", "0614", "0615", "0616", "0617", "0618", "0619", "0620", "0622", "0623", "0625", "0642", "0646", "0667", "0695", "0700", "0706", "0736", "0740", "0760", "0777", "0832", "0833", "0834", "0860", "0921", "0922", "0941", "0942", "0953", "0954", "0956", "0957", "0959", "0961", "0962", "0963", "0966", "0967", "1014", "1016", "1027", "1028", "1035", "1036", "1038", "1039", "1044", "1046", "1067", "1090", "1099", "1101", "1103", "1107", "1109", "1110", "1114", "1121", "1139", "1156", "1157", "1158", "1159", "1160", "1161", "1162", "1163", "1164", "1165", "1166", "1167", "1168", "1169", "1170", "1171", "1172", "1173", "1174", "1175", "1176", "1177", "1178", "1179", "1180", "1181", "1182", "1183", "1184", "1185", "1186", "1187", "1188", "1189", "1190", "1191", "1192", "1193", "1194", "1195", "1210", "1215" };
            for(int i = 0; i<prbid.length;i++) {
                id = prbid[i];
                try {
                    acid = LoadData.getAcId(id, mUserInfo.getUsername());
                    mtitle = new title("");
                    code = LoadData.getMyCode(acid, mUserInfo.getCookie(), mUserInfo.getUsername(), id, mtitle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                CodeShare.shareCode(acid, id, mtitle.getTitle(), code, "5120160446", "", new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        System.out.println(response);
//                    }
//                });
                //code = Html.fromHtml(code).toString();
                //f.writeTxtToFile(code,"/sdcard/Test/",id+"-"+mtitle.getTitle()+".txt");
            }
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 1500){
                Snackbar sBar = Snackbar.make(dl,"再按一次退出程序",Snackbar.LENGTH_SHORT);
                View sv = sBar.getView();
                ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                sBar.show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
