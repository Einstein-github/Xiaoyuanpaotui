package com.example.paotui.xiaoyuanpaotui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.chad.library.adapter.base.BaseQuickAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Colonel on 2018/3/24.
 */

public class Order_Fragment extends Fragment{
    private Toolbar toolbar;
    private View  orderLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private OrderAdapter orderAdapter;
    private List<OrdersBean> ordersList = new ArrayList<>();
    private final long delayMillis = 1000;
    private String Status;
    private String Now_time;

    /**********JDBC连接数据库参数********/
    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令
    /**********************************/
    private int POSITION;
    private int begin;


    @SuppressLint("HandlerLeak")
    private Handler mHanler=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            orderAdapter.notifyDataSetChanged();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        orderLayout = inflater.inflate(R.layout.fragment_order,container,false);
        toolbar= orderLayout.findViewById(R.id.order_toolbar);
        toolbar.setTitle("");


        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        mRecyclerView = orderLayout.findViewById(R.id.recycler_view_with_order);
        mLayoutManager = new LinearLayoutManager(orderLayout.getContext(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        orderAdapter= new OrderAdapter(R.layout.item_order,ordersList);
        mRecyclerView.setAdapter(orderAdapter);

        orderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                POSITION = position;
                bundle.putInt("POSITION",POSITION);
                intent.putExtras(bundle);
                intent.setClass(orderLayout.getContext(),Order_detail.class);
                startActivity(intent);

               // startActivity(new Intent(orderLayout.getContext(),Order_detail.class));

                Log.d(TAG, "onItemClick:"+position);

            }
        });
        Update_dataTask update_dataTask = new Update_dataTask();
        update_dataTask.execute();

        orderAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {

            @Override public void onLoadMoreRequested() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(runnable).start();
                        orderAdapter.loadMoreComplete();
                        // homeAdapter.setLoadMoreView(new CustomLoadMoreView());

                        //  homeAdapter.loadMoreComplete(); 加载完成
                        //  homeAdapter.loadMoreFail(); 加载失败
                        //homeAdapter.loadMoreEnd();   没有更多数据
                    }
                }, delayMillis);
            }
        }, mRecyclerView);


        return orderLayout;
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
                Log.i("LoginActivity", "连接失败"+e.getMessage()+"");
            }
        }
        private void sqlCon(Connection con1) throws SQLException {
            try { // 监控异常
                Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
                Connection con;// 获得连接对象
                con = DriverManager.getConnection(strCon, strUser, strPwd);
                Statement sta = con.createStatement(); // 创建语句对象
                int j=0;
                ResultSet rs = sta.executeQuery("SELECT * FROM bill,user where user.id = bill.uid ORDER BY bill.addtime DESC limit "+begin+",10");// 执行SQL语句
                while (rs.next()) {
                    if (rs.getString("status").equals("1"))
                    {
                        Status = "新发布";
                    }else if (rs.getString("status").equals("2"))
                    {
                        Status = "已被抢";
                    }else if (rs.getString("status").equals("3"))
                    {
                        Status = "已送达";
                    }else if (rs.getString("status").equals("4"))
                    {
                        Status = "已完成";
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
                    //OrdersBean one = new OrdersBean(rs.getString("username"),Now_time,rs.getString("photo"),rs.getString("content"),Status,rs.getString("price"));
                    //ordersList.add(one);
                    ordersList.add(new OrdersBean(rs.getString("username"),Now_time,rs.getString("photo"),rs.getString("content"),Status,rs.getString("price")));
                    j++;
                }
                begin=begin+j;
                mHanler.sendEmptyMessage(0);
                rs.close();
                sta.close();
                con.close(); // 关闭所有已经打开的资源
            } catch (ClassNotFoundException e ) {
                e.printStackTrace();
                Log.i("LoginActivity", "连接失败:"+e.getMessage()+"");
            }
        }
    };

    private void loadMoreData() {
        for (int i =0; i < 5; i++){
            ordersList.add(new OrdersBean("用户名6"+"'"+i+"'","2小时前","http://106.15.200.130:8080/Tomcat_test/fj1.jpg","我是内容","被抢单","5元"));
            orderAdapter.notifyDataSetChanged();
        }
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_get:
                    startActivity(new Intent(getActivity(),Order_get.class));
                    return true;
                case R.id.action_buy:
                    startActivity(new Intent(getActivity(),Order_purchasing.class));
                    return true;
            }
            return true;
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        inflater.inflate(R.menu.order, menu);
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
                ResultSet rs = sta.executeQuery("SELECT * FROM bill,user where user.id = bill.uid ORDER BY bill.addtime DESC limit 0,10");
                while (rs.next()) {

                    if (rs.getString("status").equals("1"))
                    {
                        Status = "新发布";
                    }else if (rs.getString("status").equals("2"))
                    {
                        Status = "已被抢";
                    }else if (rs.getString("status").equals("3"))
                    {
                        Status = "已送达";
                    }else if (rs.getString("status").equals("4"))
                    {
                        Status = "已完成";
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
                    OrdersBean one = new OrdersBean(rs.getString("username"),Now_time,rs.getString("photo"),rs.getString("content"),Status,rs.getString("price"));
                    ordersList.add(one);

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
            orderAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            Log.d("Order_Fragment", "finish");
            super.onCancelled();
        }
    }

}
