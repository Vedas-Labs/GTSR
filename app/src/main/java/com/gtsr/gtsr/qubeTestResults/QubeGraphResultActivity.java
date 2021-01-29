package com.gtsr.gtsr.qubeTestResults;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.gtsr.gtsr.HomeActivity;
import com.gtsr.gtsr.R;

public class QubeGraphResultActivity extends AppCompatActivity {
    CheckBox toggleBtn;
    ImageView backimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qube_graph_result);
        loadActions();
    }
    private void loadActions(){
       /* toggleBtn = findViewById(R.id.toggle_btn);
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        backimage = findViewById(R.id.backimage);
        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
    }
}