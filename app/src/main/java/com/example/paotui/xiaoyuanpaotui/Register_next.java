package com.example.paotui.xiaoyuanpaotui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Created by Colonel on 2018/5/20.
 */

public class Register_next extends AppCompatActivity implements View.OnClickListener{
    EditText register_next_username_edit;
    EditText register_next_password_edit;
    RadioGroup register_next_sex_RadioGroup;
    RadioButton register_next_sex_ladies_bt;
    RadioButton register_next_sex_gentlemen_bt;
    Button register_next;
    TextView register_next_username_text;
    TextView register_next_password_text;
    TextView register_next_sex_text;

    /**********JDBC连接数据库参数********/
    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令
    /**********************************/

    private static String username;
    private static String password;
    private static String sex;
    private static String Phone_num;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_next);
        init();
        register_next.setOnClickListener(Onclick_register); //外部类点击事件
        Bundle bundle = this.getIntent().getExtras();
        Phone_num = bundle.getString("PHONE_NUM");

        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        IsLogin();

    }

        private  void IsLogin(){
            String a = sharedPreference.getString("PHONE_NUM_VAl","");
            String b = sharedPreference.getString("PASSWORD_VAL","");

            if ( a.length()>0 && b.length()>0 ) {
                String FRAG_id =1+"";
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("FRAG_id",FRAG_id);
                intent.putExtras(bundle);
                intent.setClass(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        }

    View.OnClickListener Onclick_register = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            username = register_next_username_edit.getText().toString();
            password = register_next_password_edit.getText().toString();

            if (username.isEmpty()){
                register_next_username_text.setText("用户名不能为空");
            }else{
                register_next_username_text.setText("");
            }

            if(password.isEmpty()){
                register_next_password_text.setText("密码不能为空");
            }else{
                register_next_password_text.setText("");
            }

            if(register_next_sex_ladies_bt.isChecked()==false && register_next_sex_gentlemen_bt.isChecked()==false){
                register_next_sex_text.setText("选项为空");
                register_next_sex_text.setTextColor(getResources().getColor(R.color.purple));
            }else if (register_next_sex_ladies_bt.isChecked()){
                sex = register_next_sex_ladies_bt.getText().toString();
                register_next_sex_text.setText("您选择了"+sex);
                register_next_sex_text.setTextColor(getResources().getColor(R.color.purple));
            }else if (register_next_sex_gentlemen_bt.isChecked()){
                sex = register_next_sex_gentlemen_bt.getText().toString();
                register_next_sex_text.setText("您选择了"+sex);
                register_next_sex_text.setTextColor(getResources().getColor(R.color.purple));
            }
            if(sex!=null && sex.length()!=0 && username!=null && username.length()!=0 && password!=null && password.length()!=0){
                new Thread(runnable).start();
            }
        }
    };


    public  void init(){
        register_next_username_edit = findViewById(R.id.register_next_username_edit);
        register_next_password_edit = findViewById(R.id.register_next_password_edit);
        register_next_sex_RadioGroup = findViewById(R.id.register_next_sex_RadioGroup);
        register_next_sex_ladies_bt = findViewById(R.id.register_next_sex_ladies_bt);
        register_next_sex_gentlemen_bt =findViewById(R.id.register_next_sex_gentlemen_bt);
        register_next_username_text = findViewById(R.id.register_next_username_text);
        register_next_password_text = findViewById(R.id.register_next_password_text);
        register_next_sex_text = findViewById(R.id.register_next_sex_text);
        register_next = findViewById(R.id.register_next);
    }
    @Override
    public void onClick(View v) {

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
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                sqlCon(con);    //测试数据库连接
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i("Register_next", "网络连接失败"+e.getMessage());
            }
        }
        private void sqlCon(Connection con1) throws SQLException {
            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                username = register_next_username_edit.getText().toString();
                password = register_next_password_edit.getText().toString();
                int i = 2;
                if(sex.equals("女")){
                    i=0;
                }else if(sex.equals("男")){
                    i=1;
                }
                Date date = new Date();
                String time = String.format("%ts",date);
                //Timestamp time= new Timestamp(System.currentTimeMillis());
                Log.d("TAG",time);
                int rs = sta.executeUpdate("INSERT INTO user (username,telephone,password,sex,addtime) VALUES ('"+username+"', '"+Phone_num+"', '"+password+"', '"+i+"', '"+time+"')");// 执行SQL语句
                if(rs>0){
                    ResultSet res = sta.executeQuery("SELECT * FROM user WHERE  telephone='"+Phone_num+"' and deltime is null");// 执行SQL语句
                    if(res.next()){
                        int USER_ID= res.getInt("id");
                        String user_id= String.valueOf(USER_ID);
                        editor.putString("USER_ID",user_id);
                        int query = sta.executeUpdate("INSERT INTO notice (uid,type,content,addtime) VALUES ("+USER_ID+", "+1+", '恭喜您成功注册', '"+time+"')");// 执行SQL语句
                        if(query>0){
                            Log.d("xiaoxi", "消息添加成功");
                        }
                        editor.commit();
                    }
                    editor.putString("username",username);
                    editor.putString("sex",sex);
                    editor.commit();
                    Log.d("Register_next", "INSERT SUCCESS");
                    Looper.prepare();
                    String FRAG_id =1+"";
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("FRAG_id",FRAG_id);
                    intent.putExtras(bundle);
                    intent.setClass(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                    Looper.loop();
                }else{
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    Log.d("Register_next", "INSERT FAIL");
                }
                sta.close();
                con.close(); // 关闭所有已经打开的资源
            } catch (ClassNotFoundException e ) {
                e.printStackTrace();
                Log.i("Register_next", "连接失败:"+e.getMessage()+"");
            } finally {
                if (con1 != null)
                    try {
                        con1.close();
                        Log.i("Register_next", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Register_next", "连接失败:"+sqle.getMessage());
                    }
            }
            con.close();
        }
    };
}
