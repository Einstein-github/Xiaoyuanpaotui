package com.example.paotui.xiaoyuanpaotui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Colonel on 2018/5/19.
 */

public class Account_address extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private RelativeLayout address_new_layout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AddressAdapter addressAdapter;
    private List<AddressBean> addressList = new ArrayList<>();
    private String USER_id;

    /**********JDBC连接数据库参数********/
    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令
    /**********************************/
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    private int flag = 1;
    private String address;
    private int POSITION;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.account_address);

        Bundle bundle = this.getIntent().getExtras();
        flag = bundle.getInt("FLAG");

        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();

        toolbar = findViewById(R.id.account_address_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        address_new_layout = findViewById(R.id.address_new_layout);
        address_new_layout.setOnClickListener(this);

        mRecyclerView = findViewById(R.id.recycler_view_with_address);
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        addressAdapter= new AddressAdapter(R.layout.address_item,addressList);
        mRecyclerView.setAdapter(addressAdapter);



        addressAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                POSITION = position;

                if(flag ==1){
                    Log.d(TAG, "onItemClick:"+POSITION);
                }else {
                    new Thread(runnable).start();
                   /* Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    POSITION = position;
                    bundle.putInt("POSITION",POSITION);
                    intent.putExtras(bundle);
                    intent.setClass(getApplicationContext(),Order_get.class);
                    startActivity(intent);*/
                }




            }
        });

        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        Update_dataTask update_dataTask = new Update_dataTask();
        update_dataTask.execute();


    }
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.address_new_layout:
                startActivity(new Intent(getApplicationContext(),Add_New_Address.class));
                finish();
                break;
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
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                sqlCon(con);    //测试数据库连接
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i("Account_address", "网络连接失败"+e.getMessage());
            }
        }
        private void sqlCon(Connection con1) throws SQLException {
            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                USER_id = sharedPreference.getString("USER_ID","");
                int user_id = Integer.parseInt(USER_id);

                ResultSet rs = sta.executeQuery("select * from address where uid = "+user_id+" and deltime is null limit "+POSITION+",1");// 执行SQL语句
                if(rs.next()){
                    int id = rs.getInt("id");
                    String ads = rs.getString("ads");
                    String house = rs.getString("house");
                    address = ads + house;

                    editor.putInt("ACCOUNT_ADDRESS_ID",id);
                    editor.putString("ACCOUNT_ADDRESS",address);
                    editor.commit();

                    if (flag ==2){
                        Looper.prepare();
                        startActivity(new Intent(getApplicationContext(),Order_get.class));
                        finish();
                        Looper.loop();
                    }
                    else if (flag ==3)
                    {
                        Looper.prepare();
                        startActivity(new Intent(getApplicationContext(),Order_purchasing.class));
                        finish();
                        Looper.loop();
                    }
                }else{
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),"获取地址出错",Toast.LENGTH_SHORT).show();
                    finish();
                    Looper.loop();
                    Log.d("Account_address", "INSERT FAIL");
                }
                sta.close();
                con.close(); // 关闭所有已经打开的资源
            } catch (ClassNotFoundException e ) {
                e.printStackTrace();
                Log.i("Account_address", "连接失败:"+e.getMessage()+"");
            } finally {
                if (con1 != null)
                    try {
                        con1.close();
                        Log.i("Account_address", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Account_address", "连接失败:"+sqle.getMessage());
                    }
            }
            con.close();
        }
    };







    class Update_dataTask extends AsyncTask<Void,String,Void> {
        private Connection con = null;// 获得连接对象
        @Override
        protected Void doInBackground(Void... voids) {


            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                USER_id = sharedPreference.getString("USER_ID","");
                int user_id = Integer.parseInt(USER_id);
                ResultSet rs = sta.executeQuery("SELECT * FROM address WHERE uid="+user_id+" and deltime is null");
                while (rs.next()) {
                    String ads = rs.getString("ads");
                    String house = rs.getString("house");
                    String name = rs.getString("order_name");
                    String telephone = rs.getString("order_telephone");
                    String address = ads+house;

                    AddressBean one = new AddressBean(address,name,telephone,"http://101.132.152.183/edit.png");

                    addressList.add(one);

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
            addressAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onCancelled() {
            Log.d("MainActivity", "finish");
            super.onCancelled();

        }
    }
}
