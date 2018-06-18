package com.example.paotui.xiaoyuanpaotui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Home_Fragment homeFragment;
    private Account_Fragment accountFragment;
    private Order_Fragment orderFragment;

    private View homeLayout;
    private View orderLayout;
    private View accountLayout;

    private ImageView homeImage;
    private ImageView orderImage;
    private ImageView accountImage;

    private TextView homeText;
    private TextView orderText;
    private TextView accountText;

    private FragmentManager fragmentManager;

    private static boolean isExit = false;
    private String FRAG_id;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        fragmentManager=getFragmentManager();
        Bundle bundle = this.getIntent().getExtras();
        FRAG_id = bundle.getString("FRAG_id");
        Log.v("TAGASD",FRAG_id);
        if (FRAG_id.equals("3")){
            setTabSelection(2);
        }else if(FRAG_id.equals("2")){
            setTabSelection(1);
        }else {
            setTabSelection(0);
        }
    }



    /**
     * 在这里获取到每个需要用到的控件的实例，并设置好点击事件。
     */
    private void initView() {
        homeLayout  = findViewById(R.id.home_layout);
        orderLayout = findViewById(R.id.order_layout);
        accountLayout  = findViewById(R.id.account_layout);

        homeImage  = findViewById(R.id.home_image);
        orderImage = findViewById(R.id.order_image);
        accountImage  = findViewById(R.id.account_image);

        homeText  = findViewById(R.id.home_text);
        orderText = findViewById(R.id.order_text);
        accountText  = findViewById(R.id.account_text);

        homeLayout.setOnClickListener(this);
        orderLayout.setOnClickListener(this);
        accountLayout.setOnClickListener(this);
    }

    /*private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction =fragmentManager.beginTransaction();
        transaction.replace(R.id.show_layout,fragment);
        transaction.commit();

    }*/


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_layout:
                setTabSelection(0);
                break;
            case R.id.order_layout:
                setTabSelection(1);
                break;
            case R.id.account_layout:
                setTabSelection(2);
                break;
        }

    }
    private void setTabSelection(int index) {
        clearSelection();
        FragmentTransaction transaction =fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index){
            case 0:
                homeImage.setImageResource(R.drawable.home_selected);
                homeText.setTextColor(Color.WHITE);
                if(homeFragment == null){
                    homeFragment = new Home_Fragment();
                    transaction.add(R.id.content,homeFragment);
                }else{
                    transaction.show(homeFragment);
                }
                break;
            case 1:
                orderImage.setImageResource(R.drawable.order_selected);
                orderText.setTextColor(Color.WHITE);
                if(orderFragment == null){
                    orderFragment = new Order_Fragment();
                    transaction.add(R.id.content,orderFragment);
                }else{
                    transaction.show(orderFragment);
                }
                break;
            case 2:
                accountImage.setImageResource(R.drawable.account_selected);
                accountText.setTextColor(Color.WHITE);
                if(accountFragment == null){
                    accountFragment = new Account_Fragment();
                    transaction.add(R.id.content,accountFragment);
                }else{
                    transaction.show(accountFragment);
                }
                break;

        }
        transaction.commit();
    }



    private void clearSelection() {
        homeImage.setImageResource(R.drawable.home_unselected);
        homeText.setTextColor(Color.parseColor("#82858b"));
        orderImage.setImageResource(R.drawable.order_unselected);
        orderText.setTextColor(Color.parseColor("#82858b"));
        accountImage.setImageResource(R.drawable.account_unselected);
        accountText.setTextColor(Color.parseColor("#82858b"));
    }
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null){
            transaction.hide(homeFragment);
        }
        if (orderFragment != null){
            transaction.hide(orderFragment);
        }
        if (accountFragment != null){
            transaction.hide(accountFragment);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 0001:
                if(requestCode==RESULT_OK)
                {
                    setTabSelection(2);
                    break;
                }
            case 0002:
                if(requestCode==RESULT_OK)
                {
                    setTabSelection(2);
                    break;
                }
            case 0003:
                if(requestCode==RESULT_OK)
                {
                    setTabSelection(2);
                    break;
                }
            case 0004:
                if(requestCode==RESULT_OK)
                {
                    setTabSelection(2);
                    break;
                }
            case 0005:
                if(requestCode==RESULT_OK)
                {
                    setTabSelection(2);
                    break;
                }

        }
    }
}