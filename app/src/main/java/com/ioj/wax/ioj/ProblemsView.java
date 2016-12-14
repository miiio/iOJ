package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.drakeet.materialdialog.MaterialDialog;


public class ProblemsView extends Activity{
    String pbId;
    String pbTitle;
    ProblemsInfo mProblemsInfo;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView TextView_des;
    TextView TextView_in;
    TextView TextView_out;
    TextView TextView_si;
    TextView TextView_so;
    WebView WebView_code;
    WebSettings settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Pbv_Theme);
        setContentView(R.layout.problemsview);
        TextView tx_title = (TextView)findViewById(R.id.problemsview_title);
        TextView_des = (TextView)findViewById(R.id.pbv_description);
        TextView_in = (TextView)findViewById(R.id.pbv_input);
        TextView_out = (TextView)findViewById(R.id.pbv_output);
        TextView_si = (TextView)findViewById(R.id.pbv_sampleinput);
        TextView_so = (TextView)findViewById(R.id.pbv_sampleoutput);
        ImageView imagback = (ImageView)findViewById(R.id.problemsView_Back);
        imagback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FloatingActionButton btn_submit = (FloatingActionButton)findViewById(R.id.floatbtn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                View   dialog = inflater.inflate(R.layout.problem_submit,(ViewGroup) findViewById(R.id.prb_submit));
                WebView_code = (WebView) dialog.findViewById(R.id.WebView_submitcode);
                settings = WebView_code.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setBuiltInZoomControls(false);
                WebView_code.requestFocus();
                WebView_code.loadUrl("file:///android_asset/highlight.html");
                WebView_code.requestFocus();
                final MaterialDialog mMaterialDialog = new MaterialDialog(ProblemsView.this).setTitle("Submit");
                mMaterialDialog.setView(dialog);
                mMaterialDialog.setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
                mMaterialDialog.setPositiveButton("Submit", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                mMaterialDialog.show();
                WebView_code.requestFocus();
            }
        });
        Intent intent = getIntent();
        pbTitle = intent.getStringExtra("title");
        pbId = intent.getStringExtra("id");
        tx_title.setText(pbTitle);
        //mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.pbv_swiperefre);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //mSwipeRefreshLayout.setRefreshing(true);
                    mProblemsInfo  = getProblemsInfo(pbId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mHandler.obtainMessage(1).sendToTarget();
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //mSwipeRefreshLayout.setRefreshing(false);
                    TextView_des.setText(Html.fromHtml(mProblemsInfo.getDescription()));
                    TextView_in.setText(Html.fromHtml(mProblemsInfo.getInput()));
                    TextView_out.setText(Html.fromHtml(mProblemsInfo.getOutput()));
                    TextView_si.setText(Html.fromHtml(mProblemsInfo.getSampleInput()));
                    TextView_so.setText(Html.fromHtml(mProblemsInfo.getSampleOutput()));
                    break;
            }
            super.handleMessage(msg);
        }
    };
    public static String getStrMid(String str,String left,String right)
    {
        return str.substring(str.indexOf(left)+left.length(),str.indexOf(right));
    }
    private ProblemsInfo getProblemsInfo(String pbId) throws IOException {
        ProblemsInfo ret = new ProblemsInfo();
        URL pbUrl = new URL("http://acm.swust.edu.cn/mobile/submit/"+pbId+"/");
        HttpURLConnection pbConn = (HttpURLConnection)pbUrl.openConnection();
        pbConn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        pbConn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        pbConn.setRequestMethod("GET");
        String html=null;
        int id = pbConn.getResponseCode();
        if (id == 200) {
            // 获取响应的输入流对象
            InputStream is = pbConn.getInputStream();
            // 创建字节输出流对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 定义读取的长度
            int len = 0;
            // 定义缓冲区
            byte buffer[] = new byte[1024];
            // 按照缓冲区的大小，循环读取
            while ((len = is.read(buffer)) != -1) {
                // 根据读取的长度写入到os对象中
                baos.write(buffer, 0, len);
            }
            // 释放资源
            is.close();
            baos.close();
            // 返回字符串
            html = new String(baos.toByteArray());
            ret.setDescription(getStrMid(html,"<h1>Description</h1>\n","    </div>\n" +
                    "    <div id=\"input\" class=\"ui-body-d ui-content detial\">\n"));
            ret.setInput(getStrMid(html,"      <h1>Input</h1>\n","    </div>\n"+
                    "    <div id=\"output\" class=\"ui-body-d ui-content detial\">\n"));
            ret.setOutput(getStrMid(html,"      <h1>Output</h1>\n","    </div>\n"+
                    "    <div id=\"sinput\" class=\"ui-body-d ui-content detial\">\n"));
            ret.setSampleInput(getStrMid(html,  "      <h1>SampleInput</h1>\n"+
                    "      \n","    </div>\n"+
                    "    <div id=\"soutput\" class=\"ui-body-d ui-content detial\">\n"));
            ret.setSampleOutput(getStrMid(html, "      <h1>SampleOutput</h1>\n"+
                    "      \n", "    </div>\n"+
                    "    <div id=\"hint\" class=\"ui-body-d ui-content detial\">\n"));
        }
        return ret;
    }
}
