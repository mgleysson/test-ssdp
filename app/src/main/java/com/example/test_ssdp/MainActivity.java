package com.example.test_ssdp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.test_ssdp.first.FirstImplementationActivity;
import com.example.test_ssdp.second.SecondImplementationActivity;
import com.example.test_ssdp.third.ThirdImplementationActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        if(v.getId()==R.id.btnFirst){
            startActivity(new Intent(this, FirstImplementationActivity.class));
        }

        if(v.getId()==R.id.btnSecond) {
            startActivity(new Intent(this, SecondImplementationActivity.class));
        }

        if (v.getId()==R.id.btnThird){
            startActivity(new Intent(this, ThirdImplementationActivity.class));
        }
    }
}