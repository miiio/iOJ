package com.ioj.wax.ioj;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

public class ContestViewAdapt extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int RUNNING_ITEM = 0;
    private static final int PENDING_ITEM = 1;
    private static final int ENDED_ITEM = 2;
    private static final int NORMAL_ITEM = 3;

    private String ServerTime;
    private Context mContext;
    private List<Problems_p> mProblems;
    private LayoutInflater mLayoutInflater;
    public RecyItemOnclick recyitemonclick;

    public ContestViewAdapt(Context Context, List<Problems_p> mProblems) {
        this.mContext = Context;
        this.mProblems = mProblems;
        mLayoutInflater = LayoutInflater.from(Context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(mLayoutInflater.inflate(R.layout.contestview_problems, parent, false),recyitemonclick);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Problems_p entity = mProblems.get(position);
        if (null == entity)
            return;
        ItemHolder holder = (ItemHolder) viewHolder;
        char id = (char) ('A'+position);
        holder.tv_pId.setText(String.valueOf(id));
        holder.tv_pIdNum.setText(entity.id);
        holder.tv_pScore.setText(entity.score);
        holder.tv_pTitle.setText(entity.title);
        if(entity.isAc == "true") {
            holder.img_pIsAc.setImageResource(R.drawable.ac);
        }
    }

    @Override
    public int getItemCount() {
        return mProblems.size();
    }
    // 设置点击事件
    public void setRecyitemonclick(RecyItemOnclick recyitemonclick) {
        this.recyitemonclick = recyitemonclick;
    }

    public interface RecyItemOnclick {
        //item点击
        public void onItemOnclick(View view,String title,String prbId,String contestId);
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        public TextView tv_pId;
        public TextView tv_pIdNum;
        public TextView tv_pTitle;
        public TextView tv_pScore;
        public ImageView img_pIsAc;
        public RecyItemOnclick mRecyItemOnclick;
        public ItemHolder(View itemView,RecyItemOnclick recyItemOnclick) {
            super(itemView);
            tv_pId = (TextView)itemView.findViewById(R.id.contestview_pId);
            tv_pIdNum = (TextView)itemView.findViewById(R.id.contestview_pIdNum);
            tv_pTitle = (TextView)itemView.findViewById(R.id.contestview_pTitle);
            tv_pScore = (TextView)itemView.findViewById(R.id.contestview_pScore);
            img_pIsAc = (ImageView)itemView.findViewById(R.id.contestview_pIsAc);
            this.mRecyItemOnclick = recyItemOnclick;
            ((LinearLayout)(itemView.findViewById(R.id.contestview_linearlayout))).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Problems_p p = mProblems.get(getAdapterPosition());
                    if(recyitemonclick != null){
                        int position = getPosition();
                        mRecyItemOnclick.onItemOnclick(v, p.title,p.id,p.contestId);
                    }
                }
            });
        }
    }

}
