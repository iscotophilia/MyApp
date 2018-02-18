package sazhijie.com.myapp;

/**
 *Created by sazhijie on 2018/1/25.
 * 主界面Activity
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.*;
import java.net.*;



public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvpic;
    private ImageView ivpic;
    private DrawerLayout mDrawerLayout;
    private Bitmap bimage;
    private Handler hr;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start);

        Newnav();
        findCrl();
        setCrl();
        ivpic.setOnClickListener(this);
    }

    public void Newnav(){
        navView= findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_wanshan);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_wanshan:
                        break;
                    case R.id.nav_xiugai:
                        break;
                }

                mDrawerLayout.closeDrawer(Gravity.LEFT);
                return false;
            }
        });
    }

    public void findCrl(){
        View hv = navView.getHeaderView(0);
        tvpic = hv.findViewById(R.id.nav_tv_usr);
        ivpic = hv.findViewById(R.id.nav_iv_img);
        hr = new MyHandler();
    }

    public void setCrl(){
        tvpic.setText(Constant.usr);
        Thread t = new tdShowPic();
        t.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ivpic.getId()){
            Log.e("点击","0");
        }
    }

    class tdShowPic extends Thread{
        @Override
        public void run(){
            bimage=  getBitmapFromURL(Constant.pic);
            Message msg = hr.obtainMessage(1);
            hr.sendMessage(msg);
        }
    }

    class MyHandler extends Handler{
        public void handleMessage (Message msg) {
            if (msg.what == 1){
                ivpic.setImageBitmap(bimage);
            }
        }
    }

    //根据URL拉取图片
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}
