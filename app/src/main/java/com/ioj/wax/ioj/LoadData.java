package com.ioj.wax.ioj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

public class LoadData {
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
    public static void LoadStatusInfoData(List<StatusInfo> StatusInfoData, int page, boolean clear) throws Exception {
        if (clear) StatusInfoData.clear();
        //http://acm.swust.edu.cn/problem/jallstatus/?page=1&purview=normal&contest_id=
        String path = "http://acm.swust.edu.cn/problem/jallstatus/?page=" + page + "&purview=normal&contest_id=";
        URL url = new URL(path.trim());
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        int id = conn.getResponseCode();
        if (id == 200) {// 判断请求码是否200，否则为失败
            InputStream is = conn.getInputStream(); // 获取输入流
            byte[] data = readStream(is); // 把输入流转换成字符串组
            String json = new String(data); // 把字符串组转换成字符串
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
                        jsonobj2.getString("submit_time")));
            }
            //map=(LinkedHashMap<String,String>)jsonObject.get("ranks");
        }
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
    public static void initData(List<Ranklist_p> RankData,int type,int page,boolean clear) throws Exception {
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
                RankData.add(new Ranklist_p(jsonobj2.getString("username"),getHttpBitmap(picUrl),
                        jsonobj2.getString("maxim").isEmpty()?"我不想说话.":jsonobj2.getString("maxim"),
                        jsonobj2.getString("solved"),jsonobj2.getString("submit"),jsonobj2.getString("rank_num")));
            }
            //map=(LinkedHashMap<String,String>)jsonObject.get("ranks");
        }
    }
    public static Bitmap getHttpBitmap(String url){
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
    public static void LoadContestProblems(Count mCount,List<Problems_p> ProblemsData,String contestId,String cookies) throws IOException {
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
}
