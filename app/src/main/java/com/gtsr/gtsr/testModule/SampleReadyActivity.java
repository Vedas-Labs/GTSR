package com.gtsr.gtsr.testModule;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gtsr.gtsr.R;


public class SampleReadyActivity extends AppCompatActivity {
Button btnStart;
RelativeLayout imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_ready);
        casting();
    }
    public void casting(){
        btnStart = findViewById(R.id.btn_start);
        imgBack = findViewById(R.id.back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withEditText();
               // startActivity(new Intent(SampleReadyActivity.this,AnalizingPageViewController.class));
            }
        });
    }
    public String username="";
    public void withEditText() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Name");
        builder.setCancelable(false);

        final EditText input = new EditText(SampleReadyActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                username=input.getText().toString();
                Log.e("username","call"+username);
                AnalizingPageViewController.username=username;
                finish();
                startActivity(new Intent(SampleReadyActivity.this,AnalizingPageViewController.class));
            }
        });
        builder.show();
    }
}