package sazhijie.com.myapp.Connect;
import java.io.IOException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
/**
 *Created by sazhijie on 2018/1/25.
 * 用于发送短信
 * 本接口适用于中国网建平台SMS服务
 */

public class SendMsg {
    private static String number="";
    private static String check="";
    public SendMsg(String phonenumber,String checknumber){
        number=phonenumber;
        check=checknumber;
    };

    public boolean Send() throws HttpException, IOException{
        if(number==null){
            return false;
        }
        if(check == null){
            return false;
        }
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod("http://gbk.sms.webchinese.cn/");
        // PostMethod post = new PostMethod("http://sms.webchinese.cn/web_api/");
        post.addRequestHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码
        NameValuePair[] data = { new NameValuePair("Uid", "你的用户名"),// 注册的用户名
                new NameValuePair("Key", "你的密钥"),// 注册成功后，登录网站后得到的密钥
                new NameValuePair("smsMob", number),// 手机号码
                new NameValuePair("smsText", "您好，您本次注册的验证码为:"+check+", 5分钟内有效") };// 短信内容
        post.setRequestBody(data);

        client.executeMethod(post);
        Header[] headers = post.getResponseHeaders();
        int statusCode = post.getStatusCode();
        System.out.println("statusCode:" + statusCode);

        for (Header h : headers) {
            System.out.println("---" + h.toString());
        }
        String result = new String(post.getResponseBodyAsString().getBytes(
                "gbk"));
        if(result.equals("1")){
            return true;
        }
        System.out.println(result);
        return false;
    }
}
