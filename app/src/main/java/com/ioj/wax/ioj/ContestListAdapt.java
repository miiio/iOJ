package com.ioj.wax.ioj;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ContestListAdapt extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int RUNNING_ITEM = 0;
    private static final int PENDING_ITEM = 1;
    private static final int ENDED_ITEM = 2;
    private static final int NORMAL_ITEM = 3;

    private Context mContext;
    private List<ContestInfo> mDataList;
    private LayoutInflater mLayoutInflater;
    public RecyItemOnclick recyitemonclick;

    public ContestListAdapt(Context mContext, List<ContestInfo> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ENDED_ITEM) {
            return new NormalItemHolder(mLayoutInflater.inflate(R.layout.contest_list_ended, parent, false),viewType,recyitemonclick);
        } else if(viewType == RUNNING_ITEM) {
            return new NormalItemHolder(mLayoutInflater.inflate(R.layout.contest_list_running, parent, false),viewType,recyitemonclick);
        } else if(viewType == PENDING_ITEM){
            return new NormalItemHolder(mLayoutInflater.inflate(R.layout.contest_list_pending, parent, false),viewType,recyitemonclick);
        }else{
            return new NormalItemHolder(mLayoutInflater.inflate(R.layout.contest_list_notitle, parent, false),viewType,recyitemonclick);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ContestInfo entity = mDataList.get(position);

        if (null == entity)
            return;
        NormalItemHolder holder = (NormalItemHolder) viewHolder;
        holder.tv_holder.setText(entity.getHolder());
        holder.tv_starttime.setText(entity.getStartTime());
        holder.tv_endtime.setText(entity.getEndTime());
        holder.tv_title.setText(entity.getTitle());
        holder.tv_purview.setText(entity.getPurview());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int statusid = Integer.parseInt(mDataList.get(position).getStatus());
        if(position==0) {
            return statusid;
        }else{
            int prestatusid =Integer.parseInt(mDataList.get(position-1).getStatus());
            if(statusid == prestatusid){
                return NORMAL_ITEM;
            }else{
                return statusid;
            }
        }

    }

    // 设置点击事件
    public void setRecyitemonclick(RecyItemOnclick recyitemonclick) {
        this.recyitemonclick = recyitemonclick;
    }

    public interface RecyItemOnclick {
        //item点击
        public void onItemOnclick(View view, int index,String id,String title,int status);
    }

    public class NormalItemHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        TextView tv_holder;
        TextView tv_starttime;
        TextView tv_endtime;
        TextView tv_purview;
        int statusid;
        private RecyItemOnclick mRecyItemOnclick;
        public NormalItemHolder(View itemView,int id,RecyItemOnclick recyItemOnclick) {
            super(itemView);
            statusid = id;
            tv_title = (TextView)itemView.findViewById(R.id.contestlist_title);
            tv_starttime = (TextView)itemView.findViewById(R.id.contestlist_starttime);
            tv_endtime = (TextView)itemView.findViewById(R.id.contestlist_endtime);
            tv_purview = (TextView)itemView.findViewById(R.id.contestlist_purview);
            tv_holder = (TextView)itemView.findViewById(R.id.contestlist_holder);
            itemView.findViewById(R.id.contestlist_itemlayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContestInfo cp = mDataList.get(getAdapterPosition());
                    if(recyitemonclick != null){
                        int position = getPosition();
                        int status = Integer.parseInt(mDataList.get(position).getStatus());
                        mRecyItemOnclick.onItemOnclick(v, position,cp.getContestId(),cp.getTitle(),status);
                    }
                }
            });
            this.mRecyItemOnclick = recyItemOnclick;
        }
    }
}

