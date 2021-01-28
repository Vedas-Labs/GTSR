package com.gtsr.gtsr.testModule;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.gtsr.gtsr.Constants;
import com.gtsr.gtsr.LanguagesKeys;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.dataController.UrineTestDataCreatorController;
import com.gtsr.gtsr.database.TestFactorDataController;
import com.gtsr.gtsr.database.TestFactors;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.gtsr.gtsr.model.RangeLimit;
import com.gtsr.gtsr.model.UrineTestObject;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCTestAnalysis;
import com.spectrochips.spectrumsdk.FRAMEWORK.SpectroCareSDK;
import com.spectrochips.spectrumsdk.MODELS.SpectroDeviceDataController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;

import static android.view.View.GONE;

public class PastResultsActivity extends AppCompatActivity {
    RelativeLayout layout;
    int selectedPosition = -1;
    int selectedIndex = 0;
    ArrayList<String> testTypeArray;
    LineChart mChart;
    ImageView back;
    ArrayList<String> dayAxiesArray;
    ArrayList<Double> dayValuesArray;
    ArrayList<UrineresultsModel> resultsDayArray;
    SimpleDateFormat dateformat, weekFormatter;
    XAxis xAxis;
    boolean isDataAvailable = false;
    UrineTestObject selectedUrineTestRecord;
    RelativeLayout fourplusView, threePlusView, twoPlusView, plusView, plusMinusView, negativeview;
    TextView fourplusText, threePlusText, twoPlusText, plusText, plusMinusText, negativeText;
    RecyclerView resultRecyclerView;
    ResultsTableViewCell resultsTableViewCell;
    int selectedTestTypeRecordIndex = 0;
    TextView txt_currentDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pastresults);

        back = findViewById(R.id.backimage);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txt_currentDate = findViewById(R.id.txt_currentDate);
        fourplusView = (RelativeLayout) findViewById(R.id.l_red);
        threePlusView = (RelativeLayout) findViewById(R.id.orange);
        twoPlusView = (RelativeLayout) findViewById(R.id.l_purple);
        plusView = (RelativeLayout) findViewById(R.id.l_blue);
        plusMinusView = (RelativeLayout) findViewById(R.id.l_lemonyellow);
        negativeview = (RelativeLayout) findViewById(R.id.green_view);

        fourplusText = (TextView) findViewById(R.id.redcondition);
        threePlusText = (TextView) findViewById(R.id.orangecondition);
        twoPlusText = (TextView) findViewById(R.id.violetcondition);
        plusText = (TextView) findViewById(R.id.bluecondition);
        plusMinusText = (TextView) findViewById(R.id.yellwcondition);
        negativeText = (TextView) findViewById(R.id.greencondition);
        dateformat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        weekFormatter = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH);


        mChart = (LineChart) findViewById(R.id.chart_combine_week);
        xAxis = mChart.getXAxis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
        SpectroCareSDK.getInstance().fillContext(getApplicationContext());
        SpectroDeviceDataController.getInstance().fillContext(getApplicationContext());
        SCTestAnalysis.getInstance().fillContext(getApplicationContext());
        loadLatestRecord();
        setResultRecyclerViewData();
        loadDayData(testTypeArray.get(0/*TabsGraphActivity.selectedTestTypeRecordIndex*/), dateForLand);
        loadFirstRecordData();

    }
    public void setResultRecyclerViewData() {
        fillTestTypeArray();
        resultRecyclerView = (RecyclerView) findViewById(R.id.week_list);
        resultRecyclerView.setNestedScrollingEnabled(false);
        resultsTableViewCell = new ResultsTableViewCell(testTypeArray, this.getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        resultRecyclerView.setLayoutManager(horizontalLayoutManager);
        resultRecyclerView.setAdapter(resultsTableViewCell);
        resultsTableViewCell.notifyDataSetChanged();
    }
    public void fillTestTypeArray() {
        testTypeArray = new ArrayList<String>();
        testTypeArray.add(Constants.UrineTestItems.LEUKOCYTES.toString());
        testTypeArray.add(Constants.UrineTestItems.Nitrite.toString());
        testTypeArray.add(Constants.UrineTestItems.Urobilinogen.toString());
        testTypeArray.add(Constants.UrineTestItems.Protein.toString());
        testTypeArray.add(Constants.UrineTestItems.PH.toString());
        testTypeArray.add(Constants.UrineTestItems.OccultBlood.toString());
        testTypeArray.add(Constants.UrineTestItems.SpecificGravity.toString());
        testTypeArray.add(Constants.UrineTestItems.Ketone.toString());
        testTypeArray.add(Constants.UrineTestItems.Bilirubin.toString());
        testTypeArray.add(Constants.UrineTestItems.Glucose.toString());
        testTypeArray.add(Constants.UrineTestItems.Ascorbic.toString());
    }

    Date dateForLand;
    Calendar calender;

    public void loadLatestRecord() {
        UrineResultsDataController.getInstance().fetchAllUrineResults();
        TestFactorDataController.getInstance().fetchTestFactorresults(UrineResultsDataController.getInstance().currenturineresultsModel);
        for (int i = 0; i < UrineResultsDataController.getInstance().allUrineResults.size(); i++) {
            Log.e("humanurinesize", "" + i);
            if (UrineResultsDataController.getInstance().allUrineResults.size() > 0) {
                UrineresultsModel selectedObjects1 = UrineResultsDataController.getInstance().allUrineResults.get(i);
                String objTimeStamp1 = selectedObjects1.getTestedTime();
                long yourmilliseconds1 = Long.parseLong(objTimeStamp1);
                calender = Calendar.getInstance();
                Date date1 = new Date(yourmilliseconds1 * 1000);
                calender.setTime(date1);
                dateForLand = calender.getTime();
                Log.e("dateForLand", "" + dateForLand);
            }
        }
    }

    private void loadDayData(String testType, Date requiredDate) {
        dayAxiesArray = new ArrayList<String>();
        dayValuesArray = new ArrayList<Double>();
        resultsDayArray = new ArrayList<>();
        mChart.clear();

        String objDateString = dateformat.format(requiredDate.getTime());
        Log.e("objDateString", "" + objDateString);
        txt_currentDate.setText(weekFormatter.format(requiredDate));

        try {
            resultsDayArray = UrineTestDataCreatorController.getInstance().getFilterArrayForDateString(objDateString);
            Log.e("resultsDayArray", "" + resultsDayArray.size());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (resultsDayArray.size() > 0) {

            for (int i = 0; i < resultsDayArray.size(); i++) {
                UrineresultsModel objUrineRecord = resultsDayArray.get(i);
                long yourmilliseconds = Long.parseLong(objUrineRecord.getTestedTime());
                Date resultdate = new Date(yourmilliseconds * 1000);
                dayAxiesArray.add(dateformat.format(resultdate));//load x-Axies formate

                try {
                    Log.e("resultsDayArray", "" + resultsDayArray);
                    ArrayList<TestFactors> testFactors = TestFactorDataController.getInstance().fetchTestFactorresults(objUrineRecord);
                    UrineTestObject objUrineTest = UrineTestDataCreatorController.getInstance().getUrineTestDataAvgTestFactors(testFactors);
                    if (testType.equals(Constants.UrineTestItems.LEUKOCYTES.toString())) {
                        dayValuesArray.add(objUrineTest.getLeokocit());
                    } else if (testType.equals(Constants.UrineTestItems.Nitrite.toString())) {
                        dayValuesArray.add(objUrineTest.getNitrite());
                    } else if (testType.equals(Constants.UrineTestItems.Urobilinogen.toString())) {
                        dayValuesArray.add(objUrineTest.getUrobiliogen());
                    } else if (testType.equals(Constants.UrineTestItems.Protein.toString())) {
                        dayValuesArray.add(objUrineTest.getProtein());
                    } else if (testType.equals(Constants.UrineTestItems.PH.toString())) {
                        dayValuesArray.add(objUrineTest.getPh());
                    } else if (testType.equals(Constants.UrineTestItems.OccultBlood.toString())) {
                        dayValuesArray.add(objUrineTest.getOccultBlood());
                    } else if (testType.equals(Constants.UrineTestItems.SpecificGravity.toString())) {
                        dayValuesArray.add(objUrineTest.getSg());
                    } else if (testType.equals(Constants.UrineTestItems.Ketone.toString())) {
                        dayValuesArray.add(objUrineTest.getKetones());
                    } else if (testType.equals(Constants.UrineTestItems.Bilirubin.toString())) {
                        dayValuesArray.add(objUrineTest.getBillirubinValue());
                    } else if (testType.equals(Constants.UrineTestItems.Glucose.toString())) {
                        dayValuesArray.add(objUrineTest.getGlucose());
                    } else if (testType.equals(Constants.UrineTestItems.Ascorbic.toString())) {
                        dayValuesArray.add(objUrineTest.getAscorbinAcid());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            hideAllIndicationViews();

            setBarGroupChartData(dayValuesArray);
            setBarGroupChartData(dayValuesArray);

        } else {
            mChart.clear();
        }

    }

    public void setBarGroupChartData(ArrayList<Double> values) {
        final RangeLimit objRangeLimit = UrineTestDataCreatorController.getInstance().getRangeAndLimitLineValuesForTestItem(testTypeArray.get(selectedTestTypeRecordIndex));
        int seperateCount = UrineTestDataCreatorController.getInstance().getCountForMoreThanZero(objRangeLimit.getcArray());
        mChart.getAxisLeft().setAxisMinimum(0);
        mChart.getAxisLeft().setAxisMaximum(300);
        final double rangeValue = 300.0 / (double) seperateCount;
        UrineTestDataCreatorController.getInstance().minimalValue = (rangeValue / 2) + 5;
        final ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        final ArrayList<Entry> lineEntries = new ArrayList<Entry>();
        ArrayList<Integer> colorArray = new ArrayList<Integer>();
        ArrayList<Double> colorValue = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            Log.e("values", "" + values.toString());
            colorValue.add((double) Color.argb((int) 1.0, (int) 0.15, (int) 0.31, (int) 0.07));
            colorValue.add(0.0);
            double compareValue = values.get(i);
            String testType = testTypeArray.get(0/*TabsGraphActivity.selectedTestTypeRecordIndex*/);
            if (testType.equals(Constants.UrineTestItems.LEUKOCYTES.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getLeukocyteColor(compareValue, rangeValue);
            } else if (testType.equals(Constants.UrineTestItems.Nitrite.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getNitriteColor(compareValue, rangeValue);
            } else if (testType.equals(Constants.UrineTestItems.Urobilinogen.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getUrobilinozenColor(compareValue, rangeValue);
            } else if (testType.equals(Constants.UrineTestItems.Protein.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getProtineColor(compareValue, rangeValue);
            } else if (testType.equals(Constants.UrineTestItems.PH.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getPhColor(compareValue, rangeValue);
            } else if (testType.equals(Constants.UrineTestItems.OccultBlood.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getOccultBloodColor(compareValue, rangeValue);
                Log.e("colorar", "call" + colorValue.size());

            } else if (testType.equals(Constants.UrineTestItems.SpecificGravity.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getSgColor(compareValue, rangeValue);
                Log.e("colorarvvvv", "call" + colorValue.size());

            } else if (testType.equals(Constants.UrineTestItems.Ketone.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getKetonesColor(compareValue, rangeValue);
            } else if (testType.equals(Constants.UrineTestItems.Bilirubin.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getBilirubinColor(compareValue, rangeValue);
            } else if (testType.equals(Constants.UrineTestItems.Glucose.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getGlucoseColor(compareValue, rangeValue);
            }
            colorArray.add(colorValue.get(0).intValue());
            BarEntry dataEntry = new BarEntry((float) i, colorValue.get(1).floatValue());
            barEntries.add(dataEntry);
            Entry objEntry = new Entry((float) i, colorValue.get(1).floatValue());
            lineEntries.add(objEntry);
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(colorArray);
        // Bar Data Creation
        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(12f);
        barData.setDrawValues(true);
        barData.setBarWidth(0.15f);
        barData.setValueTextColor(Color.parseColor("#274e13"));
        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                //rather than diaplaying value show label
                int index = (int) entry.getX();
                String dayText = String.valueOf(dayValuesArray.get(index));
                dayText = String.valueOf(dayValuesArray.get(index).floatValue());
                return dayText;
            }
        });

        // Line Data Creation
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        LineData lineData = new LineData(lineDataSet);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setColor(Color.parseColor("#FFFFFF"));
        lineDataSet.setValueTextColor(Color.parseColor("#FFFFFF"));


        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleRadius(6.0f);
        lineDataSet.setCircleHoleRadius(3.0f);
        lineDataSet.setLineWidth(2.0f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setCircleColor(Color.parseColor("#FFFFFF"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleColorHole(Color.parseColor("#FF8C00"));

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.fade_red);
        lineDataSet.setFillDrawable(drawable);
        ////
        lineData.addDataSet(lineDataSet);
        lineData.setDrawValues(true);


        lineData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                //rather than diaplaying value show label
                int index = (int) entry.getX();
                String dayText = String.valueOf(dayValuesArray.get(index));
                dayText = String.valueOf(dayValuesArray.get(index).floatValue());

                return dayText;
            }
        });
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        leftAxis.removeAllLimitLines();
        ////////////set rage to chart
        mChart.setData(lineData);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum((float) (values.size() - 0.5));
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        mChart.getXAxis().setLabelRotationAngle(-35f);
        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dayAxiesArray));
        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getDescription().setText("");
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.animateXY(500, 500);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        mChart.getLegend().setEnabled(false);
        mChart.setScaleXEnabled(true);
        mChart.setScaleYEnabled(false);
        //safe line
        float lineValue = 0.0f;
        double  normal = Double.parseDouble(objRangeLimit.getcArray().get(0));

        if (normal > 0) {
            lineValue = (float) (lineValue + rangeValue);
            LimitLine safelimitLine = new LimitLine(lineValue, "");
            safelimitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            safelimitLine.setLineWidth(3);
            safelimitLine.setLineColor(Color.parseColor("#274e13"));
            safelimitLine.setTextColor(Color.argb((int) 1.0, (int) 0.15, (int) 0.31, (int) 0.07));
            mChart.getAxisLeft().addLimitLine(safelimitLine);
        }
        double plusOrMinus = Double.parseDouble(objRangeLimit.getcArray().get(1));;

        if (plusOrMinus > 0) {
            lineValue = (float) (lineValue + rangeValue);
            LimitLine limitLine = new LimitLine(lineValue, " ");
            limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            limitLine.setLineColor(Color.YELLOW);
            limitLine.setTextColor(Color.YELLOW);
            limitLine.setLineWidth(3);
            mChart.getAxisLeft().addLimitLine(limitLine);
        }
        double plus = Double.parseDouble(objRangeLimit.getcArray().get(2));
        if (plus > 0) {
            lineValue = (float) (lineValue + rangeValue);
            LimitLine limitplus = new LimitLine(lineValue, " ");
            limitplus.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            limitplus.setLineColor(Color.parseColor("#3D85C7"));
            limitplus.setTextColor(Color.parseColor("#3D85C7"));
            limitplus.setLineWidth(3);
            mChart.getAxisLeft().addLimitLine(limitplus);
        }
        double plusplus = Double.parseDouble(objRangeLimit.getcArray().get(3));
        if (plusplus > 0) {
            lineValue = (float) (lineValue + rangeValue);
            LimitLine limit2plus = new LimitLine(lineValue, " ");
            limit2plus.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            limit2plus.setLineColor(Color.parseColor("#800080"));
            limit2plus.setTextColor(Color.parseColor("#800080"));
            limit2plus.setLineWidth(3);
            mChart.getAxisLeft().addLimitLine(limit2plus);
        }
        final double plusplusplus = Double.parseDouble(objRangeLimit.getcArray().get(4));
        if (plusplusplus > 0) {
            lineValue = (float) (lineValue + rangeValue);
            LimitLine limit3plus = new LimitLine(lineValue, " ");
            limit3plus.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            limit3plus.setLineColor(Color.parseColor("#FFA500"));
            limit3plus.setTextColor(Color.parseColor("#FFA500"));
            limit3plus.setLineWidth(3);
            mChart.getAxisLeft().addLimitLine(limit3plus);
        }

        double fourplus = Double.parseDouble(objRangeLimit.getcArray().get(5));
        if (fourplus > 0) {
            lineValue = (float) (lineValue + rangeValue);
            LimitLine limit4plus = new LimitLine(lineValue, " ");
            limit4plus.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            limit4plus.setLineColor(Color.RED);
            limit4plus.setTextColor(Color.RED);
            limit4plus.setLineWidth(3);
            mChart.getAxisLeft().addLimitLine(limit4plus);
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // this code will be executed after 2 seconds
                hideAllIndicationViews();
                float lineValue = 0.0f;
                double normal = Double.parseDouble(objRangeLimit.getcArray().get(0));
                if (normal > 0) {
                    lineValue = (float) (lineValue + rangeValue);
                    BarEntry limiteLineEntry = new BarEntry((float) 0, lineValue);
                    MPPointF axisPos = mChart.getPosition(limiteLineEntry, mChart.getAxisLeft().getAxisDependency());
                    setLabel(negativeview, axisPos, negativeText, objRangeLimit.getLimitLineTextArray().get(0));
                }
                double plusOrMinus = Double.parseDouble(objRangeLimit.getcArray().get(1));
                if (plusOrMinus > 0) {
                    lineValue = (float) (lineValue + rangeValue);
                    BarEntry limiteLineEntry = new BarEntry((float) 0, lineValue);
                    MPPointF axisPos = mChart.getPosition(limiteLineEntry, mChart.getAxisLeft().getAxisDependency());
                    //set layout  params
                    setLabel(plusMinusView, axisPos, plusMinusText, objRangeLimit.getLimitLineTextArray().get(1));
                }
                double plus = Double.parseDouble(objRangeLimit.getcArray().get(2));
                if (plus > 0) {
                    lineValue = (float) (lineValue + rangeValue);
                    BarEntry limiteLineEntry = new BarEntry((float) 0, lineValue);
                    MPPointF axisPos = mChart.getPosition(limiteLineEntry, mChart.getAxisLeft().getAxisDependency());
                    setLabel(plusView, axisPos, plusText, objRangeLimit.getLimitLineTextArray().get(2));
                }
                double plusplus = Double.parseDouble(objRangeLimit.getcArray().get(3));
                if (plusplus > 0) {
                    lineValue = (float) (lineValue + rangeValue);
                    BarEntry limiteLineEntry = new BarEntry((float) 0, lineValue);
                    MPPointF axisPos = mChart.getPosition(limiteLineEntry, mChart.getAxisLeft().getAxisDependency());
                    setLabel(twoPlusView, axisPos, twoPlusText, objRangeLimit.getLimitLineTextArray().get(3));
                }
                double plusplusplus = Double.parseDouble(objRangeLimit.getcArray().get(4));
                if (plusplusplus > 0) {
                    lineValue = (float) (lineValue + rangeValue);
                    BarEntry limiteLineEntry = new BarEntry((float) 0, lineValue);
                    MPPointF axisPos = mChart.getPosition(limiteLineEntry, mChart.getAxisLeft().getAxisDependency());
                    setLabel(threePlusView, axisPos, threePlusText, objRangeLimit.getLimitLineTextArray().get(4));
                }
                double fourplus = Double.parseDouble(objRangeLimit.getcArray().get(5));
                if (fourplus > 0) {
                    lineValue = (float) (lineValue + rangeValue);
                    BarEntry limiteLineEntry = new BarEntry((float) 0, lineValue);
                    MPPointF axisPos = mChart.getPosition(limiteLineEntry, mChart.getAxisLeft().getAxisDependency());
                    //loadCircleColors();
                    setLabel(fourplusView, axisPos, fourplusText, objRangeLimit.getLimitLineTextArray().get(5));

                }
                Log.e("viwetexts", "call" + objRangeLimit.getLimitLineTextArray().get(0) + objRangeLimit.getLimitLineTextArray().get(1) + objRangeLimit.getLimitLineTextArray().get(2) +
                        objRangeLimit.getLimitLineTextArray().get(3) + objRangeLimit.getLimitLineTextArray().get(4) + objRangeLimit.getLimitLineTextArray().get(5));

            }
        }, 1000);
        loadListData();

    }

    private void setLabel(RelativeLayout relativeLayout, MPPointF axisPos, TextView textView, String text) {
        relativeLayout.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParams.setMargins((int) relativeLayout.getX(), (int) axisPos.getY() - 20, relativeLayout.getWidth(), relativeLayout.getHeight());
        relativeLayout.setLayoutParams(relativeParams);
        textView.setText(text);

    }

    public void loadTableDataForSelectedObject(UrineresultsModel urineObject) {
        ArrayList<TestFactors> testFactorsArrayList = TestFactorDataController.getInstance().fetchTestFactorresults(urineObject);
        UrineTestObject objUrineResultObject = UrineTestDataCreatorController.getInstance().getUrineTestDataAvgTestFactors(testFactorsArrayList);
        Log.e("sddsdsddsds","call"+objUrineResultObject.getNitriteFlag());
        selectedUrineTestRecord = objUrineResultObject;
        isDataAvailable = UrineTestDataCreatorController.getInstance().getIsDataAvailable(objUrineResultObject);
        long yourmilliseconds = Long.parseLong(urineObject.getTestedTime());
        Date resultdate = new Date(yourmilliseconds * 1000);
        txt_currentDate.setText(weekFormatter.format(resultdate));
       // txt_unit.setText(getUnitValueForTestName(testType_txt.getText().toString()));

        resultsTableViewCell.notifyDataSetChanged();
    }

    public void loadFirstRecordData() {
        if (resultsDayArray.size() > 0) {
            selectedIndex = resultsDayArray.size() - 1;
            loadTableDataForSelectedObject(resultsDayArray.get(selectedIndex));
        } else {
            isDataAvailable = false;
            resultsTableViewCell.notifyDataSetChanged();
        }

        if (isDataAvailable) {
            Log.e("isData", "call");
            resultRecyclerView.setVisibility(View.VISIBLE);
            resultsTableViewCell.notifyDataSetChanged();
        } else {
            Log.e("isDataAvailableelse", "call");
            resultsTableViewCell.notifyDataSetChanged();
        }

    }

    public void loadListData() {
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                selectedIndex = index;
                UrineresultsModel objSelectRecord = resultsDayArray.get(selectedIndex);
                loadTableDataForSelectedObject(objSelectRecord);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public void hideAllIndicationViews() {

        negativeview.setVisibility(View.INVISIBLE);
        plusMinusView.setVisibility(View.INVISIBLE);
        plusView.setVisibility(View.INVISIBLE);
        twoPlusView.setVisibility(View.INVISIBLE);
        threePlusView.setVisibility(View.INVISIBLE);
        fourplusView.setVisibility(View.INVISIBLE);
    }

    public class ResultsTableViewCell extends RecyclerView.Adapter<ResultsTableViewCell.ViewHolder> {
        ArrayList<String> arrayList = new ArrayList<>();
        Context ctx;

        public ResultsTableViewCell(ArrayList<String> arrayList, Context ctx) {
            this.ctx = ctx;
            this.arrayList = arrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_list_adapter, parent, false);
            ButterKnife.bind(itemView);
            ViewHolder contactViewHolder = new ViewHolder(itemView, ctx, arrayList);

            return contactViewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            String testType = testTypeArray.get(position);

            holder.unitsName.setVisibility(View.VISIBLE);
            if (testType.equals(Constants.UrineTestItems.LEUKOCYTES.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getLeokocit()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getLeokocitResult()));
               // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getLeokocitResult()));
               // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getLeokocitResult()));
                holder.txt_discription.setText(LanguagesKeys.LECHOCYTE_DISCRIPTION_KEY);
                holder.unitsName.setText(selectedUrineTestRecord.getLeokocitUnit());

                holder.testName.setText(LanguagesKeys.LEUKOCYTE_KEY);
               if(selectedUrineTestRecord.getLeokocitFlag() != null) {
                   setTestConditionForLable(holder, selectedUrineTestRecord.getLeokocitFlag());
               }
                setTestConditionForLable(holder, false);
            } else if (testType.equals(Constants.UrineTestItems.Nitrite.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getNitrite()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getNitriteResult()));
               // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getNitriteResult()));
               // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getNitriteResult()));
                holder.txt_discription.setText(LanguagesKeys.NITRATE_DISCRIPTION_KEY);
                holder.unitsName.setText(selectedUrineTestRecord.getNitriteUnit());
                setTestConditionForLable(holder, selectedUrineTestRecord.getNitriteFlag());

                holder.testName.setText(LanguagesKeys.NITRATE_KEY);

            } else if (testType.equals(Constants.UrineTestItems.Urobilinogen.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getUrobiliogen()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getUrobiliogenResult()));

               // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getUrobiliogenResult()));
               // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getUrobiliogenResult()));
                holder.txt_discription.setText(LanguagesKeys.UROBILLINGEN_DESCRIPTION_RESULT_KEY);
                holder.unitsName.setText(selectedUrineTestRecord.getUrobiliogenUnit());
                holder.testName.setText(LanguagesKeys.UROBILIOGEN_KEY);

                setTestConditionForLable(holder, selectedUrineTestRecord.getUrobiliogenFlag());
            } else if (testType.equals(Constants.UrineTestItems.Protein.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getProtein()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getProteinResult()));

               // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getProteinResult()));
               // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getProteinResult()));
                holder.txt_discription.setText(LanguagesKeys.PROTINE_DESCRIPTION_KEY);
                holder.unitsName.setText(selectedUrineTestRecord.getProteinUnit());
                holder.testName.setText(LanguagesKeys.PROTEIN_KEY);

                setTestConditionForLable(holder, selectedUrineTestRecord.getProteinFlag());
            } else if (testType.equals(Constants.UrineTestItems.PH.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getPh()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getPhResult()));

               // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getPhResult()));
               // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getPhResult()));
                holder.txt_discription.setText(LanguagesKeys.PH_DISCRIPTION_KEY);
                holder.unitsName.setText(selectedUrineTestRecord.getPhUnit());
                holder.testName.setText(LanguagesKeys.PH_KEY);

                setTestConditionForLable(holder, selectedUrineTestRecord.getPhFlag());
            } else if (testType.equals(Constants.UrineTestItems.OccultBlood.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getOccultBlood()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getOccultBloodResult()));

               // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getOccultBloodResult()));
               // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getOccultBloodResult()));
                holder.txt_discription.setText(LanguagesKeys.OCCULT_BLOOD_DESCRIPTION_RESULT_KEY);
                holder.unitsName.setText(selectedUrineTestRecord.getOccultBloodUnit());
                holder.testName.setText(LanguagesKeys.OCCULT_BLOOD_RBC_KEY);

                setTestConditionForLable(holder, selectedUrineTestRecord.getOccultBloodFlag());
            } else if (testType.equals(Constants.UrineTestItems.SpecificGravity.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getSg()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getSgResult()));

               // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getSgResult()));
               // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getSgResult()));
                holder.txt_discription.setText(LanguagesKeys.SG_DESCRIPTION_KEY);
                holder.unitsName.setText(selectedUrineTestRecord.getSgUnit());
                holder.testName.setText(LanguagesKeys.SG_KEY);

                setTestConditionForLable(holder, selectedUrineTestRecord.getSgFlag());
            } else if (testType.equals(Constants.UrineTestItems.Ketone.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getKetones()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getKetonesResult()));

               // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getKetonesResult()));
               // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getKetonesResult()));
                holder.txt_discription.setText(LanguagesKeys.KETONS_DESCRIPTION_KEY);
                holder.unitsName.setText(selectedUrineTestRecord.getKetonesUnit());
                holder.testName.setText(LanguagesKeys.KETONES_KEY);

                setTestConditionForLable(holder, selectedUrineTestRecord.getKetonesFlag());
            } else if (testType.equals(Constants.UrineTestItems.Bilirubin.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getBillirubinValue()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getBillirubinResult()));

               // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getBillirubinResult()));
               // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getBillirubinResult()));
                holder.txt_discription.setText(LanguagesKeys.BILLIBURIN_DESCRIPTION_RESULT_KEY);
                holder.unitsName.setText(selectedUrineTestRecord.getBillirubinUnit());
                holder.testName.setText(LanguagesKeys.BILIRUBIN_VALUE_KEY);

                setTestConditionForLable(holder, selectedUrineTestRecord.getBillirubinFlag());
            } else if (testType.equals(Constants.UrineTestItems.Glucose.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getGlucose()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getGlucoseResult()));
               // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getGlucoseResult()));
               // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getGlucoseResult()));
                holder.txt_discription.setText(LanguagesKeys.GLOUCOSE_DESCRIPTION_KEY);
                holder.unitsName.setText(selectedUrineTestRecord.getGlucoseUnit());
                holder.testName.setText(LanguagesKeys.GLUCOSE_KEY);
                 setTestConditionForLable(holder, selectedUrineTestRecord.getGlucoseFlag());
            }else if (testType.equals(Constants.UrineTestItems.Ascorbic.toString())) {
                holder.testValue.setText(String.valueOf(selectedUrineTestRecord.getAscorbinAcid()));
                holder.txt_result.setText(String.valueOf(selectedUrineTestRecord.getAscorbinAcidResult()));
                // holder.txt_bg.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getGlucoseResult()));
                // holder.txt_bg1.setBackgroundColor(changeLableColor(selectedUrineTestRecord.getGlucoseResult()));
                holder.txt_discription.setText("");
                holder.unitsName.setText(selectedUrineTestRecord.getAscorbinAcidUnit());
                holder.testName.setText("Ascorbic Acid");
                setTestConditionForLable(holder, selectedUrineTestRecord.getAscorbinAcidFlag());
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedTestTypeRecordIndex != position) {
                        selectedTestTypeRecordIndex = position;
                        // txt_testname.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex)));
                        // testType_txt.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex)));
                        loadDayData(testTypeArray.get(selectedTestTypeRecordIndex), dateForLand);
                        //    loadFirstRecordData();
                        // txt_unit.setText(getUnitValueForTestName(testType_txt.getText().toString()));
                        notifyDataSetChanged();
                    } else {
                        selectedTestTypeRecordIndex = -1;
                        notifyDataSetChanged();
                    }
                    /*if (togglebutton.isChecked() == true) {
                        RelativeLayout layout = (RelativeLayout) findViewById(R.id.linear_week);
                        layout.setVisibility(View.VISIBLE);
                        mChart.setVisibility(View.VISIBLE);
                        togglebutton.setChecked(false);
                        arrow.setVisibility(View.VISIBLE);
                        testType_txt.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(testTypeArray.get(position)));
                        txt_testname.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(testTypeArray.get(position)));
                        TabsGraphActivity.isDayToggleChecked = false;
                        Log.e("togglelistelse", "call" + togglebutton.isChecked() + "" + TabsGraphActivity.isDayToggleChecked);
                        txt_unit.setText(getUnitValueForTestName(testType_txt.getText().toString()));

                    }*/
                }

            });

            if (selectedTestTypeRecordIndex == position) {
                holder.testName.setTextColor(Color.parseColor("#1891B0"));
            } else {
                holder.testName.setTextColor(Color.parseColor("#000000"));

            }

            if (selectedPosition == position) {
                holder.rlScrollMeg.setVisibility(View.VISIBLE);
            } else {
                holder.rlScrollMeg.setVisibility(GONE);
            }
            holder.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedPosition != position) {
                        selectedPosition = position;
                        resultsTableViewCell.notifyDataSetChanged();
                    } else {
                        selectedPosition = -1;
                        resultsTableViewCell.notifyDataSetChanged();
                    }
                }


            });
        }

        // step 4:-
        @Override
        public int getItemCount() {
            if (isDataAvailable) {
                resultRecyclerView.setVisibility(View.VISIBLE);
                Log.e("testTypeArray", "" + testTypeArray.size());
                return testTypeArray.size();
            } else {
                // rl_nodata.setVisibility(View.VISIBLE);
                return 0;
            }
        }

        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView testName, testValue, testCondition, txt_discription, unitsName, txt_result;
            ArrayList<String> arrayList = new ArrayList<String>();
            Context ctx;
            ImageView imageView;
            Button down;
            TextView txt_bg, txt_bg1;
            RelativeLayout rlScrollMeg;

            public ViewHolder(View itemView, Context ctx, final ArrayList<String> arrayList) {
                super(itemView);
                this.arrayList = arrayList;
                this.ctx = ctx;
                testName = (TextView) itemView.findViewById(R.id.testName);
                unitsName = (TextView) itemView.findViewById(R.id.unitsName);
                testValue = (TextView) itemView.findViewById(R.id.testVal);
                txt_result = (TextView) itemView.findViewById(R.id.testrsult);
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

    public int changeLableColor(String condition) {
        int color = 0;
        String[] val = condition.split("\n");
        color = UrineTestDataCreatorController.getInstance().getTestColorForLable(val[0]);
        return color;

    }


    public void setTestConditionForLable(ResultsTableViewCell.ViewHolder holder, boolean isSafe) {
        if (isSafe) {
            holder.imageView.setImageResource(R.drawable.happiness);
            holder.testCondition.setText(LanguagesKeys.NORMAL_VALUE_KEY);
            holder.testCondition.setTextColor(Color.parseColor("#03DAC5"));
        } else {
            holder.imageView.setImageResource(R.drawable.sad);
            holder.testCondition.setText(LanguagesKeys.ABNORMAL_VALUE_KEY);
            holder.testCondition.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    private String getUnitValueForTestName(String testName) {
        switch (testName) {
            case "LEUKOCYTES":
                return selectedUrineTestRecord.getLeokocitUnit();
            case "Nitrite":
                return selectedUrineTestRecord.getNitriteUnit();
            case "Urobilinogen":
                return selectedUrineTestRecord.getUrobiliogenUnit();
            case "Protein":
                return selectedUrineTestRecord.getProteinUnit();
            case "PH":
                return selectedUrineTestRecord.getPhUnit();
            case "Occult Blood":
                return selectedUrineTestRecord.getOccultBloodUnit();
            case "Specific Gravity":
                return selectedUrineTestRecord.getSgUnit();
            case "Ketone":
                return selectedUrineTestRecord.getKetonesUnit();
            case "Bilirubin":
                return selectedUrineTestRecord.getBillirubinUnit();
            case "Glucose":
                return selectedUrineTestRecord.getGlucoseUnit();
            default:
                return selectedUrineTestRecord.getLeokocitUnit();
        }

    }

    private void loadCircleColors() {
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.RED);
    }

}

