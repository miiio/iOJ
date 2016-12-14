package com.ioj.wax.ioj;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ProblemsFragment extends Fragment {
    private UserInfo mUserInfo;
    private ProblemsAdapter mProblemsAdapter;
    private List<Problems_p> mProblemsData = new ArrayList<Problems_p>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton btn_search;
    private LinearLayoutManager layoutManager;
    private String search_title;
    private boolean isRerfer=false;
    private boolean isLoadmore = false;
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    private final static int PROBLEMS_REQUEST_CODE=1;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.problems_fragment, container, false);
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
        //为mRecyclerView设置点击事件
        mProblemsAdapter.setRecyitemonclick(new ProblemsAdapter.RecyItemOnclick() {
            @Override
            public void onItemOnclick(View view, int index, String id,String title) {
                Intent intent = new Intent(getActivity(),ProblemsView.class);
                intent.putExtra("title",title);
                intent.putExtra("id",id);
                startActivity(intent);
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
                    //myRLAdapter.isLoadingMore=true;
                    new AddDataThread().start();
                }
            }
        }
    });
        new RefreshThread().start();

        //设置问题搜索按钮事件
        btn_search = (FloatingActionButton)view.findViewById(R.id.floatbtn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final EditText mEditText_search = new EditText(view.getContext());
                builder.setView(mEditText_search,60,0,60,0);
                builder.setTitle("Search");
                builder.setMessage("Enter the ID or Title:");
                builder.setNegativeButton("cancel", null);
                builder.setPositiveButton("search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(),ProblemsView.class);
                        String edittext = mEditText_search.getText().toString();
                        if(isInteger(edittext)) {
                            intent.putExtra("title", edittext);
                            intent.putExtra("id", edittext);
                            startActivity(intent);
                        }else{
                            search_title = edittext;
                            new SearchThread().start();
                        }
                    }
                });
                builder.show();

            }
        });
        return view;
    }
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
    class SearchThread extends Thread
    {
        @Override
        public void run() {
            try {
                isRerfer=true;
                //mSwipeRefreshLayout.setRefreshing(true);
                LoadProblemsData.SearchProblems(search_title,mProblemsData,1,mUserInfo);
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