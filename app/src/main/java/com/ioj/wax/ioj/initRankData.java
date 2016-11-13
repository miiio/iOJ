package com.ioj.wax.ioj;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class initRankData {
    /*
        type=0:all ranklist
        type=1:month ranklist
        type=2:week ranklist
        type=3:day ranklist
        type=4:2016 ranklist
        clear
     */
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

    private static byte[] readStream(InputStream inputStream) throws Exception {
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

}