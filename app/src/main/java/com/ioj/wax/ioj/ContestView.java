package com.ioj.wax.ioj;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ContestView extends AppCompatActivity {
    private static final int RUNNING_ITEM = 0;
    private static final int PENDING_ITEM = 1;
    private static final int ENDED_ITEM = 2;
    private static final int NORMAL_ITEM = 3;
    private String mTitle;
    private String mId;
    private int mStatus;
    private Count mCount;
    private List<Problems_p> mProblemsData = new ArrayList<Problems_p>();
    //private UserInfo mUserInfo;
    private String cookies;
    private ContestViewAdapt mContestViewAdapt;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private boolean isRerfer=false;
    private boolean isLoadmore = false;
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contestview);
        //获取传入信息
        Intent intent = getIntent();
        cookies = intent.getStringExtra("cookies");
        mTitle = intent.getStringExtra("title");
        TextView contestTitle_tv = (TextView)findViewById(R.id.contestview_title);
        contestTitle_tv.setText(mTitle);
        mId = intent.getStringExtra("id");
        mStatus = intent.getIntExtra("status",ENDED_ITEM);
        setTheme(R.style.Pbv_Theme);
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.contestview_toolbar);
        //设置返回icon
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.contestview_collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setTitle("Contest");
        //通过CollapsingToolbarLayout修改字体颜色
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#ffffffff"));//设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#ffffffff"));//设置收缩后Toolbar上字体的颜色
        FloatingActionButton floatbtn_score = (FloatingActionButton)findViewById(R.id.floatbtn_score);
        floatbtn_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar sBar = Snackbar.make(mToolbar,"You have got "+mCount.Score+" of "+mCount.TotalScore+" score.",Snackbar.LENGTH_SHORT);
                View sv = sBar.getView();
                ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                sBar.show();
            }
        });
        //mUserInfo = ((MainActivity)getActivity()).mUserInfo;
        //mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.contestview_swip);
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.contestview_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ContestView.this));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        mContestViewAdapt = new ContestViewAdapt(ContestView.this,mProblemsData);
        // 为mRecyclerView设置适配器
        mRecyclerView.setAdapter(mContestViewAdapt);
        //为item设置点击事件
        mContestViewAdapt.setRecyitemonclick(new ContestViewAdapt.RecyItemOnclick() {
            @Override
            public void onItemOnclick(View view, String title, String prbId, String contestId) {
                Intent intent = new Intent(ContestView.this,ProblemsView.class);
                intent.putExtra("title",title);
                intent.putExtra("id",prbId);
                intent.putExtra("cookies",cookies);
                intent.putExtra("contestid",contestId);
                startActivityForResult(intent,2);
            }
        });
        new RefreshThread().start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2){
            Intent ResultIntent=new Intent();
            setResult(2, ResultIntent);
            finish();
        }
    }

    class RefreshThread extends Thread
    {
        @Override
        public void run() {
            try {
                isRerfer=true;
                //mSwipeRefreshLayout.setRefreshing(true);
                mCount = new Count(0,0,0,0);
                LoadData.LoadContestProblems(mCount,mProblemsData,mId,cookies);
                isRerfer=false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
        }
    }
    public int getImageResourceId(String name) {
        R.drawable drawables=new R.drawable();
        //默认的id
        int resId=R.drawable.isac;
        try {
            //根据字符串字段名，取字段//根据资源的ID的变量名获得Field的对象,使用反射机制来实现的
            java.lang.reflect.Field field=R.drawable.class.getField(name);
            //取值
            resId=(Integer)field.get(drawables);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resId;
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SUCCESS:
                    //mSwipeRefreshLayout.setRefreshing(false);
                    mContestViewAdapt.notifyDataSetChanged();
                    TextView tv1 = (TextView)findViewById(R.id.contestview_acNum);
                    tv1.setText(mCount.AcNum+" / "+mCount.ProblemsNum);
                    tv1 = (TextView)findViewById(R.id.contestview_totalscore);
                    tv1.setText("/  score: "+mCount.TotalScore);
                    tv1 = (TextView)findViewById(R.id.contestview_problemsNum);
                    tv1.setText("problems: "+mCount.ProblemsNum);
                    FloatingActionButton fb=(FloatingActionButton)findViewById(R.id.floatbtn_score);
                    fb.setBackgroundResource(R.drawable.n2);
                    //fb.setBackgroundResource(getImageResourceId("n"+mCount.Score));
                    isLoadmore=false;
                    isRerfer=false;
                    break;
                case MSG_FAILURE:
                    mContestViewAdapt.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
