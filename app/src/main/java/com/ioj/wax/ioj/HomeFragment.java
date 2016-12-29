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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;


public class HomeFragment extends Fragment {
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
        inintBanner();
        return view;
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
        image.add("http://acm.swust.edu.cn/media/marquee/the_39th_ACMICPC_Xian.jpg");
        image.add("http://acm.swust.edu.cn/media/marquee/the_10th_SCPC_2.jpg");
        image.add("http://acm.swust.edu.cn/media/marquee/36th_ACM_ICPC_Chengdu_3.jpg");
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