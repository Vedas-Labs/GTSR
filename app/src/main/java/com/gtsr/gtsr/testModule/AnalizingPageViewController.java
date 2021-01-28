package com.gtsr.gtsr.testModule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gtsr.gtsr.AlertShowingDialog;
import com.gtsr.gtsr.Constants;
import com.gtsr.gtsr.HomeActivity;
import com.gtsr.gtsr.LanguagesKeys;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.RefreshShowingDialog;
import com.gtsr.gtsr.dataController.ExportToExcelFileController;
import com.gtsr.gtsr.dataController.LanguageTextController;
import com.gtsr.gtsr.database.TestFactorDataController;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.j256.ormlite.stmt.query.In;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCTestAnalysis;
import com.spectrochips.spectrumsdk.FRAMEWORK.TestFactors;
import com.spectrochips.spectrumsdk.MODELS.IntensityChart;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Abhilash on 2/1/2019.
 */

public class AnalizingPageViewController extends AppCompatActivity {
    ImageView imagerotateanalizine;
    RotateAnimation rotate;
    long unixTime;
    TextView abort, toolbartext, waiting, txt_response, txt_results, txt_counter,precenttext;
    Handler handler=new Handler();
    RefreshShowingDialog testDialogue;
    public static String username;
    ProgressBar progressBar;
    int pStatus = 0;
    public boolean isAbort=false;
    RefreshShowingDialog ejectDialogue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analizingpage);
        ejectDialogue = new RefreshShowingDialog(AnalizingPageViewController.this, "ejecting..");

        init();
        unixTime = System.currentTimeMillis() / 1L;
        // TestDairyViewController.selectedPosition = 0;
        updatelanguage();
        testDialogue = new RefreshShowingDialog(AnalizingPageViewController.this, "Analyzing...");
        activateNotification();
        loadProgress();
    }

    private void loadProgress() {
        progressBar = (ProgressBar) findViewById(R.id.progressbar_updatedevice);
        precenttext=findViewById(R.id.precenttext);
        handler.postDelayed(updateTimerThread,100);
    }
    public Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            progressBar.setVisibility(View.VISIBLE);
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.custom_progress);
            progressBar.setProgress(0);   // Main Progress
            progressBar.setSecondaryProgress(0); // Secondary Progress
            progressBar.setMax(100); // Maximum Progress
            progressBar.setProgressDrawable(drawable);
            progressBar.setProgress(pStatus);
            String pre = pStatus + "%";
            precenttext.setText(pre);
            handler.postDelayed(this, 100);
           /* if (pStatus == 11) {
                Log.e("profor10", "" + pStatus);
                handler.removeCallbacks(updateTimerThread);
                precenttext.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                pStatus = 0;
                progressBar.setProgress(0);
                precenttext.setText("" + pStatus);
            }*/
        }
    };
    public void activateNotification() {
        if (SCConnectionHelper.getInstance().isConnected) {
            abort.setVisibility(View.VISIBLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// for stopping screen off while in testing
            SCTestAnalysis.getInstance().loadInterface();
            SCTestAnalysis.getInstance().startTestAnalysis(new SCTestAnalysis.TeststaResultInterface() {
                @Override
                public void onSuccessForTestComplete(ArrayList<TestFactors> results, String msg, ArrayList<IntensityChart> intensityChartsArray) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.removeCallbacks(updateTimerThread);
                            precenttext.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            pStatus = 0;
                            progressBar.setProgress(0);
                            precenttext.setText("" + pStatus);
                            ///////////
                            HtmltoPdfConversionActivity.testResults = results;
                            HtmltoPdfConversionActivity.intensityArray = intensityChartsArray;
                            loadResultsDataTODb(results);
                        }
                    });
                }

                @Override
                public void getRequestAndResponse(String data) {
                }

                @Override
                public void onFailureForTesting(String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                try {
                                  //  performTestFailedFunctibon();
                                } catch (WindowManager.BadTokenException e) {
                                    Log.e("WindowManagerBad ", e.toString());
                                }
                            }
                        }
                    });
                }
            });
            SCTestAnalysis.getInstance().startTesting();
        } else {
          //  Toast.makeText(getApplicationContext(), "Device not Connected", Toast.LENGTH_SHORT).show();
        }
        SCConnectionHelper.getInstance().activateScanNotification(new SCConnectionHelper.ScanDeviceInterface() {
            @Override
            public void onSuccessForConnection(String msg) {
            }
            @Override
            public void onSuccessForScanning(ArrayList<BluetoothDevice> deviceArray, boolean msg) {
            }
            @Override
            public void onFailureForConnection(String error) {
                Log.e("onFailureForConnection", "msssasnasn");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rotate.cancel();
                        new AlertShowingDialog(AnalizingPageViewController.this, "Device Disconnected", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                    }
                });
            }
            @Override
            public void uartServiceClose(String error) {
            }
            @Override
            public void onBLEStatusChange(int state) {

            }
        });
        SCTestAnalysis.getInstance().toastIntterfaceMethod(new SCTestAnalysis.TestToastInterface() {
            @Override
            public void getResponse(String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (data.length() > 0) {
                            if(data.contains("StripNumber")){
                            String a= data.replace("StripNumber","");
                                int i=Integer.parseInt(a.trim());
                                double d=100/11*i;
                                if(i==11){
                                    pStatus=100;
                                }else {
                                    pStatus = (int) d;
                                }
                               // Log.e("pStatus", "pStatus"+d+"nnn"+pStatus);
                            }
                        }
                    }
                });
            }
        });
        SCTestAnalysis.getInstance().ejectTesting(new SCTestAnalysis.EjectInterface() {
            @Override
            public void ejectStrip(boolean bool) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SCConnectionHelper.getInstance().disconnectWithPeripheral();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        Toast.makeText(getApplicationContext(), "Strip Ejected.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void startTestForEjectTest(boolean bool) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        removeProgressBar();
                        abort.setVisibility(View.GONE);
                        waiting.setText("Strip Ejecting..");
                        //  Toast.makeText(getApplicationContext(), "Test Aborted.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void stoptestForEjectTest(boolean bool) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(getApplicationContext(), ResultPageViewController.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                    }
                });
            }
            @Override
            public void insertStrip(boolean bool) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Insert Strip.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void init() {
        imagerotateanalizine = (ImageView) findViewById(R.id.image);
        RelativeLayout back = findViewById(R.id.home);
        toolbartext = (TextView) findViewById(R.id.tool_txt);
        txt_results = findViewById(R.id.next);
        txt_results.setVisibility(View.GONE);
        txt_response = findViewById(R.id.response);
        txt_response.setMovementMethod(new ScrollingMovementMethod());
        txt_counter = findViewById(R.id.count);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  performAbortFunction();
                // finish();
            }
        });

        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        imagerotateanalizine.setAnimation(rotate);

        abort = (TextView) findViewById(R.id.abort);
        waiting = (TextView) findViewById(R.id.waiting);

        abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performAbortFunction();
            }
        });

        txt_results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), ResultPageViewController.class));
            }
        });
    }


    public void updatelanguage() {
        waiting.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WAITING_FOR_RESULTS_KEY));
        abort.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ABORT_KEY));
        toolbartext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ANALZING_KEY));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void performAbortFunction() {
        final Dialog dialog = new Dialog(AnalizingPageViewController.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.stopanimate_alert);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        TextView text = (TextView) dialog.findViewById(R.id.text_reminder);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERT_KEY));

        TextView text1 = (TextView) dialog.findViewById(R.id.text);
        text1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DO_YOU_WANT_TO_ABORT_TEST_KEY));

        Button no = (Button) dialog.findViewById(R.id.btn_no);
        no.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        Button yes = (Button) dialog.findViewById(R.id.btn_yes);
        yes.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YES_KEY));


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProgressBar();

                dialog.dismiss();
                imagerotateanalizine.setVisibility(View.VISIBLE);
                imagerotateanalizine.setAnimation(rotate);

                waiting.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ABORTING_KEY));
                isAbort = true;
                SCTestAnalysis.getInstance().abortTesting(new SCTestAnalysis.AbortInterface() {
                    @Override
                    public void onAbortForTesting(boolean isabort) {
                        if (isabort) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    abort.setVisibility(View.GONE);
                                    waiting.setText("Strip Ejecting..");
                                    Toast.makeText(getApplicationContext(), "Test Aborted.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
    private void removeProgressBar(){
        handler.removeCallbacks(updateTimerThread);
        precenttext.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        pStatus = 0;
        progressBar.setProgress(0);
        precenttext.setText("" + pStatus);
    }
    AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder;
    private void performTestFailedFunctibon() {
         alertDialogBuilder = new AlertDialog.Builder(AnalizingPageViewController.this);
        alertDialogBuilder.setMessage(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_STRIP_HOLDER_WAS_DETECTED_KEY))
                .setCancelable(false)
                .setPositiveButton(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.TRY_AGAIN_KEY), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SCTestAnalysis.getInstance().performTryAgainFunction();
                        SCTestAnalysis.getInstance().startTesting();
                    }
                })
                .setNegativeButton(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SCTestAnalysis.getInstance().performTestCancelFunction();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));

        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#FF0012"));

        positiveButton.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
        negativeButton.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

    }

    String currentTime = String.valueOf(System.currentTimeMillis() / 1000L);
    String testID = "test" + String.valueOf(getRandomInteger(1000, 10));

    public int getRandomInteger(int maximum, int minimum) {
        return ((int) (Math.random() * (maximum - minimum))) + minimum;
    }

    public void loadResultsDataTODb(ArrayList<TestFactors> testResults) {
        UrineresultsModel objResult = new UrineresultsModel();
        objResult.setTestReportNumber("1");
        objResult.setTestType(Constants.TestNames.urine.toString());
        objResult.setLatitude("0.0");
        objResult.setLongitude("0.0");
        objResult.setTestedTime(currentTime);
        objResult.setIsFasting("false");
        objResult.setRelationtype(Constants.TestNames.urine.toString());
        objResult.setTest_id(testID);
        objResult.setUserName(username + " Urine Analysis");

        if (UrineResultsDataController.getInstance().insertUrineResultsForMember(objResult)) {
            for (int index = 0; index < testResults.size(); index++) {
                TestFactors object = testResults.get(index);
                com.gtsr.gtsr.database.TestFactors objTest = new com.gtsr.gtsr.database.TestFactors();
                objTest.setFlag(object.isFlag());
                objTest.setUnit(object.getUnits());
                objTest.setHealthReferenceRanges(object.getReferenceRange());
                objTest.setTestName(object.getTestname());
                objTest.setResult(object.getResult());
                objTest.setValue(object.getValue());
                objTest.setUrineresultsModel(UrineResultsDataController.getInstance().currenturineresultsModel);
                if (TestFactorDataController.getInstance().insertTestFactorResults(objTest)) {

                }
            }
             Toast.makeText(getApplicationContext(), "Testing Completed", Toast.LENGTH_SHORT).show();
           /* finish();
            startActivity(new Intent(getApplicationContext(), ResultPageViewController.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );*/
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* if(alertDialog.isShowing())
        alertDialog.dismiss();*/
    }

    @Override
    public void onBackPressed() {
        // performAbortFunction();
    }
}
