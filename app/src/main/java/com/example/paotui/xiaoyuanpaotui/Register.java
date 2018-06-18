package com.example.paotui.xiaoyuanpaotui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Colonel on 2018/5/20.
 */

public class Register extends AppCompatActivity implements View.OnClickListener{
    /**********阿里云短信服务参数**************/
    //产品名称:云通信短信API产品,开发者无需替换
    public static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    public static final String domain = "dysmsapi.aliyuncs.com";
    public static String accessKeyId="LTAIvvYr4qOted4N";
    public static String accessKeySecret="KhvPKvjFZRQn7OBAc5MNp1x8UOuFDk";
    public static String code="SMS_135044882";
    public static String signName="校园跑腿";
    /**********************************/

    /**********JDBC连接数据库参数********/
    private static String strCon = "jdbc:mysql://101.132.152.183:3306/run"; // 连接字符串
    private static String strUser = "root"; // 数据库用户名
    private static String strPwd = "root"; // 口令
    /**********************************/

    private boolean result = true;
    private int time = 60;
    private int count = 0;
    private static String PHONE_NUM = null;
    private static String ver_code = null;
    public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";
    public static int Verification_code ;

    Button register_next;
    TextView register_verification_text;
    TextView register_phone_text;
    EditText register_phone_edit;
    EditText register_verification_edit;
    private   static int flag =0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        init();



        register_verification_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PHONE_NUM =register_phone_edit.getText().toString();


                if (PHONE_NUM.isEmpty()){
                    register_phone_text.setText("");
                    register_phone_text.setText("号码不能为空");
                }else if (PHONE_NUM.matches(REGEX_MOBILE_EXACT)){
                    register_phone_text.setText("");
                    Update_dataTask update_dataTask = new Update_dataTask();
                    update_dataTask.execute();
                }
                else {
                    register_phone_text.setText("");
                    register_phone_text.setText("请输入正确号码");
                }

            }
        });
        register_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ver_code = register_verification_edit.getText().toString();
                PHONE_NUM =register_phone_edit.getText().toString();

                if (PHONE_NUM.isEmpty()){
                    register_phone_text.setText("");
                    register_phone_text.setText("号码不能为空");
                }else  if (PHONE_NUM.matches(REGEX_MOBILE_EXACT)){
                    register_phone_text.setText("");
                }else {
                    register_phone_text.setText("");
                    register_phone_text.setText("请输入正确号码");
                }


                if(ver_code.isEmpty() && PHONE_NUM.isEmpty() ==false ) {
                    register_phone_text.setText("");
                    register_phone_text.setText("验证码不能为空");
                }else if (ver_code.isEmpty()==false && PHONE_NUM.matches(REGEX_MOBILE_EXACT)==false){
                    register_phone_text.setText("");
                    register_phone_text.setText("请输入正确号码");
                }else if (Verification_code == Integer.parseInt(ver_code)&&Integer.parseInt(ver_code)!=0){

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("PHONE_NUM",PHONE_NUM);
                    intent.putExtras(bundle);
                    intent.setClass(getApplicationContext(),Register_next.class);
                    startActivity(intent);
                }
                else{
                    register_phone_text.setText("");
                    register_phone_text.setText("验证码错误");
                }
            }
        });

        /*Handler_ThreadMessageProcess = new Handler() {
            public void handleMessage(Message Msg) {//此入口处理Msg
                //从Msg里提取Bundle数据
                Bundle B=Msg.getData();
                //从Bundle数据中提取带"BtRxData"标志的数据
                //并转换成String赋给str
                String str = (String) B.get("CHERRY");
                flag=Integer.parseInt(str);
                //在主界面tv_ProgramStatus框里显示str
                Log.d("Register TAG",flag+"");
            }
        };*/

    }
    private  void init(){
        register_next = findViewById(R.id.register_next);
        register_phone_edit = findViewById(R.id.register_phone_edit);
        register_verification_text = findViewById(R.id.register_verification_text);
        register_phone_text = findViewById(R.id.register_phone_text);
        register_verification_edit = findViewById(R.id.register_verification_edit);
    }

    Runnable runnableSms = new Runnable() {

        @Override
        public void run() {
            try {
                sendSms();
            } catch (ClientException e) {
                e.printStackTrace();
                Log.d("Register",e.getMessage());
            }
        }
    };
    @Override
    public void onClick(View v) {

    }

    public static SendSmsResponse sendSms() throws ClientException {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号
        request.setPhoneNumbers(PHONE_NUM);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(code);
        //随机生成六位验证码
        Verification_code = (int) ((Math.random() * 9 + 1) * 100000);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"code\":\""+Verification_code+"\"}");
        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")){
            Log.d("Register","请求成功");
        }else {
            Log.d("Register",sendSmsResponse.getCode());
        }
        return sendSmsResponse;
    }

    public void showTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (result) {
                    time--;
                    try {
                        Thread.sleep(1000);
//                                     tvShowTime.setText(time + "s后从新获取");
                        register_verification_text.post(new Runnable() {
                            @Override
                            public void run() {
                                register_verification_text.setText(time + "s后重新获取");
                                register_verification_text.setClickable(false);
                                register_verification_text.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_radius_click));
                            }
                        });
                        if (time <= 1) {
                            count=0;
                            result = false;
                            register_verification_text.post(new Runnable() {
                                @Override
                                public void run() {
                                    register_verification_text.setText("获取验证码");
                                    register_verification_text.setClickable(true);
                                    register_verification_text.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_radius_small));
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                result = true;
                time = 60;
            }
        }).start();
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
                PHONE_NUM=register_phone_edit.getText().toString();
                // 执行SQL语句判断该手机号是否被注册过
                ResultSet rs = sta.executeQuery("SELECT * FROM user WHERE telephone='"+PHONE_NUM+"' and deltime is null");
                if(rs.next()){
                     flag=1;

                }else {
                    flag=0;
                }
                sta.close();
                rs.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Register", "MYSQL ERROR"+e.getMessage()+"");
            }finally {
                if (con != null)
                    try {
                        con.close();
                        Log.i("Register", "连接关闭:");
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        Log.i("Register", "连接失败:"+sqle.getMessage()+"");
                    }
            }

            return flag;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            if (flag==1){
                register_phone_text.setText("账户已存在");
            } else if (flag ==0){
                showTime();
                new Thread(runnableSms).start();
            }
        }

        @Override
        protected void onCancelled() {
            Log.d("Register", "finish");
            super.onCancelled();

        }
    }

}
