package com.gtsr.gtsr.dataController;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gtsr.gtsr.Constants;
import com.gtsr.gtsr.database.TestFactors;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.gtsr.gtsr.model.RangeLimit;
import com.gtsr.gtsr.model.UrineTestObject;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCTestAnalysis;
import com.spectrochips.spectrumsdk.MODELS.LimetLineRanges;
import com.spectrochips.spectrumsdk.MODELS.RCTableData;
import com.spectrochips.spectrumsdk.MODELS.SpectroDeviceDataController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by WAVE on 11/13/2017.
 */
public class UrineTestDataCreatorController {
    public double minimalValue = 20.0;
    public static UrineTestDataCreatorController myObj;

    ////
    public static UrineTestDataCreatorController getInstance() {
        if (myObj == null) {
            myObj = new UrineTestDataCreatorController();
        }
        return myObj;
    }

    public ArrayList<UrineresultsModel> getFilterArrayForDateString(String date) throws ParseException {
        ArrayList<UrineresultsModel> filterDateList = new ArrayList<>();
        for (int i = 0; i < UrineResultsDataController.getInstance().allUrineResults.size(); i++) {
            UrineresultsModel urineTestModel = UrineResultsDataController.getInstance().allUrineResults.get(i);
            filterDateList.add(urineTestModel);
        }
        Log.e("fitedr", "" + filterDateList.size());

        return filterDateList;
    }
    public boolean getIsDataAvailable(UrineTestObject objRecord) {

        if (objRecord.getOccultBlood() > 0 || objRecord.getBillirubinValue() > 0 || objRecord.getUrobiliogen() > 0 || objRecord.getKetones() > 0 || objRecord.getProtein() > 0 || objRecord.getNitrite() > 0 || objRecord.getGlucose() > 0 || objRecord.getPh() > 0 || objRecord.getSg() > 0 || objRecord.getLeokocit() > 0) {
            return true;
        }
        return false;
    }

    public UrineTestObject getUrineTestDataAvgTestFactors(ArrayList<TestFactors> testobjects) {
        UrineTestObject averageUrineTestObj = new UrineTestObject();
        averageUrineTestObj.setOccultBlood(0.0);
        averageUrineTestObj.setBillirubinValue(0.0);
        averageUrineTestObj.setUrobiliogen(0.0);
        averageUrineTestObj.setKetones(0.0);
        averageUrineTestObj.setProtein(0.0);
        averageUrineTestObj.setNitrite(0.0);
        averageUrineTestObj.setGlucose(0.0);
        averageUrineTestObj.setPh(0.0);
        averageUrineTestObj.setSg(0.0);
        averageUrineTestObj.setLeokocit(0.0);
        averageUrineTestObj.setAscorbinAcid(0.0);

        if (testobjects.size() > 0) {

            for (int i = 0; i < testobjects.size(); i++) {
                TestFactors obj = testobjects.get(i);
                Log.e("TestFactorsaaaaaa", "" + obj.getTestName() + Constants.UrineTestItems.LEUKOCYTES.toString());

                if (obj.getTestName().equals(Constants.UrineTestItems.LEUKOCYTES.toString())) {
                    double val = averageUrineTestObj.getLeokocit() + Double.parseDouble(obj.getValue().replace(",","."));
                    ////String testValue =SCTestAnalysis.getInstance(). getNumberFormatStringforTestNameWithValue(obj.getTestName(), val);
                   // Log.e("testvlaue","call"+testValue+"xxxx"+Double.valueOf(testValue));
                    averageUrineTestObj.setLeokocit(val);
                    averageUrineTestObj.setLeokocitUnit(obj.getUnit());
                    averageUrineTestObj.setLeokocitResult(obj.getResult());
                    averageUrineTestObj.setLeokocitFlag(obj.getFlag());

                } else if (obj.getTestName().equals(Constants.UrineTestItems.Nitrite.toString())) {
                    double val = averageUrineTestObj.getNitrite() +Double.parseDouble(obj.getValue().replace(",","."));
                   // String testValue =SCTestAnalysis.getInstance(). getNumberFormatStringforTestNameWithValue(obj.getTestName(), val);
                   // Log.e("testvlaue1","call"+testValue+"xxxx"+Double.valueOf(testValue));
                    averageUrineTestObj.setNitrite(val);
                    averageUrineTestObj.setNitriteUnit(obj.getUnit());
                    averageUrineTestObj.setNitriteResult(obj.getResult());
                    averageUrineTestObj.setNitriteFlag(obj.getFlag());

                } else if (obj.getTestName().equals(Constants.UrineTestItems.Urobilinogen.toString())) {
                    double val = averageUrineTestObj.getUrobiliogen() + Double.parseDouble(obj.getValue().replace(",","."));
                  //  String testValue =SCTestAnalysis.getInstance(). getNumberFormatStringforTestNameWithValue(obj.getTestName(), val);
                    averageUrineTestObj.setUrobiliogen(val);
                    averageUrineTestObj.setUrobiliogenUnit(obj.getUnit());
                    averageUrineTestObj.setUrobiliogenResult(obj.getResult());
                    averageUrineTestObj.setUrobiliogenFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.Protein.toString())) {
                    double val = averageUrineTestObj.getProtein() + Double.parseDouble(obj.getValue().replace(",","."));
                  //  String testValue =SCTestAnalysis.getInstance(). getNumberFormatStringforTestNameWithValue(obj.getTestName(), val);
                    averageUrineTestObj.setProtein(val);
                    averageUrineTestObj.setProteinUnit(obj.getUnit());
                    averageUrineTestObj.setProteinResult(obj.getResult());
                    averageUrineTestObj.setProteinFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.PH.toString())) {
                    double val = averageUrineTestObj.getPh() + Double.parseDouble(obj.getValue().replace(",","."));
                   // String testValue =SCTestAnalysis.getInstance(). getNumberFormatStringforTestNameWithValue(obj.getTestName(), val);
                    averageUrineTestObj.setPh(val);
                    averageUrineTestObj.setPhUnit(obj.getUnit());
                    averageUrineTestObj.setPhResult(obj.getResult());
                    averageUrineTestObj.setPhFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.OccultBlood.toString())) {
                    double val = averageUrineTestObj.getOccultBlood() +Double.parseDouble(obj.getValue().replace(",","."));
                   // String testValue =SCTestAnalysis.getInstance(). getNumberFormatStringforTestNameWithValue(obj.getTestName(), val);
                    averageUrineTestObj.setOccultBlood(val);
                    averageUrineTestObj.setOccultBloodUnit(obj.getUnit());
                    averageUrineTestObj.setOccultBloodResult(obj.getResult());
                    averageUrineTestObj.setOccultBloodFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.SpecificGravity.toString())) {
                    double val = averageUrineTestObj.getSg() + Double.parseDouble(obj.getValue().replace(",","."));
                 //   String testValue =SCTestAnalysis.getInstance(). getNumberFormatStringforTestNameWithValue(obj.getTestName(), val);
                    averageUrineTestObj.setSg(val);
                    averageUrineTestObj.setSgUnit(obj.getUnit());
                    averageUrineTestObj.setSgResult(obj.getResult());
                    averageUrineTestObj.setSgFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.Ketone.toString())) {
                    double val = averageUrineTestObj.getKetones() + Double.parseDouble(obj.getValue().replace(",","."));
                  //  String testValue =SCTestAnalysis.getInstance(). getNumberFormatStringforTestNameWithValue(obj.getTestName(), val);
                    averageUrineTestObj.setKetones(val);
                    averageUrineTestObj.setKetonesUnit(obj.getUnit());
                    averageUrineTestObj.setKetonesResult(obj.getResult());
                    averageUrineTestObj.setKetonesFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.Bilirubin.toString())) {
                    double val = averageUrineTestObj.getBillirubinValue() + Double.parseDouble(obj.getValue().replace(",","."));
                   // String testValue =SCTestAnalysis.getInstance(). getNumberFormatStringforTestNameWithValue(obj.getTestName(), val);
                    averageUrineTestObj.setBillirubinValue(val);
                    averageUrineTestObj.setBillirubinUnit(obj.getUnit());
                    averageUrineTestObj.setBillirubinResult(obj.getResult());
                    averageUrineTestObj.setBillirubinFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.Glucose.toString())) {
                    double val = averageUrineTestObj.getGlucose() +Double.parseDouble(obj.getValue().replace(",","."));
                   // String testValue =SCTestAnalysis.getInstance(). getNumberFormatStringforTestNameWithValue(obj.getTestName(), val);
                    averageUrineTestObj.setGlucose(val);
                    averageUrineTestObj.setGlucoseUnit(obj.getUnit());
                    averageUrineTestObj.setGlucoseResult(obj.getResult());
                    averageUrineTestObj.setGlucoseFlag(obj.getFlag());
                }else if (obj.getTestName().equals(Constants.UrineTestItems.Ascorbic.toString())) {
                    double val = averageUrineTestObj.getAscorbinAcid() +Double.parseDouble(obj.getValue().replace(",","."));
                    averageUrineTestObj.setAscorbinAcid(val);
                    averageUrineTestObj.setAscorbinAcidUnit(obj.getUnit());
                    averageUrineTestObj.setAscorbinAcidResult(obj.getResult());
                    averageUrineTestObj.setAscorbinAcidFlag(obj.getFlag());
                }
            }
        }
        Log.e("averageUrineTestObj", "" + averageUrineTestObj.getLeokocitResult());
        return averageUrineTestObj;

    }


    public static int getRandomIntValue(int min, int max) {
        Random randomNumber = new Random();
        int lower = min;
        int upper = max;
        int randomnum = randomNumber.nextInt((upper - lower) + lower);
        Log.e("randomnum", "" + randomnum);

        return randomnum;
    }

    public static double getRandomDoubleValue(float firstNum, float secondNum) {
        Random randomNdouble = new Random();
        double f = randomNdouble.nextFloat() * (secondNum - firstNum) + firstNum;
        Log.e("randomdouble", "" + f);
        return f;
    }

    public String convertTimestampToDate(String stringData) throws ParseException {

        long yourmilliseconds = Long.parseLong(stringData);
        Log.e("yourmilliseconds", "" + yourmilliseconds);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("EEEE,dd | hh:mm:ss a", Locale.ENGLISH);
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        SimpleDateFormat weekmonthtimeFormatter = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        //
        Date resultdate = new Date(yourmilliseconds * 1000);

        String weekString = weekFormatter.format(resultdate);
        String monthString = monthFormatter.format(resultdate);
        String combineString = weekmonthtimeFormatter.format(resultdate);
        String timeString = formatter.format(resultdate);
        return weekString + ";" + monthString + ";" + combineString + ";" + timeString;

    }


   /* public UrineTestObject getUrineTestDataAvgObject(ArrayList<HumanUrineResultModel> objects) {
        UrineTestObject averageUrineTestObj = new UrineTestObject();

        averageUrineTestObj.setOccultBlood(0.0);
        averageUrineTestObj.setBillirubinValue(0.0);
        averageUrineTestObj.setUrobiliogen(0.0);
        averageUrineTestObj.setKetones(0.0);
        averageUrineTestObj.setProtein(0.0);
        averageUrineTestObj.setNitrite(0.0);
        averageUrineTestObj.setGlucose(0.0);
        averageUrineTestObj.setPh(0.0);
        averageUrineTestObj.setSg(0.0);
        averageUrineTestObj.setLeokocit(0.0);

        if (objects.size() > 0) {
            //  averageUrineTestObj.setRelationName(objects.get(0).getRelationName());
            // averageUrineTestObj.setRelationType(objects.get(0).getRelationtype());

            for (HumanUrineResultModel model : objects) {
                HumanUrineResultModel objUrineRecord = model;
                ArrayList<TestFactors> testsArray = TestFactorDataController.getInstance().fetchTestFactorresults();

                for (int i = 0; i >= testsArray.size(); i++) {
                    TestFactors obj = testsArray.get(i);

                    if (obj.getTestName().equals(Constants.UrineTestItems.LEUKOCYTES.toString())) {
                        averageUrineTestObj.setLeokocit(Double.valueOf(averageUrineTestObj.getLeokocit() + obj.getValue()));
                        averageUrineTestObj.setLeokocitUnit(obj.getUnit());
                    } else if (obj.getTestName().equals(Constants.UrineTestItems.Nitrite.toString())) {
                        averageUrineTestObj.setNitrite(Double.valueOf(averageUrineTestObj.getNitrite() + obj.getValue()));
                        averageUrineTestObj.setNitriteUnit(obj.getUnit());
                    } else if (obj.getTestName().equals(Constants.UrineTestItems.Urobilinogen.toString())) {
                        averageUrineTestObj.setUrobiliogen(Double.valueOf(averageUrineTestObj.getUrobiliogen() + obj.getValue()));
                        averageUrineTestObj.setProteinUnit(obj.getUnit());
                    } else if (obj.getTestName().equals(Constants.UrineTestItems.Protein.toString())) {
                        averageUrineTestObj.setProtein(Double.valueOf(averageUrineTestObj.getProtein() + obj.getValue()));
                        averageUrineTestObj.setProteinUnit(obj.getUnit());
                    } else if (obj.getTestName().equals(Constants.UrineTestItems.PH.toString())) {
                        averageUrineTestObj.setPh(Double.valueOf(averageUrineTestObj.getPh() + obj.getValue()));
                        averageUrineTestObj.setPhUnit(obj.getUnit());
                    } else if (obj.getTestName().equals(Constants.UrineTestItems.OccultBlood.toString())) {
                        averageUrineTestObj.setOccultBlood(Double.valueOf(averageUrineTestObj.getOccultBlood() + obj.getValue()));
                        averageUrineTestObj.setOccultBloodUnit(obj.getUnit());
                    } else if (obj.getTestName().equals(Constants.UrineTestItems.SpecificGravity.toString())) {
                        averageUrineTestObj.setSg(Double.valueOf(averageUrineTestObj.getSg() + obj.getValue()));
                        averageUrineTestObj.setSgUnit(obj.getUnit());
                    } else if (obj.getTestName().equals(Constants.UrineTestItems.Ketone.toString())) {
                        averageUrineTestObj.setKetones(Double.valueOf(averageUrineTestObj.getKetones() + obj.getValue()));
                        averageUrineTestObj.setKetonesUnit(obj.getUnit());
                    } else if (obj.getTestName().equals(Constants.UrineTestItems.Bilirubin.toString())) {
                        averageUrineTestObj.setBillirubinValue(Double.valueOf(averageUrineTestObj.getBillirubinResult() + obj.getValue()));
                        averageUrineTestObj.setBillirubinUnit(obj.getUnit());
                    } else if (obj.getTestName().equals(Constants.UrineTestItems.Glucose.toString())) {
                        averageUrineTestObj.setGlucose(Double.valueOf(averageUrineTestObj.getGlucose() + obj.getValue()));
                        averageUrineTestObj.setGlucoseUnit(obj.getUnit());
                    }
                }

                averageUrineTestObj.setUrobiliogen(averageUrineTestObj.getUrobiliogen() / new Double(objects.size()));
                averageUrineTestObj.setProtein(averageUrineTestObj.getProtein() / new Double(objects.size()));
                averageUrineTestObj.setGlucose(averageUrineTestObj.getGlucose() / new Double(objects.size()));
                averageUrineTestObj.setOccultBlood(averageUrineTestObj.getOccultBlood() / new Double(objects.size()));
                averageUrineTestObj.setKetones(averageUrineTestObj.getKetones() / new Double(objects.size()));
                averageUrineTestObj.setNitrite(averageUrineTestObj.getNitrite() / new Double(objects.size()));
                averageUrineTestObj.setPh(averageUrineTestObj.getPh() / new Double(objects.size()));
                averageUrineTestObj.setSg(averageUrineTestObj.getSg() / new Double(objects.size()));
                averageUrineTestObj.setLeokocit(averageUrineTestObj.getLeokocit() / new Double(objects.size()));
                averageUrineTestObj.setBillirubinValue(averageUrineTestObj.getBillirubinValue() / new Double(objects.size()));


                averageUrineTestObj.setUrobiliogen(Double.valueOf(Math.round(1000 * averageUrineTestObj.getUrobiliogen()) / 1000));
                averageUrineTestObj.setProtein(Double.valueOf(Math.round(1000 * averageUrineTestObj.getProtein()) / 1000));
                averageUrineTestObj.setGlucose(Double.valueOf(Math.round(1000 * averageUrineTestObj.getGlucose()) / 1000));
                averageUrineTestObj.setOccultBlood(Double.valueOf(Math.round(1000 * averageUrineTestObj.getOccultBlood()) / 1000));
                averageUrineTestObj.setKetones(Double.valueOf(Math.round(1000 * averageUrineTestObj.getKetones()) / 1000));
                averageUrineTestObj.setNitrite(Double.valueOf(Math.round(1000 * averageUrineTestObj.getNitrite()) / 1000));
                averageUrineTestObj.setPh(Double.valueOf(Math.round(1000 * averageUrineTestObj.getPh()) / 1000));
                averageUrineTestObj.setSg(Double.valueOf(Math.round(1000 * averageUrineTestObj.getSg()) / 1000));
                averageUrineTestObj.setLeokocit(Double.valueOf(Math.round(1000 * averageUrineTestObj.getLeokocit()) / 1000));
                averageUrineTestObj.setBillirubinValue(Double.valueOf(Math.round(1000 * averageUrineTestObj.getBillirubinValue()) / 1000));

                int values = Constants.UrineTestItems.values().length;

                for (int i = 0; i < values; i++) {
                    TestFactors objtestname = testsArray.get(i);
                    Boolean flag;
                    String resultText;

                    if (objtestname.getTestName().equals(Constants.UrineTestItems.LEUKOCYTES.toString())) {
                        flag = SCTestAnalysis.getInstance().getFlagForTestItemWithValue(objtestname.getTestName(), averageUrineTestObj.getLeokocit());
                        resultText = SCTestAnalysis.getInstance().getResultTextForTestItemwithValue(objtestname.getTestName(), averageUrineTestObj.getLeokocit());
                        averageUrineTestObj.setLeokocitFlag(flag);
                        averageUrineTestObj.setLeokocitResult(resultText);
                    } else if (objtestname.getTestName().equals(Constants.UrineTestItems.Nitrite.toString())) {
                        flag = SCTestAnalysis.getInstance().getFlagForTestItemWithValue(objtestname.getTestName(), averageUrineTestObj.getLeokocit());
                        resultText = SCTestAnalysis.getInstance().getResultTextForTestItemwithValue(objtestname.getTestName(), averageUrineTestObj.getLeokocit());
                        averageUrineTestObj.setLeokocitFlag(flag);
                        averageUrineTestObj.setLeokocitResult(resultText);
                    } else if (objtestname.getTestName().equals(Constants.UrineTestItems.Urobilinogen.toString())) {
                        flag = SCTestAnalysis.getInstance().getFlagForTestItemWithValue(objtestname.getTestName(), averageUrineTestObj.getUrobiliogen());
                        resultText = SCTestAnalysis.getInstance().getResultTextForTestItemwithValue(objtestname.getTestName(), averageUrineTestObj.getUrobiliogen());
                        averageUrineTestObj.setUrobiliogenFlag(flag);
                        averageUrineTestObj.setUrobiliogenResult(resultText);

                    } else if (objtestname.getTestName().equals(Constants.UrineTestItems.Protein.toString())) {
                        flag = SCTestAnalysis.getInstance().getFlagForTestItemWithValue(objtestname.getTestName(), averageUrineTestObj.getProtein());
                        resultText = SCTestAnalysis.getInstance().getResultTextForTestItemwithValue(objtestname.getTestName(), averageUrineTestObj.getProtein());
                        averageUrineTestObj.setProteinFlag(flag);
                        averageUrineTestObj.setProteinResult(resultText);

                    } else if (objtestname.getTestName().equals(Constants.UrineTestItems.PH.toString())) {
                        flag = SCTestAnalysis.getInstance().getFlagForTestItemWithValue(objtestname.getTestName(), averageUrineTestObj.getPh());
                        resultText = SCTestAnalysis.getInstance().getResultTextForTestItemwithValue(objtestname.getTestName(), averageUrineTestObj.getPh());
                        averageUrineTestObj.setPhFlag(flag);
                        averageUrineTestObj.setPhResult(resultText);

                    } else if (objtestname.getTestName().equals(Constants.UrineTestItems.OccultBlood.toString())) {
                        flag = SCTestAnalysis.getInstance().getFlagForTestItemWithValue(objtestname.getTestName(), averageUrineTestObj.getOccultBlood());
                        resultText = SCTestAnalysis.getInstance().getResultTextForTestItemwithValue(objtestname.getTestName(), averageUrineTestObj.getOccultBlood());
                        averageUrineTestObj.setOccultBloodFlag(flag);
                        averageUrineTestObj.setOccultBloodResult(resultText);

                    } else if (objtestname.getTestName().equals(Constants.UrineTestItems.SpecificGravity.toString())) {
                        flag = SCTestAnalysis.getInstance().getFlagForTestItemWithValue(objtestname.getTestName(), averageUrineTestObj.getSg());
                        resultText = SCTestAnalysis.getInstance().getResultTextForTestItemwithValue(objtestname.getTestName(), averageUrineTestObj.getSg());
                        averageUrineTestObj.setSgFlag(flag);
                        averageUrineTestObj.setSgResult(resultText);

                    } else if (objtestname.getTestName().equals(Constants.UrineTestItems.Ketone.toString())) {
                        flag = SCTestAnalysis.getInstance().getFlagForTestItemWithValue(objtestname.getTestName(), averageUrineTestObj.getKetones());
                        resultText = SCTestAnalysis.getInstance().getResultTextForTestItemwithValue(objtestname.getTestName(), averageUrineTestObj.getKetones());
                        averageUrineTestObj.setKetonesFlag(flag);
                        averageUrineTestObj.setKetonesResult(resultText);

                    } else if (objtestname.getTestName().equals(Constants.UrineTestItems.Bilirubin.toString())) {
                        flag = SCTestAnalysis.getInstance().getFlagForTestItemWithValue(objtestname.getTestName(), averageUrineTestObj.getBillirubinValue());
                        resultText = SCTestAnalysis.getInstance().getResultTextForTestItemwithValue(objtestname.getTestName(), averageUrineTestObj.getBillirubinValue());
                        averageUrineTestObj.setBillirubinFlag(flag);
                        averageUrineTestObj.setBillirubinResult(resultText);

                    } else if (objtestname.getTestName().equals(Constants.UrineTestItems.Glucose.toString())) {
                        flag = SCTestAnalysis.getInstance().getFlagForTestItemWithValue(objtestname.getTestName(), averageUrineTestObj.getBillirubinValue());
                        resultText = SCTestAnalysis.getInstance().getResultTextForTestItemwithValue(objtestname.getTestName(), averageUrineTestObj.getBillirubinValue());
                        averageUrineTestObj.setGlucoseFlag(flag);
                        averageUrineTestObj.setGlucoseResult(resultText);
                    }
                }
            }
        }
        return averageUrineTestObj;
    }


    public UrineTestObject getUrineTestDataAvgTestFactors(ArrayList<TestFactors> testobjects) {
        UrineTestObject averageUrineTestObj = new UrineTestObject();
        averageUrineTestObj.setOccultBlood(0.0);
        averageUrineTestObj.setBillirubinValue(0.0);
        averageUrineTestObj.setUrobiliogen(0.0);
        averageUrineTestObj.setKetones(0.0);
        averageUrineTestObj.setProtein(0.0);
        averageUrineTestObj.setNitrite(0.0);
        averageUrineTestObj.setGlucose(0.0);
        averageUrineTestObj.setPh(0.0);
        averageUrineTestObj.setSg(0.0);
        averageUrineTestObj.setLeokocit(0.0);

        if (testobjects.size() > 0) {

            for (int i = 0; i < testobjects.size(); i++) {
                TestFactors obj = testobjects.get(i);
                Log.e("TestFactors", "" + obj.getValue() + obj.getTestName());

                if (obj.getTestName().equals(Constants.UrineTestItems.LEUKOCYTES.toString())) {
                    double val = averageUrineTestObj.getLeokocit() + Double.parseDouble(obj.getValue());
                    averageUrineTestObj.setLeokocit(val);
                    averageUrineTestObj.setLeokocitUnit(obj.getUnit());
                    averageUrineTestObj.setLeokocitResult(obj.getResult());
                    averageUrineTestObj.setLeokocitFlag(obj.getFlag());
                    Log.e("averageUrineTestObj", "" + averageUrineTestObj.getLeokocit());

                } else if (obj.getTestName().equals(Constants.UrineTestItems.Nitrite.toString())) {
                    double val = averageUrineTestObj.getNitrite() + Double.parseDouble(obj.getValue());
                    averageUrineTestObj.setNitrite(val);
                    averageUrineTestObj.setNitriteUnit(obj.getUnit());
                    averageUrineTestObj.setNitriteResult(obj.getResult());
                    averageUrineTestObj.setNitriteFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.Urobilinogen.toString())) {
                    double val = averageUrineTestObj.getUrobiliogen() + Double.parseDouble(obj.getValue());
                    averageUrineTestObj.setUrobiliogen(val);
                    averageUrineTestObj.setUrobiliogenUnit(obj.getUnit());
                    averageUrineTestObj.setUrobiliogenResult(obj.getResult());
                    averageUrineTestObj.setUrobiliogenFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.Protein.toString())) {
                    double val = averageUrineTestObj.getProtein() + Double.parseDouble(obj.getValue());
                    averageUrineTestObj.setProtein(val);
                    averageUrineTestObj.setProteinUnit(obj.getUnit());
                    averageUrineTestObj.setProteinResult(obj.getResult());
                    averageUrineTestObj.setProteinFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.PH.toString())) {
                    double val = averageUrineTestObj.getPh() + Double.parseDouble(obj.getValue());
                    averageUrineTestObj.setPh(val);
                    averageUrineTestObj.setPhUnit(obj.getUnit());
                    averageUrineTestObj.setPhResult(obj.getResult());
                    averageUrineTestObj.setPhFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.OccultBlood.toString())) {
                    double val = averageUrineTestObj.getOccultBlood() + Double.parseDouble(obj.getValue());
                    averageUrineTestObj.setOccultBlood(val);
                    averageUrineTestObj.setOccultBloodUnit(obj.getUnit());
                    averageUrineTestObj.setOccultBloodResult(obj.getResult());
                    averageUrineTestObj.setOccultBloodFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.SpecificGravity.toString())) {
                    double val = averageUrineTestObj.getSg() + Double.parseDouble(obj.getValue());
                    averageUrineTestObj.setSg(val);
                    averageUrineTestObj.setSgUnit(obj.getUnit());
                    averageUrineTestObj.setSgResult(obj.getResult());
                    averageUrineTestObj.setSgFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.Ketone.toString())) {
                    double val = averageUrineTestObj.getKetones() + Double.parseDouble(obj.getValue());
                    averageUrineTestObj.setKetones(val);
                    averageUrineTestObj.setKetonesUnit(obj.getUnit());
                    averageUrineTestObj.setKetonesResult(obj.getResult());
                    averageUrineTestObj.setKetonesFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.Bilirubin.toString())) {
                    double val = averageUrineTestObj.getBillirubinValue() + Double.parseDouble(obj.getValue());
                    averageUrineTestObj.setBillirubinValue(val);
                    averageUrineTestObj.setBillirubinUnit(obj.getUnit());
                    averageUrineTestObj.setBillirubinResult(obj.getResult());
                    averageUrineTestObj.setBillirubinFlag(obj.getFlag());
                } else if (obj.getTestName().equals(Constants.UrineTestItems.Glucose.toString())) {
                    double val = averageUrineTestObj.getGlucose() + Double.parseDouble(obj.getValue());
                    averageUrineTestObj.setGlucose(val);
                    averageUrineTestObj.setGlucoseUnit(obj.getUnit());
                    averageUrineTestObj.setGlucoseResult(obj.getResult());
                    averageUrineTestObj.setGlucoseFlag(obj.getFlag());
                }
            }
        }
        Log.e("averageUrineTestObj", "" + averageUrineTestObj.getLeokocitResult());
        return averageUrineTestObj;

    }

*/
    public String getRbcText(int value) {

        if (value >= 0 && value <= 7) {
            return "-ve";
        } else if (value >= 8 && value <= 29) {
            return "+";
        } else if (value >= 30 && value <= 140) {
            return "++";
        } else if (value <= 141) {
            return "+++";
        } else {
            return "++++";
        }
    }

    public String getBillirubinText(double value) {
        if (value >= 0 && value <= 0.4) {

            return "-ve";

        } else if (value >= 0.5 && value <= 0.7) {

            return "+";

        } else if (value >= 0.8 && value <= 1.9) {

            return "++";

        }/*else if(value<=2.0){

            return "+++"+"\n"+value;
        }*/ else {
            return "+++";
        }
    }

    public String getUroboliogenText(double value) {
        if (value >= 0.1 && value <= 1.9) {

            return "-ve";

        } else if (value >= 2.0 && value <= 2.9) {

            return "+";

        } else if (value >= 3.0 && value <= 6.5) {

            return "++";

        } else if (value >= 6.6 && value <= 10.5) {

            return "+++";

        }/*else if(value<=10.6){

            return "++++"+"\n"+value;
        }*/ else {

            return "++++";

        }
    }

    public String getKetonesText(int value) {
        if (value >= 0 && value <= 4) {

            return "-ve";
        } else if (value >= 5 && value <= 7) {

            return "+/-";
        } else if (value >= 8 && value <= 29) {

            return "+";
        } else if (value >= 30 && value <= 74) {

            return "++";
        }/*else if(value<=75){

            return "+++"+"\n"+value;
        }*/ else {

            return "+++";
        }
    }

    public String getProteinText(int value) {
        if (value >= 0 && value <= 29) {

            return "-ve";
        } else if (value >= 30 && value <= 60) {

            return "+/-";
        } else if (value >= 61 && value <= 80) {

            return "+";
        } else if (value >= 81 && value <= 200) {

            return "++";
        } else if (value >= 201 && value <= 600) {

            return "+++";
        }/*else if(value<=601){

            return "++++"+"\n"+value;
        }*/ else {
            return "++++";

        }

    }

    public String getNitriteText(double value) {
        if (value >= 0 && value <= 0.04) {

            return "-ve";
        }/*else if(value<=0.05){

            return "+"+"\n"+value;
        }*/ else {
            return "+";

        }

    }

    public String getGlucoseText(int value) {
        if (value >= 0 && value <= 49) {

            return "-ve";
        } else if (value >= 50 && value <= 175) {

            return "+/-";
        } else if (value >= 176 && value <= 375) {

            return "+";
        } else if (value >= 376 && value <= 750) {

            return "++";
        } else if (value >= 751 && value <= 1499) {

            return "+++";
        }/*else if(value<=1500){

            return "++++"+"\n"+value;
        }*/ else {
            return "++++";

        }
    }

    public String getPhText(double value) {
        if (value >= 0 && value <= 4.4) {
            return "-ve";
        } else if (value >= 4.5 && value <= 6.0) {

            return "+/-";
        } else if (value >= 6.1 && value <= 7.5) {

            return "+";
        } else if (value >= 7.6 && value <= 8.5) {

            return "++";
        } else if (value <= 8.6) {

            return "+++";
        } else {
            return "++++";

        }
    }

    public String getSgText(double value) {
        if (value > 0 && value <= 1.002) {

            return "-ve";
        } else if (value >= 1.003 && value <= 1.007) {

            return "+/-";
        } else if (value >= 1.008 && value <= 1.014) {

            return "+";
        } else if (value >= 1.015 && value <= 1.022) {

            return "++";
        } else if (value >= 1.023 && value <= 1.029) {

            return "+++";
        }/*else if(value<=1.030){

            return "++++"+"\n"+value;
        }*/ else {
            return "++++";

        }
    }

    public String getLeukocyteText(int value) {
        if (value >= 0 && value <= 17) {

            return "-ve";
        } else if (value >= 18 && value <= 60) {

            return "+";
        } else if (value >= 61 && value <= 300) {

            return "++";
        } else if (value <= 301) {

            return "+++";
        } else {
            return "++++";

        }
    }


    public int getCountForMoreThanZero(ArrayList<String> valuesArray) {
        int count = 0;
        for (String objValue : valuesArray) {
            double value=0.0;
            if(objValue.contains(",")){
                if(objValue.contains(",".replace(",","."))){
                     value=Double.parseDouble(objValue);
                    Log.e("getCountForMoreThanZero", "" + value);
                }
            }else{
                value=Double.parseDouble(objValue);
            }

            if (value > 0) {
                count = count + 1;
            }
        }
        return count;
    }


    public int getCountValuesForRanges(double green, double yellow, double cyan, double purple, double orange, double red) {

        int seperateValue = 0;

        if (green > 0) {
            seperateValue = seperateValue + 1;
        }
        if (yellow > 0) {
            seperateValue = seperateValue + 1;
        }
        if (cyan > 0) {
            seperateValue = seperateValue + 1;
        }
        if (purple > 0) {
            seperateValue = seperateValue + 1;
        }
        if (orange > 0) {
            seperateValue = seperateValue + 1;
        }
        if (red > 0) {
            seperateValue = seperateValue + 1;
        }

        return seperateValue;

    }

    public ArrayList<Double> getRangeValuesForRBC() {
        ArrayList<Double> rbcList = new ArrayList<>();
        rbcList.add(1d);
        rbcList.add(0d);
        rbcList.add(1d);
        rbcList.add(1d);
        rbcList.add(1d);
        rbcList.add(0d);

        return rbcList;
    }

    public ArrayList<Double> getRangeValuesForBiliruin() {
        ArrayList<Double> biliruinList = new ArrayList<>();
        biliruinList.add(1d);
        biliruinList.add(0d);
        biliruinList.add(1d);
        biliruinList.add(1d);
        biliruinList.add(1d);
        biliruinList.add(1d);

        return biliruinList;
    }

    public ArrayList<Double> getRangeValuesUroBilogen() {
        ArrayList<Double> UroBilogen = new ArrayList<>();
        UroBilogen.add(1d);
        UroBilogen.add(0d);
        UroBilogen.add(1d);
        UroBilogen.add(1d);
        UroBilogen.add(1d);
        UroBilogen.add(1d);

        return UroBilogen;
    }

    public ArrayList<Double> getRangeValuesKetones() {
        ArrayList<Double> Ketones = new ArrayList<>();
        Ketones.add(1d);
        Ketones.add(1d);
        Ketones.add(1d);
        Ketones.add(1d);
        Ketones.add(1d);
        Ketones.add(0d);
        return Ketones;
    }

    public ArrayList<Double> getRangeValuesProtiens() {
        ArrayList<Double> Protiens = new ArrayList<>();
        Protiens.add(1d);
        Protiens.add(1d);
        Protiens.add(1d);
        Protiens.add(1d);
        Protiens.add(1d);
        Protiens.add(1d);

        return Protiens;
    }

    public ArrayList<Double> getRangeValuesNitrate() {
        ArrayList<Double> Nitrate = new ArrayList<>();
        Nitrate.add(1d);
        Nitrate.add(0d);
        Nitrate.add(1d);
        Nitrate.add(0d);
        Nitrate.add(0d);
        Nitrate.add(0d);

        return Nitrate;
    }

    public ArrayList<Double> getRangeValuesGlucose() {
        ArrayList<Double> Glucose = new ArrayList<>();
        Glucose.add(1d);
        Glucose.add(1d);
        Glucose.add(1d);
        Glucose.add(1d);
        Glucose.add(1d);
        Glucose.add(1d);

        return Glucose;
    }

    public ArrayList<Double> getRangeValuespH() {
        ArrayList<Double> pH = new ArrayList<>();
        pH.add(1d);
        pH.add(1d);
        pH.add(1d);
        pH.add(1d);
        pH.add(1d);
        pH.add(0d);
        return pH;
    }

    public ArrayList<Double> getRangeValuesSG() {
        ArrayList<Double> SG = new ArrayList<>();
        SG.add(1d);
        SG.add(1d);
        SG.add(1d);
        SG.add(1d);
        SG.add(1d);
        SG.add(1d);

        return SG;
    }

    public ArrayList<Double> getRangeValuesWBC() {
        //(17,0,60,300,0)
        ArrayList<Double> WBC = new ArrayList<>();
        WBC.add(1d);
        WBC.add(0d);
        WBC.add(1d);
        WBC.add(1d);
        WBC.add(1d);
        WBC.add(0d);
        return WBC;
    }

    public RangeLimit getRangeAndLimitLineValuesForTestItem(String testname) {

        RangeLimit objRangeLimit = new RangeLimit();

        int rangeMax = 6;
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

    /////////////////get colors for tests
    public ArrayList<Double> getOccultBloodColor(Double value, Double seperatorValue) {
        ArrayList<Double> rbccolorList = new ArrayList<>();
        double partValue = (seperatorValue) / 4;
        if (value == 0) {
            rbccolorList.add(0.0);
            rbccolorList.add(0.0);
            return rbccolorList;
        } else if (value > 0 && value <= 0.1875) {
            if (value > 0.12) {
                rbccolorList.add((double) Color.parseColor("#0F1F07"));
                rbccolorList.add(seperatorValue);
                return rbccolorList;
            } else if (value > 0.60) {
                rbccolorList.add((double) Color.parseColor("#0F1F07"));
                rbccolorList.add(seperatorValue - partValue * 2);
                return rbccolorList;
            } else {
                rbccolorList.add((double) Color.parseColor("#0F1F07"));
                rbccolorList.add(seperatorValue - partValue * 3);
                return rbccolorList;
            }
            //  return (UIColor(red: 0.15, green: 0.31, blue: 0.07, alpha: 1.0),seperatorValue-minimalValue)
        } else if (value > 0.1875 && value <= 0.375) {
            if (value > 0.32) {
                rbccolorList.add((double) Color.parseColor("#18344E"));
                rbccolorList.add(seperatorValue * 2);
                return rbccolorList;
            } else if (value > 0.26) {
                rbccolorList.add((double) Color.parseColor("#18344E"));
                rbccolorList.add(seperatorValue * 2 - partValue * 2);
                return rbccolorList;
            } else {
                rbccolorList.add((double) Color.parseColor("#18344E"));
                rbccolorList.add(seperatorValue * 2 - partValue * 3);
                return rbccolorList;
            }
        } else if (value > 0.375 && value <= 0.75) {
            if (value > 0.65) {
                rbccolorList.add((double) Color.parseColor("#18344E"));
                rbccolorList.add(seperatorValue * 3);
                return rbccolorList;
            } else if (value > 0.52) {
                rbccolorList.add((double) Color.parseColor("#18344E"));
                rbccolorList.add(seperatorValue * 3 - partValue * 2);
                return rbccolorList;
            } else {
                rbccolorList.add((double) Color.parseColor("#18344E"));
                rbccolorList.add(seperatorValue * 3 - partValue * 3);
                return rbccolorList;
            }
            //  return (UIColor.purple,seperatorValue*3-minimalValue)
        } else if (value > 0.75 && value <= 1.5) {
            if (value > 1.20) {
                rbccolorList.add((double) Color.parseColor("#800080"));
                rbccolorList.add(seperatorValue * 3);
                return rbccolorList;
            } else if (value > 0.90) {
                rbccolorList.add((double) Color.parseColor("#800080"));
                rbccolorList.add(seperatorValue * 3 - partValue * 2);
                return rbccolorList;
            } else {
                rbccolorList.add((double) Color.parseColor("#800080"));
                rbccolorList.add(seperatorValue * 3 - partValue * 3);
                return rbccolorList;
            }
            //  return (UIColor.purple,seperatorValue*3-minimalValue)
        } else {
            if (value > 2.2) {
                rbccolorList.add((double) Color.parseColor("#FF0000"));
                rbccolorList.add(seperatorValue * 3);
                return rbccolorList;
            } else {
                rbccolorList.add((double) Color.parseColor("#FF0000"));
                rbccolorList.add(seperatorValue * 4 - partValue * 3);
                return rbccolorList;
            }
        }
    }

    ////Bilubrin////
    public static ArrayList<Double> getBilirubinColor(double value, double seperatorValue) {
        ArrayList<Double> bilirubinList = new ArrayList<>();
        double partValue = (seperatorValue) / 4;
        if (value == 0) {
            bilirubinList.add((double) Color.TRANSPARENT);
            bilirubinList.add(0.0);
            return bilirubinList;
        } else if (value > 0 && value <= 1.0) {
            if (value > 0.70) {
                bilirubinList.add((double) Color.parseColor("#0F1F07"));
                bilirubinList.add(seperatorValue);
                return bilirubinList;
            } else if (value > 0.30) {
                bilirubinList.add((double) Color.parseColor("#0F1F07"));
                bilirubinList.add(seperatorValue - partValue * 2);
                return bilirubinList;
            } else {
                bilirubinList.add((double) Color.parseColor("#0F1F07"));
                bilirubinList.add(seperatorValue - partValue * 3);
                return bilirubinList;
            }
        } else if (value >= 1 && value <= 2) {
            if (value > 1.5) {
                bilirubinList.add((double) Color.parseColor("#18344E"));
                bilirubinList.add(seperatorValue * 3);
                return bilirubinList;
            } else {
                bilirubinList.add((double) Color.parseColor("#18344E"));
                bilirubinList.add(seperatorValue * 3 - partValue * 2);
                return bilirubinList;
            }
        } else {
            if (value > 3) {
                bilirubinList.add((double) Color.parseColor("#800080"));
                bilirubinList.add(seperatorValue * 4);
                return bilirubinList;
            } else {
                bilirubinList.add((double) Color.parseColor("#800080"));
                bilirubinList.add(seperatorValue * 4 - partValue * 2);
                return bilirubinList;
            }
        }
    }

    ///Urobilinogen///
    public ArrayList<Double> getUrobilinozenColor(double value, double seperatorValue) {
        ArrayList<Double> urobilinozenList = new ArrayList<>();
        double partValue = seperatorValue / 4;
        if (value == 0) {
            urobilinozenList.add((double) Color.TRANSPARENT);
            urobilinozenList.add(0.0);
            return urobilinozenList;
        } else if (value >= 0 && value <= 1.0) {
            if (value > 0.5) {
                urobilinozenList.add((double) Color.parseColor("#0F1F07"));
                urobilinozenList.add(seperatorValue);
                return urobilinozenList;
            } else {
                urobilinozenList.add((double) Color.parseColor("#0F1F07"));
                urobilinozenList.add(seperatorValue - partValue * 3);
                return urobilinozenList;
            }

        } else if (value >= 1.0 && value <= 2.0) {

            if (value > 1.5) {
                urobilinozenList.add((double) Color.parseColor("#18344E"));
                urobilinozenList.add(seperatorValue * 2);
                return urobilinozenList;
            } else {
                urobilinozenList.add((double) Color.parseColor("#18344E"));
                urobilinozenList.add(seperatorValue * 2 - partValue * 3);
                return urobilinozenList;
            }
        } else if (value >= 2.0 && value <= 4.0) {
            if (value > 3.0) {
                urobilinozenList.add((double) Color.parseColor("#800080"));
                urobilinozenList.add(seperatorValue * 3);
                return urobilinozenList;
            } else {
                urobilinozenList.add((double) Color.parseColor("#800080"));
                urobilinozenList.add(seperatorValue * 3 - partValue * 3);
                return urobilinozenList;
            }
        } else if (value >= 4.0 && value <= 6.0) {
            if (value > 5.5) {
                urobilinozenList.add((double) Color.parseColor("#FFA500"));
                urobilinozenList.add(seperatorValue * 4);
                return urobilinozenList;
            } else {
                urobilinozenList.add((double) Color.parseColor("#FFA500"));
                urobilinozenList.add(seperatorValue * 4 - partValue * 3);
                return urobilinozenList;
            }
        } else {
            if (value > 10.0) {
                urobilinozenList.add((double) Color.RED);
                urobilinozenList.add(seperatorValue * 5);
                return urobilinozenList;
            } else {
                urobilinozenList.add((double) Color.RED);
                urobilinozenList.add(seperatorValue * 5 - partValue * 3);
                return urobilinozenList;
            }
        }
    }

    //// Ketones
    public ArrayList<Double> getKetonesColor(double value, double seperatorValue) {
        ArrayList<Double> ColorList = new ArrayList<>();
        double partValue = (seperatorValue) / 4;

        if (value == 0) {
            ColorList.add((double) Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;
        } else if (value > 0 && value <= 5) {
            if (value > 3) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue);
                return ColorList;
            } else if (value > 1.5) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 2.5);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 3);
                return ColorList;
            }

        } else if (value >= 5 && value <= 15) {
            if (value > 10) {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2);
                return ColorList;
            } else {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2 - partValue * 2.5);
                return ColorList;
            }
        } else if (value >= 15 && value <= 40) {
            if (value > 32) {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3);
                return ColorList;
            } else if (value > 24) {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3 - partValue * 2.5);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3 - partValue * 3);
                return ColorList;
            }
        } else if (value >= 40 && value <= 80) {
            if (value > 60) {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4);
                return ColorList;
            } else if (value > 46) {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4 - partValue * 2.5);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4 - partValue * 3);
                return ColorList;
            }
        } else {
            if (value > 90) {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 5);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 5 - partValue * 2.5);
                return ColorList;
            }
        }
    }

    //////Protine
    public ArrayList<Double> getProtineColor(double value, double seperatorValue) {
        ArrayList<Double> ColorList = new ArrayList<>();
        double partValue = seperatorValue / 4;
        if (value == 0) {
            ColorList.add((double) Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;
        } else if (value > 0 && value <= 15) {
            if (value > 7.5) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 3);
                return ColorList;
            }
        } else if (value >= 15 && value <= 30) {
            if (value > 23) {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2);
                return ColorList;
            } else {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2 - partValue * 2);
                return ColorList;
            }
        } else if (value >= 30 && value <= 100) {
            if (value > 65) {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3 - partValue * 3);
                return ColorList;
            }
        } else if (value >= 100 && value <= 300) {
            if (value > 200) {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4 - partValue * 3);
                return ColorList;
            }
        } else {
            if (value > 700) {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 6);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 6 - partValue * 3);
                return ColorList;
            }
        }
    }

    ////////Nitrite
    public ArrayList<Double> getNitriteColor(double value, double seperatorValue) {
        ArrayList<Double> ColorList = new ArrayList<>();
        double partValue = (seperatorValue) / 4;
        if (value == 0) {
            ColorList.add((double) Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;
        } else if (value > 0 && value <= 50) {
            if (value > 30) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue);
                return ColorList;
            } else if (value > 15) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 2);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 3);
                return ColorList;
            }
        } else {
            if (value > 75) {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 2);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 2 - partValue * 2);
                return ColorList;
            }
        }

    }

    ////////Glucose
    public ArrayList<Double> getGlucoseColor(double value, double seperatorValue) {
        ArrayList<Double> ColorList = new ArrayList<>();
        double partValue = (seperatorValue) / 4;
        if (value == 0) {
            ColorList.add((double) Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;
        } else if (value > 0 && value <= 100) {

            if (value > 50) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 3);
                return ColorList;
            }
        } else if (value >= 100 && value <= 200) {
            if (value > 150) {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2);
                return ColorList;
            } else {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2 - partValue * 3);
                return ColorList;
            }
        } else if (value >= 200 && value <= 500) {
            if (value > 350) {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3 - partValue * 3);
                return ColorList;
            }
        } else if (value >= 500 && value <= 1000) {
            if (value > 536) {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4 - partValue * 3);
                return ColorList;
            }
        } else {
            if (value > 1500) {
                ColorList.add((double) Color.RED);
                ColorList.add(seperatorValue * 5);
                return ColorList;
            } else {
                ColorList.add((double) Color.RED);
                ColorList.add(seperatorValue * 5 - partValue * 3);
                return ColorList;
            }
        }

    }

    ////PH
    public ArrayList<Double> getPhColor(double value, double seperatorValue) {
        ArrayList<Double> ColorList = new ArrayList<>();
        double partValue = (seperatorValue) / 4;
        if (value == 0) {
            ColorList.add((double) Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;

        } else if (value > 0 && value <= 6.0) {
            if (value > 5.0) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue);
                return ColorList;
            } else if (value > 3.0) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 2.5);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 3);
                return ColorList;
            }
        } else if (value >= 6.0 && value <= 6.5) {

            if (value > 6.35) {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2);
                return ColorList;
            } else if (value > 5.0) {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2 - partValue * 2.5);
                return ColorList;
            } else {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2 - partValue * 3);
                return ColorList;
            }
        } else if (value >= 6.5 && value <= 7.0) {
            if (value > 6.85) {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3);
                return ColorList;
            } else if (value > 6.70) {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3 - partValue * 2.5);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3 - partValue * 3);
                return ColorList;
            }
        } else if (value >= 7.0 && value <= 8.0) {
            if (value > 7.7) {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4);
                return ColorList;
            } else if (value > 7.4) {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4 - partValue * 2.5);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4 - partValue * 3);
                return ColorList;
            }

        } else {
            if (value > 9.0) {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 5);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 5 - partValue * 2.5);
                return ColorList;
            }
        }
    }

    ///////SG
    public ArrayList<Double> getSgColor(double value, double seperatorValue) {
        ArrayList<Double> ColorList = new ArrayList<>();
        double partValue = (seperatorValue) / 4;
        if (value == 0) {
            ColorList.add((double) Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;
        } else if (value > 0 && value <= 1.005) {
            if (value > 0.007) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 3);
                return ColorList;
            }
        } else if (value >= 1.005 && value <= 1.010) {
            if (value > 0.007) {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2);
                return ColorList;
            } else {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2 - partValue * 3);
                return ColorList;
            }
        } else if (value >= 1.010 && value <= 1.015) {
            if (value > 1.013) {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 3 - partValue * 3);
                return ColorList;
            }
        } else if (value >= 1.015 && value <= 1.020) {
            if (value > 1.018) {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4 - partValue * 3);
                return ColorList;
            }
        } else if (value >= 1.020 && value <= 1.025) {
            if (value > 1.023) {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 5);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 5 - partValue * 3);
                return ColorList;
            }

        } else {
            if (value > 1.027) {
                ColorList.add((double) Color.RED);
                ColorList.add(seperatorValue * 6);
                return ColorList;
            } else {
                ColorList.add((double) Color.RED);
                ColorList.add(seperatorValue * 6 - partValue * 3);
                return ColorList;

            }
        }
    }

    //////Leukocyte
    public ArrayList<Double> getLeukocyteColor(double value, double seperatorValue) {
        ArrayList<Double> ColorList = new ArrayList<>();
        double partValue = (seperatorValue) / 4;
        if (value == 0) {
            ColorList.add((double) Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;
        } else if (value > 0 && value <= 15) {
            if (value > 10) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue);
                return ColorList;
            } else if (value > 5) {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 2);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#0F1F07"));
                ColorList.add(seperatorValue - partValue * 3);
                return ColorList;

            }
        } else if (value >= 15 && value <= 70) {
            if (value > 50) {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 2);
                return ColorList;
            } else if (value > 30) {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 2 - partValue * 2);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#18344E"));
                ColorList.add(seperatorValue * 2 - partValue * 3);
                return ColorList;
            }
        } else if (value >= 70 && value <= 125) {
            if (value > 110) {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 3);
                return ColorList;
            } else if (value > 90) {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 3 - partValue * 2);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 3 - partValue * 3);
                return ColorList;
            }

        } else {
            if (value > 400) {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 4);
                return ColorList;
            } else {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 4 - partValue * 2);
                return ColorList;
            }
        }
    }


    public int getTestColorForLable(String value) {
        int color = 0;
        if (value.equals("-ve")) {
            color = Color.parseColor("#274E13");

        } else if (value.equals("+/-")) {

            color = Color.parseColor("#FFFF66");

        } else if (value.equals("+")) {

            color = Color.parseColor("#3D85C7");

        } else if (value.equals("++")) {

            color = Color.parseColor("#800080");

        } else if (value.equals("+++")) {

            color = Color.parseColor("#FFA500");

        } else if (value.equals("++++")) {

            color = Color.parseColor("#ff0012");
        }
        return color;
    }

    public String convertTimestampTodate(String stringData) {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds * 1000);
        String weekString = weekFormatter.format(resultdate);
        return weekString;
    }


    public String convertTimestampTodateInPdf(String stringData) {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds * 1000);
        String weekString = weekFormatter.format(resultdate);
        return weekString;
    }

    public static String convertTimestampToMonth(String stringData) {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("yyyy/MM", Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds * 1000);
        String monthString = weekFormatter.format(resultdate);
        return monthString;
    }

    public int getProtineScoreValue(String value) {
        int protineValue = Integer.parseInt(value);
        if (protineValue == 0) {
            return 100;
        } else if (protineValue > 0 && protineValue < 3) {
            return 95;
        } else if (protineValue >= 3 && protineValue <= 10) {
            return 90;
        } else if (protineValue > 10 && protineValue < 20) {
            return 60;
        } else {
            return 50;
        }
    }

    public int getOccultBloodScore(String value) {
        double occultValue = Double.parseDouble(value);
        if (occultValue == 0.0) {
            return 100;
        } else if (occultValue >= 0.01 && occultValue <= 0.03) {
            return 95;
        } else if (occultValue > 0.03 && occultValue <= 0.06) {
            return 60;
        } else {
            return 50;
        }
    }

    public int getNitriteScore(String value) {
        double nitriteValue = Double.parseDouble(value);
        if (nitriteValue == 0.0) {
            return 100;
        } else if (nitriteValue >= 0.01 && nitriteValue <= 0.02) {
            return 95;
        } else if (nitriteValue >= 0.03 && nitriteValue <= 0.04) {
            return 90;
        } else if (nitriteValue > 0.04 && nitriteValue <= 0.10) {
            return 60;
        } else {
            return 55;
        }
    }

    public int getLeukocyteScore(String value) {
        int leukocuteVal = Integer.parseInt(value);
        if (leukocuteVal == 0) {
            return 100;
        } else if (leukocuteVal > 0 && leukocuteVal <= 10) {
            return 95;
        } else if (leukocuteVal >= 11 && leukocuteVal <= 17) {
            return 90;
        } else if (leukocuteVal > 17 && leukocuteVal <= 30) {
            return 60;
        } else {
            return 50;
        }
    }

    public int getSgScore(String value) {
        double sgValue = Double.parseDouble(value);
        if (sgValue > 0.0 && sgValue < 1.005) {
            return 60;
        } else if (sgValue >= 1.005 && sgValue <= 1.025) {
            return 100;
        } else {
            return 60;
        }
    }

    public int getPhScore(String value) {
        double phValue = Double.parseDouble(value);
        if (phValue > 0.0 && phValue < 3.0) {
            return 60;
        } else if (phValue >= 3.0 && phValue < 5.0) {
            return 70;
        } else if (phValue >= 5.0 && phValue <= 7.5) {
            return 100;
        } else if (phValue > 8.0 && phValue <= 10.0) {
            return 70;
        } else {
            return 60;
        }
    }

    public int getGlucoseScore(String value) {
        int glucoseVal = Integer.parseInt(value);
        if (glucoseVal == 0) {
            return 100;
        } else if (glucoseVal > 0 && glucoseVal <= 10) {
            return 95;
        } else if (glucoseVal > 10 && glucoseVal <= 30) {
            return 90;
        } else if (glucoseVal > 30 && glucoseVal <= 40) {
            return 60;
        } else if (glucoseVal > 40 && glucoseVal <= 50) {
            return 55;
        } else {
            return 50;
        }
    }

    public int getKetonScore(String value) {
        int ketonVal = Integer.parseInt(value);
        if (ketonVal == 0) {
            return 100;
        } else if (ketonVal > 0 && ketonVal <= 4) {
            return 95;
        } else if (ketonVal > 4 && ketonVal <= 10) {
            return 60;
        } else {
            return 50;
        }
    }

    public int getBilirubinScore(String value) {
        double biirubinValue = Double.parseDouble(value);
        if (biirubinValue == 0.0) {
            return 100;
        } else if (biirubinValue >= 0.1 && biirubinValue <= 0.5) {
            return 95;
        } else if (biirubinValue > 0.5 && biirubinValue <= 1.0) {
            return 60;
        } else {
            return 50;
        }
    }

    public int getUrobilinogenScore(String value) {
        double UrobilinValue = Double.parseDouble(value);
        if (UrobilinValue == 0.0) {
            return 100;
        } else if (UrobilinValue >= 0.0 && UrobilinValue <= 0.5) {
            return 95;
        } else if (UrobilinValue >= 1.0 && UrobilinValue <= 1.5) {
            return 90;
        } else if (UrobilinValue > 1.5 && UrobilinValue <= 3.0) {
            return 60;
        } else {
            return 55;
        }
    }
}