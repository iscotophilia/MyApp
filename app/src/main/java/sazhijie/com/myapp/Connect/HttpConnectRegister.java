package sazhijie.com.myapp.Connect;
import java.io.*;
import java.net.*;
/**
 * Created by sazhijie on 2018/2/5
 * 用于注册
 */

public class HttpConnectRegister {

    public static String readContentFromPost(String username,String number,String password) throws IOException {
        String result=null;
        URL url = new URL("http://120.79.184.27:8081/Register");
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

            String data="{"+"\'"+"username"+"\'"+":"+"\'"+URLEncoder.encode(username,"utf-8")+"\'"+","
                           +"\'"+"number"+"\'"+":"+"\'"+URLEncoder.encode(number,"utf-8")+"\'"+","
                           +"\'"+"password"+"\'"+":"+"\'"+URLEncoder.encode(password,"utf-8")+"\'"+"}";
            System.out.println(data);
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.length())); // 设置长度
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            out.writeBytes(data);
            out.flush();
            out.close();

            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                while ((inputLine = buffer.readLine())!=null){
                    result = inputLine;
                    System.out.println(result);
                }
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
        return result;
    }
}

