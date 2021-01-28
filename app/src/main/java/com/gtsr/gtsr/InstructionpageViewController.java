package com.gtsr.gtsr;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCTestAnalysis;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by Rise on 20/09/2017.
 */

public class InstructionpageViewController extends AppCompatActivity  {
    ImageView image_animate, back, play, pause;
    ArrayList<Integer> imageArray = new ArrayList<>();
    TextView myTextView, myTextView1, stepone, next;
    int pStatus;
    public static boolean isCircularBackbtnCilcked = false;
    Handler handler = new Handler();
    TextView toolbartext, toolskiptext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructionpage);

        activateInterface();
       /* imageArray.add(R.drawable.ic_strip_two);
        imageArray.add(R.drawable.ic_strip_three);
        imageArray.add(R.drawable.ic_strip_four);
        imageArray.add(R.drawable.ic_strip_five);
        imageArray.add(R.drawable.ic_strip_six);
        imageArray.add(R.drawable.ic_strip_seven);*/
        init();


        image_animate = (ImageView) findViewById(R.id.animation);
        myTextView = (TextView) findViewById(R.id.urinetext);
        myTextView.setTextColor(Color.parseColor("#233760"));
        //   myTextView1 = (TextView) findViewById(R.id.urinetext1);

        play = (ImageView) findViewById(R.id.play);
        pause = (ImageView) findViewById(R.id.pause);
        // stepone = (TextView) findViewById(R.id.stepone);


        next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(updateTimerThread);
               /* StripinsertPage.isBackbtnClicked = false;
                startActivity(new Intent(getApplicationContext(), StripinsertPage.class));
*/
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                imageArray.add(R.drawable.ic_strip_one);
                pStatus = 7;
                image_animate.setVisibility(View.VISIBLE);
                handler.postDelayed(updateTimerThread, 1000);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                pStatus = 0;
                image_animate.setVisibility(View.VISIBLE);
                handler.removeCallbacks(updateTimerThread);
            }
        });
        updateLanguageTexts();


    }
    private void activateInterface(){
        SCConnectionHelper.getInstance().activateScanNotification(new SCConnectionHelper.ScanDeviceInterface() {
            @Override
            public void onSuccessForConnection(String msg) {

            }

            @Override
            public void onSuccessForScanning(ArrayList<BluetoothDevice> deviceArray, boolean msg) {

            }

            @Override
            public void onFailureForConnection(String error) {
                EventBus.getDefault().unregister(this);
                SCTestAnalysis.getInstance().clearCache();
                SCTestAnalysis.getInstance().clearPreviousTestResulsArray();
                SCConnectionHelper.getInstance().disconnectWithPeripheral();
               // startActivity(new Intent(getApplicationContext(), SelectStripOneViewController.class));
            }

            @Override
            public void uartServiceClose(String error) {

            }

            @Override
            public void onBLEStatusChange(int state) {

            }
        });
    }

    private void init() {
        back = (ImageView) findViewById(R.id.home);
       // back.setImageResource(R.drawable.ic_back);
        toolskiptext = (TextView) findViewById(R.id.toolskip_txt);
        toolskiptext.setVisibility(View.VISIBLE);
        toolskiptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", "call");
                handler.removeCallbacks(updateTimerThread);
               /* StripinsertPage.isBackbtnClicked = false;
                startActivity(new Intent(getApplicationContext(), AnalizingPageViewController.class));*/
            }
        });
        toolbartext = (TextView) findViewById(R.id.tool_txt);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                 handler.removeCallbacksAndMessages(null);
                // startActivity(new Intent(getApplicationContext(), SelectClientViewController.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        activateInterface();

        if (isCircularBackbtnCilcked) {
            image_animate.setVisibility(View.VISIBLE);
            pStatus = 0;
            handler.removeCallbacks(updateTimerThread);
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);

        } else {
            pStatus = 6;
            image_animate.setVisibility(View.VISIBLE);
            handler.postDelayed(updateTimerThread, 1000);

        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        pStatus = 0;
        handler.removeCallbacks(updateTimerThread);
        play.setVisibility(View.VISIBLE);
    }

    public void updateLanguageTexts() {
        // stepone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.STEP1_KEY));
      /*  myTextView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.COLLECT_URINE_SAMPLE_WITH_TESTTUBE_KEY));
        //  myTextView1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.STRIP_INSTRUCTION_DESCRIPTION_KEY));
        toolbartext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.COLLECT_URINE_SAMPLE_KEY));
        next.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NEXT_KEY));
        toolskiptext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SKIP_BUTTON_KEY));
*/
    }

    public Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            image_animate.setVisibility(View.VISIBLE);
            pStatus -= 1;
            image_animate.setBackgroundResource(imageArray.get(pStatus));
            handler.postDelayed(this, 1000);

            if (pStatus == 0) {
                image_animate.setVisibility(View.VISIBLE);
                pStatus = 0;
                pause.setVisibility(View.INVISIBLE);
                play.setVisibility(View.VISIBLE);
                handler.removeCallbacks(updateTimerThread);
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        handler.removeCallbacks(updateTimerThread);
       // startActivity(new Intent(getApplicationContext(), SelectClientViewController.class));
    }
}
