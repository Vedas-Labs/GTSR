package com.gtsr.gtsr.loginModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gtsr.gtsr.HomeActivity;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.WifiReceiver;

public class LoginActivity extends AppCompatActivity {
TextView txtSkip;
Button login;
public static boolean isGtsrSelected=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        casting();
    }

    public void casting(){
        txtSkip = findViewById(R.id.txt_skip);
        login = (Button) findViewById(R.id.loginbutton);
        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
        });


    }
}