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

public class StatusFragment extends Fragment {
    private View view;
    private UserInfo mUserInfo;
    private StatusAdapter mStatusAdapter;
    private List<StatusInfo> mStatusData = new ArrayList<StatusInfo>();
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isRerfer=false;
    private boolean isLoadmore = false;
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    private final static int PROBLEMS_REQUEST_CODE=1;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.status_fragment, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh_status);
        mUserInfo = ((MainActivity)getActivity()).mUserInfo;
        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.Stuats_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        mStatusAdapter = new StatusAdapter(getActivity(), mStatusData);
        // 为mRecyclerView设置适配器
        mRecyclerView.setAdapter(mStatusAdapter);
        //为mRecyclerView设置点击事件
        mStatusAdapter.setRecyitemonclick(new StatusAdapter.RecyItemOnclick() {
            @Override
            public void onItemOnclick(View view, int index, String id, String title) {

            }
        });
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
        new RefreshThread().start();
        return view;
    }
    class RefreshThread extends Thread
    {
        @Override
        public void run() {
            try {
                isRerfer=true;
                mSwipeRefreshLayout.setRefreshing(true);
                LoadStatusInfoData.LoadStatusInfoData(mStatusData,1,true);
                isRerfer=false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
        }
    }

    class AddDataThread extends Thread
    {
        @Override
        public void run() {
            try {
                isLoadmore=true;
                //LoadProblemsData.LoadProblems(mProblemsData,mProblemsData.size()/20+1,mUserInfo,false);
                LoadStatusInfoData.LoadStatusInfoData(mStatusData,mStatusData.size()/20+1,false);
                isLoadmore=false;
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
                    mStatusAdapter.notifyDataSetChanged();
                    isLoadmore=false;
                    isRerfer=false;
                    break;
                case MSG_FAILURE:
                    mStatusAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}