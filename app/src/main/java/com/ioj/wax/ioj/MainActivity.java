package com.ioj.wax.ioj;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(intent,Login_REQUEST_CODE);
                //startActivity(new Intent(MainActivity.this,ProblemsView.class));
            }
        });

        //从文件读取UserInfo信息
        mUserInfo.setLogin(false);
        mUserInfo = (UserInfo)ObjectSaveUtils.getObject(MainActivity.this,"UserInfo");
        if(mUserInfo.isLogin()){
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
    //负责更新UI
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==85)
                if(mUserInfo.isLogin()){
                    Snackbar sBar = Snackbar.make(dl,"欢迎回来，"+mUserInfo.getUsername()+"！",Snackbar.LENGTH_SHORT);
                    View sv = sBar.getView();
                    ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                    sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                    sBar.show();
                }
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
