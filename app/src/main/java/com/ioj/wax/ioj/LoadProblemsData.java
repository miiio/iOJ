package com.ioj.wax.ioj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadProblemsData {
    public static void LoadProblems(List<Problems_p>ProblemsData, int page, UserInfo user, boolean clear) throws IOException, JSONException {
        if(clear)ProblemsData.clear();
        //http://acm.swust.edu.cn/problem/jlist/
        String path = "http://acm.swust.edu.cn/problem/jlist/";
        URL url = new URL(path);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        // 设置请求的方式
        urlConnection.setRequestMethod("POST");
        // 设置请求的超时时间
        urlConnection.setReadTimeout(5000);
        urlConnection.setConnectTimeout(5000);
        // 传递的数据
        //page=2&operation=ALL&csrfmiddlewaretoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH
        String data = "page=" + URLEncoder.encode(page+"", "UTF-8")
                + "&operation=" + URLEncoder.encode("ALL", "UTF-8")+"&csrfmiddlewaretoken="+URLEncoder.encode("Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH","UTF-8");
        // 设置请求的头
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        urlConnection.setRequestProperty("Accept", "*/*");
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        urlConnection.setRequestProperty("Accept-Language:zh-CN", "zh-CN,zh;q=0.8");
        urlConnection.setRequestProperty("Connection", "keep-alive");
        urlConnection.setRequestProperty("Host", "acm.swust.edu.cn");
        urlConnection.setRequestProperty("Origin", "http://acm.swust.edu.cn");
        urlConnection.setRequestProperty("Referer:", "http://acm.swust.edu.cn/mobile/index/");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
        if(user.isLogin()){
            urlConnection.setRequestProperty("Cookie","yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; " +
                    "csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH; " +
                    "Hm_lvt_f5127c6793d40d199f68042b8a63e725=1478845119; " +
                    "Hm_lpvt_f5127c6793d40d199f68042b8a63e725=1478846104; " +
                    user.getCookie());
        }
        else {
            urlConnection.setRequestProperty("Cookie", "yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH");
        }
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
            String html = new String(baos.toByteArray());
            //获取头像url
            JSONObject jsonobj  = new JSONObject(html); // 返回的数据形式是一个Object类型，所以可以直接转换成一个Object
            JSONArray jsonArr = new JSONArray(jsonobj.getString("problems"));
            for(int i = 0;i<jsonArr.length();i++)
            {
                JSONObject jsonobj2 = jsonArr.getJSONObject(i);
                //if(maxim.isEmpty()) {this.maxim="我不想说话.";}
                //"submit_num": 2846, "ac_num": 1227
                double diff =(jsonobj2.getInt("ac_num")*1.0) / (jsonobj2.getInt("submit_num")*1.0) *100;
                ProblemsData.add(new Problems_p(jsonobj2.getString("id"),
                        jsonobj2.getString("title"),jsonobj2.getString("ac"),100-(int)diff,"0"));
            }
        }
    }
    public static void SearchProblems(String title,List<Problems_p>ProblemsData, int page, UserInfo user) throws IOException, JSONException {
        ProblemsData.clear();
        //csrfmiddlewaretoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH&_id=
        // &title=test&source=&cloud=&page=1&operation=COMBINE
        String path = "http://acm.swust.edu.cn/problem/jlist/";
        URL url = new URL(path);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        // 设置请求的方式
        urlConnection.setRequestMethod("POST");
        // 设置请求的超时时间
        urlConnection.setReadTimeout(5000);
        urlConnection.setConnectTimeout(5000);
        // 传递的数据
        //page=2&operation=ALL&csrfmiddlewaretoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH
        String data = "csrfmiddlewaretoken="+
                URLEncoder.encode("Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH","UTF-8")
                +"&_id=&title="+URLEncoder.encode(title)+"&source=&cloud=&page=1&operation=COMBINE";
        // 设置请求的头
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        urlConnection.setRequestProperty("Accept", "*/*");
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        urlConnection.setRequestProperty("Accept-Language:zh-CN", "zh-CN,zh;q=0.8");
        urlConnection.setRequestProperty("Connection", "keep-alive");
        urlConnection.setRequestProperty("Host", "acm.swust.edu.cn");
        urlConnection.setRequestProperty("Origin", "http://acm.swust.edu.cn");
        urlConnection.setRequestProperty("Referer:", "http://acm.swust.edu.cn/mobile/index/");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
        if(user.isLogin()){
            urlConnection.setRequestProperty("Cookie","yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; " +
                    "csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH; " +
                    "Hm_lvt_f5127c6793d40d199f68042b8a63e725=1478845119; " +
                    "Hm_lpvt_f5127c6793d40d199f68042b8a63e725=1478846104; " +
                    user.getCookie());
        }
        else {
            urlConnection.setRequestProperty("Cookie", "yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH");
        }
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
            String html = new String(baos.toByteArray());
            //获取头像url
            JSONObject jsonobj  = new JSONObject(html); // 返回的数据形式是一个Object类型，所以可以直接转换成一个Object
            JSONArray jsonArr = new JSONArray(jsonobj.getString("problems"));
            for(int i = 0;i<jsonArr.length();i++)
            {
                JSONObject jsonobj2 = jsonArr.getJSONObject(i);
                double diff =(jsonobj2.getInt("ac_num")*1.0) / (jsonobj2.getInt("submit_num")*1.0) *100;
                ProblemsData.add(new Problems_p(jsonobj2.getString("id"),
                        jsonobj2.getString("title"),jsonobj2.getString("ac"),100-(int)diff,"0"));
            }
        }
    }

}
