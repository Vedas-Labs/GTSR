package com.gtsr.gtsr.qubeTestResults;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gtsr.gtsr.R;
import com.gtsr.gtsr.dataController.QubeController;

public class QubeSampleReadyActivity extends AppCompatActivity {
Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qube_sample_ready);
        btnNext = findViewById(R.id.btn_start);
        QubeController.getInstance().fillCOntext(getApplicationContext());
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QubeSampleReadyActivity.this,QubeTestResultActivity.class));
            }
        });
    }
}