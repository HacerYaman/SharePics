package com.sharepicinstclonehy.SharePic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PolicyActivity extends AppCompatActivity {

    private ImageView close;
    private TextView policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

         policy= findViewById(R.id.policy);
         close=findViewById(R.id.close_policy);

         close.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent= new Intent(PolicyActivity.this, RegisterActivity.class);
                 startActivity(intent);
             }
         });

        policy.setMovementMethod(new ScrollingMovementMethod());



    }

}