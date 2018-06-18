package com.example.paotui.xiaoyuanpaotui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Colonel on 2018/5/19.
 */

public class Account_settings extends AppCompatActivity {
    private Toolbar toolbar;
    TextView setting_logout;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.account_settings);
        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        toolbar = findViewById(R.id.account_settings_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setting_logout = findViewById(R.id.setting_logout);

        setting_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String a = sharedPreference.getString("PHONE_NUM_VAl","");
                String b = sharedPreference.getString("username","");
                Log.d("TAG",a+b);
                if ( a.length()>0 || b.length()>0) {
                    editor.clear();
                    editor.commit();
                    startActivity(new Intent(getApplicationContext(),Login_And_Register.class));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
