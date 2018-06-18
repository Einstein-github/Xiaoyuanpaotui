package com.example.paotui.xiaoyuanpaotui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

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
 * Created by Colonel on 2018/3/24.
 */

public class Home_Fragment extends Fragment implements View.OnClickListener{
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<String> list_title;
    private ArrayList<String> list_path;
    private HomeAdapter homeAdapter;

    /**********JDBC连接数据库参数********/
    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令
    /**********************************/

    private List<RemindBean> remindList = new ArrayList<>();
    private View header;

    private View comment_Layout;
    private View remind_Layout;

    private ImageView img_comment;
    private ImageView img_remind;

    private TextView tv_comment;
    private TextView tv_remind;

    private Button bt_get;
    private Button bt_purchasing;

    private final long delayMillis = 1000;

    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    private static String USER_id;

    private String Now_time;
    private String IMGURL;
    private String NAME;
    private String CONTENT;

    private int POSITION;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        final View  homeLayout = inflater.inflate(R.layout.fragment_home,container,false);

        sharedPreference =homeLayout.getContext().getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();

        mRecyclerView = homeLayout.findViewById(R.id.recycler_view_with_home);
        mLayoutManager = new LinearLayoutManager(homeLayout.getContext(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        homeAdapter= new HomeAdapter(R.layout.item_remind,remindList);
        mRecyclerView.setAdapter(homeAdapter);

        homeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                POSITION = position;
                bundle.putInt("POSITION",POSITION);
                intent.putExtras(bundle);
                intent.setClass(homeLayout.getContext(),Remind_detail.class);
                startActivity(intent);
                //Toast.makeText(view.getContext(), "onItemClick" + position, Toast.LENGTH_SHORT).show();
            }
        });

        Update_dataTask update_dataTask = new Update_dataTask();
        update_dataTask.execute();

        /*homeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {

            @Override public void onLoadMoreRequested() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // loadMoreData();
                        //  homeAdapter.setLoadMoreView(new CustomLoadMoreView());

                         homeAdapter.loadMoreComplete();
                        //  homeAdapter.loadMoreFail(); 加载失败
                        //homeAdapter.loadMoreEnd();   没有更多数据
                    }
                }, delayMillis);
            }
        }, mRecyclerView);*/

        setHeader(homeLayout.getContext(),container);
        initBanner(header);
        initView(header);
       // setLayoutSelection(0);
        return homeLayout;
    }

    private void loadMoreData() {
        for (int i =0; i < 5; i++){
            remindList.add(new RemindBean("123","dt","http://106.15.200.130:8080/Tomcat_test/fj1.jpg","我是评论4"+i));
            homeAdapter.notifyDataSetChanged();
        }
    }

    private void initView(View parentView){

        bt_get = parentView.findViewById(R.id.bt_home_head_get);
        bt_purchasing = parentView.findViewById(R.id.bt_home_head_purchasing);

        /*comment_Layout = parentView.findViewById(R.id.comment_layout);
        remind_Layout = parentView.findViewById(R.id.remind_layout);

        img_comment = parentView.findViewById(R.id.img_home_head_comment);
        img_remind = parentView.findViewById(R.id.img_home_head_remind);

        tv_comment = parentView.findViewById(R.id.tv_home_head_comment);
        tv_remind = parentView.findViewById(R.id.tv_home_head_remind);*/

        /*comment_Layout.setOnClickListener(this);
        remind_Layout.setOnClickListener(this);*/
        bt_get.setOnClickListener(this);
        bt_purchasing.setOnClickListener(this);
    }

    private void initBanner(View parentView) {
        list_title = new ArrayList<>();
        list_title.add("合理利用碎片化时间");
        list_title.add("多样化服务");
        list_title.add("快速送达");
        list_path = new ArrayList<>();
        list_path.add("http://101.132.152.183/tupian1.jpg");
        list_path.add("http://101.132.152.183/tupian2.jpg");
        list_path.add("http://101.132.152.183/tupian3.jpg");
        Banner banner = parentView.findViewById(R.id.banner);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setImageLoader(new GlideImageLoader());
        banner.setImages(list_path);
        banner.setBannerTitles(list_title);
        banner.isAutoPlay(true);
        banner.setDelayTime(3000);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.start();
    }
    public void init(){
        RemindBean one = new RemindBean("123","五分钟前","http://106.15.200.130:8080/Tomcat_test/fj1.jpg","我是内容1");
        remindList.add(one);
        RemindBean two = new RemindBean("123","五分钟前","http://106.15.200.130:8080/Tomcat_test/fj1.jpg","我是内容2");
        remindList.add(two);
        RemindBean three = new RemindBean("123","五分钟前","http://106.15.200.130:8080/Tomcat_test/fj1.jpg","我是内容3");
        remindList.add(three);
    }

    private void setHeader(Context context,ViewGroup view) {
        header = LayoutInflater.from(context).inflate(R.layout.home_head,view, false);
        homeAdapter.addHeaderView(header);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_home_head_get:
                startActivity(new Intent(getActivity(),Order_get.class));
                break;
            case R.id.bt_home_head_purchasing:
                startActivity(new Intent(getActivity(),Order_purchasing.class));
                break;
        }

    }

    private void clearSelection() {
        img_remind.setImageResource(R.drawable.remind_unselected);
        tv_remind.setTextColor(Color.parseColor("#bfbfbf"));
        img_comment.setImageResource(R.drawable.comment_unselected);
        tv_comment.setTextColor(Color.parseColor("#bfbfbf"));
    }
    private void setLayoutSelection(int index) {
        clearSelection();
        switch (index){
            case 0:
                img_comment.setImageResource(R.drawable.comment_selected);
                tv_comment.setTextColor(getResources().getColor(R.color.red));
                break;
            case 1:
                img_remind.setImageResource(R.drawable.remind_selected);
                tv_remind.setTextColor(getResources().getColor(R.color.red));
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
                // 执行SQL语句判断该手机号是否被注册过

                ResultSet rs = sta.executeQuery("SELECT * FROM notice where uid = "+user_id+" and deltime is null ORDER BY addtime DESC");
                while (rs.next()) {

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
                    NAME = rs.getString("addtime");
                    CONTENT = rs.getString("content");

                    RemindBean one = new RemindBean("校园跑腿",Now_time,"http://101.132.152.183/run.png",CONTENT);
                    remindList.add(one);
                }
                sta.close();
                rs.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Home_Fragment", "MYSQL ERROR" + e.getMessage() + "");
            } finally {
                if (con != null)
                    try {
                        con.close();
                        Log.i("Home_Fragment", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Home_Fragment", "连接失败:" + sqle.getMessage() + "");
                    }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            homeAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            Log.d("Home_Fragment", "finish");
            super.onCancelled();
        }


    }

}
