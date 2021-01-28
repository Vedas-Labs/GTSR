package com.gtsr.gtsr.qubeTestResults;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import com.gtsr.gtsr.Constants;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.RefreshShowingDialog;
import com.gtsr.gtsr.dataController.QubeController;
import com.gtsr.gtsr.database.TestFactorDataController;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.gtsr.gtsr.testModule.AnalizingPageViewController;
import com.spectrochips.spectrumsdk.FRAMEWORK.TestFactors;

public class QubeSampleReadyActivity extends AppCompatActivity {
Button btnNext;
    RefreshShowingDialog refreshShowingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qube_sample_ready);
        btnNext = findViewById(R.id.btn_start);
        refreshShowingDialog = new RefreshShowingDialog(QubeSampleReadyActivity.this, "testing..");

        btnNext.setVisibility(View.GONE);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QubeSampleReadyActivity.this,QubeTestResultActivity.class));
            }
        });
         refreshShowingDialog.showAlert();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("starting", "startLogin1");
                loadQubeResultsDataTODb();

            }
        }, 1000);
    }
    String currentTime = String.valueOf(System.currentTimeMillis() / 1000L);
    String testID = "test" + String.valueOf(getRandomInteger(1000, 10));

    public int getRandomInteger(int maximum, int minimum) {
        return ((int) (Math.random() * (maximum - minimum))) + minimum;
    }

    public void loadQubeResultsDataTODb() {
        UrineresultsModel objResult = new UrineresultsModel();
        objResult.setTestReportNumber("1");
        objResult.setTestType(Constants.TestNames.qube.toString());
        objResult.setLatitude("0.0");
        objResult.setLongitude("0.0");
        objResult.setTestedTime(currentTime);
        objResult.setIsFasting("false");
        objResult.setRelationtype(Constants.TestNames.qube.toString());
        objResult.setTest_id(testID);
        objResult.setUserName(getRandomInteger(1,100)+"_" + "AntiBody Analysis");


       /* int val= getRandomInteger(1,200);
        if((val%2) == 0 ){
            Log.e("setUserName","call"+val);
        }else{
            Log.e("aaaaaaaaa","call"+val);
        }*/

        if (UrineResultsDataController.getInstance().insertUrineResultsForMember(objResult)) {
            com.gtsr.gtsr.database.TestFactors objTest = new com.gtsr.gtsr.database.TestFactors();
            objTest.setFlag(false);
            objTest.setUnit("");

            objTest.setTestName("SARS-COV-2 AntiBody");
           int val= getRandomInteger(1,200);
            if((val%2) == 0 ){
               objTest.setResult("Positive");
               objTest.setHealthReferenceRanges("High");
               objTest.setValue(String.valueOf(val));
           }else {
               objTest.setResult("Negative");
               objTest.setHealthReferenceRanges("Borderline");
               objTest.setValue(String.valueOf(val));
           }
            objTest.setUrineresultsModel(UrineResultsDataController.getInstance().currenturineresultsModel);
            if (TestFactorDataController.getInstance().insertTestFactorResults(objTest)) {
                refreshShowingDialog.hideRefreshDialog();
            }
            btnNext.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Testing Completed", Toast.LENGTH_SHORT).show();
        }
    }
}