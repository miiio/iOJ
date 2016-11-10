package com.ioj.wax.ioj;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout dl;

    private HomeFragment mHomeFragment;
    private RanklistFragment mRanklistFragment;
    private StatusFragment mStatusFragment;
    private ProblemsFragment mProblemsFragment;
    private ContestFragment mContestFragment;
    private NavigationView mNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        ImageView btn_login = (ImageView)view.findViewById(R.id.id_usericon);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

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
        para.height=15;
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
            }
            transaction.commit();
            //事务提交
            item.setChecked(true);
            dl.closeDrawers();
            return false;
        }
    };
}
