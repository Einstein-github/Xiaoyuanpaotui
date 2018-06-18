package com.example.paotui.xiaoyuanpaotui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * Created by Colonel on 2018/5/25.
 */

public class Order_detail extends AppCompatActivity  {
    private int POSITION;
    /**********JDBC连接数据库参数********/
    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令
    /**********************************/
    private String Status = "订单状态:";
    private String Now_time;
    private String Content;
    private String IMGURL;
    private String PRICE = "跑腿费:";
    private String USERNAME;
    private String Address_id;
    private String ORDER_ID;
    private String USER_id;
    private String ORDER_UID;
    private String ads;
    private String house;
    private String Address;

    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;

    Button order_detail_accept_bt;
    ImageView order_detail_image;
    TextView  order_detail_name;
    TextView  order_detail_time;
    TextView  order_detail_content;
    TextView  order_detail_address_text;
    TextView  order_detail_price_text;
    TextView  order_detail_status_text;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);
        Init();
        Bundle bundle = this.getIntent().getExtras();
        POSITION = bundle.getInt("POSITION");
        Update_dataTask update_dataTask = new Update_dataTask();
        update_dataTask.execute();

        order_detail_accept_bt. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(runnable).start();
            }
        });
        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
    }
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
    class Update_dataTask extends AsyncTask<Void,String,Void> {
        private Connection con = null;// 获得连接对象
        @Override
        protected Void doInBackground(Void... voids) {

            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                ResultSet rs = sta.executeQuery("SELECT * FROM bill,user where user.id = bill.uid ORDER BY bill.addtime DESC limit "+POSITION+",1");
                if(rs.next()) {





                    if (rs.getString("status").equals("1"))
                    {
                        Status = Status+"新发布";
                    }else if (rs.getString("status").equals("2"))
                    {
                        Status = Status+"已被抢";
                    }else if (rs.getString("status").equals("3"))
                    {
                        Status = Status+"已送达";
                    }else if (rs.getString("status").equals("4"))
                    {
                        Status = Status+"已完成";
                    }

                    Date date = new Date();
                    String time = String.format("%ts", date);
                    int  q = Integer.parseInt(time)-Integer.parseInt(rs.getString("addtime"));
                    if (q/60>0&&q/60<60){
                        Now_time = q/60+"分钟前";
                    }else if (q/3600>=1&&q/3600<24)
                    {
                        Now_time = q/3660+"小时前";
                    }else if (q/86400>=1&&q/86400<30)
                    {
                        Now_time = q/3660+"天前";
                    }
                    ORDER_ID = rs.getString("id");
                    ORDER_UID = rs.getString("uid");
                    Content = rs.getString("content");
                    IMGURL = rs.getString("photo");
                    PRICE = PRICE+rs.getString("price")+"元";
                    USERNAME = rs.getString("username");
                    Address_id =rs.getString("address_id");

                    ResultSet res = sta.executeQuery("SELECT * FROM address where id = "+Address_id+" and deltime is null");
                    if(res.next()) {
                        ads=res.getString("ads");
                        house=res.getString("house");
                    }
                    Address = ads+house;

                }
                sta.close();
                rs.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Register", "MYSQL ERROR" + e.getMessage() + "");
            } finally {
                if (con != null)
                    try {
                        con.close();
                        Log.i("Register", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Register", "连接失败:" + sqle.getMessage() + "");
                    }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setCon(IMGURL,USERNAME,Now_time,Content,Address,PRICE,Status);
            if(Status.equals("订单状态:新发布")==false)
            {
                order_detail_accept_bt.setEnabled(false);
                order_detail_accept_bt.setBackground(getDrawable(R.drawable.button_style_three));
            }
        }

        @Override
        protected void onCancelled() {
            Log.d("Order_detail", "finish");
            super.onCancelled();

        }
    }

    private void Init(){
        order_detail_image = findViewById(R.id.order_detail_image);
        order_detail_name = findViewById(R.id.order_detail_name);
        order_detail_time = findViewById(R.id.order_detail_time);
        order_detail_content = findViewById(R.id.order_detail_content);
        order_detail_address_text = findViewById(R.id.order_detail_address_text);
        order_detail_price_text = findViewById(R.id.order_detail_price_text);
        order_detail_status_text = findViewById(R.id.order_detail_status_text);
        order_detail_accept_bt = findViewById(R.id.order_detail_accept_bt);
    }

    private void setCon( String IMG_url,String name ,String time ,String content,String address,String price ,String status){
        Glide.with(getApplicationContext()).load(IMG_url).into(order_detail_image);
        order_detail_name.setText(name);
        order_detail_time.setText(time);
        order_detail_content.setText(content);
        order_detail_address_text.setText(address);
        order_detail_price_text.setText(price);
        order_detail_status_text.setText(status);
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
                Toast.makeText(getApplicationContext(),"连接失败",Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.i("Login", "连接失败"+e.getMessage()+"");
            }
        }
        private void sqlCon(Connection con1) throws java.sql.SQLException {
            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                Date date = new Date();
                String time = String.format("%ts",date);
                USER_id = sharedPreference.getString("USER_ID","");
                int user_id = Integer.parseInt(USER_id);
                int rs = sta.executeUpdate("INSERT INTO accept_bill (uid,bid,addtime) VALUES ("+user_id+", "+ORDER_ID+", '"+time+"') ");// 执行SQL语句
                if(rs>0){
                    int res = sta.executeUpdate("INSERT INTO notice (uid,bid,type,content,addtime) VALUES ("+ORDER_UID+","+ORDER_ID+","+1+", '您的订单已被接收了,请点击查看跑腿者信息', '"+time+"')");// 执行SQL语句
                    if(res>0){
                        int rees = sta.executeUpdate("UPDATE bill SET status = "+2+" WHERE id = "+ORDER_ID+" and deltime is null");
                        if(rees>0){
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(),"接单成功",Toast.LENGTH_SHORT).show();
                            String FRAG_id =2+"";
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("FRAG_id",FRAG_id);
                            intent.putExtras(bundle);
                            intent.setClass(getApplicationContext(),MainActivity.class);
                            startActivity(intent);

                            Looper.loop();

                        }else{
                            Log.d("Order_detail", "单子状态修改失败");
                        }
                    }else{
                        Log.d("Order_detail", "订单消息插入失败");
                    }
                }else{
                    Log.d("Order_detail", "订单插入失败");
                }
                //rs.close();
                sta.close();
                con.close(); // 关闭所有已经打开的资源
            } catch (ClassNotFoundException e ) {
                e.printStackTrace();
                Log.i("Login", "连接失败:"+e.getMessage()+"");
            } finally {
                if (con1 != null)
                    try {
                        con1.close();
                        Log.i("Login", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Login", "连接失败:"+sqle.getMessage()+"");
                    }
            }
        }
    };

}
