package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity {
    private final static int Login_REQUEST_CODE=1;
    private EditText input_user;
    private  EditText input_password;
    UserInfo info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnlogin=(Button)findViewById(R.id.buttonLogin);
        input_user = (EditText) findViewById(R.id.editTextName);
        input_password = (EditText) findViewById(R.id.editTextPassword);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String ret = null;
                        ret = Login(input_user.getText().toString(),input_password.getText().toString());
                        try {
                            //pic = getHttpBitmap(getPic(ret));
                            info = GetUserInfo(ret);
                            info.setLogin(true);
                            info.setUsername( input_user.getText().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent ResultIntent=new Intent();
                        ObjectSaveUtils.saveObject(LoginActivity.this,"UserInfo",info);
                        ResultIntent.putExtra("info",info);
                        setResult(Login_REQUEST_CODE, ResultIntent);
                        finish();
                    }
                }).start();

            }

        });
    }

    private UserInfo GetUserInfo(String cookie) throws IOException {
        UserInfo ret=new UserInfo();
        ret.setCookie(cookie);
        //URL picurl=new URL("http://acm.swust.edu.cn/user/userinfo/");
        URL picurl = new URL("http://acm.swust.edu.cn/mobile/index/");
        HttpURLConnection picConn = (HttpURLConnection)picurl.openConnection();
        picConn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        picConn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        picConn.setRequestProperty("Cookie","yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; " +
                "csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH; " +
                "Hm_lvt_f5127c6793d40d199f68042b8a63e725=1478845119; " +
                "Hm_lpvt_f5127c6793d40d199f68042b8a63e725=1478846104; " +
                cookie);
        picConn.setRequestMethod("GET");
        String html=null;
        int id = picConn.getResponseCode();
        if (id == 200) {
            // 获取响应的输入流对象
            InputStream is = picConn.getInputStream();
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
            //获取头像url
            String regPic = "(/media/avatar/.*..*)\" ";
            Pattern patternPic = Pattern.compile(regPic);
            Matcher matcherPic = patternPic.matcher(html);
            while (matcherPic.find())
                ret.setPicurl("http://acm.swust.edu.cn" + matcherPic.group(1));
            String regMaxin = "lor:green\">(.*)</div>";
            //获取maxin
            Pattern patternMaxin = Pattern.compile(regMaxin);
            Matcher matcherMaxin = patternMaxin.matcher(html);
            while (matcherMaxin.find())
                ret.setMaxin(matcherMaxin.group(1));
            if (ret.getMaxin().isEmpty()) {
                ret.setMaxin("我不想说话.");

            } else {
                System.out.println("链接失败.........");
            }
        }
        //System.out.println(html);
        //ret.picurl = ret.picurl.substring(0,ret.picurl.length()-1);
        return ret;
    }

    private String Login(String user,String password) {
        String rs=null;
        try {
            // 根据地址创建URL对象
            URL url = new URL("http://acm.swust.edu.cn/user/ajaxlogin/");
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 传递的数据
            //username=5120160446&password=asd5603312&csrfmiddlewaretoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH
            String data = "username=" + URLEncoder.encode(user, "UTF-8")
                    + "&password=" + URLEncoder.encode(password, "UTF-8")+"&csrfmiddlewaretoken="+URLEncoder.encode("Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH","UTF-8");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConnection.setRequestProperty("Accept-Language:zh-CN", "zh-CN,zh;q=0.8");
            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Cookie", "yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH");
            urlConnection.setRequestProperty("Host", "acm.swust.edu.cn");
            urlConnection.setRequestProperty("Origin", "http://acm.swust.edu.cn");
            urlConnection.setRequestProperty("Referer:", "http://acm.swust.edu.cn/mobile/index/");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
            urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int id = urlConnection.getResponseCode();
            if (id == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
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
                rs = new String(baos.toByteArray());
                //Set-Cookie:sessionid=93nw2adbafcl20cxrs8ms1puzp01m90u; httponly; Path=/
                //保存cookie
                String cookieskey = "Set-Cookie";
                Map<String, List<String>> maps = urlConnection.getHeaderFields();
                List<String> coolist = maps.get(cookieskey);
                Iterator<String> it = coolist.iterator();
                String str_cookie = it.next();
                rs= str_cookie;
                //sessionid=ouhljsm0v3ux99qjlka4eb7daxha2pdc; httponly; Path=/
            } else {
                System.out.println("链接失败.........");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }


}
