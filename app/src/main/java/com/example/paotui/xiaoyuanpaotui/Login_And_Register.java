package com.example.paotui.xiaoyuanpaotui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Colonel on 2018/5/20.
 */

public class Login_And_Register extends AppCompatActivity implements View.OnClickListener{
    Button register;
    Button login;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_and_register);
        register = findViewById(R.id.register);
        login = findViewById(R.id.log_in);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        IsLogin();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
                break;
            case R.id.log_in:
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
                break;
        }

    }
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
            finish();
        }
    }
}
