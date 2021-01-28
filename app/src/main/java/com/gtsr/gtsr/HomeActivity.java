package com.gtsr.gtsr;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.gtsr.gtsr.adapter.AntiBodyTestAdapter;
import com.gtsr.gtsr.dataController.LanguageTextController;
import com.gtsr.gtsr.dataController.QubeController;
import com.gtsr.gtsr.dataController.UrineTestDataCreatorController;
import com.gtsr.gtsr.database.TestFactorDataController;
import com.gtsr.gtsr.database.TestFactors;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.gtsr.gtsr.loginModule.LoginActivity;
import com.gtsr.gtsr.qubeTestResults.QubeGraphResultActivity;
import com.gtsr.gtsr.testModule.MyApplication;
import com.gtsr.gtsr.testModule.PairDeviceViewController;
import com.gtsr.gtsr.testModule.PastResultsActivity;
import com.gtsr.gtsr.testModule.ResultPageViewController;
import com.gtsr.gtsr.testModule.TestActivity;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    RecyclerView resultRecyclerView;
    ResultRecyclerAdapter recyclerAdapter;
    AntiBodyTestAdapter antiBodyTestAdapter;
    ImageView imgChart;
    RelativeLayout imgTextNow;
    ArrayList<TestFactors> testFactorsArrayList;
    Button btnUrineTest, btnBodyTest;
    public static boolean isFromHome = false;
    boolean isRefresh = false;
    public ArrayList<UrineresultsModel> tempUrineResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LanguageTextController.getInstance().fillCOntext(MyApplication.getAppContext());
        LanguageTextController.getInstance().loadLanguageTexts();
        QubeController.getInstance().fillCOntext(getApplicationContext());
        UrineTestDataCreatorController.getInstance();
        inti();
    }

    public void inti() {
        resultRecyclerView = findViewById(R.id.result_recyclerview);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        imgTextNow = findViewById(R.id.img_test_now);
        imgChart = findViewById(R.id.img_chart);
        btnBodyTest = findViewById(R.id.btn_body_test);
        btnUrineTest = findViewById(R.id.btn_urine_test);
        if (UrineResultsDataController.getInstance().allUrineResults != null) {
            if (UrineResultsDataController.getInstance().allUrineResults.size() > 0) {
                loadResults();
            }
        }
        imgChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginActivity.isGtsrSelected) {
                    if (UrineResultsDataController.getInstance().allUrineResults != null) {
                        isFromHome = false;
                        if (UrineResultsDataController.getInstance().allUrineResults.size() > 0) {
                            startActivity(new Intent(HomeActivity.this, PastResultsActivity.class));
                        }
                    }
                } else {
                    startActivity(new Intent(HomeActivity.this, QubeGraphResultActivity.class));
                }
            }
        });

        imgTextNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromHome = false;
                startActivity(new Intent(getApplicationContext(), PairDeviceViewController.class));
            }
        });
        if (LoginActivity.isGtsrSelected) {
           urineTestAction();
        }else {
            antiBodyTestAction();
        }
        btnUrineTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urineTestAction();
            }
        });
        btnBodyTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                antiBodyTestAction();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LoginActivity.isGtsrSelected) {
            loadResults();
            urineTestAction();
        }else {
            antiBodyTestAction();
        }

    }

    private void urineTestAction() {
        btnChangeOnClick(btnUrineTest, btnBodyTest);
        LoginActivity.isGtsrSelected = true;
        recyclerAdapter = new ResultRecyclerAdapter(HomeActivity.this, testFactorsArrayList);
        resultRecyclerView.setAdapter(recyclerAdapter);
    }

    private void antiBodyTestAction() {
        btnChangeOnClick(btnBodyTest, btnUrineTest);
        LoginActivity.isGtsrSelected = false;
        antiBodyTestAdapter = new AntiBodyTestAdapter(HomeActivity.this);
        resultRecyclerView.setAdapter(antiBodyTestAdapter);
    }

    public void btnChangeOnClick(Button btn1, Button btn2) {
        btn1.setBackgroundResource(R.drawable.btn_color_background);
        btn2.setBackgroundResource(R.drawable.btn_boarder);
    }

    private void loadResults() {
        UrineResultsDataController.getInstance().fetchAllUrineResults();
        if (UrineResultsDataController.getInstance().currenturineresultsModel != null) {
            testFactorsArrayList = TestFactorDataController.getInstance().fetchTestFactorresults(UrineResultsDataController.getInstance().currenturineresultsModel);
        }
        recyclerAdapter = new ResultRecyclerAdapter(HomeActivity.this, testFactorsArrayList);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        resultRecyclerView.setAdapter(recyclerAdapter);
    }

    public class ResultRecyclerAdapter extends RecyclerView.Adapter<ResultRecyclerAdapter.ResultHolder> {
        Context context;
        int itemPosition = -1;
        ArrayList<TestFactors> testFactorsArrayList;

        public ResultRecyclerAdapter(Context context, ArrayList<TestFactors> testFactorsArrayList1) {
            this.context = context;
            this.testFactorsArrayList = testFactorsArrayList1;
        }

        @NonNull
        @Override
        public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View resultView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_result_item, parent,
                    false);
            return new ResultHolder(resultView);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
            UrineresultsModel objTestFactors = tempUrineResults.get(position);
            holder.txtDate.setText(convertTimestampTodate(objTestFactors.getTestedTime()));
            holder.txtName.setText(objTestFactors.getUserName());
            if (itemPosition == position) {
                holder.imgRound.setImageResource(R.drawable.check);
                HomeActivity.isFromHome = true;
                UrineResultsDataController.getInstance().currenturineresultsModel = tempUrineResults.get(itemPosition);
                if (!isRefresh) {
                    selectionBottomSheet();
                } else {
                    isRefresh = false;
                }
            } else {
                holder.imgRound.setImageResource(R.drawable.ellipse);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemPosition != position) {
                        itemPosition = position;
                        notifyDataSetChanged();
                    } else {
                        itemPosition = -1;
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            loadResultData();
            if (tempUrineResults.size() > 0) {
                return tempUrineResults.size();
            } else {
                return 0;
            }
        }

        public class ResultHolder extends RecyclerView.ViewHolder {
            ImageView imgRound;
            TextView txtDate, txtName;

            public ResultHolder(@NonNull View itemView) {
                super(itemView);
                imgRound = itemView.findViewById(R.id.img_check);
                txtDate = itemView.findViewById(R.id.txtdate);
                txtName = itemView.findViewById(R.id.txtname);
            }
        }

        public String convertTimestampTodate(String stringData) {
            long yourmilliseconds = Long.parseLong(stringData);
            SimpleDateFormat weekFormatter = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
            Date resultdate = new Date(yourmilliseconds * 1000);
            String weekString = weekFormatter.format(resultdate);
            return weekString;
        }

        public void selectionBottomSheet() {
            TextView third;
            View dialogView = getLayoutInflater().inflate(R.layout.file_dailog, null);
            final BottomSheetDialog cameraBottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(HomeActivity.this), R.style.BottomSheetDialogTheme);
            cameraBottomSheetDialog.setContentView(dialogView);
            LinearLayout rename = cameraBottomSheetDialog.findViewById(R.id.renamefile);
            LinearLayout show = cameraBottomSheetDialog.findViewById(R.id.showresult);
            LinearLayout dismiss = cameraBottomSheetDialog.findViewById(R.id.layout_dismiss);
            LinearLayout layout = cameraBottomSheetDialog.findViewById(R.id.thirdlayout);
            layout.setVisibility(View.VISIBLE);
            FrameLayout bottomSheet = (FrameLayout) cameraBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.setBackground(null);
            third = cameraBottomSheetDialog.findViewById(R.id.third);
            third.setText("Delete Result");
            cameraBottomSheetDialog.show();

            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraBottomSheetDialog.dismiss();
                    withEditText();
                }
            });
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraBottomSheetDialog.dismiss();
                    if (UrineResultsDataController.getInstance().deleteurineresultsData(UrineResultsDataController.getInstance().currenturineresultsModel)) {
                        isRefresh = true;
                        Toast.makeText(getApplicationContext(), "Delete successfully", Toast.LENGTH_SHORT).show();
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }
            });
            show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraBottomSheetDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), ResultPageViewController.class));
                }
            });
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraBottomSheetDialog.cancel();
                }
            });

        }
    }

    public String username = "";

    public void withEditText() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename File");
        builder.setCancelable(false);

        final EditText input = new EditText(HomeActivity.this);
        input.setText(UrineResultsDataController.getInstance().currenturineresultsModel.getUserName());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                username = input.getText().toString();
                isRefresh = true;
                Log.e("username", "call" + username);
                UrineResultsDataController.getInstance().currenturineresultsModel.setUserName(username);
                UrineResultsDataController.getInstance().updateUrineResults(UrineResultsDataController.getInstance().currenturineresultsModel);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getApplicationWindowToken(), 0);
                recyclerAdapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }
    private void loadResultData() {
        tempUrineResults.clear();
        if (UrineResultsDataController.getInstance().allUrineResults.size() > 0) {
            for (int i = 0; i < UrineResultsDataController.getInstance().allUrineResults.size(); i++) {
                UrineresultsModel urineresultsModel = UrineResultsDataController.getInstance().allUrineResults.get(i);
                if (urineresultsModel.getTestType().contains(Constants.TestNames.urine.toString())) {
                    tempUrineResults.add(urineresultsModel);
                }
            }
            Log.e("urinetemparray","call"+tempUrineResults.size());

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}