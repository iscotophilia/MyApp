package sazhijie.com.myapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import sazhijie.com.myapp.Connect.HttpConnectLogin;
import sazhijie.com.myapp.Connect.WHttpConnectLogin;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener{

    private TextInputLayout tiusr;
    private TextInputLayout tipwd;
    private EditText etusr;
    private EditText etpwd;
    private CardView cvusr;
    private CardView cvpwd;
    private ProgressBar pbcir;
    private Button btnlgn;
    private Button btnrgt;
    private Handler hr;
    private String num="";
    private String pwd="";
    private CircleImageView civqq;
    private CircleImageView civwb;
    private static Tencent mTencent;
    private String APPID = "你的QQ APPID";//QQ互联应用APPID
    private UserInfo mInfo;//QQ拉取用户信息
    private static boolean isServerSideLogin = false;
    private static Intent mPrizeIntent = null;
    private SsoHandler mSsoHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NewCrl();
        findCtr();
        addListener();
    }

    //新建控件
    public void NewCrl(){
        hr = new ConnectHandler();
        mTencent = Tencent.createInstance(APPID,this);
        if (null != getIntent()) {
            mPrizeIntent = getIntent();
        }
        WbSdk.install(this,new AuthInfo(this, WConstants.APP_KEY, WConstants.REDIRECT_URL, WConstants.SCOPE));
        mSsoHandler = new SsoHandler(MainActivity.this);
    }

    public void findCtr(){
        tiusr = findViewById(R.id.ti_usr);
        tipwd = findViewById(R.id.ti_pwd);
        cvusr = findViewById(R.id.cv_usr);
        cvpwd = findViewById(R.id.cv_pwd);
        etusr = findViewById(R.id.et_usr);
        etpwd = findViewById(R.id.et_pwd);
        pbcir = findViewById(R.id.pb_cir);
        btnlgn = findViewById(R.id.btn_lgn);
        btnrgt = findViewById(R.id.btn_rgt);
        civqq = findViewById(R.id.cim_iv_img);
        civwb = findViewById(R.id.cim_iv_weiboimg);
    }

    public void addListener(){
        btnlgn.setOnClickListener(this);
        btnrgt.setOnClickListener(this);
        etusr.setOnFocusChangeListener(this);
        etpwd.setOnFocusChangeListener(this);
        civqq.setOnClickListener(this);
        civwb.setOnClickListener(this);
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_lgn:
                Login();
                break;
            case R.id.btn_rgt:
                JumpRgt();
                break;
            case R.id.cim_iv_img:
                Log.i("点击","QQ");
                QQlogin();
                break;
            case R.id.cim_iv_weiboimg:
                mSsoHandler.authorize(new SelfWbAuthListener());
                break;
        }
    }

    //登录验证，验证成功后登录
    public void Login(){
        if (tiusr.getEditText().getText().toString().equals("")) {
            tiusr.setError("手机号不能为空");
        }
        else if (tipwd.getEditText().getText().toString().equals("")) {
            tipwd.setError("密码不能为空");
        }else {
            pbcir.setVisibility(View.VISIBLE);
            setNum(tiusr.getEditText().getText().toString());
            setPwd(tipwd.getEditText().getText().toString());
            Thread t = new NetWorkThread();
            t.start();
        }
    }

    //微博登录
    public class SelfWbAuthListener implements WbAuthListener{

        @Override
        public void onSuccess(final Oauth2AccessToken oauth2AccessToken) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (oauth2AccessToken.isSessionValid()){
                        AccessTokenKeeper.writeAccessToken(MainActivity.this,oauth2AccessToken);
                        Toast.makeText(MainActivity.this,"授权成功",Toast.LENGTH_SHORT).show();
                        if (oauth2AccessToken.isSessionValid()){
                            String t = oauth2AccessToken.getToken();
                            final Map<String,String> prams = new HashMap<>();
                            prams.put("access_token",oauth2AccessToken.getToken());
                            prams.put("uid",oauth2AccessToken.getUid());
                            Log.i("acees",oauth2AccessToken.getToken());
                            Log.i("uid",oauth2AccessToken.getUid());
                            new Thread(){
                                @Override
                                public void run(){
                                    try{
                                        if(WHttpConnectLogin.readContentFromGet(prams)){
                                            JumpStart();
                                        }
                                    }catch (IOException e){
                                        Log.e("微博登录","HTTP请求失败");
                                    }

                                }
                            }.start();
                        }
                    }
                }
            });
        }

        @Override
        public void cancel() {
            Toast.makeText(MainActivity.this,"授权取消",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
            Toast.makeText(MainActivity.this,"授权失败",Toast.LENGTH_SHORT).show();
        }
    }

    //QQ登录界面跳转
    public void QQlogin() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(MainActivity.this, "all", loginListener);
            isServerSideLogin = false;
        } else {
            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
                mTencent.logout(this);
                mTencent.login(MainActivity.this, "all", loginListener);
                isServerSideLogin = false;
                return;
            }
            mTencent.logout(this);
            updateUserInfo();
        }
    }

    //QQ、微博--参数回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("QQ", "回调 ");
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
        }
        Log.d("QQ","回调2");
        if (mSsoHandler != null){
            Log.i("微博","回调");
            mSsoHandler.authorizeCallBack(requestCode,resultCode,data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //QQ获取用户OPENID和TOKEN
    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
                Log.i("OPENID",openId);
            }
        } catch(Exception e) {
        }
    }

    //qq登录后拉取用户信息
    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener iUiListener = new IUiListener() {

                @Override
                public void onError(UiError e) {
                    Log.e("结果updateUserInfo","错误");
                }

                @Override
                public void onComplete(final Object response) {
                    Log.i("结果updateUserInfo","Oncomplete成功");
                    JSONObject jsonObject = (JSONObject) response;
                    try{
                        Constant.usr = jsonObject.getString("nickname");
                        Constant.pic = jsonObject.getString("figureurl_qq_2");
                        Constant.sex = jsonObject.getString("gender");
                        Toast.makeText(MainActivity.this,"授权成功",Toast.LENGTH_SHORT).show();
                        JumpStart();
                    }catch (JSONException e){
                        Log.e("JSON","解析用户信息失败");
                    }

                }

                @Override
                public void onCancel() {
                    Log.e("结果updateUserInfo","取消");
                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(iUiListener);

        }
    }

    //新建BaseListener实例重写doComplete方法
    IUiListener loginListener = new BaseUiListener(){
        @Override
        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };

    //实现IUListener
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Log.e("QQ ---> BaseListener","返回为空，登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (jsonResponse.length() == 0) {
                Log.e("QQ ---> BaseListener","返回为空，登录失败2");
                return;
            }
            Log.i("登陆成功",response.toString());
            doComplete(jsonResponse);
        }

        protected void doComplete(JSONObject values){

        }

        @Override
        public void onError(UiError e) {
            Log.e("QQ ---> BaseListener","错误");
        }

        @Override
        public void onCancel() {
            Log.e("QQ ---> BaseListener","取消");
            if (isServerSideLogin) {
                isServerSideLogin = false;
            }
        }
    }


    //用于监听文本框是否被选中，以及设置选中后的样式
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v.getId() == etusr.getId()){
            if(hasFocus){
                cvusr.setElevation(10);
            }else {
                cvusr.setElevation(3);
            }
        }
        if (v.getId() == etpwd.getId()){
            if (hasFocus){
                cvpwd.setElevation(10);
            }else {
                cvpwd.setElevation(3);
            }
        }
    }

    //开启线程访问网络实现登录
    class NetWorkThread extends Thread{
        @Override
        public void run(){
            boolean b = false;
            try{
                Map<String,String> map = new HashMap<String, String>();
                map = HttpConnectLogin.readContentFromPost(getNum(),getPwd());
                if (map.get("result").equals("Success")) {
                    b = true;
                    Constant.pic = map.get("pic");
                    Constant.usr = map.get("usr");
                    System.out.println(Constant.usr);
                    Constant.sex = map.get("sex");
                    Constant.bdy = map.get("bdy");
                }
            }catch (IOException e){
                System.err.println("解析map失败");
            }
            Message msg = hr.obtainMessage();
            msg.obj = b;
            hr.sendMessage(msg);
        }
    }

    //handler处理信息
    class ConnectHandler extends Handler{
        public void handleMessage (Message msg) {
            boolean b = (boolean)msg.obj;
            if(b) {
                JumpStart();
            }else {
                System.out.println("登录失败");
                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setTitle("登录失败").setMessage("手机号或密码错误");
                ab.setNegativeButton("确定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });
                ab.show();
            }
            pbcir.setVisibility(View.INVISIBLE);
        }
    }

    //登录成功跳转
    public void JumpStart(){
        System.out.println("登录成功");
        Constant.num = getNum();
        Constant.pwd = getPwd();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,StartActivity.class);
        MainActivity.this.startActivity(intent);
        MainActivity.this.finish();
    }

    //注册界面跳转
    public void JumpRgt() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,RgtActivity.class);
        startActivity(intent);
    }

    public void setNum(String u) {
        this.num = u;
    }
    public String getNum() {
        return this.num;
    }
    public void setPwd(String p) {
        this.pwd = p;
    }
    public String getPwd() {
        return this.pwd;
    }

}
