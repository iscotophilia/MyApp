package sazhijie.com.myapp.Connect;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import sazhijie.com.myapp.Constant;

/*
 Created by sazhijie on 2018/1/24.
 用使用GET方法来拉取微博用户信息
 */

public class WHttpConnectLogin {

    public static boolean readContentFromGet(Map<String,String> m) throws IOException {
        String token = null;
        String uid = null;
        String username = null;
        String pic = null;
        String sex = null;

        token = m.get("access_token");
        uid = m.get("uid");

        String param = "access_token="+token+"&"+"uid="+uid;
        String url = "https://api.weibo.com/2/users/show.json" + "?" + param;

        Log.i("URL",url);

        Map<String,String> map = new HashMap<String, String>();
        URL realurl = new URL(url);
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) realurl.openConnection();
            urlConnection.setRequestMethod("GET");                                     //发送一个POST请求
            urlConnection.setInstanceFollowRedirects(true);                             //自动执行HTTP重定向
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");   //设置内容类型
            // 固定格式
            urlConnection.setRequestProperty("Charset", "utf-8");

            urlConnection.setRequestProperty("accept", "*/*");
            urlConnection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                StringBuffer jb = new StringBuffer();
                String inputLine = null;
                while ((inputLine = buffer.readLine())!=null){
                    Log.w("微博登录返回",inputLine);
                    jb.append(URLDecoder.decode(inputLine,"utf-8"));
                }
                JSONObject jsonObject = JSONObject.parseObject(jb.toString());
                pic = jsonObject.getString("profile_image_url");
                username = jsonObject.getString("name");
                sex = jsonObject.getString("gender");
            }else{
                System.out.println("error!!");
                return false;
            }
            urlConnection.disconnect();
        }catch (Exception e) {
            System.out.println("error!");
            return  false;
        }
        Constant.usr = username;
        Constant.pic = pic;
        Constant.sex = sex;
        return true;
    }
}
