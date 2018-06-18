package com.example.paotui.xiaoyuanpaotui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Colonel on 2018/5/19.
 */

public class Account_information extends AppCompatActivity {
    private Toolbar toolbar;

    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令


    private String[] sexArry = new String[] { "女", "男" };
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    private static String USER_id;

    private ImageView account_information_user_img;
    TextView account_information_name_text;
    TextView account_information_telephone_text;
    TextView account_information_sex_text;

    RelativeLayout account_information_icon_layout;
    RelativeLayout account_information_username_layout;
    RelativeLayout account_information_sex_layout;
    RelativeLayout account_information_telephone_layout;
    RelativeLayout account_information_password_layout;

    private Button information_save;

    private String icon;
    private String username;
    private String sex;
    private String telephone;
    private String password;


    private Handler mHanler=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            account_information_name_text.setText(username);
            account_information_telephone_text.setText(telephone);
            account_information_sex_text.setText(sex);
        }
    };





    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.account_information);
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
        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();

        information_save.setOnClickListener(new View.OnClickListener() {//保存按钮
            @Override
            public void onClick(View view) {
                new Thread(save_info).start();
            }
        });
        account_information_username_layout.setOnClickListener(new View.OnClickListener() {//昵称填写
            @Override
            public void onClick(View view) {
                inputNameDialog();
            }
        });
        account_information_telephone_layout.setOnClickListener(new View.OnClickListener() {//联系方式填写
            @Override
            public void onClick(View view) {
                inputPhoneDialog();
            }
        });
        new Thread(runnable).start();
    }

    private void init(){
        toolbar = findViewById(R.id.account_information_toolbar);
        account_information_icon_layout = findViewById(R.id.account_information_icon_layout);
        account_information_username_layout = findViewById(R.id.account_information_username_layout);
        account_information_sex_layout = findViewById(R.id.account_information_sex_layout);
        account_information_telephone_layout = findViewById(R.id.account_information_telephone_layout);
        account_information_password_layout = findViewById(R.id.account_information_password_layout);

        account_information_user_img=findViewById(R.id.account_information_user_img);
        account_information_name_text=findViewById(R.id.account_information_name_text);
        account_information_telephone_text=findViewById(R.id.account_information_telephone_text);
        account_information_sex_text=findViewById(R.id.account_information_sex_text);
        information_save = findViewById (R.id.information_save);

    }




    private void inputNameDialog() {
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
                        String inputName = inputServer.getText().toString();
                        account_information_name_text.setText(inputName);
                    }
                });
        builder.show();
    }

    private void inputPhoneDialog() {
        final EditText inputServer = new EditText(this);
        inputServer.setInputType(InputType.TYPE_CLASS_PHONE);
        inputServer.setFocusable(true);
        inputServer.setFocusableInTouchMode(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inputServer).setNegativeButton(
                getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = inputServer.getText().toString();
                        account_information_telephone_text.setText(inputName);
                    }
                });
        builder.show();
    }




    private void showSexChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
        builder.setSingleChoiceItems(sexArry, 0, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                // showToast(which+"");
                account_information_sex_text.setText(sexArry[which]);
                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
            }
        });
        builder.show();// 让弹出框显示
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
                Log.i("Account_information", "连接失败"+e.getMessage()+"");
            }
        }
        private void sqlCon(Connection con1) throws SQLException {
            USER_id = sharedPreference.getString("USER_ID","");
            int user_id = Integer.parseInt(USER_id);
            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                ResultSet rs = sta.executeQuery("SELECT * FROM user WHERE id="+user_id+" and deltime is null");// 执行SQL语句
                if (rs.next()) {
                    Log.d("Account_information", ""+rs.getString("id")+"");
                    Log.d("Account_information", ""+rs.getString("password")+"");
                    icon=rs.getString("photo");
                    username=rs.getString("username");

                    int xb=rs.getInt("sex");

                    if(xb==1){
                        sex="男";
                    }else if(xb==0){
                        sex="女";
                    }
                    password=rs.getString("password");

                    String c2 =  rs.getString("telephone");
                    String a2 = c2.substring(0, 3);
                    String b2 = c2.substring(7, c2.length());
                    telephone = a2+"****"+b2;

                }
                mHanler.sendEmptyMessage(0);
                rs.close();
                sta.close();
                con.close(); // 关闭所有已经打开的资源
            } catch (ClassNotFoundException e ) {
                e.printStackTrace();
                Log.i("Account_information", "连接失败:"+e.getMessage()+"");
            }
        }
    };

    Runnable save_info = new Runnable() {
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
                Log.i("LoginActivity", "连接失败"+e.getMessage()+"");
            }
        }
        private void sqlCon(Connection con1) throws SQLException {
            USER_id = sharedPreference.getString("USER_ID","");
            int user_id = Integer.parseInt(USER_id);
            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                String user_name=account_information_name_text.getText().toString().trim();
                String user_sex=account_information_sex_text.getText().toString().trim();
                int i=2;
                if(user_sex.equals("男")){
                    i=1;
                }else if(user_sex.equals("女")){
                    i=0;
                }
                //String user_birth=tv_birth.getText().toString().trim();
                String user_phone=account_information_telephone_text.getText().toString().trim();
                //String user_address=tv_address.getText().toString().trim();
                Statement sta = con.createStatement(); // 创建语句对象
                int result=sta.executeUpdate("UPDATE user SET username='"+user_name+"',sex="+i+",telephone='"+user_phone+"' WHERE id="+user_id+" and deltime is null");// 执行SQL语句
                if(result>0){
                    Looper.prepare();
                    SharedPreferences.Editor edit=sharedPreference.edit();
                    edit.putString("user_name",user_name);
                    edit.apply();
                    Toast toast=Toast.makeText(Account_information.this,"修改成功", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    String FRAG_id =3+"";
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("FRAG_id",FRAG_id);
                    intent.putExtras(bundle);
                    intent.setClass(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();

                    Looper.loop();
                }
                sta.close();
                con.close(); // 关闭所有已经打开的资源
            } catch (ClassNotFoundException e ) {
                e.printStackTrace();
                Log.i("LoginActivity", "连接失败:"+e.getMessage()+"");
            }
        }
    };


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
