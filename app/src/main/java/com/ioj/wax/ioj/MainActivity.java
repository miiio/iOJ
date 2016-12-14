package com.ioj.wax.ioj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.InterpolatorRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


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
    public UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserInfo = new UserInfo();
        dl=(DrawerLayout)findViewById(R.id.id_drawer_layout);
        ImageView iv_menu = (ImageView) findViewById(R.id.Image_menu);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dl.openDrawer(Gravity.LEFT,true);
            }
        });
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
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(intent,Login_REQUEST_CODE);
                //startActivity(new Intent(MainActivity.this,ProblemsView.class));
            }
        });

    }
    //负责更新UI
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==85)
                btn_login.setImageBitmap(pic_bitmap);
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
                    pic_bitmap = initRankData.getHttpBitmap(mUserInfo.getPicurl());
                    mHandler.obtainMessage(85).sendToTarget();
                }
            }).start();

        }
    }

    private void setDefaultFragment() {

        FragmentManager FragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = FragmentManager.beginTransaction();

        mHomeFragment = new HomeFragment();
        transaction.replace(R.id.id_content, mHomeFragment);
        transaction.commit();
    }
    private void setBarTitle(String title){
        TextView Ttextview = (TextView)findViewById(R.id.TextView_title);
        Ttextview.setText(title);
    }
    private void unshadow()
    {
        ImageView image=(ImageView) findViewById(R.id.shadow_view);
        ViewGroup.LayoutParams para;
        para = image.getLayoutParams();
        para.height=0;
        image.setLayoutParams(para);
    }
    private void setshadow()
    {
        ImageView image=(ImageView) findViewById(R.id.shadow_view);
        ViewGroup.LayoutParams para;
        para = image.getLayoutParams();
        para.height=12;
        image.setLayoutParams(para);
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
                    break;

                case R.id.nav_status:
                    if (mStatusFragment == null) {
                        mStatusFragment = new StatusFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Status");
                    setshadow();
                    transaction.replace(R.id.id_content, mStatusFragment);
                    break;
                case R.id.nav_problems:
                    if (mProblemsFragment == null) {
                        mProblemsFragment = new ProblemsFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Problems");
                    setshadow();
                    transaction.replace(R.id.id_content, mProblemsFragment);
                    break;

                case R.id.nav_contest:
                    if (mContestFragment == null) {
                        mContestFragment = new ContestFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Contest");
                    setshadow();
                    transaction.replace(R.id.id_content, mContestFragment);
                    break;
                case R.id.nav_home:
                    if (mHomeFragment == null) {
                        mHomeFragment = new HomeFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Home");
                    setshadow();
                    transaction.replace(R.id.id_content, mHomeFragment);
                    break;
                case R.id.nav_exit:
                    iojexit();
                    return false;

            }
            transaction.commit();
            //事务提交
            item.setChecked(true);
            dl.closeDrawers();
            return false;
        }
    };
    public void iojexit(){
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                //Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                Snackbar sBar = Snackbar.make(dl,"再按一次退出程序",Snackbar.LENGTH_SHORT);
                View sbView = sBar.getView();
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
