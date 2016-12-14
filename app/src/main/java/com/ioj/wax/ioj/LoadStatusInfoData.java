package com.ioj.wax.ioj;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoadStatusInfoData {
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
}
