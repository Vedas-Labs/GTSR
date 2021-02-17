package com.gtsr.gtsr.qubeTestResults;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PdfPrint;
import android.print.PrintAttributes;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.gtsr.gtsr.LanguagesKeys;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.dataController.ExportToExcelFileController;
import com.gtsr.gtsr.dataController.LanguageTextController;
import com.gtsr.gtsr.database.TestFactorDataController;
import com.gtsr.gtsr.database.TestFactors;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.database.UrineresultsModel;
import com.spectrochips.spectrumsdk.MODELS.IntensityChart;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Abhilash on 12/4/2018.
 */

public class QUBEHtmltoPdfConversionActivity extends Activity {
    Bitmap bitmap;
    boolean boolean_save;
    WebView webView;
    TextView export, cancel, testResult;
    RelativeLayout ll_pdflayout, rl_texts;
    ProgressDialog progressDialog;
    String htmlContentString;
    String fileName = "";
    int position;
    UrineresultsModel urineresultsModel;
    public static ArrayList<com.spectrochips.spectrumsdk.FRAMEWORK.TestFactors> testResults = new ArrayList<>();
    public static ArrayList<IntensityChart> intensityArray = new ArrayList<>();
    // public static String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htmlconverttopdf);
        Bundle extras = getIntent().getExtras();
        urineresultsModel=UrineResultsDataController.getInstance().currenturineresultsModel;
        /*if (extras != null) {
            String position1 = extras.getString("key");
            position = Integer.parseInt(position1);
            ArrayList<UrineresultsModel> urineresultsList = UrineResultsDataController.getInstance().fetchAllUrineResults();
            urineresultsModel = urineresultsList.get(position);
            TestFactorDataController.getInstance().fetchTestFactorresults(urineresultsModel);
            //The key argument here must match that used in the other activity
        }*/
       /* ExportToExcelFileController.getInstance().fillContext(getApplicationContext());
        ExportToExcelFileController.getInstance().creatTotalExcel(true,testResults,intensityArray);

       */
        init();
        exportpdf();
        updateLanguages();
    }

    private void init() {
        final String AUTHORITY = "com.spectrochip.spectrocare";


        cancel = (TextView) findViewById(R.id.cancel);//Spectrum//testrsult
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        testResult = (TextView) findViewById(R.id.testrsult);//Spectrum//testrsult

        ll_pdflayout = (RelativeLayout) findViewById(R.id.rl);

        rl_texts = (RelativeLayout) findViewById(R.id.rl_texts);

        webView = (WebView) findViewById(R.id.wv);
        export = (TextView) findViewById(R.id.export);

        webView.setWebViewClient(new WebViewClient());
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        // Set a click listener for Button widget
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = UrineResultsDataController.getInstance().convertTimestampTodateInPdf(urineresultsModel.getTestedTime());
                String fileNmae1;
                if(urineresultsModel!=null){//+"UrineTestReport"
                    fileNmae1 =  urineresultsModel.getUserName() + "_" + date + ".pdf";
                }else{
                    fileNmae1 ="UrineTestReport" + "_" + date + ".pdf";
                }
                fileName = fileNmae1;

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    File file = createWebPrintJob(webView);
                    Intent install = new Intent(Intent.ACTION_SEND);
                    if (file.exists()) {
                        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        install.setDataAndType(Uri.fromFile(file), "application/pdf");
                        Uri apkURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
                                .getPackageName() + ".provider", file);
                        install.setDataAndType(apkURI, "application/pdf");
                        install.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file + "/" + fileName));
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(install, "Share File"));
                    }

                } else {
                    File file1 = createWebPrintJob(webView);
                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                    if (file1.exists()) {
                        intentShareFile.setType("application/pdf");
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file1 + "/" + fileName));
                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                        startActivity(Intent.createChooser(intentShareFile, "Share File"));
                    }
                }

            }
        });

    }


    public Bitmap loadBitmapFromView(View v, int width, int height) {

        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    public void exportpdf() {
        Log.e("exportpdf", "exportpdf");
        try {
            Log.e("exportpdf1", "exportpdf1");


            htmlContentString = "<html lang=\"en\">\n" +
                    "    <head>\n" +
                    "        <meta charset=\"UTF-8\">\n" +
                    "            <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                    "            <title>Test Report</title>\n" +
                    "            <style>\n" +
                    "                body{\n" +
                    "                    font-family: Arial;\n" +
                    "                    margin-bottom: 10px;\n" +
                    "                }\n" +
                    "            .float-right{\n" +
                    "                float: right;\n" +
                    "                list-style: none;\n" +
                    "            }\n" +
                    "            .float-right li{\n" +
                    "                padding: 3px;\n" +
                    "                font-size: 14px;\n" +
                    "            }\n" +
                    "            \n" +
                    "            .list-unstyled li{\n" +
                    "                padding: 3px;\n" +
                    "            }\n" +
                    "            .clint-details tr td{\n" +
                    "                font-size: 12px;\n" +
                    "                font-weight: bold;\n" +
                    "                padding: 4px;\n" +
                    "            }\n" +
                    "            \n" +
                    "            ul li span {\n" +
                    "                display: inline-block;\n" +
                    "                width: 60%;\n" +
                    "            }\n" +
                    "            #table{\n" +
                    "                width: 100%;\n" +
                    "                padding-right: 15px;\n" +
                    "                padding-left: 15px;\n" +
                    "                margin-right: auto;\n" +
                    "                margin-left: auto;\n" +
                    "                margin-bottom:auto;\n" +
                    "                border: 1px solid rgba(198,189,255,0.62);\n" +
                    "                border-collapse: collapse;\n" +
                    "            }\n" +
                    "            .font-weight-bold {\n" +
                    "                font-weight: 600 !important;\n" +
                    "            }\n" +
                    "            .table-head{font-weight:600!important;\n" +
                    "                font-size: 12px;}\n" +
                    "            .bg-light{\n" +
                    "                background-color: #B3D8FF;\n" +
                    "            }\n" +
                    "            .bg-light td{\n" +
                    "                padding: 6px;\n" +
                    "            }\n" +
                    "            .table tr td{\n" +
                    "                padding: 10px;\n" +
                    "                font-size: 12px;\n" +
                    "                text-align: center;\n" +
                    "                border-bottom: 1px solid rgba(198,189,255,0.62);\n" +
                    "            }\n" +
                    "            .container {\n" +
                    "                width: 100%;\n" +
                    "                padding-right: 15px;\n" +
                    "                padding-left: 15px;\n" +
                    "                margin-right: auto;\n" +
                    "                margin-left: auto;\n" +
                    "                margin-bottom:auto;\n" +
                    "                \n" +
                    "            }\n" +
                    "            \n" +
                    "                </style>\n" +
                    "            </head>\n" +
                    "    <body>\n" +
                    "        <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" class=\"container\" style=\" font-family:Lato, Helvetica, sans-serif; table-layout:fixed; margin:0 auto;max-width: 95% \">\n" +
                   /* "            <tr>\n" +
                    "                <td align=\"right\">\n" +
                    "                    <ul class=\"float-right\">\n" +
                    "                        <li><span><b>#COMPANY_NAME#</b></span></li>\n" +
                    "                        <li><span><b>#PHONE_TITLE# :</b>#PHONE_NUMBER#</span></li>\n" +
                    "                        #FAX_STRING#\n" +
                    "                        <li><span word-wrap=\"break-word\">#ADDRESS1#</span></li>\n" +
                    "                    </ul>\n" +
                    "                </td>\n" +
                    "            </tr>\n" +*/
                    "            <tr>\n" +
                    "                <td style=\"background-color: #0c5460; text-align: center; font-family: Lato; color: white;padding: 6px;\">\n" +
                    "                    <span style=\"font-size: 20px;\">#URINE_TEST_REPORT#</span>\n" +
                    "                </td>\n" +
                    "            </tr>\n" +
                    "        </table>\n" +
                    "        <br>\n" +
                    "        <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" class=\"container clint-details\" style=\" font-family:Lato, Helvetica, sans-serif; table-layout:fixed; margin:0 auto;max-width: 95% \">\n" +
                    "            <tr>\n" +
                    "                <td style=\"width: 110px\">\n" +
                    "                    #CLIENT_ID_TITLE# :\n" +
                    "                </td>\n" +
                    "                <td>#CLIENT_ID#</td>\n" +
                    "                <td></td>\n" +
                    "                <td style=\"width: 110px\">#GENDER_TITLE# :</td>\n" +
                    "                <td>#GENDER#</td>\n" +
                    "            </tr>\n" +
                    "            <tr>\n" +
                    "                <td>#ClLIENT_NAME_TITLE# :</td>\n" +
                    "                <td>#CLEINT_NAME#</td>\n" +
                    "                \n" +
                    "                <td></td>\n" +
                    "                <td>#AGE_TITLE# :</td>\n" +
                    "                <td>#AGE#</td>\n" +
                    "            </tr>\n" +
                    "            <tr>\n" +
                    "                <td>#Report_Date_TITLE# :</td>\n" +
                    "                <td>#REPORT_DATE#</td>\n" +
                    "                <td></td>\n" +
                    "                <td>#Test_Time_TITLE# :</td>\n" +
                    "                <td>#TEST_TIME#</td>\n" +
                    "            </tr>\n" +
                    "        </table>\n" +
                    "        <br>\n" +
                    "        \n" +
                    "        <table class=\"table\" id=\"table\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"  style=\" font-family:Lato, Helvetica, sans-serif; table-layout:fixed; margin:0 auto; max-width: 91.1%\" >\n" +
                    "            <tbody>\n" +
                    "                <tr class=\"bg-light\">\n" +
                    "                    <td>\n" +
                    "                        <span class=\"table-head\">#SNO_HEADER#</span>\n" +
                    "                    </td>\n" +
                    "                    <td>\n" +
                    "                        <span class=\"table-head\">#TEST_ITEM_HEADER#</span>\n" +
                    "                    </td>\n" +
                    "                    <td>\n" +
                    "                        <span class=\"table-head\">#VALUE_HEADER#</span>\n" +
                    "                    </td>\n" +
                    "                    <td>\n" +
                    "                        <span class=\"table-head\">#RESULT_HEADER#</span>\n" +
                    "                    </td>\n" +
                    "                    <td>\n" +
                    "                        <span class=\"table-head\">#UNIT_HEADER#</span>\n" +
                    "                    </td>\n" +
                  /*  "                    <td>\n" +
                    "                        <span class=\"table-head\">#FLAG_HEADER#</span>\n" +
                    "                    </td>\n" +*/
                    "                    <td>\n" +
                    "                        <span class=\"table-head\">#REFERENCE_RANGE_HEADER#</span>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "                #TEST_FACTORS#\n" +
                    "            </tbody>\n" +
                    "        </table>\n" +
                    "        <br>\n" +
                    "    </body>\n" +
                    "</html>\n";
            prepareHumanPDF();

        } catch (Exception e) {
            Log.e("exception", "preparepdf"+e);
            e.printStackTrace();
        }
    }

    public void prepareHumanPDF() {
        Log.e("preparepdf", "preparepdf");
        // UrineResultsDataController.getInstance().fetchAllUrineResults();
        UrineresultsModel client = UrineResultsDataController.getInstance().currenturineresultsModel;
        String convertDate = null;
        try {
            convertDate = UrineResultsDataController.getInstance().convertTimestampTodate(client.getTestedTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String pathToReportHTMLTemplate = "/sdcard/myTest.html";
        if(urineresultsModel!=null) {
            renderTestReport(pathToReportHTMLTemplate, convertDate, urineresultsModel.getUserName(), "f", "12", client.getTest_id());
        }else{
            renderTestReport(pathToReportHTMLTemplate, convertDate, "zz", "f", "12", client.getTest_id());
        }
    }
    public String renderTestReport(String htmlPath, String date, String patientName, String gender, String age, String clientID) {
        try {

            htmlContentString = htmlContentString.replace("#URINE_TEST_REPORT#", "QUBE Test Report");

            htmlContentString = htmlContentString.replace("#PHONE_TITLE#", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PHONE_KEY));
            htmlContentString = htmlContentString.replace("#Report_Date_TITLE#", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.REPORT_DATE_KEY));
            htmlContentString = htmlContentString.replace("#Test_Time_TITLE#", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.TEST_TIME_KEY));

            htmlContentString = htmlContentString.replace("#COMPANY_NAME#", "");
            htmlContentString = htmlContentString.replace("#PHONE_NUMBER#","");
            String faxString = "<li><span><b>Fax:</b>#FAX_NUMBER#</span></li>";

            /*if (HospitalDataController.getInstance().currentHospital.getFax().isEmpty())
            {
                htmlContentString = htmlContentString.replace("#FAX_STRING#", "");
            } else {*/
            htmlContentString = htmlContentString.replace("#FAX_STRING#", "");
            //   htmlContentString = htmlContentString.replace("#FAX_STRING#", faxString);
            htmlContentString = htmlContentString.replace("Fax", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FAX_KEY));
            htmlContentString = htmlContentString.replace("#FAX_NUMBER#", "");

            //  }
            htmlContentString = htmlContentString.replace("#ADDRESS1#","");
            htmlContentString = htmlContentString.replace("#CLIENT_ID#", clientID);
            htmlContentString = htmlContentString.replace("#CLEINT_NAME#", patientName);
            htmlContentString = htmlContentString.replace("#REPORT_DATE#",date/* UrineResultsDataController.getInstance().convertTimestampTodate(String.valueOf(System.currentTimeMillis() / 1000L))*/);
            htmlContentString = htmlContentString.replace("#GENDER#", "");
            htmlContentString = htmlContentString.replace("#AGE#", age);
            htmlContentString = htmlContentString.replace("#TEST_TIME#", date);
            htmlContentString = htmlContentString.replace("#SNO_HEADER#", "S.NO");
            htmlContentString = htmlContentString.replace("#TEST_ITEM_HEADER#", "TEST ITEM");
            htmlContentString = htmlContentString.replace("#VALUE_HEADER#", "VALUE");
            htmlContentString = htmlContentString.replace("#RESULT_HEADER#", "RESULT");
            htmlContentString = htmlContentString.replace("#UNIT_HEADER#", "UNIT");
            //  htmlContentString = htmlContentString.replace("#FLAG_HEADER#", "FLAG");
            htmlContentString = htmlContentString.replace("#REFERENCE_RANGE_HEADER#", "REFERENCE RANGE");
            // htmlContentString = htmlContentString.replace("#R_VALUES#", "R_VALUES");
            StringBuilder testFactorshtmlString = new StringBuilder();

            int sno = 1;
//<td><span style="color:red">#R_VALUES#</span></td>
            for (TestFactors objTestFactor : TestFactorDataController.getInstance().testfactorlist) {
                String oneItemsHtmlString = "<tr class='table-row'><td><span>#S_NO#</span></td><td><span>#TEST_NAME#</span></td><td><span>#TEST_VALUE#</span></td><td><span>#RANGE_LINE#</span></td><td><span>#TEST_UNIT#</span></td><td><span>#REFERENCE_RANGE#</span></td></tr>";

                oneItemsHtmlString = oneItemsHtmlString.replace("#S_NO#", String.valueOf(sno));

                oneItemsHtmlString = oneItemsHtmlString.replace("#TEST_NAME#", objTestFactor.getTestName());

                oneItemsHtmlString = oneItemsHtmlString.replace("#TEST_VALUE#", objTestFactor.getValue());

                oneItemsHtmlString = oneItemsHtmlString.replace("#RANGE_LINE#", objTestFactor.getResult());

                oneItemsHtmlString = oneItemsHtmlString.replace("#TEST_UNIT#", "index"/*objTestFactor.getUnit()*/);

                   /* if (objTestFactor.getFlag()) {
                        oneItemsHtmlString = oneItemsHtmlString.replace("#IMAGE_LINK#", "<img src=\"happy.png\">");
                    } else {
                        oneItemsHtmlString = oneItemsHtmlString.replace("#IMAGE_LINK#", "<img src=\"sad.png\">");
                    }*/
                oneItemsHtmlString = oneItemsHtmlString.replace("#REFERENCE_RANGE#", objTestFactor.getHealthReferenceRanges());
                //  oneItemsHtmlString = oneItemsHtmlString.replace("#R_VALUES#", objTestFactor.getHealthReferenceRanges());

                testFactorshtmlString.append(oneItemsHtmlString);
                sno = sno + 1;

            }

            htmlContentString = htmlContentString.replace("#TEST_FACTORS#", testFactorshtmlString);
            htmlContentString = htmlContentString.replace("#CLIENT_ID_TITLE#", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CLIENT_ID_KEY));
            htmlContentString = htmlContentString.replace("#AGE_TITLE#", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.AGE_KEY));
            htmlContentString = htmlContentString.replace("#GENDER_TITLE#", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROFILE_GENDER_KEY));
            htmlContentString = htmlContentString.replace("#ClLIENT_NAME_TITLE#", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CLIENT_NAME_KEY));
            htmlContentString = htmlContentString.replace("Report Date", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.REPORT_DATE_KEY));
            htmlContentString = htmlContentString.replace("Test Time", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.TEST_TIME_KEY));
            htmlContentString = htmlContentString.replace("S.NO", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SNO_KEY));
            htmlContentString = htmlContentString.replace("UNIT","Unit");
            htmlContentString = htmlContentString.replace("REFERENCE RANGE", "Reference Range");
            htmlContentString = htmlContentString.replace("Urine Test Report", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.URINE_TEST_REPORT_KEY));
            htmlContentString = htmlContentString.replace("TEST ITEM", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.TEST_ITEM_KEY));
            htmlContentString = htmlContentString.replace("VALUE", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.VALUE_KEY));
            htmlContentString = htmlContentString.replace("RESULT", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.RESULT_KEY));
            //  htmlContentString = htmlContentString.replace("FLAG", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FLAG_KEY));
            htmlContentString = htmlContentString.replace("Phone","");

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setLoadsImagesAutomatically(true);

            webView.getSettings().setUseWideViewPort(true);
            // webView.setWebChromeClient(new WebChromeClient());
            //webView.loadData(htmlContentString, "text/html", "UTF-8");
            //  webView.loadUrl("https://www.facebook.com/");
            webView.loadDataWithBaseURL("file:///android_asset/", htmlContentString, "text/html", "utf-8", "");
            BufferedWriter write = new BufferedWriter(new FileWriter(htmlPath));
            write.write(htmlContentString);
            write.close();
            return htmlContentString;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Calculate BMI
    private float calculateBMI(float weight, float height) {
        return (float) (weight / (height * height));
    }


    public String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();
        return ageS;
    }

    public void updateLanguages() {
        export.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.EXPORT_KEY));
        cancel.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));
        testResult.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.TEST_RESULTS_TILE_KEY));

    }

    private File createWebPrintJob(WebView webView) {
        String jobName = "Chandu" + " Document";
        Log.e("createjob", "call" + jobName);
        PrintAttributes attributes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            attributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A3)
                    .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 1400, 800))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
        }

        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/QUBEPdf/"); //it is my root directory
        dir.mkdirs();

        // File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/PDFTest/");
        PdfPrint pdfPrint = new PdfPrint(attributes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pdfPrint.print(webView.createPrintDocumentAdapter(jobName), dir, fileName);
        }
        return dir;
    }
}