package com.gtsr.gtsr.testModule;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gtsr.gtsr.AlertShowingDialog;
import com.gtsr.gtsr.HomeActivity;
import com.gtsr.gtsr.LanguagesKeys;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.RefreshShowingDialog;
import com.gtsr.gtsr.dataController.LanguageTextController;
import com.gtsr.gtsr.dataController.UrineTestDataCreatorController;
import com.gtsr.gtsr.database.TestFactorDataController;
import com.gtsr.gtsr.database.TestFactors;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCFileHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCTestAnalysis;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
//import de.hdodenhof.circleimageview.CircleImageView;

import static com.gtsr.gtsr.LanguagesKeys.BILLIBURIN_DESCRIPTION_RESULT_KEY;
import static com.gtsr.gtsr.LanguagesKeys.GLOUCOSE_DESCRIPTION_KEY;
import static com.gtsr.gtsr.LanguagesKeys.KETONS_DESCRIPTION_KEY;
import static com.gtsr.gtsr.LanguagesKeys.LECHOCYTE_DISCRIPTION_KEY;
import static com.gtsr.gtsr.LanguagesKeys.NITRATE_DISCRIPTION_KEY;
import static com.gtsr.gtsr.LanguagesKeys.OCCULT_BLOOD_DESCRIPTION_RESULT_KEY;
import static com.gtsr.gtsr.LanguagesKeys.PH_DISCRIPTION_KEY;
import static com.gtsr.gtsr.LanguagesKeys.PROTINE_DESCRIPTION_KEY;
import static com.gtsr.gtsr.LanguagesKeys.SG_DESCRIPTION_KEY;
import static com.gtsr.gtsr.LanguagesKeys.UROBILLINGEN_DESCRIPTION_RESULT_KEY;

/**
 * Created by WAVE on 9/29/2017.
 */

public class ResultPageViewController extends AppCompatActivity {
    Toolbar toolbar;
    ImageView back, img_share, deleteButton, img_doctor;
    TextView tool_text, txt_currenrDate, txt_month, txt_name, clientid, txt_nodata, healthdata, userage, usergender;
    public int selectedPosition = -1;
    RecyclerView resultRecyclerView;
    ResultsTableViewCell resultsTableViewCell;
    public static Calendar resultCalendar;
    SimpleDateFormat simpleDateFormat, simpleMonthFormat, dateFormat/*, currentdateFormat*/;
    RelativeLayout rl_home, rl_nodata, rl_recycler, relativeshare;
    RefreshShowingDialog alertDilogue;
    ArrayList<String> unitTypeArray;
    ArrayList<TestFactors> testFactorsArrayList;
    private static final int REQUEST_WRITE_PERMISSION = 56;
    EditText txt_editname;
    RefreshShowingDialog inserDialogue/*,ejectDialogue*/;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultpage);
        ButterKnife.bind(this);
        resultCalendar = Calendar.getInstance();
       // ejectDialogue = new RefreshShowingDialog(ResultPageViewController.this, "ejecting..");

        inserDialogue = new RefreshShowingDialog(ResultPageViewController.this, "Strip tray returning..");
        simpleDateFormat = new SimpleDateFormat("EEEE,dd", Locale.ENGLISH); //for get Monday ,sunday and dd for date
        simpleMonthFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);//for get,month year
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        // currentdateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH);
        alertDilogue = new RefreshShowingDialog(ResultPageViewController.this);
        //set current member testNameArray and reelation to textviws
        deleteButton = (ImageView) findViewById(R.id.btn_delete);
        txt_name = (TextView) findViewById(R.id.name);
        userage = (TextView) findViewById(R.id.userage);
        usergender = (TextView) findViewById(R.id.usergender);
        clientid = (TextView) findViewById(R.id.clientid);
        rl_nodata = (RelativeLayout) findViewById(R.id.rl_nodata);
        txt_nodata = (TextView) findViewById(R.id.txt_nodata);
        rl_recycler = (RelativeLayout) findViewById(R.id.rl_recycler);
        txt_editname = findViewById(R.id.text_editname);

        setToolbar();
        setResultRecyclerViewData();
        unitTypeArray = new ArrayList<String>();
        unitTypeArray.add("(mg/dl)");
        //  TabsGraphActivity.fillTestTypeArray();
        txt_currenrDate = (TextView) findViewById(R.id.txt_currentDate);
        loadCurrentDate();
        if (UrineResultsDataController.getInstance().currenturineresultsModel != null) {
            testFactorsArrayList = TestFactorDataController.getInstance().fetchTestFactorresults(UrineResultsDataController.getInstance().currenturineresultsModel);
        }
        activateNotifications();
    }

    private void loadCurrentDate() {
        // Calendar calobj = Calendar.getInstance();
        if (UrineResultsDataController.getInstance().currenturineresultsModel != null) {
            try {
                txt_editname.setText(UrineResultsDataController.getInstance().currenturineresultsModel.getUserName());
                txt_currenrDate.setText(UrineResultsDataController.getInstance().convertTestTimeTodate(UrineResultsDataController.getInstance().currenturineresultsModel.getTestedTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void setResultRecyclerViewData() {

        resultRecyclerView = (RecyclerView) findViewById(R.id.result_recycler);
        resultRecyclerView.setNestedScrollingEnabled(false);
        resultsTableViewCell = new ResultsTableViewCell();
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        resultRecyclerView.setLayoutManager(horizontalLayoutManager);
        resultRecyclerView.setAdapter(resultsTableViewCell);
        resultRecyclerView.setMotionEventSplittingEnabled(false);
        resultsTableViewCell.notifyDataSetChanged();
        resultsTableViewCell.notifyItemChanged(selectedPosition);
        //  txt_month = (TextView) findViewById(R.id.txt_month);
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back = (ImageView) findViewById(R.id.toolbar_icon);//Spectrum
        back.setBackgroundResource(R.drawable.back);
        tool_text = (TextView) toolbar.findViewById(R.id.toolbar_text);
        tool_text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.TEST_RESULTS_TILE_KEY));
        img_share = (ImageView) toolbar.findViewById(R.id.img_share);
        relativeshare = (RelativeLayout) toolbar.findViewById(R.id.relativeshare);
        // relativeshare.setOnClickListener(mShareListener);
        img_share.setBackgroundResource(R.drawable.share);
        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HtmltoPdfConversionActivity.class));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HomeActivity.isFromHome) {
                    HomeActivity.isFromHome = false;
                    finish();
                } else {
                    selectionBottomSheet();
                }
            }
        });
        rl_home = (RelativeLayout) findViewById(R.id.rootView);
        txt_editname.setImeOptions(EditorInfo.IME_ACTION_DONE);

    }

    View.OnClickListener mShareListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private void activateNotifications() {
        SCTestAnalysis.getInstance().ejectTesting(new SCTestAnalysis.EjectInterface() {
            @Override
            public void ejectStrip(boolean bool) {
            }
            @Override
            public void startTestForEjectTest(boolean bool) {
            }
            @Override
            public void stoptestForEjectTest(boolean bool) {
            }
            @Override
            public void insertStrip(boolean bool) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inserDialogue.hideRefreshDialog();
                        SCConnectionHelper.getInstance().disconnectWithPeripheral();
                        finish();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                });
            }
        });
    }
    public class ResultsTableViewCell extends RecyclerView.Adapter<ResultsTableViewCell.ViewHolder> {

        public ResultsTableViewCell() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_result_items, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            TestFactors objTestFactors = testFactorsArrayList.get(position);
            holder.testName.setText(objTestFactors.getTestName());

            holder.testValue.setText(objTestFactors.getResult());
            holder.unitsName.setText(objTestFactors.getUnit());

            holder.testrsult.setText(objTestFactors.getValue());

            Log.e("testresults", "all" + holder.testrsult.getText().toString());
            holder.txt_bg.setBackgroundColor(changeLableColor(holder.testrsult.getText().toString()));
            holder.txt_bg1.setBackgroundColor(changeLableColor(holder.testrsult.getText().toString()));
            if (objTestFactors.isFlag()) {
                holder.testCondition.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NORMAL_VALUE_KEY));
                holder.testCondition.setTextColor(Color.parseColor("#03DAC5"));
                holder.imageView.setBackgroundResource(R.drawable.happiness);
            } else {
                holder.imageView.setBackgroundResource(R.drawable.sad);
                holder.testCondition.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ABNORMAL_VALUE_KEY));
                holder.testCondition.setTextColor(Color.parseColor("#FF0000"));

            }

            if (selectedPosition == position) {
                holder.rlScrollMeg.setVisibility(View.VISIBLE);
                holder.down.setBackgroundResource(R.drawable.down_blue);
                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(getTheDescriptionForTestName(holder.testName.getText().toString()));
            } else {
                holder.rlScrollMeg.setVisibility(View.GONE);
                holder.down.setBackgroundResource(R.drawable.down_blue);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectedPosition != position) {
                        selectedPosition = position;
                    } else {
                        selectedPosition = -1;
                    }
                    notifyDataSetChanged();

                }

            });
        }

        @Override
        public int getItemCount() {
            if (testFactorsArrayList.size() > 0) {
                return testFactorsArrayList.size();
            } else {
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView testName, testValue, testCondition, txt_discription, unitsName, testrsult;
            Context ctx;
            ImageView imageView;
            Button down;
            TextView txt_bg, txt_bg1;
            RelativeLayout rlScrollMeg;

            public ViewHolder(View itemView) {
                super(itemView);
                testName = (TextView) itemView.findViewById(R.id.testName);
                unitsName = (TextView) itemView.findViewById(R.id.unitsName);
                testValue = (TextView) itemView.findViewById(R.id.testVal);
                testrsult = (TextView) itemView.findViewById(R.id.testrsult);
                testCondition = (TextView) itemView.findViewById(R.id.testCondition1);
                imageView = (ImageView) itemView.findViewById(R.id.img_icon);
                down = (Button) itemView.findViewById(R.id.btn_down);
                txt_bg = (TextView) itemView.findViewById(R.id.txt1);
                txt_bg1 = (TextView) itemView.findViewById(R.id.txt2);
                rlScrollMeg = (RelativeLayout) itemView.findViewById(R.id.rl_msg);
                txt_discription = (TextView) itemView.findViewById(R.id.test_discription);
            }
        }
    }

    public Bitmap convertByteArrayTOBitmap(byte[] profilePic) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(profilePic);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (HomeActivity.isFromHome) {
            HomeActivity.isFromHome = false;
            finish();
        } else {
            selectionBottomSheet();
            //finish();
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                Date selectedDateOject = null;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    selectedDateOject = df.parse(strEditText);
                    resultCalendar.setTime(selectedDateOject);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public int changeLableColor(String condition) {
        int color = 0;
        color = UrineTestDataCreatorController.getInstance().getTestColorForLable(condition);
        return color;
    }

    private String getTheDescriptionForTestName(String testName) {
        switch (testName) {
            case "LEUKOCYTES":
                return LanguageTextController.getInstance().currentLanguageDictionary.get(LECHOCYTE_DISCRIPTION_KEY);
            case "Nitrite":
                return LanguageTextController.getInstance().currentLanguageDictionary.get(NITRATE_DISCRIPTION_KEY);
            case "Urobilinogen":
                return LanguageTextController.getInstance().currentLanguageDictionary.get(UROBILLINGEN_DESCRIPTION_RESULT_KEY);
            case "Protein":
                return LanguageTextController.getInstance().currentLanguageDictionary.get(PROTINE_DESCRIPTION_KEY);
            case "PH":
                return LanguageTextController.getInstance().currentLanguageDictionary.get(PH_DISCRIPTION_KEY);
            case "Occult Blood":
                return LanguageTextController.getInstance().currentLanguageDictionary.get(OCCULT_BLOOD_DESCRIPTION_RESULT_KEY);
            case "Specific Gravity":
                return LanguageTextController.getInstance().currentLanguageDictionary.get(SG_DESCRIPTION_KEY);
            case "Ketone":
                return LanguageTextController.getInstance().currentLanguageDictionary.get(KETONS_DESCRIPTION_KEY);
            case "Bilirubin":
                return LanguageTextController.getInstance().currentLanguageDictionary.get(BILLIBURIN_DESCRIPTION_RESULT_KEY);
            default:  // GLUCOSE
                return LanguageTextController.getInstance().currentLanguageDictionary.get(GLOUCOSE_DESCRIPTION_KEY);
        }

    }

    public void selectionBottomSheet() {
        TextView first, second, third;
        View dialogView = getLayoutInflater().inflate(R.layout.file_dailog, null);
        final BottomSheetDialog cameraBottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(ResultPageViewController.this), R.style.BottomSheetDialogTheme);
        cameraBottomSheetDialog.setContentView(dialogView);
        LinearLayout rename = cameraBottomSheetDialog.findViewById(R.id.renamefile);
        LinearLayout show = cameraBottomSheetDialog.findViewById(R.id.showresult);
        LinearLayout dismiss = cameraBottomSheetDialog.findViewById(R.id.layout_dismiss);
        LinearLayout layout = cameraBottomSheetDialog.findViewById(R.id.thirdlayout);

        layout.setVisibility(View.VISIBLE);
        first = cameraBottomSheetDialog.findViewById(R.id.first);
        second = cameraBottomSheetDialog.findViewById(R.id.second);
        third = cameraBottomSheetDialog.findViewById(R.id.third);

        first.setText("Return Strip Tray and Go Home");
        second.setText("Go Home");
        third.setText("Test Again");

        FrameLayout bottomSheet = (FrameLayout) cameraBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheet.setBackground(null);

        cameraBottomSheetDialog.show();

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraBottomSheetDialog.dismiss();
                HomeActivity.isFromHome=false;
                saveSrting("true");
                inserDialogue.showAlert();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (SCConnectionHelper.getInstance().isConnected) {
                            SCTestAnalysis.getInstance().insertStripCommand();
                        }
                    }
                }, 1000 * 1);
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraBottomSheetDialog.dismiss();
                HomeActivity.isFromHome = false;
                finish();
                SCConnectionHelper.getInstance().disconnectWithPeripheral();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.isFromHome = false;
                cameraBottomSheetDialog.dismiss();
                finish();
            }
        });
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.isFromHome = false;
                cameraBottomSheetDialog.dismiss();
            }
        });

    }
    public void saveSrting(String isRetrunstrip) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("isRetrunstrip", isRetrunstrip);
        Log.e("isRetrunstrip", "call" + isRetrunstrip);
        editor.apply();
        editor.commit();
    }
}


