package com.gtsr.gtsr.dataController;

import android.content.Context;
import android.util.Log;

import com.gtsr.gtsr.model.QubeResultModel;

import java.util.ArrayList;

public class QubeController {
    public static QubeController myObj;
    public Context context;
    ArrayList<QubeResultModel> qubeResultList;
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
        qubeOne.setResultMessage("Your result is positive, indicating you have\n" +
                "developed IgG antibodies against SARS-CoV-2.\n" +
                "Please re-test urinary every month to track your\n" +
                "immunity (IgG)");

        QubeResultModel qubeTwo=new QubeResultModel();
        qubeTwo.setTestResult("Negative");
        qubeTwo.setIgGLevel("Borderline");
        qubeTwo.setReferenceRange("0.9-11");
        qubeTwo.setResultMessage("Your result is negative but the level is between\n" +
                "borderline. It is suggested to re-test urinary 1\n" +
                "week later to track your immunity (IgG).");

        QubeResultModel qube3=new QubeResultModel();
        qube3.setTestResult("Negative");
        qube3.setIgGLevel("Low");
        qube3.setReferenceRange("<0.9");
        qube3.setResultMessage("Your result is negative, indicating you have not\n" +
                "developed IgG antibodies against SARS-CoV-2.\n" +
                "Please be careful, respect distance, barrier\n" +
                "gestures and wear mask, and re-test urinary 2\n" +
                "weeks later to track your immunity (IgG).");

        qubeResultList.add(qubeOne);
        qubeResultList.add(qubeTwo);
        qubeResultList.add(qube3);

    }
}
