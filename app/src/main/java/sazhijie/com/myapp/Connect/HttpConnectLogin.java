package sazhijie.com.myapp.Connect;
import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.*;
/**
 *Created by sazhijie on 2018/1/24.
 * 用于登录
 */

public class HttpConnectLogin {

    public static Map readContentFromPost(String number, String password) throws IOException {
        String result = null;
        String pic = null;
        String username = null;
        String sex = null;
        String birthday = null;
        Map<String,String> map = new HashMap<String, String>();
        URL url = new URL("http://120.79.184.27:8081/Login");
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");                                     //发送一个POST请求
            urlConnection.setDoInput(true);                                             //向连接中写入数据
            urlConnection.setDoOutput(true);                                            //向连接中读取数据
            urlConnection.setUseCaches(false);                                          //禁止缓存
            urlConnection.setInstanceFollowRedirects(true);                             //自动执行HTTP重定向
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");   //设置内容类型
            // 固定格式
            urlConnection.setRequestProperty("Charset", "utf-8");

            String data="{"+"\'"+"number"+"\'"+":"+"\'"+URLEncoder.encode(number,"utf-8")+"\'"+","
                           +"\'"+"password"+"\'"+":"+"\'"+URLEncoder.encode(password,"utf-8")+"\'"+"}";
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length())); // 设置长度
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            out.writeBytes(data);
            out.flush();
            out.close();

            //返回成功
            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                StringBuffer jb = new StringBuffer();
                String inputLine = null;
                while ((inputLine = buffer.readLine())!=null){
                    Log.e("登录返回",inputLine);
                    jb.append(URLDecoder.decode(inputLine,"utf-8"));
                }
                JSONObject jsonObject = JSONObject.parseObject(jb.toString());
                result = jsonObject.getString("result");
                pic = jsonObject.getString("pic");
                username = jsonObject.getString("username");
                sex = jsonObject.getString("sex");
                birthday = jsonObject.getString("birthday");
            }else{
                System.out.println("error!!");
                result = "error!!";
            }
            urlConnection.disconnect();
        }catch (Exception e) {
            System.out.println("error!");
            result = "error!";
            e.printStackTrace();
        }
        map.put("result",result);
        map.put("pic",pic);
        map.put("usr",username);
        map.put("sex",sex);
        map.put("bdy",birthday);
        return map;
    }
}
