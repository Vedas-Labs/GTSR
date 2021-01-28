package com.gtsr.gtsr.database;

import android.util.Log;
import android.widget.Toast;

import com.gtsr.gtsr.dataController.LanguageTextController;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dell on 03-10-2017.
 */
public class UrineResultsDataController {
    public ArrayList<UrineresultsModel> allUrineResults = new ArrayList<>();
    public static UrineResultsDataController myObj;
    public UrineresultsModel currenturineresultsModel;


    public static UrineResultsDataController getInstance() {
        if (myObj == null) {
            myObj = new UrineResultsDataController();
        }
        return myObj;
    }

    public boolean insertUrineResultsForMember(UrineresultsModel urineresultsModel) {
        try {
            LanguageTextController.getInstance().helper.getUrineresultsDao().create(urineresultsModel);
            fetchAllUrineResults();
            return  true;
        } catch (SQLException e) {
            e.printStackTrace();
           /* String s=urineresultsModel.getLatitude()+urineresultsModel.getLongitude()+urineresultsModel.getTestType()+urineresultsModel.getTestReportNumber()+urineresultsModel.getUserName()+urineresultsModel.getTestedTime()+urineresultsModel.getRelationtype()+ urineresultsModel.getIsFasting()+urineresultsModel.getTest_id();
            Toast.makeText(LanguageTextController.getInstance().context.getApplicationContext(),s, Toast.LENGTH_SHORT).show();
            Toast.makeText(LanguageTextController.getInstance().context.getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
           */ return false;
        }
    }
    public ArrayList<UrineresultsModel> fetchAllUrineResults() {
        allUrineResults = new ArrayList<UrineresultsModel>();
        ArrayList<UrineresultsModel> urineresultsModels = null;
        try {
            urineresultsModels = new ArrayList<UrineresultsModel>(LanguageTextController.getInstance().helper.getUrineresultsDao().queryForAll());
            Log.e("call", "" + urineresultsModels);
            if (urineresultsModels != null) {
                allUrineResults = urineresultsModels;
                if (allUrineResults.size() > 0) {
                    currenturineresultsModel = allUrineResults.get(allUrineResults.size() - 1);
                    allUrineResults = sortUrineResultsBasedOnTime(allUrineResults);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allUrineResults;
    }


    public ArrayList<UrineresultsModel> sortUrineResultsBasedOnTime(ArrayList<UrineresultsModel> urineResults) {
        Collections.sort(urineResults, new Comparator<UrineresultsModel>() {
            @Override
            public int compare(UrineresultsModel s1, UrineresultsModel s2) {
                return s1.getTestedTime().compareTo(s2.getTestedTime());
            }
        });
        return urineResults;

    }

    public boolean deleteurineresultsData(UrineresultsModel urineresultsModel) {
        try {
            LanguageTextController.getInstance().helper.getUrineresultsDao().delete(urineresultsModel);
            Log.e("Delete", "delete all urineresultsModel");
            fetchAllUrineResults();
            Log.e("deleteUrineRecord", "call" + UrineResultsDataController.getInstance().allUrineResults.size());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void updateUrineResults(UrineresultsModel urineresultsModel) {
        try {
            UpdateBuilder<UrineresultsModel, Integer> updateBuilder = LanguageTextController.getInstance().helper.getUrineresultsDao().updateBuilder();
            updateBuilder.updateColumnValue("latitude", urineresultsModel.getLatitude());
            updateBuilder.updateColumnValue("longitude", urineresultsModel.getLongitude());
            updateBuilder.updateColumnValue("testtype", urineresultsModel.getTestType());
            updateBuilder.updateColumnValue("testReportNumber", urineresultsModel.getTestReportNumber());
            updateBuilder.updateColumnValue("userName", urineresultsModel.getUserName());
            updateBuilder.updateColumnValue("testedTime", urineresultsModel.getTestedTime());
            updateBuilder.updateColumnValue("relationType", urineresultsModel.getRelationtype());
            updateBuilder.updateColumnValue("isFasting", urineresultsModel.getIsFasting());
            updateBuilder.updateColumnValue("testid", urineresultsModel.getTest_id());

            updateBuilder.where().eq("testid", urineresultsModel.getTest_id());
            updateBuilder.update();
            Log.e("update", "urine results updated sucessfully"+urineresultsModel.getUserName());
            fetchAllUrineResults();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String convertTimestampTodateInPdf(String stringData) {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("yyyyMMdd hh:mm", Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds * 1000);
        String weekString = weekFormatter.format(resultdate);
        return weekString;
    }
    public String convertTimestampTodate(String stringData)
            throws ParseException {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds * 1000);
        String weekString = weekFormatter.format(resultdate);
        return weekString;
    } public String convertTestTimeTodate(String stringData)
            throws ParseException {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds * 1000);
        String weekString = weekFormatter.format(resultdate);
        return weekString;
    }
    public void deleteUrineResults(List<UrineresultsModel> results_list) {
        try {
            LanguageTextController.getInstance().helper.getUrineresultsDao().delete(results_list);
            Log.e("deleted", "deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
