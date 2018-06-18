package com.example.paotui.xiaoyuanpaotui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaDataSource;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by Colonel on 2018/5/20.
 */

public class Login extends AppCompatActivity implements View.OnClickListener{
    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令
    public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";
    private static String PHONE_NUM = null;
    private static String PASSWORD = null;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    Button login_log_in;
    TextView login_not_account;
    TextView login_forget_password;
    TextView login_phone_text;
    EditText login_phone_edit;
    EditText login_enter_password_edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);



        init();
        login_not_account.setOnClickListener(this);
        login_log_in .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PHONE_NUM =login_phone_edit.getText().toString();
                PASSWORD = login_enter_password_edit.getText().toString();

                if (PHONE_NUM.isEmpty()){
                    login_phone_text.setText("");
                    login_phone_text.setText("号码不能为空");
                }else if (PHONE_NUM.matches(REGEX_MOBILE_EXACT)&& PASSWORD.isEmpty() ==false){
                    login_phone_text.setText("");
                    new Thread(runnable).start();
                }else if (PASSWORD.isEmpty()){
                    login_phone_text.setText("");
                    login_phone_text.setText("密码不能为空");
                }else {
                    login_phone_text.setText("");
                    login_phone_text.setText("请输入正确号码");
                }
            }
        });

        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        IsLogin();


    }
    public void init(){

        login_log_in = findViewById(R.id.login_log_in);
        login_not_account = findViewById(R.id.login_not_account);
        login_forget_password = findViewById(R.id.login_forget_password);
        login_phone_edit = findViewById(R.id.login_phone_edit);
        login_enter_password_edit = findViewById(R.id.login_enter_password_edit);
        login_phone_text = findViewById(R.id.login_phone_text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_not_account:
                startActivity(new Intent(this,Register.class));
        }
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
                PHONE_NUM=login_phone_edit.getText().toString();
                PASSWORD=login_enter_password_edit.getText().toString();
                ResultSet rs = sta.executeQuery("SELECT * FROM user WHERE  telephone='"+PHONE_NUM+"' and deltime is null");// 执行SQL语句
                if(rs.next()){
                    if (PASSWORD.equals(""+rs.getString("password")+"")) {

                        int USER_ID= rs.getInt("id");
                        String user_id= String.valueOf(USER_ID);
                        editor.putString("PHONE_NUM_VAl",PHONE_NUM);
                        editor.putString("PASSWORD_VAL",PASSWORD);
                        editor.putString("USER_ID",user_id);
                        editor.commit();
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"登陆成功",Toast.LENGTH_SHORT).show();
                        Log.d("TAG", PHONE_NUM);
                        String FRAG_id =1+"";
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("FRAG_id",FRAG_id);
                        intent.putExtras(bundle);
                        intent.setClass(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                        Looper.loop();
                        Log.d("Login", "登陆成功");
                    }
                    else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        Log.d("Login", "密码错误");
                    }
                }else{
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),"号码不存在",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    Log.d("Login", "号码不存在");
                }
                rs.close();
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
    private  void IsLogin(){
             String a = sharedPreference.getString("PHONE_NUM_VAl","");
             String b = sharedPreference.getString("PASSWORD_VAL","");
             String c = sharedPreference.getString("username","");
        if ( a.length()>0 || b.length()>0 ||c.length()>0) {
            String FRAG_id =1+"";
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("FRAG_id",FRAG_id);
            intent.putExtras(bundle);
            intent.setClass(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
    }


}
