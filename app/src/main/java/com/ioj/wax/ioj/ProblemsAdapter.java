package com.ioj.wax.ioj;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
public class ProblemsAdapter extends RecyclerView.Adapter<ViewHolder>{
    private List<Ranklist_p> person;
    private Context mContext;
    public boolean isLoadingMore = false;
    public boolean isRefer = false;
    public static final int TYPE_FOOTER = 11;
    public static final int TYPE_ITEM = 0;
    public ProblemsAdapter( Context context , List<Ranklist_p> person)
    {
        this.mContext = context;
        this.person = person;
    }

    @Override
    public  ViewHolder  onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // 给ViewHolder设置布局文件
        View v;
        if(viewType==TYPE_ITEM){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ranklistshow_cardview, viewGroup, false);
            return new itemViewHolder(v);
        }
        else if(viewType==TYPE_FOOTER ) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.refresh_loading, viewGroup, false);
            return new FootViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if(viewHolder instanceof itemViewHolder) {
            // 给ViewHolder设置元素
            Ranklist_p p = person.get(position);
            ((itemViewHolder)viewHolder).mTextView_user.setText(p.name);
            ((itemViewHolder)viewHolder).mTextView_solved.setText(p.solved);
            ((itemViewHolder)viewHolder).mTextView_submit.setText(p.submit);
            ((itemViewHolder)viewHolder).mTextView_ranknum.setText(p.rank);
            ((itemViewHolder)viewHolder).mTextView_maxim.setText(p.maxim);
            //viewHolder.mImageView.setImageDrawable(mContext.getDrawable(p.getImageResourceId(mContext)));
            ((itemViewHolder)viewHolder).mImageView.setImageBitmap(p.pic);
        }
    }
    @Override
    public int getItemCount() {
        return person == null ? 1 : person.size();
    }
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() -1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }
    public static class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
    public static class itemViewHolder
            extends ViewHolder
    {
        public TextView mTextView_user;
        public TextView mTextView_maxim;
        public TextView mTextView_solved;
        public TextView mTextView_submit;
        public TextView mTextView_ranknum;
        public ImageView mImageView;
        public itemViewHolder( View v )
        {
            super(v);
            mTextView_user = (TextView) v.findViewById(R.id.cardview_user);
            mTextView_maxim = (TextView) v.findViewById(R.id.cardview_maxim);
            mImageView = (ImageView) v.findViewById(R.id.cardview_imagView);
            mTextView_solved = (TextView) v.findViewById(R.id.cardview_ac);
            mTextView_submit = (TextView) v.findViewById(R.id.carview_submit);
            mTextView_ranknum = (TextView) v.findViewById(R.id.cardview_ranknum);
        }
    }
}
