package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private Bitmap pic;
    private EditText input_user;
    private  EditText input_password;
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
                        String picUrl=null;
                        String ret = null;
                        ret = Login(input_user.getText().toString(),input_password.getText().toString());
                        try {
                            //pic = getHttpBitmap(getPic(ret));
                            picUrl = getPic(ret);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent intent2=new Intent();
                        intent2.putExtra("pic_url", picUrl);
                        intent2.putExtra("user",input_user.getText().toString());
                        //startActivityForResult(intent2,Login_REQUEST_CODE);
                        setResult(Login_REQUEST_CODE, intent2);
                        finish();
                    }
                }).start();

            }

        });
    }

    private static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(true);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    private String getPic(String cookie) throws IOException {
        String ret=null;
        URL picurl=new URL("http://acm.swust.edu.cn/user/userinfo/");
        HttpURLConnection picConn = (HttpURLConnection)picurl.openConnection();
        picConn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        picConn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        picConn.setRequestProperty("Cookie","yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; " +
                "csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH; " +
                "Hm_lvt_f5127c6793d40d199f68042b8a63e725=1478845119; " +
                "Hm_lpvt_f5127c6793d40d199f68042b8a63e725=1478846104; " +
                cookie);
        picConn.setRequestMethod("GET");//gjl895706594
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
            String reg = "/media/avatar/.*..*\"";
            Pattern pattern = Pattern.compile(reg);
            // 忽略大小写的写法
            Matcher matcher = pattern.matcher(html);
            // 字符串是否与正则表达式相匹配
            while(matcher.find())
                ret ="http://acm.swust.edu.cn"+ matcher.group();

        } else {
            System.out.println("链接失败.........");
        }
        //System.out.println(html);
        ret = ret.substring(0,ret.length()-1);
        System.out.println(ret);
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
