package com.example.paotui.xiaoyuanpaotui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Colonel on 2018/5/25.
 */

public class Remind_detail extends AppCompatActivity  {

    private int POSITION;
    /**********JDBC连接数据库参数********/
    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令
    /**********************************/

    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;

    Button remind_detail_accept_bt;
    Button remind_detail_comment_bt;


    TextView  remind_detail_phone_text;
    TextView  remind_detail_name_text;
    TextView  remind_detail_price_text;
    TextView  remind_detail_time_text;
    TextView remind_detail_content;

    private String bid;
    private String USER_id;
    private String inputComment;
    private String content;
    private String telephone;
    private String username;
    private String price;
    private String addtime;
    private int acceptBillId;
    private int billId;
    private int Status;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remind_detail);
        Init();


        Bundle bundle = this.getIntent().getExtras();
        POSITION = bundle.getInt("POSITION");

        Update_dataTask update_dataTask = new Update_dataTask();
        update_dataTask.execute();
        remind_detail_phone_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Remind_detail.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    // 没有获得授权，申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Remind_detail.this,
                            Manifest.permission.CALL_PHONE)) {
                        Toast.makeText(Remind_detail.this, "请授权！", Toast.LENGTH_LONG).show();
                        // 帮跳转到该应用的设置界面，让用户手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }else{
                        // 不需要解释为何需要该权限，直接请求授权
                        ActivityCompat.requestPermissions(Remind_detail.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                }else {
                    // 已经获得授权，可以打电话
                    CallPhone();
                }

            }
        });

        remind_detail_accept_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(runnable).start();
            }
        });
        remind_detail_comment_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCommentDialog();
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
    class Update_dataTask extends AsyncTask<Void, String, Integer> {
        private Connection con = null;// 获得连接对象
        @Override
        protected Integer doInBackground(Void... voids) {

            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                USER_id = sharedPreference.getString("USER_ID","");
                int user_id = Integer.parseInt(USER_id);
                ResultSet rs = sta.executeQuery("SELECT c.content,b.telephone,b.username,c.price,d.addtime,d.id as acceptBillId,c.id as billId,c.status FROM" +
                        " notice a" +
                        " LEFT JOIN bill c ON a.bid = c.id" +
                        " LEFT JOIN accept_bill d ON c.id = d.bid" +
                        " LEFT JOIN user b ON d.uid = b.id" +
                        " WHERE" +
                        " a.uid="+user_id+"" +
                        " and a.deltime IS NULL" +
                        " AND b.deltime IS NULL" +
                        " AND c.deltime IS NULL" +
                        " AND d.deltime IS NULL" +
                        " ORDER BY a.addtime DESC limit "+POSITION+",1");
                if(rs.next()) {

                    content = rs.getString("content");
                    telephone = rs.getString("telephone");
                    username = "跑腿者："+rs.getString("username");
                    price = "赚："+rs.getDouble("price");
                    int time = rs.getInt("addtime");
                    addtime = YearMondHms(String.valueOf(time));
                    acceptBillId =rs.getInt("acceptBillId");
                    billId =rs.getInt("billId");
                    Status = rs.getInt("status");
                   /* Date date = new Date();
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
                    }*/
                }
                sta.close();
                rs.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Remind_detail", "MYSQL ERROR" + e.getMessage() + "");
            } finally {
                if (con != null)
                    try {
                        con.close();
                        Log.i("Remind_detail", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Remind_detail", "连接失败:" + sqle.getMessage() + "");
                    }
            }
            return billId;
        }


        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            setCon(content,addtime,price,telephone,username);
            if(Status==4)
            {
                remind_detail_accept_bt.setEnabled(false);
                remind_detail_accept_bt.setBackground(getDrawable(R.drawable.button_style_three));
            }else if (Status==2){
                remind_detail_comment_bt.setEnabled(false);
                remind_detail_comment_bt.setBackground(getDrawable(R.drawable.button_style_four));
            }else {
                remind_detail_accept_bt.setEnabled(false);
                remind_detail_accept_bt.setBackground(getDrawable(R.drawable.button_style_three));
                remind_detail_comment_bt.setEnabled(false);
                remind_detail_comment_bt.setBackground(getDrawable(R.drawable.button_style_four));
            }
        }

        @Override
        protected void onCancelled() {
            Log.d("Remind_detail", "finish");
            super.onCancelled();

        }
    }

    private void Init(){
        remind_detail_content = findViewById(R.id.remind_detail_content);
        remind_detail_phone_text = findViewById(R.id.remind_detail_phone_text);
        remind_detail_name_text = findViewById(R.id.remind_detail_name_text);
        remind_detail_price_text = findViewById(R.id.remind_detail_price_text);
        remind_detail_time_text = findViewById(R.id.remind_detail_time_text);
        remind_detail_accept_bt = findViewById(R.id.remind_detail_accept_bt);
        remind_detail_comment_bt = findViewById(R.id.remind_detail_comment_bt);
    }

    private void setCon(String content,String time,String price ,String phone,String name){
        remind_detail_content.setText(content);
        remind_detail_time_text.setText(time);
        remind_detail_price_text.setText(price);
        remind_detail_phone_text.setText(phone);
        remind_detail_name_text.setText(name);
    }

    private void setCon(String content,String time,double price ,String phone,String name){
        remind_detail_content.setText(content);
        remind_detail_time_text.setText(time);
        remind_detail_price_text.setText(String.valueOf(price));
        remind_detail_phone_text.setText(phone);
        remind_detail_name_text.setText(name);
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
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"连接失败",Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.i("Login", "连接失败"+e.getMessage()+"");
            }
        }

        private void sqlCon(Connection con1) throws SQLException {
            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                Date date = new Date();
                String time = String.format("%ts",date);
                USER_id = sharedPreference.getString("USER_ID","");
                int user_id = Integer.parseInt(USER_id);
                int rs = sta.executeUpdate("UPDATE accept_bill SET confirmtime = "+time+" WHERE id = "+acceptBillId+" and deltime is null");// 执行SQL语句
                if(rs>0){
                    /*int res = sta.executeUpdate("INSERT INTO notice (uid,type,content,addtime) VALUES ("+ORDER_UID+", "+1+", '您的订单已被接收了,请点击查看跑腿者信息', '"+time+"')");// 执行SQL语句
                    if(res>0){*/
                    int rees = sta.executeUpdate("UPDATE bill SET status = "+4+" WHERE id = "+billId+" and deltime is null");
                    if(rees>0){
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"确认成功",Toast.LENGTH_SHORT).show();
                        String FRAG_id =1+"";
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("FRAG_id",FRAG_id);
                        intent.putExtras(bundle);
                        intent.setClass(getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                        Looper.loop();

                    }else{
                        Log.d("Remind_detail", "单子状态修改失败");
                    }
                    /*}else{
                        Log.d("Order_detail", "订单消息插入失败");
                    }*/
                }else{
                    Log.d("Remind_detail", "订单确认时间添加失败");
                }
                //rs.close();
                sta.close();
                con.close(); // 关闭所有已经打开的资源
            } catch (ClassNotFoundException e ) {
                e.printStackTrace();
                Log.i("Remind_detail", "连接失败:"+e.getMessage()+"");
            } finally {
                if (con1 != null)
                    try {
                        con1.close();
                        Log.i("Remind_detail", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Remind_detail", "连接失败:"+sqle.getMessage()+"");
                    }
            }
        }
    };
    public  String YearMondHms(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    CallPhone();
                } else {
                    // 授权失败！
                    Toast.makeText(this, "授权失败！", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }
    private void CallPhone() {
        String number = remind_detail_phone_text.getText().toString();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(Remind_detail.this, "号码不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            // 拨号：激活系统的拨号组件
            Intent intent = new Intent(); // 意图对象：动作 + 数据
            intent.setAction(Intent.ACTION_CALL); // 设置动作
            Uri data = Uri.parse("tel:" + number); // 设置数据
            intent.setData(data);
            startActivity(intent); // 激活Activity组件
        }
    }
    private void inputCommentDialog() {
        final EditText inputServer = new EditText(this);
        inputServer.setInputType(InputType.TYPE_CLASS_TEXT);
        inputServer.setFocusable(true);
        inputServer.setFocusableInTouchMode(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inputServer).setNegativeButton(
                getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                         inputComment = inputServer.getText().toString();
                         new Thread(runComment).start();
                    }
                });
        builder.show();
    }
    Runnable runComment = new Runnable() {
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
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"连接失败",Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.i("Login", "连接失败"+e.getMessage()+"");
            }
        }

        private void sqlCon(Connection con1) throws SQLException {
            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                Date date = new Date();
                int rs = sta.executeUpdate("UPDATE bill SET comment = '"+inputComment+"' WHERE id = "+billId+" and deltime is null");// 执行SQL语句
                if(rs>0){
                    int rees = sta.executeUpdate("UPDATE bill SET status = "+5+" WHERE id = "+billId+" and deltime is null");
                    if(rees>0){
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"评价成功",Toast.LENGTH_SHORT).show();
                        String FRAG_id =1+"";
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("FRAG_id",FRAG_id);
                        intent.putExtras(bundle);
                        intent.setClass(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        Looper.loop();
                    }else{
                        Log.d("Remind_detail", "单子状态修改失败");
                    }
                }else{
                    Log.d("Remind_detail", "评价提交失败");
                }

                sta.close();
                con.close(); // 关闭所有已经打开的资源
            } catch (ClassNotFoundException e ) {
                e.printStackTrace();
                Log.i("Remind_detail", "连接失败:"+e.getMessage()+"");
            } finally {
                if (con1 != null)
                    try {
                        con1.close();
                        Log.i("Remind_detail", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Remind_detail", "连接失败:"+sqle.getMessage()+"");
                    }
            }
        }
    };

}
