package com.gtsr.gtsr.dataController;

import android.content.Context;
import android.util.Log;
import java.text.ParseException;

import com.gtsr.gtsr.Constants;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.gtsr.gtsr.model.QubeResultModel;
import com.gtsr.gtsr.model.RangeLimit;
import com.gtsr.gtsr.model.UrineTestObject;
import com.spectrochips.spectrumsdk.MODELS.LimetLineRanges;
import com.spectrochips.spectrumsdk.MODELS.RCTableData;
import com.spectrochips.spectrumsdk.MODELS.SpectroDeviceDataController;

import java.util.ArrayList;

public class QubeController {
    public static QubeController myObj;
    public Context context;
    public ArrayList<QubeResultModel> qubeResultList;
    public static QubeController getInstance() {
        if (myObj == null) {
            myObj = new QubeController();
        }
        return myObj;
    }
    public void fillCOntext(Context context1){
        context=context1;
        qubeResultList=new ArrayList<>();
        loadDummyResultData();
    }

    private void loadDummyResultData(){
        qubeResultList.clear();
        QubeResultModel qubeOne=new QubeResultModel();
        qubeOne.setTestResult("Positive");
        qubeOne.setIgGLevel("High");
        qubeOne.setReferenceRange(">11");
        qubeOne.setResultMessage("Your result is positive, indicating you have developed IgG antibodies against SARS-CoV-2. Please re-test urinary every month to track your immunity (IgG)");

        QubeResultModel qubeTwo=new QubeResultModel();
        qubeTwo.setTestResult("Negative");
        qubeTwo.setIgGLevel("Borderline");
        qubeTwo.setReferenceRange("0.9-11");
        qubeTwo.setResultMessage("Your result is negative but the level is between borderline. It is suggested to re-test urinary 1 week later to track your immunity (IgG).");

        QubeResultModel qube3=new QubeResultModel();
        qube3.setTestResult("LowNegative");
        qube3.setIgGLevel("Low");
        qube3.setReferenceRange("<0.9");
        qube3.setResultMessage("Your result is negative, indicating you have not developed IgG antibodies against SARS-CoV-2. Please be careful, respect distance, barrier gestures and wear mask, and re-test urinary 2 weeks later to track your immunity (IgG).");

        qubeResultList.add(qubeOne);
        qubeResultList.add(qubeTwo);
        qubeResultList.add(qube3);

    }
    public ArrayList<UrineresultsModel> getFilterArrayForDateString(String date) throws ParseException {
        ArrayList<UrineresultsModel> filterDateList = new ArrayList<>();
        if (UrineResultsDataController.getInstance().allUrineResults.size() > 0) {
            for (int i = 0; i < UrineResultsDataController.getInstance().allUrineResults.size(); i++) {
                UrineresultsModel urineresultsModel = UrineResultsDataController.getInstance().allUrineResults.get(i);
                if (urineresultsModel.getTestType().contains(Constants.TestNames.qube.toString())) {
                    filterDateList.add(urineresultsModel);
                }
            }
            Log.e("qubetemparray","call"+filterDateList.size());

        }
        return filterDateList;
    }
    public RangeLimit getRangeAndLimitLineValuesForTestItem(String testname) {
        RangeLimit objRangeLimit = new RangeLimit();
        int rangeMax = 3;
        ArrayList<String> cValues = new ArrayList<>();
        ArrayList<String> rValues = new ArrayList<>();
        ArrayList<String> limitLineTextArray = new ArrayList<>();

        for (int i = 0; i < rangeMax; i++) {
            cValues.add("0");
            rValues.add("0");
            limitLineTextArray.add("");
        }
        ArrayList<RCTableData> rcTable = SpectroDeviceDataController.getInstance().spectroDeviceObject.getRCTable();
        Log.e("rcTable", "" + SpectroDeviceDataController.getInstance().spectroDeviceObject.getRCTable());

        for (int i = 0; i < rcTable.size(); i++) {
            RCTableData objRCTable = rcTable.get(i);

            if (objRCTable.getTestItem().equals(testname)) {
                for (int rangeIndex = 0; rangeIndex < objRCTable.getLimetLineRanges().size(); rangeIndex++) {
                    if (rangeIndex < cValues.size()) {
                        LimetLineRanges limitLineRange = objRCTable.getLimetLineRanges().get(rangeIndex);
                        Log.e("setcArray", "" + limitLineRange.getCMaxValue());
                        cValues.set(rangeIndex, getNumberFormattedString(limitLineRange.getCMaxValue(), objRCTable.getNumberFormat()));
                        rValues.set(rangeIndex, getNumberFormattedString(limitLineRange.getrMaxValue(), objRCTable.getNumberFormat()));
                        limitLineTextArray.set(rangeIndex, limitLineRange.getLineSymbol());
                    }
                }
            }
        }
        for(int i=0;i<cValues.size();i++){
            if(cValues.get(i).contains(",".replace(",","."))){
                objRangeLimit.setcArray(cValues);
            }else{
                objRangeLimit.setcArray(cValues);
            }
        }

        objRangeLimit.setrArray(rValues);
        objRangeLimit.setLimitLineTextArray(limitLineTextArray);
        Log.e("setcArray", "" + objRangeLimit.getcArray());

        return objRangeLimit;
    }
    public String getNumberFormattedString(double value, String format) {
        String formattedString = String.valueOf(value);
        switch (format) {
            case "X":
                formattedString = String.format("%.0f", value);
            case "X.X":
                formattedString = String.format("%.1f", value);
            case "X.XX":
                formattedString = String.format("%.2f", value);
            case "X.XXX":
                formattedString = String.format("%.3f", value);

            case "X.XXXX":
                formattedString = String.format("%.4f", value);
            default:
                formattedString = String.valueOf(value);
        }
        if(formattedString.contains(",")){
            formattedString.replace(",",".");
            formattedString = String.format("%g", Double.parseDouble(formattedString));
        }else{
            formattedString = String.valueOf(Double.parseDouble(formattedString));
        }
        return formattedString;
    }
}
