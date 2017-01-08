package com.ioj.wax.ioj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import thereisnospon.codeview.Code;


public class HomeFragment extends Fragment implements View.OnClickListener{
    private View view;
    private Banner mBanner;
    private TextView title1;
    private TextView title2;
    private TextView title3;
    private String titleid1;
    private String titleid2;
    private String titleid3;
    private TextView news1;
    private TextView news2;
    private TextView news3;
    private Button btn_news_more;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        title1 = (TextView)view.findViewById(R.id.home_newstitle1);
        title2 = (TextView)view.findViewById(R.id.home_newstitle2);
        title3 = (TextView)view.findViewById(R.id.home_newstitle3);
        title1.setOnClickListener(this);
        title2.setOnClickListener(this);
        title3.setOnClickListener(this);
        news1 = (TextView)view.findViewById(R.id.home_news1);
        news2 = (TextView)view.findViewById(R.id.home_news2);
        news3 = (TextView)view.findViewById(R.id.home_news3);
        btn_news_more = (Button)view.findViewById(R.id.home_more);
        btn_news_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),NewsActivity.class));
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                inintnews();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                inintonline();
            }
        }).start();
        inintBanner();
        return view;
    }
    private void inintonline(){
        OkHttpUtils.get().url("http://acm.swust.edu.cn/online/submit/").build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonobj = new JSONObject(response);
                            JSONArray jsonarr = new JSONArray(jsonobj.getString("user"));
                            ((TextView)view.findViewById(R.id.home_online)).setText("在线人数："+jsonarr.get(jsonarr.length()-1));
                            JSONArray jsonarr2 = new JSONArray(jsonobj.getString("al"));
                            ((TextView)view.findViewById(R.id.home_submitnum)).setText("日提交量："+jsonarr2.get(jsonarr2.length()-1));
                            JSONArray jsonarr3 = new JSONArray(jsonobj.getString("ac"));
                            ((TextView)view.findViewById(R.id.home_acnum)).setText("今日AC数："+jsonarr3.get(jsonarr3.length()-1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        OkHttpUtils.get().url(CodeShare.host+"home.php").build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ((TextView) view.findViewById(R.id.home_home)).setText(" ");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ((TextView) view.findViewById(R.id.home_home)).setText(response);
                    }
                });
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(),NewsViewActivity.class);
        switch (v.getId()){
            case R.id.home_newstitle1:
                intent.putExtra("title",title1.getText().toString()).putExtra("id",titleid1);
                startActivity(intent);
                break;
            case R.id.home_newstitle2:
                intent.putExtra("title",title2.getText().toString()).putExtra("id",titleid2);
                startActivity(intent);
                break;
            case R.id.home_newstitle3:
                intent.putExtra("title",title3.getText().toString()).putExtra("id",titleid3);
                startActivity(intent);
                break;
        }
    }
    private void inintnews(){
        String url = "http://acm.swust.edu.cn/";
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String reg_title = "=\"/news/article/(.*)/\">(.*)</a></h4>";
                        Pattern pat = Pattern.compile(reg_title);
                        Matcher mat = pat.matcher(response);
                        int i=1;
                        while(mat.find()){
                            String titlee = mat.group(2);
                            String idd = mat.group(1);
                            String news = LoadData.getStrMid(response,titlee+"</a></h4>\n" +
                                    "        <p>","</p>");
                            if(i==1){
                                title1.setText(titlee);
                                titleid1 = idd;
                                news1.setText(Html.fromHtml(news));
                            }else if(i==2){
                                title2.setText(titlee);
                                titleid2 = idd;
                                news2.setText(Html.fromHtml(news));
                            }else if(i==3){
                                title3.setText(titlee);
                                titleid3 =idd;
                                news3.setText(Html.fromHtml(news));
                            }
                            i++;
                        }
                    }
                });
    }
    //设置banner
    private void inintBanner(){
        List<String> image = new ArrayList<>();
        image.add(CodeShare.host+"1.jpg");
        image.add(CodeShare.host+"2.jpg");
        image.add(CodeShare.host+"3.jpg");
        mBanner = (Banner) view.findViewById(R.id.main_banner);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        mBanner.setImages(image);
        mBanner.setBannerAnimation(Transformer.DepthPage);
        mBanner.setDelayTime(5000);
        mBanner.setIndicatorGravity(BannerConfig.RIGHT);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        mBanner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }


}