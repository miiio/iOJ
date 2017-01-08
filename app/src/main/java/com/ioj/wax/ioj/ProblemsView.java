package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.evilbinary.highliter.HighlightEditText;
import org.evilbinary.managers.Configure;
import org.evilbinary.managers.ConfigureManager;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;


public class ProblemsView extends Activity{
    String pbId;
    String pbTitle;
    String cookies;
    String contestId;
    String username;
    String limit;
    ProblemsInfo mProblemsInfo;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView TextView_des;
    TextView TextView_in;
    TextView TextView_out;
    TextView TextView_si;
    TextView TextView_so;
    TextView TextView_mycode;
    TextView tx_title;
    TextView tx_limit;
    AppBarLayout appbar;
    private FloatingActionsMenu floatingActionsMenu;
    private String preCode;
    private ConfigureManager mConfigureManager;
    private HighlightEditText mHighlightEdit;
    private Configure mConf;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Pbv_Theme);
        setContentView(R.layout.problemsview);
        tx_title = (TextView)findViewById(R.id.problemsview_title);
        TextView_des = (TextView)findViewById(R.id.pbv_description);
        TextView_in = (TextView)findViewById(R.id.pbv_input);
        TextView_out = (TextView)findViewById(R.id.pbv_output);
        TextView_si = (TextView)findViewById(R.id.pbv_sampleinput);
        TextView_so = (TextView)findViewById(R.id.pbv_sampleoutput);
        tx_limit = (TextView)findViewById(R.id.pbv_limit);
        appbar = (AppBarLayout)findViewById(R.id.problemsView_appbar);
        ImageView imagback = (ImageView)findViewById(R.id.problemsView_Back);
        imagback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //获取传入问题信息
        Intent intent = getIntent();
        cookies = intent.getStringExtra("cookies");
        contestId = intent.getStringExtra("contestid");
        pbTitle = intent.getStringExtra("title");
        pbId = intent.getStringExtra("id");
        username = intent.getStringExtra("username");
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils.get().url("http://acm.swust.edu.cn/problem/"+pbId+"/").build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                pbTitle = "获取标题失败...";
                            }
                            @Override
                            public void onResponse(String response, int id) {
                                pbTitle = LoadData.getStrMid(response,"<h3>","<small class=\"ojcolor\">");
                                mHandler.obtainMessage(2).sendToTarget();
                                //"Time limit:1000     Memory limit:65535"
                                limit = "Time limit:" + LoadData.getStrMid(response,"Time limit(ms): ","</div>")+"     Memory limit:"
                                        +LoadData.getStrMid(response,"Memory limit(kb): ","</div>");
                                tx_limit.setText(limit);
                                tx_title.setText(pbTitle);
                            }
                        });
                  }
    }).start();
        TextView_mycode = (TextView)findViewById(R.id.problemsView_mycode);
        TextView_mycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShowMyCodeThread(ProblemsView.this,appbar,pbId,cookies,username).selectView();
            }
        });
        floatingActionsMenu = (FloatingActionsMenu)findViewById(R.id.pbv_fab_multiple_actions);
        findViewById(R.id.pbv_fab_mycode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.collapse();
                new ShowMyCodeThread(ProblemsView.this,appbar,pbId,cookies,username).selectView();
            }
        });
        findViewById(R.id.pbv_fab_submit).setOnClickListener(submitBtnClickListener);
        findViewById(R.id.pbv_fab_sharecode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.collapse();
                new CodeShare(ProblemsView.this,appbar,username).findCode(pbId);
            }
        });
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
    private View.OnClickListener submitBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingActionsMenu.collapse();
            initCodeEditView();
            preCode = "";
            mHighlightEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String curCode = mHighlightEdit.getText().toString();
                    if(!curCode.equals(preCode) && Math.abs(curCode.length()-preCode.length())>10 ){
                        mHighlightEdit.setSource(curCode);
                        preCode=curCode;
                    }

                }
            });
            final AlertDialog.Builder builder = new AlertDialog.Builder(ProblemsView.this);
            builder.setTitle("Submit");
            builder.setView(linearLayout);
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean ret=false;
                            try {
                                ret = LoadData.SubmitProblem(pbId,contestId,mHighlightEdit.getText().toString(),"g++",cookies);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(ret){
                                Snackbar sBar = Snackbar.make(appbar,"提交成功！",Snackbar.LENGTH_SHORT);
                                View sv = sBar.getView();
                                ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                                sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                                sBar.show();
                                Intent ResultIntent=new Intent();
                                setResult(2, ResultIntent);
                                finish();
                            }else {
                                Snackbar sBar = Snackbar.make(appbar,"未知错误！提交失败！",Snackbar.LENGTH_SHORT);
                                View sv = sBar.getView();
                                ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                                sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                                sBar.show();
                            }
                        }
                    }).start();
                }
            });
            builder.show();
        }
    };
    private void initCodeEditView(){
        linearLayout = new LinearLayout(ProblemsView.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mConfigureManager = new ConfigureManager(ProblemsView.this);
        mConfigureManager.exractDefaultConfigure();
        mConf = mConfigureManager.getDefaultConfigure();
        mConf.mTheme = "edit-xcode";// solarized-light vampire
        mConf.mLanguage = "c";
        mHighlightEdit = new HighlightEditText(ProblemsView.this,mConf);
        mHighlightEdit.setSource("\n\n\n\n\n\n\n\n\n\n\n\n");
        linearLayout.addView(mHighlightEdit);
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //mSwipeRefreshLayout.setRefreshing(false);
                    tx_title.setText(pbTitle);
                    TextView_des.setText(Html.fromHtml(mProblemsInfo.getDescription()));
                    TextView_in.setText(Html.fromHtml(mProblemsInfo.getInput()));
                    TextView_out.setText(Html.fromHtml(mProblemsInfo.getOutput()));
                    TextView_si.setText(Html.fromHtml(mProblemsInfo.getSampleInput()));
                    TextView_so.setText(Html.fromHtml(mProblemsInfo.getSampleOutput()));
                    tx_limit.setText(limit);
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
