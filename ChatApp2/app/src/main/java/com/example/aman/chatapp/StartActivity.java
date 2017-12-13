package com.example.aman.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void login(View view)
    {

        Intent login=new Intent(StartActivity.this,LoginActivity.class);
        startActivity(login);
    }

    public void register(View view)
    {
        Intent regis=new Intent(StartActivity.this,RegisterActivity.class);
        startActivity(regis);
    }
}
