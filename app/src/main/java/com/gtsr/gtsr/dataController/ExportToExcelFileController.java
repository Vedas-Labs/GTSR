package com.gtsr.gtsr.dataController;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;


import com.gtsr.gtsr.Constants;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.spectrochips.spectrumsdk.FRAMEWORK.TestFactors;
import com.spectrochips.spectrumsdk.MODELS.IntensityChart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExportToExcelFileController {
    private static ExportToExcelFileController ourInstance;
    public Context context;
    WritableSheet sheet;
    String testName = "";
    String fileName = "";
    String targetFileName;
    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;

    public static ExportToExcelFileController getInstance() {
        if (ourInstance == null) {
            ourInstance = new ExportToExcelFileController();
        }
        return ourInstance;
    }

    public void fillContext(Context context1) {
        context = context1;
    }

    public void creatTotalExcel(boolean isSavefile, ArrayList<TestFactors> testFactors, ArrayList<IntensityChart> intensityArray) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd hh mm");
        String date = simpleDateFormat.format(calendar.getTime());
        Log.e("date", "call" + date);

        if(UrineResultsDataController.getInstance().currenturineresultsModel!=null){
            fileName = UrineResultsDataController.getInstance().currenturineresultsModel.getUserName()+"TestResults" + date + ".xls";
        }else{
            fileName ="TestResults" + date + ".xls";

        }
        testName = "Urine";
        WritableWorkbook workbook;
        File file = null;

        if (isSavefile) {
            File root = Environment.getExternalStorageDirectory();
            //   File dir = new File(root.getAbsolutePath() + "/SpectrumDeviceController/"); //it is my root directory
            //   File favourite = new File(root.getAbsolutePath() + "/SpectrumDeviceController/" + "Gtsr"); // it is my sub folder directory .. it can vary..
            File favourite = new File(root.getAbsolutePath() + "/GtsrExcels/"); // it is my sub folder directory .. it can vary..
            try {
                // dir.mkdirs();
                favourite.mkdirs();
                file = new File(favourite, fileName);
              /*encriptFile = new File(favourite, "sample.xls");
                decriptFile = new File(favourite, "sampleone.xls");*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //file path
            targetFileName = "/sdcard/" + "Spectrum_" + fileName;
            Log.e("targetFileName", "call" + targetFileName);
            //file path
            file = new File(targetFileName);
        }


        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            sheet = workbook.createSheet("urine_" + date, 0);
            sheet.getSettings().setProtected(true);
            sheet.getSettings().setPassword("gtsr");

            WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
            // Define the cell format
            times = new WritableCellFormat(times10pt);
            // Lets automatically wrap the cells
            times.setWrap(true);

            // create create a bold font with unterlines
            WritableFont times10ptBoldUnderline = new WritableFont(
                    WritableFont.TIMES, 10, WritableFont.BOLD, false,
                    UnderlineStyle.SINGLE);
            timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
            // Lets automatically wrap the cells
            timesBoldUnderline.setWrap(true);

            CellView cv = new CellView();
            cv.setFormat(times);
            cv.setFormat(timesBoldUnderline);
            cv.setAutosize(true);

            fillFileNameAndDate(sheet, "Urine");
            fillSampleData(isSavefile, testFactors, intensityArray,file);

            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    /*    CryptoUtils.getInstance().saveFile(file);
        CryptoUtils.getInstance().decodeFile();*/

    }

    private void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    private void fillFileNameAndDate(WritableSheet sheet, String testName) throws RowsExceededException, WriteException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
        String date = simpleDateFormat.format(calendar.getTime());
        Log.e("date", "call" + date);

        String fineNameText = fileName; /*+ " " + testName + " " + date + "\n";*/

        String dateText = "Date " + date + "\n";
       /* sheet.addCell(new Label(0, 0, fineNameText));
        sheet.addCell(new Label(0, 1, dateText));*/

        addCaption(sheet, 0, 0, fineNameText);
        addCaption(sheet, 1, 0, dateText);

    }

    public void fillSampleData(boolean isSavefile, ArrayList<TestFactors> testFactors, ArrayList<IntensityChart> intensityArray,File file) throws RowsExceededException, WriteException {
        Log.e("fillSampleData", "call" + testFactors.toString());

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<Float> darkArray = new ArrayList<>();
        ArrayList<Float> swArray = new ArrayList<>();
        ArrayList<Float> occultBloodArray = new ArrayList<>();
        ArrayList<Float> bilirubinArray = new ArrayList<>();
        ArrayList<Float> urobilinogenArray = new ArrayList<>();
        ArrayList<Float> ketoneArray = new ArrayList<>();
        ArrayList<Float> glucoseArray = new ArrayList<>();
        ArrayList<Float> protineArray = new ArrayList<>();
        ArrayList<Float> nitriteArray = new ArrayList<>();
        ArrayList<Float> leukocyteterArray = new ArrayList<>();
        ArrayList<Float> phArray = new ArrayList<>();
        ArrayList<Float> sgArray = new ArrayList<>();
        ArrayList<Float> ascrobicArray = new ArrayList<>();

        titles.add("Dark Spectrum");
        titles.add("Standard White (Reference)");

        for (int i = 0; i < intensityArray.size(); i++) {
            IntensityChart obj = intensityArray.get(i);
            Log.e("fillSampleData", "call" + obj.getTestName());
            if (obj.getTestName().startsWith("Dark")) {
                darkArray=obj.getyAxisArray();
                /*for(int i1=10;i1<100;i1++){
                    darkArray.add((float) i1);
                }*/
            }

            if (obj.getTestName().startsWith("Standard")) {
               swArray=obj.getyAxisArray();
                /*for(int i2=100;i2<300;i2++){
                    swArray.add((float) i2);
                }*/
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.OccultBlood.toString())) {
                occultBloodArray=obj.getyAxisArray();
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.Bilirubin.toString())) {
                bilirubinArray=obj.getyAxisArray();
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.Urobilinogen.toString())) {
                urobilinogenArray=obj.getyAxisArray();
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.Ketone.toString())) {
                ketoneArray=obj.getyAxisArray();
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.Glucose.toString())) {
                glucoseArray=obj.getyAxisArray();
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.Protein.toString())) {
                protineArray=obj.getyAxisArray();
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.Nitrite.toString())) {
                nitriteArray=obj.getyAxisArray();
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.LEUKOCYTES.toString())) {
                leukocyteterArray=obj.getyAxisArray();
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.PH.toString())) {
                phArray=obj.getyAxisArray();
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.SpecificGravity.toString())) {
                sgArray=obj.getyAxisArray();
            }
            if (obj.getTestName().equals(Constants.UrineTestItems.Ascorbic.toString())) {
                ascrobicArray=obj.getyAxisArray();
            }
        }
        for (int i = 0; i < testFactors.size(); i++) {
            TestFactors obj = testFactors.get(i);
            titles.add(obj.getTestname()+"{ R Value:"+obj.getrValue()+" C Value:"+obj.getcValue()+" CW "+obj.getCriticalWavelength()+"}");
            // titles.add(obj.getTestname()+"{ R Value:"+obj.getrValue()+" C Value:"+obj.getcValue()+"}");
        }

        Log.e("testnames", "call" + titles.toString());

        WritableSheet writableSheet = sheet;

        for (int i = 0; i < titles.size(); i++) {//// for titles.
            Label label = new Label(i, 6, titles.get(i), times);
            writableSheet.addCell(label);
        }

        int row = 7;
        for (int i = 0; i < darkArray.size(); i++) {
            Label label = new Label(0, row++, darkArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < swArray.size(); i++) {
            Label label = new Label(1, row++, swArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < occultBloodArray.size(); i++) {
            Label label = new Label(2, row++, occultBloodArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < bilirubinArray.size(); i++) {
            Label label = new Label(3, row++, bilirubinArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < urobilinogenArray.size(); i++) {
            Label label = new Label(4, row++, urobilinogenArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < ketoneArray.size(); i++) {
            Label label = new Label(5, row++, ketoneArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < glucoseArray.size(); i++) {
            Label label = new Label(6, row++, glucoseArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < protineArray.size(); i++) {
            Label label = new Label(7, row++, protineArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < nitriteArray.size(); i++) {
            Label label = new Label(8, row++, nitriteArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < leukocyteterArray.size(); i++) {
            Label label = new Label(9, row++, leukocyteterArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < phArray.size(); i++) {
            Label label = new Label(10, row++, phArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < sgArray.size(); i++) {
            Label label = new Label(11, row++, sgArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }
        row = 7;
        for (int i = 0; i < ascrobicArray.size(); i++) {
            Label label = new Label(12, row++, ascrobicArray.get(i).toString(), times);
            writableSheet.addCell(label);
        }

        OutputStreamWriter writer =
                null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                writer.getEncoding();
                Log.e("encriptionfile","call"+writer.getEncoding());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (isSavefile) {
            Log.e("export done", "call");
            // EventBus.getDefault().post(new AlertShowingDialog.MessageEvent("isSaving" + "," + "isSaving"));
        } else {
            // EventBus.getDefault().post(new AlertShowingDialog.MessageEvent("totalExceldata" + "," + targetFileName));
        }

    }
}

        /*WritableSheet writableSheet = sheet;

        for (int i = 0; i < occultbloodValues.size(); i++) {//// for titles.
            Label  label = new Label(i, 13, occultbloodValues.get(i), times);
            writableSheet.addCell(label);
        }
        titles.add("SNO");
        titles.add("TestName");
        titles.add("Value");
        titles.add("Result");
        titles.add("Unit");
        titles.add("Flag");
        titles.add("Reference Range");
        titles.add("RValue");


        ArrayList<String> snoArray = new ArrayList<>();
        ArrayList<String> nameArray = new ArrayList<>();
        ArrayList<String> valueArray = new ArrayList<>();
        ArrayList<String> reaultArray = new ArrayList<>();
        ArrayList<String> unitArray = new ArrayList<>();
        ArrayList<String> flagArray = new ArrayList<>();
        ArrayList<String> refeArray = new ArrayList<>();
        ArrayList<String> rValueArray = new ArrayList<>();

        for (int i = 0; i < testFactors.size(); i++) {
        TestFactors obj = testFactors.get(i);

            snoArray.add(String.valueOf(i + 1));
            nameArray.add(obj.getTestname());
            valueArray.add(obj.getValue());
            reaultArray.add(obj.getResult());
            unitArray.add(obj.getUnits());
            if (obj.isFlag()) {
                flagArray.add("Normal");
            } else {
                flagArray.add("Abnormal");
            }
            refeArray.add(obj.getReferenceRange());
            rValueArray.add(obj.getrValue());

        } */
/*
package com.gtsr.gtsr.dataController;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.gtsr.gtsr.database.TestFactors;
import com.gtsr.gtsr.model.FileObject;
import com.spectrochips.spectrumsdk.MODELS.IntensityChart;
import com.spectrochips.spectrumsdk.MODELS.ReflectanceChart;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExportToExcelFileController {
    private static ExportToExcelFileController ourInstance;
    public Context context;
    String excelTxt = "";
    int i = 0;
    WritableSheet sheet;
    String testName = "";
    String fileName = "";
    String concentration = "";
    String stripInformation = "";
    String spectrumType = "";
    String targetFileName;
    public JsonFileInterface jsonFileInterface;

    public static ExportToExcelFileController getInstance() {
        if (ourInstance == null) {
            ourInstance = new ExportToExcelFileController();
        }
        return ourInstance;
    }

    public void fillContext(Context context1) {
        context = context1;
    }

    public void activatenotifications(JsonFileInterface jsonFileInterface1) {
        this.jsonFileInterface = jsonFileInterface1;
        if (jsonFileInterface != null) {
            jsonFileInterface=null;
        }
        jsonFileInterface=jsonFileInterface1;
    }
    public interface JsonFileInterface {
        void onSuccessForFileStoring(FileObject file);

        void onFailureForFileStoring(String bitmapList);
    }
    public FileObject creatTotalExcel(boolean isSavefile, ArrayList<TestFactors> testFactors) {
        FileObject object=new FileObject();

        fileName = "TestResults" + ".xls";
        testName = "Urine";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(calendar.getTime());
        Log.e("date", "call" + date);

        File file = null;

        if (isSavefile){
            File root = android.os.Environment.getExternalStorageDirectory();
         //   File dir = new File(root.getAbsolutePath() + "/SpectrumDeviceController/"); //it is my root directory
          //  File favourite = new File(root.getAbsolutePath() + "/SpectrumDeviceController/" + "Gtsr"); // it is my sub folder directory .. it can vary..
            File favourite = new File(root.getAbsolutePath() + "/GtsrExcels/"); // it is my sub folder directory .. it can vary..
            try
            {
               // dir.mkdirs();
                favourite.mkdirs();
                file = new File(favourite,fileName);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }else {
            //file path
            targetFileName = "/sdcard/" + "Spectrum_" + fileName;
            Log.e("targetFileName", "call" + targetFileName);
            //file path
             file = new File(targetFileName);
        }
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            sheet = workbook.createSheet("urine_" + date, 0);
            sheet.getSettings().setProtected(true);
            workbook.getSheet(0).getSettings().setPassword("gtsr");
          //  sheet.getSettings().setPassword("mary has one cat only");
            fillFileNameAndDate(sheet, "Urine");
            fillSampleData(isSavefile,testFactors);
            workbook.write();
            object.setFileName(fileName);
            object.setFile(file);
            jsonFileInterface.onSuccessForFileStoring(object);
            try {
                workbook.close();
            } catch (WriteException e) {
                jsonFileInterface.onFailureForFileStoring("not storing");
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return object;
    }

    private void fillFileNameAndDate(WritableSheet sheet, String testName) throws RowsExceededException, WriteException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
        String date = simpleDateFormat.format(calendar.getTime());
        Log.e("date", "call" + date);

        String fineNameText = fileName + " " +testName + " "+date + "\n";

        String dateText = "Date " + date + "\n";
        sheet.addCell(new Label(0, 0, fineNameText));
        sheet.addCell(new Label(0, 1, dateText));


    }


    public void fillSampleData(boolean isSavefile,ArrayList<TestFactors> testFactors) throws RowsExceededException, WriteException {
        Log.e("fillSampleData", "call" + testFactors.toString());

        ArrayList<String> titles = new ArrayList<>();
        titles.add("SNO");
        titles.add("TestName");
        titles.add("Value");
        titles.add("Result");
        titles.add("Unit");
        titles.add("Flag");
        titles.add("Reference Range");
        titles.add("RValue");


        ArrayList<String> snoArray = new ArrayList<>();
        ArrayList<String> nameArray = new ArrayList<>();
        ArrayList<String> valueArray = new ArrayList<>();
        ArrayList<String> reaultArray = new ArrayList<>();
        ArrayList<String> unitArray = new ArrayList<>();
        ArrayList<String> flagArray = new ArrayList<>();
        ArrayList<String> refeArray = new ArrayList<>();
        ArrayList<String> rValueArray = new ArrayList<>();

        for(int i=0;i<testFactors.size();i++){
            TestFactors obj=testFactors.get(i);
            if(i==0){
                //snoArray.add("1");
            }else{
            }
            snoArray.add(String.valueOf(i+1));
            nameArray.add(obj.getTestName());
            valueArray.add(obj.getValue());
            reaultArray.add(obj.getResult());
            unitArray.add(obj.getUnit());
            if(obj.getFlag()){
                flagArray.add("Normal");
            }else{
                flagArray.add("Abnormal");
            }
            refeArray.add(obj.getHealthReferenceRanges());
            rValueArray.add("10");

        }
         Log.e("namesayyay","clal"+nameArray.toString());


        WritableSheet writableSheet = sheet;

        Log.e("fillSampleData", "call" + sheet + writableSheet);

        for (int i = 0; i < titles.size(); i++) {//// for titles.
            writableSheet.addCell(new Label(i, 8, titles.get(i)));
        }

       */
/* colums 11
            rowa 8*//*


        int row = 10;
        for (int i = 0; i < snoArray.size(); i++) {
            writableSheet.addCell(new Label(0, row++, snoArray.get(i)));

        }

        int row1 = 10;
        for (int i = 0; i < nameArray.size(); i++) {
            writableSheet.addCell(new Label(1, row1++, nameArray.get(i)));
        }

        row1 = 10;

        for (int i = 0; i < valueArray.size(); i++) {
            writableSheet.addCell(new Label(2, row1++, valueArray.get(i)));

        }

        row1 = 10;

        for (int i = 0; i < reaultArray.size(); i++) {
            writableSheet.addCell(new Label(3, row1++, reaultArray.get(i)));

        }

        row1 = 10;

        for (int i = 0; i < unitArray.size(); i++) {
            writableSheet.addCell(new Label(4, row1++, unitArray.get(i)));

        }

        row1 = 10;

        for (int i = 0; i < flagArray.size(); i++) {
            writableSheet.addCell(new Label(5, row1++, flagArray.get(i)));

        }

        row1 = 10;

        for (int i = 0; i < refeArray.size(); i++) {
            writableSheet.addCell(new Label(6, row1++, refeArray.get(i)));

        }
        row1 = 10;

        for (int i = 0; i < rValueArray.size(); i++) {
            writableSheet.addCell(new Label(7, row1++, rValueArray.get(i)));

        }
        if (isSavefile){
            Log.e("export done","call");
           // EventBus.getDefault().post(new AlertShowingDialog.MessageEvent("isSaving" + "," + "isSaving"));
        }else {
           // EventBus.getDefault().post(new AlertShowingDialog.MessageEvent("totalExceldata" + "," + targetFileName));
        }

    }


}
*/
/*
tl	 	tr
  Home | Tutorials | Articles | Videos | Products | Tools | Search
Interviews | Open Source | Tag Cloud | Follow Us | Bookmark | Contact
BE THE CODER
HomeTutorialsArticlesSearchProductsAuthorsSubmit a TutorialReport a BugInterview FAQSubscribe
 Excel > JExcel API > How to enable Spreadsheet Password Protection


How to enable Spreadsheet Password Protection
Java Excel API is an open source java library to read, write and modify Excel spread sheets. This requires the library jxl-2.6.12.jar to be in classpath. The following example shows how to enable Excel Spread sheet password protection.

File Name  :
com/bethecoder/tutorials/jexcelapi/write/SheetPasswordProtectionTest.java
Author  :  	Sudhakar KV
Email  :  	kvenkatasudhakar@gmail.com

package com.bethecoder.tutorials.jexcelapi.write;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class SheetPasswordProtectionTest {


public static void main(String[] args) throws IOException, WriteException {
    //Creates a writable workbook with the given file name

}

}

*/
