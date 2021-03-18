package com.gtsr.gtsr.qubeTestResults;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gtsr.gtsr.Constants;
import com.gtsr.gtsr.QUBETestingController;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.RefreshShowingDialog;
import com.gtsr.gtsr.database.TestFactorDataController;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.TestFactors;
import com.spectrochips.spectrumsdk.MODELS.IntensityChart;

import java.util.ArrayList;

public class QubePatientInfoActivity extends AppCompatActivity {
    Button btnNext;
    RefreshShowingDialog refreshShowingDialog, stripTrayOutDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qube_patientinfo);
        btnNext = findViewById(R.id.btn_start);
        refreshShowingDialog = new RefreshShowingDialog(QubePatientInfoActivity.this, "testing..");

        stripTrayOutDialogue = new RefreshShowingDialog(QubePatientInfoActivity.this, "Moving Strip Out..");
        loadInterfaces();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshShowingDialog.showAlert();
                QUBETestingController.getInstance().startTesting();
                //  startActivity(new Intent(QubeSampleReadyActivity.this,QubeTestResultActivity.class));
            }
        });

        performStripTrayOutFunction();
    }

    private void performStripTrayOutFunction() {
        if (SCConnectionHelper.getInstance().isConnected) {
            stripTrayOutDialogue.showAlert();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    QUBETestingController.getInstance().performStripTrayOutCommand();
                }
            }, 1000 * 1);
        }else{
            Toast.makeText(getApplicationContext(),"Device not Connected",Toast.LENGTH_SHORT).show();
        }

    }

    String currentTime = String.valueOf(System.currentTimeMillis() / 1000L);
    String testID = "test" + String.valueOf(getRandomInteger(1000, 10));

    public int getRandomInteger(int maximum, int minimum) {
        return ((int) (Math.random() * (maximum - minimum))) + minimum;
    }

    private void loadInterfaces() {
        QUBETestingController.getInstance().activatenotifications(new QUBETestingController.QUBETestDataInterface() {
            @Override
            public void gettingData(byte[] var1) {
            }
            @Override
            public void onSuccessForTestComplete(ArrayList<TestFactors> var1, String var2, ArrayList<IntensityChart> var3) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadQubeResultsDataTODb();
                       /*refreshShowingDialog.hideRefreshDialog();
                       Toast.makeText(getApplicationContext(),var2,Toast.LENGTH_SHORT).show();*/
                    }
                });
            }
            @Override
            public void getRequestAndResponse(String var1) {

            }
            @Override
            public void onFailureForTesting(String var1) {

            }
            @Override
            public void onMovingToStripTrayOut(String var1) {
                Log.e("onMovingToStripTrayOut", "call");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stripTrayOutDialogue.hideRefreshDialog();
                    }
                });
            }

            @Override
            public void isSyncingCompleted(boolean val, ArrayList<Float> standardWhiteIntensityArray) {

            }

            @Override
            public void gettingDarkSpectrum(boolean val, ArrayList<Float> standardWhiteIntensityArray) {

            }
        });

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
        objResult.setUserName(getRandomInteger(1, 100) + "_" + "AntiBody Analysis");
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
            int val = getRandomInteger(1, 200);
            if ((val % 2) == 0) {
                objTest.setResult("Positive");
                objTest.setHealthReferenceRanges("High");
                objTest.setValue(String.valueOf(val));
            } else {
                objTest.setResult("Negative");
                objTest.setHealthReferenceRanges("Borderline");
                objTest.setValue(String.valueOf(val));
            }
            objTest.setUrineresultsModel(UrineResultsDataController.getInstance().currenturineresultsModel);
            if (TestFactorDataController.getInstance().insertTestFactorResults(objTest)) {
                refreshShowingDialog.hideRefreshDialog();
                Toast.makeText(getApplicationContext(), "Testing Completed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), QubeTestResultActivity.class));
            }
        }
    }
}