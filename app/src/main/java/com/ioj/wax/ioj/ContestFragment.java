package com.ioj.wax.ioj;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ContestFragment extends Fragment {
    View view;
    UserInfo mUserInfo;
    ContestListAdapt mContestListAdapt;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private boolean isRerfer=false;
    private boolean isLoadmore = false;
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    private List<ContestInfo> mContestData = new ArrayList<ContestInfo>();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.contest_fragment, container, false);
        mUserInfo = ((MainActivity)getActivity()).mUserInfo;
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.contest_swipeRefresh);
        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.contest_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        mContestListAdapt = new ContestListAdapt(getActivity(), mContestData);
        // 为mRecyclerView设置适配器
        mRecyclerView.setAdapter(mContestListAdapt);
        //设置刷新事件
        mSwipeRefreshLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isRerfer) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isRerfer==false && isLoadmore==false){
                    new RefreshThread().start();
                }
            }
        });
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visible  = layoutManager.getChildCount();
                int total = layoutManager.getItemCount();
                int past=layoutManager.findFirstCompletelyVisibleItemPosition();
                if ((visible + past) >= total){
                    //mType=2;page++;
                    if(!isLoadmore) {
                        new AddDataThread().start();
                    }
                }
            }
        });
        //设置点击事件
        mContestListAdapt.setRecyitemonclick(new ContestListAdapt.RecyItemOnclick() {
            @Override
            public void onItemOnclick(View view, int index, String id, String title, int status) {
                Intent intent = new Intent(getActivity(),ContestView.class);
                intent.putExtra("title",title);
                intent.putExtra("id",id);
                intent.putExtra("status",status);
                intent.putExtra("cookies",mUserInfo.getCookie());
                startActivityForResult(intent,2);
            }
        });
        new RefreshThread().start();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2){
            ((MainActivity)getActivity()).openStatus();
        }
    }

    class AddDataThread extends Thread
    {
        @Override
        public void run() {
            try {
                isLoadmore=true;
                LoadData.LoadContestData(mContestData,mContestData.size()/20+1,mUserInfo,false);
                isLoadmore=false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
        }
    }

    class RefreshThread extends Thread
    {
        @Override
        public void run() {
            try {
                isRerfer=true;
                mSwipeRefreshLayout.setRefreshing(true);
                LoadData.LoadContestData(mContestData,1,mUserInfo,true);
                isRerfer=false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SUCCESS:
                    mSwipeRefreshLayout.setRefreshing(false);
                    mContestListAdapt.notifyDataSetChanged();
                    isLoadmore=false;
                    isRerfer=false;
                    break;
                case MSG_FAILURE:
                    mContestListAdapt.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}