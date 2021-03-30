package com.jqh.gpuimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NavMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_main);
    }

    public void imgClick(View view) {
        Intent intent = new Intent(NavMainActivity.this, ImageActivity.class);
        startActivity(intent);
    }

    public void recordClick(View view) {
        Intent intent = new Intent(NavMainActivity.this, DouyinActivity.class);
        startActivity(intent);
    }

    public void liveClick(View view) {
        Intent intent = new Intent(NavMainActivity.this, LiveActivity.class);
        startActivity(intent);
    }
}
