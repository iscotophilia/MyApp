package sazhijie.com.myapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.util.Random;
import sazhijie.com.myapp.Connect.HttpConnectRegister;
import sazhijie.com.myapp.Connect.SendMsg;

/**
 * Created by sazhijie on 2018/1/25.
 */

public class RgtActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener{

    private Toolbar tb;
    private TextInputLayout tiusr;
    private TextInputLayout tinum;
    private TextInputLayout ticnm;
    private TextInputLayout tipwd;
    private TextInputLayout tidbepwd;
    private CardView cvusr;
    private CardView cvnum;
    private CardView cvcnm;
    private CardView cvpwd;
    private CardView cvdbpwd;
    private EditText etusr;
    private EditText etnum;
    private EditText etcnm;
    private EditText etpwd;
    private EditText etdbpwd;
    private Button btnsnd;
    private Button btnrgt;
    private ProgressBar pbcir;
    private Handler hr;

    private int time1 = 60,time2 = 300;//1分钟后才可再点击发送验证码，5分钟内有效
    private boolean td_issend = true;
    private String checknum = "";

    private String usr = "";
    private String pwd = "";
    private String num = "";
    private Thread tdclock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgt);

        findCrl();
        addListener();
        newC();
        setCrl();
    }

    public void newC(){
        hr = new ConnectHandler();
        tdclock = new tdClock();
    }

    public void findCrl(){
        tb = findViewById(R.id.rgt_tb);
        tiusr = findViewById(R.id.rgt_ti_usr);
        tinum = findViewById(R.id.rgt_ti_num);
        ticnm = findViewById(R.id.rgt_ti_cnm);
        tipwd = findViewById(R.id.rgt_ti_pwd);
        tidbepwd = findViewById(R.id.rgt_ti_dbepwd);
        cvusr = findViewById(R.id.rgt_cv_usr);
        cvnum = findViewById(R.id.rgt_cv_num);
        cvcnm = findViewById(R.id.rgt_cv_cnm);
        cvpwd = findViewById(R.id.rgt_cv_pwd);
        cvdbpwd = findViewById(R.id.rgt_cv_dbpwd);
        etusr = findViewById(R.id.rgt_et_usr);
        etnum = findViewById(R.id.rgt_et_num);
        etcnm = findViewById(R.id.rgt_et_cnm);
        etpwd = findViewById(R.id.rgt_et_pwd);
        etdbpwd = findViewById(R.id.rgt_et_dbpwd);
        btnsnd = findViewById(R.id.rgt_btn_snd);
        btnrgt = findViewById(R.id.rgt_btn_rgt);
        pbcir = findViewById(R.id.rgt_pb_cir);
    }

    public void addListener(){
        btnsnd.setOnClickListener(this);
        btnrgt.setOnClickListener(this);
        etusr.setOnFocusChangeListener(this);
        etnum.setOnFocusChangeListener(this);
        etcnm.setOnFocusChangeListener(this);
        etpwd.setOnFocusChangeListener(this);
        etdbpwd.setOnFocusChangeListener(this);
    }

    public void setCrl(){
        setSupportActionBar(tb);
        tb.setTitle("返回");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.rgt_btn_snd:
                if (tinum.getEditText().getText().toString().equals("")){
                    tinum.setError("手机号不能为空");
                    break;
                }
                time1 = 60;
                time2=3000;
                btnsnd.setEnabled(false);
                setNum(tinum.getEditText().getText().toString());//保存手机号，防止受到验证码后修改手机号
                if (!tdclock.isAlive()){
                    td_issend = true;
                    tdclock.start();
                }
                Thread tdsend = new tdSend();
                tdsend.start();
                break;
            case R.id.rgt_btn_rgt:
                Rgt();
                break;
        }
    }

    public void Rgt(){
        System.out.println("验证码为"+checknum);
        if (tiusr.getEditText().getText().toString().equals("")){
            tiusr.setError("用户名不能为空");
        } else if (tinum.getEditText().getText().toString().equals("")){
            tinum.setError("手机号不能为空");
        } else if (ticnm.getEditText().getText().toString().equals("")){
            ticnm.setError("验证码不能为空");
        } else if (tipwd.getEditText().getText().toString().equals("")){
            tipwd.setError("密码不能为空");
        } else if (!tipwd.getEditText().getText().toString().equals(tidbepwd.getEditText().getText().toString())){//密码检测
            tidbepwd.setError("两次输入密码不一致");
        } else if (!ticnm.getEditText().getText().toString().equals(checknum)){//验证码检测
            AlertDialog.Builder ab = new AlertDialog.Builder(RgtActivity.this);
            ab.setTitle("注册失败").setMessage("验证码错误");
            ab.setNegativeButton("确定",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    etcnm.requestFocus();
                }
            });
            ab.show();
        } else if (!tinum.getEditText().getText().toString().equals(getNum())){
            tinum.setError("手机号与验证码不匹配");
        } else {//注册
            pbcir.setVisibility(View.VISIBLE);
            setUsr(tiusr.getEditText().getText().toString());
            setPwd(tipwd.getEditText().getText().toString());
            setNum(tinum.getEditText().getText().toString());
            Thread t = new tdRgt();
            t.start();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {//文本框切换效果
        if (v.getId() == etusr.getId()){
            if (hasFocus){
                cvusr.setElevation(10);
            }else {
                cvusr.setElevation(5);
            }
        }
        if (v.getId() == etnum.getId()){
            if (hasFocus){
                cvnum.setElevation(10);
            }else {
                cvnum.setElevation(5);
            }
        }
        if (v.getId() == etcnm.getId()){
            if (hasFocus){
                cvcnm.setElevation(10);
            }else {
                cvcnm.setElevation(5);
            }
        }
        if (v.getId() == etpwd.getId()){
            if (hasFocus){
                cvpwd.setElevation(10);
            }else {
                cvpwd.setElevation(5);
            }
        }
        if (v.getId() == etdbpwd.getId()){
            if (hasFocus){
                cvdbpwd.setElevation(10);
            }else {
                cvdbpwd.setElevation(5);
                if (!tipwd.getEditText().toString().equals(tidbepwd.getEditText().toString())){
                    tidbepwd.setError("两次密码不一致");
                }else{
                    tidbepwd.setError(null);
                }
            }
        }
    }


    private String getRandom(){//随机生成验证码
        Random random = new Random();
        String s = "";
        for (int i = 0; i <6; i++){
            s+= random.nextInt(10);
        }
        checknum = s;
        return s;
    }

    private void SendMsg(){//发送短信
        SendMsg sendMsg = new SendMsg(tinum.getEditText().getText().toString(),getRandom());
        try{
            Boolean b =sendMsg.Send();
            Message msg = new Message();
            Bundle bundle = new Bundle();
            if (b){
                bundle.putString("send","0");
            }else {
                bundle.putString("send","1");
            }
            msg.setData(bundle);
            hr.sendMessage(msg);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    private class tdSend extends Thread{//启动线程发送短信
        @Override
        public void run(){
            SendMsg();
            System.out.println("发送消息");
        }
    }

    private class tdClock extends Thread{
        @Override
        public void run(){
            while(td_issend){
                try{
                    time1 --;
                    time2 --;
                    if(time2 == 0){
                        checknum = "******";//重置验证码
                        td_issend = false;//线程自我阻塞
                    }
                    if(time1 >= 0){
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        b.putInt("time",time1);
                        msg.setData(b);
                        hr.sendMessage(msg);
                    }
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private class tdRgt extends Thread{
        @Override
        public void run(){
            try {
                String result = HttpConnectRegister.readContentFromPost(getUsr(),getNum(),getPwd());
                Message msg = hr.obtainMessage();
                Bundle b = new Bundle();
                b.putString("result",result);
                msg.setData(b);
                hr.sendMessage(msg);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showDialog(String s){
        if(s.equals("Success")){
            AlertDialog.Builder ab = new AlertDialog.Builder(RgtActivity.this);
            ab.setTitle("注册成功").setMessage("注册成功！");
            ab.setNegativeButton("返回登录",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(RgtActivity.this,MainActivity.class);
                    RgtActivity.this.startActivity(intent);
                    RgtActivity.this.finish();
                }
            });
            ab.show();
        }else if(s.equals("Failed1")){
            AlertDialog.Builder ab = new AlertDialog.Builder(RgtActivity.this);
            ab.setTitle("注册失败").setMessage("已有用户名");
            ab.setNegativeButton("确定",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    etusr.requestFocus();
                }
            });
            ab.show();
        }else if(s.equals("Failed11")){
            AlertDialog.Builder ab = new AlertDialog.Builder(RgtActivity.this);
            ab.setTitle("注册失败").setMessage("手机号已被注册");
            ab.setNegativeButton("确定",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    etnum.requestFocus();
                }
            });
            ab.show();
        }else if (s.equals("Failed2") || s.equals("Failed3")){
            AlertDialog.Builder ab = new AlertDialog.Builder(RgtActivity.this);
            ab.setTitle("注册失败").setMessage("注册失败，请稍后重试");
            ab.setNegativeButton("确定",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
            ab.show();
        }else if (s.equals("error!") || s.equals("error!!")){
            AlertDialog.Builder ab = new AlertDialog.Builder(RgtActivity.this);
            ab.setTitle("注册失败").setMessage("与服务器连接错误");
            ab.setNegativeButton("确定",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
            ab.show();
        }
    }

    class ConnectHandler extends Handler{
        public void handleMessage (Message msg) {
            Bundle b = msg.getData();
            int i = b.getInt("time");
            if(i < 60 && i>0){
                btnsnd.setText("发送"+"("+i+")");
                btnsnd.setEnabled(false);
            }
            if(i == 0){
                btnsnd.setText("发送");
                btnsnd.setEnabled(true);
            }

            String s = b.getString("result");
            pbcir.setVisibility(View.INVISIBLE);
            if (s != null){
                showDialog(s);
            }

            String snd = "";
            snd = b.getString("send");
            if (snd != null){
                if (snd.equals("0")){
                    Toast.makeText(RgtActivity.this,"验证码发送成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RgtActivity.this,"验证码发送失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
