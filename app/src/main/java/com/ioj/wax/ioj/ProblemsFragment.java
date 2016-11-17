package com.ioj.wax.ioj;

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

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProblemsFragment extends Fragment {
    private UserInfo mUserInfo;
    private ProblemsAdapter mProblemsAdapter;
    private List<Problems_p> mProblemsData = new ArrayList<Problems_p>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private boolean isRerfer=false;
    private boolean isLoadmore = false;
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.problems_fragment, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.all_layout_swipe_refresh_pb);
        mUserInfo = ((MainActivity)getActivity()).mUserInfo;
        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.Pb_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        mProblemsAdapter = new ProblemsAdapter(getActivity(), mProblemsData);
        // 为mRecyclerView设置适配器
        mRecyclerView.setAdapter(mProblemsAdapter);
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
                    //myRLAdapter.isLoadingMore=true;
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
                LoadProblemsData.LoadProblems(mProblemsData,1,mUserInfo,true);
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
                LoadProblemsData.LoadProblems(mProblemsData,mProblemsData.size()/20+1,mUserInfo,false);
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
                    mProblemsAdapter.notifyDataSetChanged();
                    isLoadmore=false;
                    isRerfer=false;
                    break;
                case MSG_FAILURE:
                    mProblemsAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}