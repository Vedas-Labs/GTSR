package com.gtsr.gtsr.testModule;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gtsr.gtsr.AlertShowingDialog;
import com.gtsr.gtsr.LanguagesKeys;
import com.gtsr.gtsr.QUBETestingController;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.RefreshShowingDialog;
import com.gtsr.gtsr.adapter.SelectStripAdapter;
import com.gtsr.gtsr.dataController.LanguageTextController;
import com.gtsr.gtsr.qubeTestResults.QubeSampleReadyActivity;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCTestAnalysis;
import com.spectrochips.spectrumsdk.FRAMEWORK.TestFactors;
import com.spectrochips.spectrumsdk.MODELS.IntensityChart;
import com.spectrochips.spectrumsdk.MODELS.SpectroDeviceDataController;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SelectTestStripActivity extends AppCompatActivity {
    RecyclerView stripListView;
    SelectStripAdapter stripAdapter;
    ImageView imgBack;
    Button btnNxt;
    public static int selectedPosition = -1;
    RefreshShowingDialog getfilesDialogue, syncingDialog, syncingDialog1;
    ArrayList<Float> darkArray = new ArrayList<>();
    ArrayList<Float> whiteArray = new ArrayList<>();
    TextView txt_calibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_test_strip);
        imgBack = findViewById(R.id.back);
        txt_calibration = findViewById(R.id.tool_txt2);

        loadInterface();
        getfilesDialogue = new RefreshShowingDialog(SelectTestStripActivity.this, "Strip Ejecting..");
        syncingDialog = new RefreshShowingDialog(SelectTestStripActivity.this, "Configure Settings..");
        syncingDialog1 = new RefreshShowingDialog(SelectTestStripActivity.this, "Configuring..");

        SpectroDeviceDataController.getInstance().fillContext(getApplicationContext());
        SCTestAnalysis.getInstance().startTestProcess();
        SCTestAnalysis.getInstance().initializeService();
        // SCTestAnalysis.getInstance().fillContext(getApplicationContext());
        QUBETestingController.getInstance().fillContext(getApplicationContext());

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SCConnectionHelper.getInstance().scanDeviceInterface = null;
                SCConnectionHelper.getInstance().disconnectWithPeripheral();
                finish();
            }
        });

        loadDummyCommands();
        btnNxt = findViewById(R.id.btn_nxt);
        stripListView = findViewById(R.id.strip_list);
        stripAdapter = new SelectStripAdapter(SelectTestStripActivity.this);
        stripListView.setLayoutManager(new LinearLayoutManager(SelectTestStripActivity.this));
        stripListView.setAdapter(stripAdapter);
        btnNxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition > -1) {
                    Log.e("DialogInterface", "click");
                    //syncingDialog.showAlert();
                    loadFileWithFileName("CUBE_Covid19.json", "Urine AntiBody", "");
                } else {
                    new AlertShowingDialog(SelectTestStripActivity.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SELECT_A_STRIP_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }
            }
        });

        ArrayList<Float> dark = getArrayList("DarkArray");
        ArrayList<Float> white = getArrayList("WhiteArray");
        if (dark != null && white != null) {
            darkArray = dark;
            whiteArray = white;
            Log.e("getdark", "call" + dark.toString());
            Log.e("getwhite", "call" + white.toString());
        }

        txt_calibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncingDialog.showAlert();
                QUBETestingController.getInstance().gettingDarkSpectrum();
            }
        });
    }

    private void loadDummyCommands() {
        SCTestAnalysis.getInstance().isTestingCal = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SCConnectionHelper.getInstance().isConnected) {
                    SCTestAnalysis.getInstance().sendString("$SUV0#");
                }
            }
        }, 2000 * 1);
    }

    private void loadFileWithFileName(final String fileNmae, final String category, final String date) {
        QUBETestingController.getInstance().getDeviceSettings(fileNmae, category, "");
        if (darkArray.isEmpty() && whiteArray.isEmpty()) {
            Log.e("darkArrayisempty", "call");
         //   if (darkArray.size() > 0 && whiteArray.size() > 0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setMessage("Calibration for filling Dark and StandardWhite Arrays")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                syncingDialog.showAlert();
                                QUBETestingController.getInstance().gettingDarkSpectrum();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));

                Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setTextColor(Color.parseColor("#FF0012"));
           // }
        } else {
            Log.e("darkArrayisnotempty", "call");
            QUBETestingController.getInstance().loadPreviousDarkAndSWArrays(darkArray,whiteArray);
            syncingDialog.hideRefreshDialog();
            Intent intent = new Intent(getApplicationContext(), QubeSampleReadyActivity.class);
            intent.putExtra("filename", fileNmae);
            intent.putExtra("category", "Urine AntiBody");
            intent.putExtra("addedDate", "");
            startActivity(intent);
        }
    }

   /* public void syncingSettings(final String fileNmae, final String category, final String date) {
        if (SCTestAnalysis.getInstance().canDo()) {
            syncingDialog.showAlert();
            synSettingWithDevice(fileNmae);
        }
    }*/

    public void loadInterface() {
        QUBETestingController.getInstance().activatenotifications(new QUBETestingController.QUBETestDataInterface() {
            @Override
            public void gettingData(byte[] var1) {

            }

            @Override
            public void onSuccessForTestComplete(ArrayList<TestFactors> var1, String var2, ArrayList<IntensityChart> var3) {

            }

            @Override
            public void getRequestAndResponse(String var1) {

            }

            @Override
            public void onFailureForTesting(String var1) {

            }

            @Override
            public void isSyncingCompleted(boolean val, ArrayList<Float> white) {
                saveArrayList(white, "WhiteArray");
                Log.e("isSyncingCompleted", "call" + white.toString());
                  syncingDialog.hideRefreshDialog();
                Intent intent = new Intent(getApplicationContext(), QubeSampleReadyActivity.class);
                startActivity(intent);
            }

            @Override
            public void gettingDarkSpectrum(boolean val, ArrayList<Float> dark) {
                Log.e("gettingDarkSpectrum", "call" + dark.toString());
                saveArrayList(dark, "DarkArray");
            }
        });
    }

    public void saveArrayList(ArrayList<Float> list, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<Float> getArrayList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Float>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public void onBackPressed() {
        // SCConnectionHelper.getInstance().scanDeviceInterface = null;
        SCConnectionHelper.getInstance().disconnectWithPeripheral();
        selectedPosition = -1;
        finish();
        super.onBackPressed();
    }
}