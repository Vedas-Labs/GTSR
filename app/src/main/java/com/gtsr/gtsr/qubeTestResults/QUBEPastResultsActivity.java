package com.gtsr.gtsr.qubeTestResults;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.gtsr.gtsr.dataController.QubeController;
import com.gtsr.gtsr.dataController.UrineTestDataCreatorController;
import com.gtsr.gtsr.database.TestFactorDataController;
import com.gtsr.gtsr.database.TestFactors;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.gtsr.gtsr.model.QubeResultModel;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class QUBEPastResultsActivity extends AppCompatActivity {
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
    RelativeLayout  plusView, plusMinusView, negativeview;
    TextView  plusText, plusMinusText, negativeText;
    int selectedTestTypeRecordIndex = 0;
    TextView txt_currentDate;

    @BindView(R.id.txt_result)
    TextView txt_result;

    @BindView(R.id.txt_condition)
    TextView txt_condition;

    @BindView(R.id.txt_title)
    TextView txt_title;

    @BindView(R.id.txt_color)
    TextView txt_CircleColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qube_graph_result);
         ButterKnife.bind(this);
        back = findViewById(R.id.backimage);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txt_currentDate = findViewById(R.id.txt_currentDate);

        plusView = (RelativeLayout) findViewById(R.id.l_blue);
        plusMinusView = (RelativeLayout) findViewById(R.id.l_lemonyellow);
        negativeview = (RelativeLayout) findViewById(R.id.green_view);


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
        fillTestTypeArray();
        SpectroCareSDK.getInstance().fillContext(getApplicationContext());
        SpectroDeviceDataController.getInstance().fillContext(getApplicationContext());
        SCTestAnalysis.getInstance().fillContext(getApplicationContext());
        loadLatestRecord();
        loadDayData(testTypeArray.get(0), dateForLand);
        loadFirstRecordData();

    }

    public void fillTestTypeArray() {
        testTypeArray = new ArrayList<String>();
        testTypeArray.add(Constants.TestNames.sars.toString());
    }

    Date dateForLand;
    Calendar calender;
    public ArrayList<UrineresultsModel> tempUrineResults = new ArrayList<>();

    public void loadLatestRecord() {
        UrineResultsDataController.getInstance().fetchAllUrineResults();
        tempUrineResults.clear();
        if (UrineResultsDataController.getInstance().allUrineResults.size() > 0) {
            for (int i = 0; i < UrineResultsDataController.getInstance().allUrineResults.size(); i++) {
                UrineresultsModel urineresultsModel = UrineResultsDataController.getInstance().allUrineResults.get(i);
                if (urineresultsModel.getTestType().contains(Constants.TestNames.qube.toString())) {
                    tempUrineResults.add(urineresultsModel);
                }
            }
            Log.e("qubetemparray", "call" + tempUrineResults.size());
            UrineResultsDataController.getInstance().currenturineresultsModel = tempUrineResults.get(tempUrineResults.size() - 1);
        }
        TestFactorDataController.getInstance().fetchTestFactorresults(UrineResultsDataController.getInstance().currenturineresultsModel);
        for (int i = 0; i < tempUrineResults.size(); i++) {
            if (tempUrineResults.size() > 0) {
                UrineresultsModel selectedObjects1 = tempUrineResults.get(i);
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
            resultsDayArray = QubeController.getInstance().getFilterArrayForDateString(objDateString);
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
                    ArrayList<TestFactors> testFactors = TestFactorDataController.getInstance().fetchTestFactorresults(objUrineRecord);
                   // UrineTestObject objUrineTest = UrineTestDataCreatorController.getInstance().getUrineTestDataAvgTestFactors(testFactors);
                    if (testType.equals(Constants.TestNames.sars.toString())) {
                        dayValuesArray.add(Double.parseDouble(testFactors.get(0).getValue()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.e("dayValuesArray", "" + dayValuesArray.toString());

            hideAllIndicationViews();

            setBarGroupChartData(dayValuesArray);
            setBarGroupChartData(dayValuesArray);

        } else {
            mChart.clear();
        }

    }

    public void setBarGroupChartData(ArrayList<Double> values) {
        final RangeLimit objRangeLimit = QubeController.getInstance().getRangeAndLimitLineValuesForTestItem(testTypeArray.get(selectedTestTypeRecordIndex));
        int seperateCount = UrineTestDataCreatorController.getInstance().getCountForMoreThanZero(objRangeLimit.getcArray());
        mChart.getAxisLeft().setAxisMinimum(0);
        mChart.getAxisLeft().setAxisMaximum(300);
        final double rangeValue = 300.0 / (double) 10;
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
            String testType = testTypeArray.get(0);
            if (testType.equals(Constants.TestNames.sars.toString())) {
                colorValue = UrineTestDataCreatorController.getInstance().getLeukocyteColor(compareValue, rangeValue);
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
        barData.setValueTextColor(Color.parseColor("#000000"));
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
        lineDataSet.setColor(Color.parseColor("#000000"));
        lineDataSet.setValueTextColor(Color.parseColor("#000000"));


        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleRadius(6.0f);
        lineDataSet.setCircleHoleRadius(3.0f);
        lineDataSet.setLineWidth(2.0f);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
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
        xAxis.setTextColor(Color.BLACK);
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
        Log.e("sddsdsddsds", "call" + objUrineResultObject.getNitriteFlag());
        selectedUrineTestRecord = objUrineResultObject;
        isDataAvailable=true;
      //  isDataAvailable = UrineTestDataCreatorController.getInstance().getIsDataAvailable(objUrineResultObject);
        long yourmilliseconds = Long.parseLong(urineObject.getTestedTime());
        Date resultdate = new Date(yourmilliseconds * 1000);
        txt_currentDate.setText(weekFormatter.format(resultdate));
        // txt_unit.setText(getUnitValueForTestName(testType_txt.getText().toString()));

        txt_condition.setText(testFactorsArrayList.get(0).getResult());
        txt_result.setText(testFactorsArrayList.get(0).getValue()+" index");
        txt_title.setText(testFactorsArrayList.get(0).getTestName());

        if(testFactorsArrayList.get(0).getResult().equals("Positive")){
            txt_CircleColor.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
            txt_condition.setTextColor(Color.parseColor("#77BD93"));
        }else{
            txt_CircleColor.setBackgroundTintList(getResources().getColorStateList(R.color.colorOrange));
            txt_condition.setTextColor(Color.parseColor("#EA9834"));
        }
    }

    public void loadFirstRecordData() {
        if (resultsDayArray.size() > 0) {
            selectedIndex = resultsDayArray.size() - 1;
            loadTableDataForSelectedObject(resultsDayArray.get(selectedIndex));
        } else {
           // isDataAvailable = false;
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
        negativeview.setVisibility(View.VISIBLE);
        plusMinusView.setVisibility(View.VISIBLE);
        plusView.setVisibility(View.VISIBLE);

    }
}

