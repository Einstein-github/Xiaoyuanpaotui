package com.example.paotui.xiaoyuanpaotui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * Created by Colonel on 2018/5/19.
 */

public class Order_get extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private TextView order_get_release;

    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令

    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    private static  String PHONE;
    private static String USER_id;
     EditText order_get_content_edit,
             order_get_price_edit,
             order_get_num_edit
             ,order_get_name_edit;
     RelativeLayout order_get_address_layout;
     TextView order_get_address_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_get);
        init();

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        order_get_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(runnable).start();
            }
        });
        order_get_address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int flag =2;
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("FLAG",flag);
                intent.putExtras(bundle);
                intent.setClass(getApplicationContext(),Account_address.class);
                startActivity(intent);
                finish();

            }
        });
        order_get_address_text.setText(sharedPreference.getString("ACCOUNT_ADDRESS","暨阳学院"));

    }
    private void init(){
        order_get_address_text= findViewById(R.id.order_get_address_text);
        order_get_release = findViewById(R.id.order_get_release);
        order_get_address_layout= findViewById(R.id.order_get_address_layout);
        order_get_content_edit = findViewById(R.id.order_get_content_edit);
        order_get_price_edit = findViewById(R.id.order_get_price_edit);
        order_get_num_edit = findViewById(R.id.order_get_price_edit);
        order_get_name_edit = findViewById(R.id.order_get_name_edit);
        toolbar = findViewById(R.id.order_get_toolbar);
        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
    }

    Runnable runnable = new Runnable() {
        private Connection con = null;// 获得连接对象

        @Override

        public void run() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection(strCon, strUser, strPwd);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                sqlCon(con);    //测试数据库连接
            } catch (java.sql.SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.i("Login", "连接失败" + e.getMessage() + "");
            }
        }

        private void sqlCon(Connection con1) throws java.sql.SQLException {
            //private Connection con = null;// 获得连接对象

            //protected Integer doInBackground(Void... voids) {
            int flag = 0;
            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                String CONTENT = order_get_content_edit.getText().toString();
                String PRICE = order_get_price_edit.getText().toString();
                String PHONE_NUM = order_get_num_edit.getText().toString();
                String NAME = order_get_name_edit.getText().toString();
                PHONE = sharedPreference.getString("PHONE_NUM_VAl", "");

                int address_id ;
                address_id = sharedPreference.getInt("ACCOUNT_ADDRESS_ID",1);

                USER_id = sharedPreference.getString("USER_ID","");
                int user_id = Integer.parseInt(USER_id);

                Date date = new Date();
                String time = String.format("%ts", date);


                int rs = sta.executeUpdate("INSERT INTO bill (uid,order_name,order_telephone,content,price,address_id,type,status,addtime) VALUES (" + user_id + ", '" + NAME + "', '" + PHONE_NUM + "', '" + CONTENT + "', '" + PRICE + "', '" + address_id + "',1,1, '" + time + "')");
                if (rs > 0) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),"发布成功",Toast.LENGTH_SHORT).show();
                    String FRAG_id =2+"";
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("FRAG_id",FRAG_id);
                    intent.putExtras(bundle);
                    intent.setClass(getApplicationContext(),MainActivity.class);
                    startActivity(intent);

                    Looper.loop();

                } else {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),"发布失败",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                sta.close();
                con.close();
            }catch (Exception e) {
                e.printStackTrace();
                Log.i("Order_get", "MYSQL ERROR" + e.getMessage() + "");
            }finally {
                if (con != null)
                    try {
                        con.close();
                        Log.i("Order_get", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Order_get", "连接失败:" + sqle.getMessage() + "");
                    }
            }
        }
    };




    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

    }

}
