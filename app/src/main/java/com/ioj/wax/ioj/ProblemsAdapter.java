package com.ioj.wax.ioj;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
public class ProblemsAdapter extends RecyclerView.Adapter<ViewHolder>{
    private List<Problems_p> problems;
    private Context mContext;
    public boolean isLoadingMore = false;
    public boolean isRefer = false;
    public static final int TYPE_FOOTER = 11;
    public static final int TYPE_ITEM = 0;
    RecyItemOnclick recyitemonclick;
    public ProblemsAdapter( Context context , List<Problems_p> problems)
    {
        this.mContext = context;
        this.problems = problems;
    }
    @Override
    public  ViewHolder  onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // 给ViewHolder设置布局文件
        View v;
        if(viewType==TYPE_ITEM){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.problems_show_cardview, viewGroup, false);
            return new itemViewHolder(v,recyitemonclick);
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
            Problems_p p = problems.get(position);
            int id = Integer.parseInt(p.id);
            ((itemViewHolder)viewHolder).mTextView_problemsid.setText(id+"");
            ((itemViewHolder)viewHolder).mTextView_problemstitle.setText(p.title);
            ((itemViewHolder)viewHolder).mProgressBar_diffcult.setProgress(p.diffcult);
            if(p.isAc=="true") {
                ((itemViewHolder) viewHolder).mImageViewIsAc.setImageResource(R.drawable.isac);
            }
            else
                ((itemViewHolder)viewHolder).mImageViewIsAc.setImageBitmap(null);
        }
    }
    @Override
    public int getItemCount() {
        return problems == null ? 1 : problems.size();
    }
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() -1 && position >4 ) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }


    public static class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }


    // 设置点击事件
    public void setRecyitemonclick(RecyItemOnclick recyitemonclick) {
        this.recyitemonclick = recyitemonclick;
    }

    public interface RecyItemOnclick {
        //item点击
        public void onItemOnclick(View view, int index,String id,String title);
    }

    public class itemViewHolder
            extends ViewHolder implements View.OnClickListener
    {
        public TextView mTextView_problemsid;
        public TextView mTextView_problemstitle;
        public ProgressBar mProgressBar_diffcult;
        public ImageView mImageViewIsAc;
        RecyItemOnclick recyitemonclick;
        public itemViewHolder( View v,RecyItemOnclick recyitemonclick )
        {
            super(v);
            mTextView_problemsid = (TextView) v.findViewById(R.id.textview_problemsID);
            mTextView_problemstitle = (TextView) v.findViewById(R.id.textview_problestitle);
            mProgressBar_diffcult = (ProgressBar) v.findViewById(R.id.Pb_Progressbar_diffcult);
            mImageViewIsAc = (ImageView) v.findViewById(R.id.Pb_Imageview_isac);
            this.recyitemonclick = recyitemonclick;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Problems_p pp = problems.get(getAdapterPosition());
            if (recyitemonclick != null) {
                // 重点 这里ViewHolder 中提供了 getPosition（）；
                int position = getPosition();
                recyitemonclick.onItemOnclick(v, position,pp.id,pp.title);
            }
        }
    }
}
