package com.ioj.wax.ioj;

import android.content.Context;
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
            Problems_p p = problems.get(position);
            ((itemViewHolder)viewHolder).mTextView_problemsid.setText(p.id);
            ((itemViewHolder)viewHolder).mTextView_problemstitle.setText(p.title);
            ((itemViewHolder)viewHolder).mProgressBar_diffcult.setProgress(p.diffcult);
            if(p.isAc=="true")
                ((itemViewHolder)viewHolder).mImageViewIsAc.setImageResource(R.drawable.done_black_144x144);
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

    public static class itemViewHolder
            extends ViewHolder
    {
        public TextView mTextView_problemsid;
        public TextView mTextView_problemstitle;
        public ProgressBar mProgressBar_diffcult;
        public ImageView mImageViewIsAc;
        public itemViewHolder( View v )
        {
            super(v);
            mTextView_problemsid = (TextView) v.findViewById(R.id.textview_problemsID);
            mTextView_problemstitle = (TextView) v.findViewById(R.id.textview_problestitle);
            mProgressBar_diffcult = (ProgressBar) v.findViewById(R.id.Pb_Progressbar_diffcult);
            mImageViewIsAc = (ImageView) v.findViewById(R.id.Pb_Imageview_isac);
        }
    }
}
