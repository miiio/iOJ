package com.ioj.wax.ioj;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<StatusInfo> status;
    private String username;
    private Context mContext;
    public static final int TYPE_FOOTER = 11;
    public static final int TYPE_ITEM = 0;
    RecyItemOnclick recyitemonclick;
    public StatusAdapter(Context context , List<StatusInfo> status,String username)
    {
        this.username = username;
        this.mContext = context;
        this.status = status;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 给ViewHolder设置布局文件
        View v;
        if(viewType==TYPE_ITEM){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stuats_show, parent, false);
            return new itemViewHolder(v,recyitemonclick);
        }
        else if(viewType==TYPE_FOOTER ) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.refresh_loading, parent, false);
            return new FootViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof itemViewHolder) {
            // 给ViewHolder设置元素
            StatusInfo p = status.get(position);
            ((itemViewHolder)viewHolder).tv_username.setText(p.getUsername());
            ((itemViewHolder)viewHolder).tv_pbid.setText("Problem ID:"+p.getProblemid()+"    "+p.getCompiler());
            ((itemViewHolder)viewHolder).tv_memory.setText(p.getMemory());
            ((itemViewHolder)viewHolder).tv_time.setText(p.getTime());
            ((itemViewHolder)viewHolder).tv_lenght.setText(p.getLenght());
            ((itemViewHolder)viewHolder).tv_lenght.setText(p.getLenght());
            if (p.getUsername().equals(username)){
                ((itemViewHolder)viewHolder).Img_isyou.setVisibility(View.VISIBLE);
                ((itemViewHolder)viewHolder).tv_username.setTextColor(Color.parseColor("#468847"));
            }else{
                ((itemViewHolder)viewHolder).Img_isyou.setVisibility(View.INVISIBLE);
                ((itemViewHolder)viewHolder).tv_username.setTextColor(Color.parseColor("#757575"));
            }
            String str_result="Error";
            switch (p.getResult()){
                case "-1":
                    str_result="Waiting";
                    ((itemViewHolder)viewHolder).Img_result.setImageResource(R.drawable.waitting);
                    break;
                case "0":
                    str_result="Accept";
                    ((itemViewHolder)viewHolder).Img_result.setImageResource(R.drawable.accept);
                    break;
                case "1":
                    str_result="PE";
                    ((itemViewHolder)viewHolder).Img_result.setImageResource(R.drawable.pe);
                    break;
                case "2":
                    str_result="TLE";
                    ((itemViewHolder)viewHolder).Img_result.setImageResource(R.drawable.tle);
                    break;
                case "3":
                    str_result="MLE";
                    ((itemViewHolder)viewHolder).Img_result.setImageResource(R.drawable.waring);
                    break;
                case "4":
                    str_result="WA";
                    ((itemViewHolder)viewHolder).Img_result.setImageResource(R.drawable.wrong);
                    break;
                case "5":
                    str_result="RE";
                    ((itemViewHolder)viewHolder).Img_result.setImageResource(R.drawable.re);
                    break;
                case "6":
                    str_result="OLE";
                    ((itemViewHolder)viewHolder).Img_result.setImageResource(R.drawable.waring);
                    break;
                case "7":
                    str_result="CE";
                    ((itemViewHolder)viewHolder).Img_result.setImageResource(R.drawable.ce);
                    break;
                case "8":
                case "9":
                    str_result="SE";
                    ((itemViewHolder)viewHolder).Img_result.setImageResource(R.drawable.waring);
                    break;
            }
            ((itemViewHolder)viewHolder).tv_result.setText(str_result);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;//2016-12-12 21:39:59
            try {
                date = sdf.parse(p.getSubmittime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ((itemViewHolder)viewHolder).tv_submittime.setText(getTime(date));
        }
    }
    public String getTime(Date date){
        Date curDate = new Date(System.currentTimeMillis());
        long diff = curDate.getTime() -  date.getTime()+453*1000;
        if(diff<0)diff=1000;
        long s = diff/1000;
        if(s<60)return s+"s ago";
        else if(s>=60&&s<3600)return s/60+"min"+(s-60*(s/60))+"s ago";
        else if(s>=3600 && s<86400)return s/3600+"h"+((s-s/3600*3600)/60)+"min"+(s-s/3600*3600-(s-s/3600*3600)/60*60)+"s ago";
        else return s+"s ago";
    }
    @Override
    public int getItemCount() {
        return status == null ? 1 : status.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() -1 && position >4 ) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }


    public static class FootViewHolder extends RecyclerView.ViewHolder {

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
            extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView tv_username;
        public TextView tv_pbid;
        public TextView tv_result;
        public TextView tv_memory;
        public TextView tv_time;
        public TextView tv_lenght;
        public TextView tv_submittime;
        public ImageView Img_result;
        public ImageView Img_isyou;
        RecyItemOnclick recyitemonclick;
        public itemViewHolder( View v,RecyItemOnclick recyitemonclick )
        {
            super(v);
            tv_username = (TextView) v.findViewById(R.id.status_username);
            tv_pbid = (TextView) v.findViewById(R.id.status_problemid);
            tv_result = (TextView) v.findViewById(R.id.status_result);
            tv_memory = (TextView) v.findViewById(R.id.stauts_memory);
            tv_time = (TextView) v.findViewById(R.id.status_time);
            tv_lenght = (TextView) v.findViewById(R.id.stauts_lenght);
            tv_submittime = (TextView) v.findViewById(R.id.status_submittime);
            Img_result = (ImageView) v.findViewById(R.id.status_ic_result);
            Img_isyou = (ImageView) v.findViewById(R.id.status_Img_isyou);
            this.recyitemonclick = recyitemonclick;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            StatusInfo pp = status.get(getAdapterPosition());
            if (recyitemonclick != null) {
                // 重点 这里ViewHolder 中提供了 getPosition（）；
                int position = getPosition();
                //recyitemonclick.onItemOnclick(v, position,pp.id,pp.title);
            }
        }
    }
}
