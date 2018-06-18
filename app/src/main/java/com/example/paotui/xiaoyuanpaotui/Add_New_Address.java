package com.example.paotui.xiaoyuanpaotui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * Created by Colonel on 2018/5/21.
 */

public class Add_New_Address extends AppCompatActivity implements View.OnClickListener {

    /**********JDBC连接数据库参数********/
    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令
    /**********************************/



    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    private Toolbar toolbar;
    private String ADDRESS ="暨阳学院";
    private static String USER_id;
    private String sex;
    private String NAME;
    private String HOUSE;
    private String PHONE_NUM;
    public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";


    ImageView add_new_address_more_img;
    EditText add_new_address_name_edit;
    EditText add_new_address_num_edit;
    EditText add_new_address_address_edit;
    EditText add_new_address_house_edit;
    Button add_new_address_confirm;
    RadioGroup add_new_address_sex_group;
    RadioButton add_new_address_sex_man;
    RadioButton add_new_address_sex_woman;
    TextView add_new_address_sex_point;
    TextView add_new_address_name_point;
    TextView add_new_address_num_point;
    TextView add_new_address_house_point;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_address);
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

        add_new_address_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NAME = add_new_address_name_edit.getText().toString();
                PHONE_NUM = add_new_address_num_edit.getText().toString();
                HOUSE = add_new_address_house_edit.getText().toString();

                if (PHONE_NUM.isEmpty()){
                    add_new_address_num_point.setText("");
                    add_new_address_num_point.setText("内容不能为空");
                }else if (PHONE_NUM.matches(REGEX_MOBILE_EXACT)){
                    add_new_address_num_point.setText("");
                }else {
                    add_new_address_num_point.setText("");
                    add_new_address_num_point.setText("请输入正确号码");
                }

                if(NAME.isEmpty()){
                    add_new_address_name_point.setText("姓名不能为空");
                }else{
                    add_new_address_name_point.setText("");
                }
                if(HOUSE.isEmpty()){
                    add_new_address_house_point.setText("门牌号不能为空");
                }else{
                    add_new_address_house_point.setText("");
                }

                if(add_new_address_sex_man.isChecked()==false && add_new_address_sex_woman.isChecked()==false){
                    add_new_address_sex_point.setText("");
                    add_new_address_sex_point.setText("选项不能为空");
                    add_new_address_sex_point.setTextColor(getResources().getColor(R.color.purple));
                }else if (add_new_address_sex_woman.isChecked()){
                    add_new_address_sex_point.setText("");
                    sex = add_new_address_sex_woman.getText().toString();
                }else if (add_new_address_sex_man.isChecked()){
                    add_new_address_sex_point.setText("");
                    sex = add_new_address_sex_man.getText().toString();
                }

                if(sex!=null && sex.length()!=0 && PHONE_NUM!=null && PHONE_NUM.length()!=0 && HOUSE!=null && HOUSE.length()!=0&& NAME!=null && NAME.length()!=0){
                    new Thread(runnable).start();
                }
            }
        });


        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        ADDRESS = sharedPreference.getString("ADDRESS","");
        add_new_address_address_edit.setText(ADDRESS);

    }
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_new_address_more_img:
                startActivity(new Intent(getApplicationContext(),Address_map.class));
                finish();
                break;
            case  R.id. add_new_address_address_edit:
                startActivity(new Intent(getApplicationContext(),Address_map.class));
                finish();
                break;

        }
    }
    private  void init(){
        toolbar = findViewById(R.id.add_new_address_toolbar);
        add_new_address_address_edit = findViewById(R.id.add_new_address_address_edit);
        add_new_address_more_img = findViewById(R.id.add_new_address_more_img);
        add_new_address_name_edit = findViewById(R.id.add_new_address_name_edit);
        add_new_address_num_edit = findViewById(R.id.add_new_address_num_edit);
        add_new_address_house_edit = findViewById(R.id.add_new_address_house_edit);
        add_new_address_confirm = findViewById(R.id.add_new_address_confirm);
        add_new_address_sex_group = findViewById(R.id.add_new_address_sex_group);
        add_new_address_sex_man = findViewById(R.id.add_new_address_sex_man);
        add_new_address_sex_woman = findViewById(R.id.add_new_address_sex_woman);
        add_new_address_sex_point = findViewById(R.id.add_new_address_sex_point);
        add_new_address_name_point = findViewById(R.id.add_new_address_name_point);
        add_new_address_num_point= findViewById(R.id.add_new_address_num_point);
        add_new_address_house_point= findViewById(R.id.add_new_address_house_point);
        add_new_address_address_edit.setOnClickListener(this);
        add_new_address_more_img.setOnClickListener(this);


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

            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                USER_id = sharedPreference.getString("USER_ID","");
                int user_id = Integer.parseInt(USER_id);

                Date date = new Date();
                String time = String.format("%ts", date);

                int i = 2;
                if(sex.equals("女")){
                    i=0;
                }else if(sex.equals("男")){
                    i=1;
                }


                int rs = sta.executeUpdate("INSERT INTO address (uid,ads,house,order_name,order_telephone,sex,addtime) VALUES (" + user_id + ", '"+ ADDRESS +"','"+ HOUSE +"', '" + NAME + "', '" + PHONE_NUM + "',"+i+", '" + time + "')");

                if (rs > 0) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),Account_address.class));
                    finish();
                    Looper.loop();
                    Log.i("Add_New_Address", "添加成功");
                } else {

                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    Log.i("Add_New_Address", "添加失败");
                }
                sta.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Add_New_Address", "MYSQL ERROR" + e.getMessage() + "");
            } finally {
                if (con != null)
                    try {
                        con.close();
                        Log.i("Add_New_Address", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Add_New_Address", "连接失败:" + sqle.getMessage() + "");
                    }
            }

        }

    };


}
