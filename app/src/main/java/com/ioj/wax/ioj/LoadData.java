package com.ioj.wax.ioj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadData {
    public static String getStrMid(String str,String left,String right){
        int len = left.length();
        int indexl = str.indexOf(left);
        int indexr = str.indexOf(right,indexl+len);
        return str.substring(indexl+len,indexr);
    }
    /***
     * delete CRLF; delete empty line ;delete blank lines
     *
     * @param input
     * @return
     */
    private static String deleteCRLFOnce(String input) {
        return input.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");
    }

    /**
     * delete CRLF; delete empty line ;delete blank lines
     *
     * @param input
     * @return
     */
    public static String deleteCRLF(String input) {
        input =deleteCRLFOnce(input);
        return deleteCRLFOnce(input);
    }
    public static void LoadProblems(List<Problems_p> ProblemsData, int page, UserInfo user, boolean clear) throws IOException, JSONException {
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
    public static void LoadStatusInfoData_o(List<StatusInfo> StatusInfoData, int page, boolean clear,String username,String prbId,String result) throws Exception {
        if (clear) StatusInfoData.clear();
        //http://acm.swust.edu.cn/problem/jallstatus/?page=1&purview=normal&contest_id=
        String path = "http://acm.swust.edu.cn/problem/jallstatus/";
        URL url = new URL(path.trim());
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // 设置请求的方式
        conn.setRequestMethod("POST");
        // 设置请求的超时时间
        conn.setReadTimeout(5000);
        conn.setConnectTimeout(5000);
        // 传递的数据
        //?page=" + page +
        //"&purview=normal&contest_id="+"&userid="+username+"&problemid="+prbId+"&result="+result+
                //"&csrfmiddlewaretoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH'";
        String data = "csrfmiddlewaretoken="+
                URLEncoder.encode("Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH","UTF-8")
                +"&userid="+username+"&problemid="+prbId+
                "&result="+result+"&compiler=&page="+page+"&purview=normal&contest_id=";
        // 设置请求的头
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setRequestProperty("Accept-Language:zh-CN", "zh-CN,zh;q=0.8");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Host", "acm.swust.edu.cn");
        conn.setRequestProperty("Origin", "http://acm.swust.edu.cn");
        conn.setRequestProperty("Referer:", "http://acm.swust.edu.cn/problem/allstatus/");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
        conn.setRequestProperty("Cookie", "yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH");
        conn.setDoOutput(true); // 发送POST请求必须设置允许输出
        conn.setDoInput(true); // 发送POST请求必须设置允许输入
        //setDoInput的默认值就是true
        //获取输出流
        OutputStream os = conn.getOutputStream();
        os.write(data.getBytes());
        os.flush();
        int id = conn.getResponseCode();
        if (id == 200) {// 判断请求码是否200，否则为失败
            InputStream is = conn.getInputStream(); // 获取输入流
            byte[] datas = readStream(is); // 把输入流转换成字符串组
            String json = new String(datas); // 把字符串组转换成字符串
            JSONObject jsonobj  = new JSONObject(json); // 返回的数据形式是一个Object类型，所以可以直接转换成一个Object
            JSONArray jsonArr = new JSONArray(jsonobj.getString("submits"));
            for(int i = 0;i<jsonArr.length();i++)
            {
                JSONObject jsonobj2 = jsonArr.getJSONObject(i);
                StatusInfoData.add(new StatusInfo(
                        jsonobj2.getString("username"),
                        jsonobj2.getString("problemid"),
                        jsonobj2.getString("result"),
                        jsonobj2.getString("memory"),
                        jsonobj2.getString("time"),
                        jsonobj2.getString("length"),
                        jsonobj2.getString("compiler"),
                        jsonobj2.getString("submit_time"),
                        jsonobj2.getString("id")));
            }
            //map=(LinkedHashMap<String,String>)jsonObject.get("ranks");
        }
    }
    public static void LoadStatusInfoData(List<StatusInfo> StatusInfoData, int page, boolean clear) throws Exception {
        LoadStatusInfoData_o(StatusInfoData,page,clear,"","","");
    }
    public static byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();
        return bout.toByteArray();
    }
    public static void initData(Context context,List<Ranklist_p> RankData,int type,int page,boolean clear) throws Exception {
        //RankData=new ArrayList<Ranklist_p>();
        String path = "http://acm.swust.edu.cn/user/jranklist/?page="+page+"&operation=ALL&range=0";
        URL url = new URL(path.trim());
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        int id = conn.getResponseCode();
        if (id == 200) {// 判断请求码是否200，否则为失败
            InputStream is = conn.getInputStream(); // 获取输入流
            byte[] data = readStream(is); // 把输入流转换成字符串组
            String json = new String(data); // 把字符串组转换成字符串
            JSONObject jsonobj  = new JSONObject(json); // 返回的数据形式是一个Object类型，所以可以直接转换成一个Object
            JSONArray jsonArr = new JSONArray(jsonobj.getString("ranks"));
            if(clear)   RankData.clear();
            for(int i = 0;i<jsonArr.length();i++)
            {

                JSONObject jsonobj2 = jsonArr.getJSONObject(i);
                //if(maxim.isEmpty()) {this.maxim="我不想说话.";}
                String picUrl = "http://acm.swust.edu.cn" + jsonobj2.getString("avatar");
                RankData.add(new Ranklist_p(jsonobj2.getString("username"),getHttpBitmap(context,picUrl),
                        jsonobj2.getString("maxim").isEmpty()?"我不想说话.":jsonobj2.getString("maxim"),
                        jsonobj2.getString("solved"),jsonobj2.getString("submit"),jsonobj2.getString("rank_num"),picUrl));
            }
            //map=(LinkedHashMap<String,String>)jsonObject.get("ranks");
        }
    }
    public static void LoadContestData(List<ContestInfo> ContestData, int page, UserInfo user, boolean clear) throws IOException, JSONException {
        if(clear)ContestData.clear();
        //http://acm.swust.edu.cn/problem/jlist/
        String path = "http://acm.swust.edu.cn/contest/jlist/";
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
                + "&operation=" + URLEncoder.encode("ALL", "UTF-8")+
                "&csrfmiddlewaretoken="+URLEncoder.encode("Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH","UTF-8");
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
            JSONObject jsonobj  = new JSONObject(html); // 返回的数据形式是一个Object类型，所以可以直接转换成一个Object
            JSONArray jsonArr = new JSONArray(jsonobj.getString("contests"));
            for(int i = 0;i<jsonArr.length();i++)
            {
                JSONObject jsonobj2 = jsonArr.getJSONObject(i);
                String type = "Experiment";
                if(jsonobj2.getString("is_contest")=="true")
                    type = "Contest";
                String purview = "Public";
                if(jsonobj2.getString("is_private") .equals( "true"))
                    purview = "Private";
                String statusid = jsonobj2.getString("status");
                String status;
                if(statusid == "0")
                    status = "Running";
                else if(statusid == "1")
                    status = "Pending";
                else{
                    status = "Ended";
                }
                String holder = jsonobj2.getString("holder");
                if(holder.isEmpty())holder="NULL";
                ContestData.add(new ContestInfo(type,jsonobj2.getString("id"),
                        jsonobj2.getString("title"),jsonobj2.getString("start_time"),
                        jsonobj2.getString("end_time"),statusid,purview,
                        holder));
            }
        }
    }
    public static String LoadContestProblems(Count mCount,List<Problems_p> ProblemsData,String contestId,String cookies) throws IOException {
        ProblemsData.clear();
        //http://acm.swust.edu.cn/contest/0273/problem/
        URL ContestUrl = new URL("http://acm.swust.edu.cn/contest/"+contestId+"/problem/");
        HttpURLConnection Conn = (HttpURLConnection)ContestUrl.openConnection();
        Conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        Conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        Conn.setRequestProperty("Cookie","yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; " +
                "csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH; " +
                "Hm_lvt_f5127c6793d40d199f68042b8a63e725=1478845119; " +
                "Hm_lpvt_f5127c6793d40d199f68042b8a63e725=1478846104; " +
                cookies);
        Conn.setRequestMethod("GET");
        String html=null;
        String servertime = null;
        int id = Conn.getResponseCode();
        if (id == 200) {
            // 获取响应的输入流对象
            InputStream is = Conn.getInputStream();
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
            String regTime = "Server time: (.*)</div>";
            Pattern pattime = Pattern.compile(regTime);
            Matcher mattime = pattime.matcher(html);
            while(mattime.find()){
                servertime = mattime.group(1);
            }
            String regEx = "/problem/(.*)/\">(.*)</a></td>";
            Pattern pat = Pattern.compile(regEx);
            Matcher mat = pat.matcher(html);
            String regEx2 = "dth\">(.*)</td>";
            Pattern pat2 = Pattern.compile(regEx2);
            Matcher mat2 = pat2.matcher(html);
            while(mat.find()){
                mat2.find();
                String str1=mat.group(1);
                str1 = "</td>\n" +
                        "      \n" +
                        "\n" +
                        "      <td><a href=\"/contest/"+contestId+"/problem/"+str1+"/\">";

                int index = html.indexOf(str1);
                String isac = "false";
                String pre = html.substring(index-2,index);
                int score=Integer.parseInt(mat2.group(1));
                mCount.TotalScore+=score;
                if(pre.equals("\">") ){
                    isac = "true";
                    mCount.AcNum++;
                    mCount.Score +=score;
                }
                ProblemsData.add(new Problems_p(mat.group(1),mat.group(2),isac,0,contestId,mat2.group(1)));
                mCount.ProblemsNum++;
            }
        }
        return servertime;
    }
    public static boolean SubmitProblem(String PrbId,String ContestId,String Code,String compile,String cookies)throws IOException, JSONException{
        String path = "http://acm.swust.edu.cn/contest/"+ContestId+"/problem/"+PrbId+"/";
        if(ContestId.equals("0")){
            path = "http://acm.swust.edu.cn/problem/"+PrbId+"/";
        }
        URL url = new URL(path);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        // 设置请求的方式
        urlConnection.setRequestMethod("POST");
        // 设置请求的超时时间
        urlConnection.setReadTimeout(5000);
        urlConnection.setConnectTimeout(5000);
        // 传递的数据
        //csrfmiddlewaretoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH&compiler=gcc&problemid=2&source_code=1
        //csrfmiddlewaretoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH&compiler=g%2B%2B&problemid=34&source_code=1
        String data = "csrfmiddlewaretoken="+
                URLEncoder.encode("Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH","UTF-8")
                +"&compiler="+URLEncoder.encode(compile)+"&problemid="+PrbId+"&source_code="+URLEncoder.encode(Code);
        // 设置请求的头
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        urlConnection.setRequestProperty("Cookie","yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; " +
                "csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH; " +
                "Hm_lvt_f5127c6793d40d199f68042b8a63e725=1478845119; " +
                "Hm_lpvt_f5127c6793d40d199f68042b8a63e725=1478846104; " +
                cookies);
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
            System.out.println(html);
            return true;
        }
        return false;
    }
    public static void GetUserInfo(String cookie,UserInfo info) throws IOException {
        //UserInfo ret=new UserInfo();
        info.setCookie(cookie);
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
                info.setPicurl("http://acm.swust.edu.cn" + matcherPic.group(1));
            String regMaxin = "lor:green\">(.*)</div>";
            //获取maxin
            Pattern patternMaxin = Pattern.compile(regMaxin);
            Matcher matcherMaxin = patternMaxin.matcher(html);
            while (matcherMaxin.find())
                info.setMaxin(matcherMaxin.group(1));
            if (info.getMaxin().isEmpty()) {
                info.setMaxin("我不想说话.");

            } else {
                System.out.println("链接失败.........");
            }
        }
        //System.out.println(html);
        //ret.picurl = ret.picurl.substring(0,ret.picurl.length()-1);
    }
    public static String Login(String user,String password) {
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
            rs=null;
            e.printStackTrace();
        }
        return rs;
    }
    public static String getAcId(String prbId,String username) throws Exception {
        List<StatusInfo> mStatusData = new ArrayList<StatusInfo>();
        LoadStatusInfoData_o(mStatusData,1,true,username,prbId,"0");
        if(mStatusData.size()==0){
            return null;
        }
        return mStatusData.get(0).getAcid();
    }
    public static String getMyCode(String AcId,String cookies,String username,String prbid,title tit) throws IOException {
        String ret = null;
        URL picurl = new URL("http://acm.swust.edu.cn/problem/code/" + AcId + "/");
        HttpURLConnection picConn = (HttpURLConnection) picurl.openConnection();
        picConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        picConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        picConn.setRequestProperty("Cookie", "yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; " +
                "csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH; " +
                "Hm_lvt_f5127c6793d40d199f68042b8a63e725=1478845119; " +
                "Hm_lpvt_f5127c6793d40d199f68042b8a63e725=1478846104; " +
                cookies);
        picConn.setRequestMethod("GET");
        String html = null;
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
            int pid = Integer.parseInt(prbid);
            tit.setTitle(getStrMid(html, "</a></td>\n" +
                    "      <td><a href=\"/problem/" + pid + "\">", "</a></td>\n" +
                    "      \n" +
                    "      <td>"));
            ret = getStrMid(html, "<textarea id=\"code_source\" class=\"hide\">",
                    "</textarea>\n" +
                            "\n" +
                            "<div id=\"result_div\"></div>");
            System.out.println(ret);
        }
//        ret = ret.replace("\\","\\\\");
//        ret = ret.replace("'","\\'");
        ret = ret.replace("&lt;", "<");
        ret = ret.replace("&gt;", ">");
        ret = ret.replace("&nbsp;", " ");
        ret = ret.replace("&amp;", "&");
        return ret;
    }

    public static String getResult(String ResultId){
        String str_result = "null";
        switch (ResultId){
            case "-1":
                str_result="Waiting";
                break;
            case "0":
                str_result="Accept";
                break;
            case "1":
                str_result="PE";
                break;
            case "2":
                str_result="TLE";
                break;
            case "3":
                str_result="MLE";
                break;
            case "4":
                str_result="WA";
                break;
            case "5":
                str_result="RE";
                break;
            case "6":
                str_result="OLE";
                break;
            case "7":
                str_result="CE";
                break;
            case "8":
            case "9":
                str_result="SE";
                break;
        }
        return str_result;
    }
    public static void getMyAc(List<String> acData,List<String> chData,String username) throws IOException, JSONException {
        acData.clear();
        chData.clear();
        //http://acm.swust.edu.cn/user/sproblem/20131295/
        URL picurl = new URL("http://acm.swust.edu.cn/user/sproblem/"+username+"/");
        HttpURLConnection conn = (HttpURLConnection)picurl.openConnection();
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        conn.setRequestProperty("Cookie","yunsuo_session_verify=d63358bc4c466b16467deff7f066a890; " +
                "csrftoken=Beg45JXf9ZxOM5VaTvECAfmPdbKRZIVH; " +
                "Hm_lvt_f5127c6793d40d199f68042b8a63e725=1478845119; " +
                "Hm_lpvt_f5127c6793d40d199f68042b8a63e725=1478846104; " +
                "");
        conn.setRequestMethod("GET");
        String html=null;
        int id = conn.getResponseCode();
        if (id == 200) {
            // 获取响应的输入流对象
            InputStream is = conn.getInputStream();
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
            JSONObject jsonobj  = new JSONObject(html); // 返回的数据形式是一个String类型，所以可以直接转换成一个Object
            JSONArray jsonArr = new JSONArray(jsonobj.getString("ac_pros"));
            for(int i = 0;i<jsonArr.length();i++)
            {
                acData.add(jsonArr.getString(i));
            }
            JSONArray ChjsonArr = new JSONArray(jsonobj.getString("ch_pros"));
            for(int i = 0;i<ChjsonArr.length();i++)
            {
                chData.add(ChjsonArr.getString(i));
            }
        }
    }
    public static Bitmap getHttpBitmap(Context context,String url){
        return getImage(context,url);
//        URL myFileURL;
//        Bitmap bitmap=null;
//        try{
//            myFileURL = new URL(url);
//            //获得连接
//            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
//            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
//            conn.setConnectTimeout(6000);
//            //连接设置获得数据流
//            conn.setDoInput(true);
//            //不使用缓存
//            conn.setUseCaches(true);
//            //这句可有可无，没有影响
//            //conn.connect();
//            //得到数据流
//            InputStream is = conn.getInputStream();
//            //解析得到图片
//            bitmap = BitmapFactory.decodeStream(is);
//            //关闭数据流
//            is.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return bitmap;
    }
    public static void clearImageCache(Context context){
        try {
            delAllFile(context.getCacheDir() + "/"); //删除完里面所有内容
            String filePath = context.getCacheDir() + "/";
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                flag = true;
            }
        }
        return flag;
    }

    public static  Bitmap getImage(Context context, String url) {
        if(url == null )
            return null;
        String imagePath="";
        String   fileName   = "";
        Bitmap mBitmap=null;

        // 获取url中图片的文件名与后缀
        if(url!=null&&url.length()!=0){
            fileName  = url.substring(url.lastIndexOf("/")+1);
        }else{
            return null;
        }

        // 图片在手机本地的存放路径,注意：fileName为空的情况
        imagePath = context.getCacheDir() + "/" + fileName;
        //Log.i(TAG,"imagePath = " + imagePath);
        File file = new File(context.getCacheDir(),fileName);// 保存文件,
        if(!file.exists())
        {
            //Log.i(TAG, "file 不存在 ");
            try {
                byte[] data =  readInputStream(getRequest(url));
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                        data.length);
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(
                        file));

                imagePath = file.getAbsolutePath();
                mBitmap = bitmap;
                //Log.i(TAG,"imagePath : file.getAbsolutePath() = " +  imagePath);

            } catch (Exception e) {
                //Log.e(TAG, e.toString());
            }
        }else{
            mBitmap = BitmapFactory.decodeFile(imagePath);
        }
        //return imagePath;
        return mBitmap;
    } // getImagePath( )结束。
    public static InputStream getRequest(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000); // 5秒
        if(conn.getResponseCode() == 200){
            return conn.getInputStream();
        }
        return null;

    }
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len = 0;
        while( (len = inStream.read(buffer)) != -1 ){
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }
}
