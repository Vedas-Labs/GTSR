package com.gtsr.gtsr.testModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.gtsr.gtsr.AlertShowingDialog;
import com.gtsr.gtsr.LanguagesKeys;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.adapter.SelectStripAdapter;
import com.gtsr.gtsr.dataController.LanguageTextController;
import com.gtsr.gtsr.qubeTestResults.QubeSampleReadyActivity;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;

public class SelectTestStripActivity extends AppCompatActivity {
RecyclerView stripListView;
   SelectStripAdapter stripAdapter;
    ImageView imgBack;
     Button btnNxt;
    public static int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_test_strip);
        imgBack = findViewById(R.id.back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // SCConnectionHelper.getInstance().scanDeviceInterface = null;
                SCConnectionHelper.getInstance().disconnectWithPeripheral();
                finish();
            }
        });

        btnNxt = findViewById(R.id.btn_nxt);
        stripListView = findViewById(R.id.strip_list);
        stripAdapter = new SelectStripAdapter(SelectTestStripActivity.this);
        stripListView.setLayoutManager(new LinearLayoutManager(SelectTestStripActivity.this));
        stripListView.setAdapter(stripAdapter);
        btnNxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPosition>-1) {
                    startActivity(new Intent(getApplicationContext(), QubeSampleReadyActivity.class));
                }else {
                    new AlertShowingDialog(SelectTestStripActivity.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SELECT_A_STRIP_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
       // SCConnectionHelper.getInstance().scanDeviceInterface = null;
        SCConnectionHelper.getInstance().disconnectWithPeripheral();
        finish();
        super.onBackPressed();
    }
}