package com.ioj.wax.ioj;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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

public class Ranklist_all extends Fragment {
    private boolean isLoadmore;
    private boolean isRerfer;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private RanklistAdapter myRLAdapter;
    private List<Ranklist_p> mRankData = new ArrayList<Ranklist_p>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    View view;
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SUCCESS:
                    mSwipeRefreshLayout.setRefreshing(false);
                    myRLAdapter.notifyDataSetChanged();
                    break;
                case MSG_FAILURE:
                    myRLAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    class AddDataThread extends Thread
    {
        @Override
        public void run() {
            try {
                isLoadmore=true;
                initRankData.initData(mRankData,0,mRankData.size()/20+1,false);
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
                initRankData.initData(mRankData,0,1,true);
                isRerfer=false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
        }
    }

    private void initload(){
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        isLoadmore=false;
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
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isLoadmore=false;
        isRerfer=false;
        view = inflater.inflate(R.layout.ranklist_all,container,false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.all_layout_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isRerfer &&!isLoadmore)
                    new RefreshThread().start();
            }
        });
        new RefreshThread().start();
        mRecyclerView = (RecyclerView)view.findViewById(R.id.Rs_recyclerview);
        //设置RecyclerView的item的间距
        //int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_space);
        //mRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        // 设置LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        myRLAdapter = new RanklistAdapter(getActivity(), mRankData);
        // 为mRecyclerView设置适配器
        mRecyclerView.setAdapter(myRLAdapter);
        initload();
        mRecyclerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (isRerfer) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );
        return view;
    }

}