package com.example.paotui.xiaoyuanpaotui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * Created by Colonel on 2018/3/24.
 */

public class Account_Fragment extends Fragment implements View.OnClickListener {
    RelativeLayout address_Layout;
    RelativeLayout settings_Layout;
    RelativeLayout response_Layout;
    RelativeLayout information_Layout;
    ImageView account_icon;
    TextView account_user_name;
    TextView account_user_num;

    private String IMGURL;
    private String NAME;
    private String TELEPHONE;

    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    private static String USER_id;
    private Context mContext;

    /**********JDBC连接数据库参数********/
    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令
    /**********************************/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View accountLayout = inflater.inflate(R.layout.fragment_account,container,false);

        sharedPreference =accountLayout.getContext().getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();

        Update_dataTask update_dataTask = new Update_dataTask();
        update_dataTask.execute();

        account_user_name = accountLayout.findViewById(R.id.account_user_name);
        account_user_num = accountLayout.findViewById(R.id.account_user_num);
        address_Layout = accountLayout.findViewById(R.id.account_address_layout);
        information_Layout= accountLayout.findViewById(R.id.account_information_layout);
        settings_Layout = accountLayout.findViewById(R.id.account_settings_layout);
        response_Layout = accountLayout.findViewById(R.id.account_response_layout);

        address_Layout.setOnClickListener(this);
        information_Layout.setOnClickListener(this);
        settings_Layout.setOnClickListener(this);
        response_Layout.setOnClickListener(this);

        account_icon = accountLayout.findViewById(R.id.account_icon);
        account_icon.setOnClickListener(this);
        mContext = accountLayout.getContext();
        return accountLayout;
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.account_address_layout:
                 int flag =1;
                 Intent intent = new Intent();
                 Bundle bundle = new Bundle();
                 bundle.putInt("FLAG",flag);
                 intent.putExtras(bundle);
                 intent.setClass(getActivity(),Account_address.class);
                 startActivity(intent);
                 break;
             case R.id.account_information_layout:
                 (getActivity()).startActivityForResult(new Intent(getActivity(),Account_information.class),0002);
                 break;
             case R.id.account_response_layout:
                 (getActivity()).startActivityForResult(new Intent(getActivity(),Account_response.class),0003);
                 break;
             case R.id.account_settings_layout:
                 (getActivity()).startActivityForResult(new Intent(getActivity(),Account_settings.class),0004);
                 break;
             case R.id.account_icon:
                 (getActivity()).startActivityForResult(new Intent(getActivity(),Account_information.class),0005);
                 break;
         }

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
                USER_id = sharedPreference.getString("USER_ID","");
                int user_id = Integer.parseInt(USER_id);
                ResultSet rs = sta.executeQuery("SELECT * from user where id = "+user_id+" and deltime is null");

                if(rs.next()) {
                    IMGURL= rs.getString("photo");

                    String c1 = rs.getString("username");
                    String a1 = c1.substring(0,1);
                    String b1 = c1.substring(c1.length()-1,c1.length());
                    NAME = a1+"**"+b1;

                    String c2 =  rs.getString("telephone");
                    String a2 = c2.substring(0, 3);
                    String b2 = c2.substring(7, c2.length());
                    TELEPHONE = a2+"****"+b2;

                }
                sta.close();
                rs.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Account_Fragment", "MYSQL ERROR" + e.getMessage() + "");
            } finally {
                if (con != null)
                    try {
                        con.close();
                        Log.i("Account_Fragment", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Account_Fragment", "连接失败:" + sqle.getMessage() + "");
                    }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setCon(IMGURL,NAME,TELEPHONE);
        }

        @Override
        protected void onCancelled() {
            Log.d("Order_detail", "finish");
            super.onCancelled();

        }
    }
    private void setCon( String IMGURL,String NAME ,String TELEPHONE){
        Glide.with(mContext).load(IMGURL).into(account_icon);
        account_user_name.setText(NAME);
        account_user_num.setText(TELEPHONE);
    }


}
