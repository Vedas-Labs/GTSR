package com.gtsr.gtsr.qubeTestResults;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.VoiceInteractor;
import android.graphics.drawable.Drawable;
import java.text.ParseException;
import android.os.Build;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.gtsr.gtsr.Constants;
import com.gtsr.gtsr.HomeActivity;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.dataController.QubeController;
import com.gtsr.gtsr.database.TestFactorDataController;
import com.gtsr.gtsr.database.TestFactors;
import com.gtsr.gtsr.database.UrineResultsDataController;
import com.gtsr.gtsr.model.QubeResultModel;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.DisplayMetrics;
import android.content.ActivityNotFoundException;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QubeTestResultActivity extends AppCompatActivity {
    ImageView backimage;
    ImageView img_print, img_share;
    RelativeLayout rl_pdf;
    Bitmap bitmap;
    TextView txt_currentDate/*,txt_result,txt_condition,txt_value,txt_resultMsg*/;
    private static final int REQUEST_WRITE_PERMISSION = 56;
    ArrayList<TestFactors> testFactorsArrayList;
    TestFactors currentTestFactor = null;
    SimpleDateFormat weekFormatter;

    @BindView(R.id.txt_result)
    TextView txt_result;
    @BindView(R.id.txt_condition)
    TextView txt_condition;
    @BindView(R.id.txt_value)
    TextView txt_value;
    @BindView(R.id.txt_resultMsg)
    TextView txt_resultMsg;
    @BindView(R.id.txt_round)
    TextView txt_round;

    @BindView(R.id.img_indicator)
    ImageView img_indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qube_test_result);
        ButterKnife.bind(this);
        weekFormatter = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH);
        requestPermission();
        loadIds();
    }

    private void loadIds() {
        img_print = findViewById(R.id.img_print);
        rl_pdf = findViewById(R.id.rl_pdftag);
        img_share = findViewById(R.id.img_share);
        txt_currentDate = findViewById(R.id.txt_currentDate);
        backimage = findViewById(R.id.backimage);

        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadBackAction();
            }
        });
        img_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = loadBitmapFromView(rl_pdf, rl_pdf.getWidth(), rl_pdf.getHeight());
                createPdf();
            }
        });
        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap map = bitmapGetBitmapFromView(rl_pdf);
                savebitmap2(map);
            }
        });
        loadResultData();
        loadCurrentModelData();
    }

    private void loadResultData() {
        if (UrineResultsDataController.getInstance().currenturineresultsModel != null) {
            if (UrineResultsDataController.getInstance().currenturineresultsModel.getTestType().contains(Constants.TestNames.qube.toString())) {
                try {
                    txt_currentDate.setText(UrineResultsDataController.getInstance().convertTestTimeTodate(UrineResultsDataController.getInstance().currenturineresultsModel.getTestedTime()));
                    testFactorsArrayList = TestFactorDataController.getInstance().fetchTestFactorresults(UrineResultsDataController.getInstance().currenturineresultsModel);
                    currentTestFactor = testFactorsArrayList.get(0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                testFactorsArrayList = new ArrayList();
            }
        }
    }
    private void loadCurrentModelData() {
        if (currentTestFactor != null) {
            txt_result.setText(currentTestFactor.getResult());
            txt_condition.setText(currentTestFactor.getHealthReferenceRanges());
            txt_value.setText(currentTestFactor.getValue()+" index");

            if(currentTestFactor.getResult().equals("Positive")){
                txt_round.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
                img_indicator.setBackgroundResource(R.drawable.image_1);
                txt_result.setTextColor(Color.parseColor("#77BD93"));
                Log.e("result", "call " + currentTestFactor.getResult());
            }else{
                Log.e("adsf", "call " + currentTestFactor.getResult());
                txt_round.setBackgroundTintList(getResources().getColorStateList(R.color.colorOrange));

                txt_result.setTextColor(Color.parseColor("#EA9834"));
                img_indicator.setImageResource(R.drawable.image_2);
            }
            if (QubeController.getInstance().qubeResultList.size() > 0) {
                for (int i = 0; i < QubeController.getInstance().qubeResultList.size(); i++) {
                    QubeResultModel resultModel = QubeController.getInstance().qubeResultList.get(i);
                    if (resultModel.getTestResult().contains(currentTestFactor.getResult())) {
                        txt_resultMsg.setText(resultModel.getResultMessage());
                    }
                }
            }
        }

    }
    public Bitmap bitmapGetBitmapFromView(RelativeLayout view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(getResources().getColor(R.color.colorWhite));

        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private void loadBackAction() {
        if (HomeActivity.isFromHome) {
            HomeActivity.isFromHome = false;
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            // intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private void createPdf() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = null;
        Canvas canvas = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            canvas = page.getCanvas();
            Paint paint = new Paint();
            canvas.drawPaint(paint);

            bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

            paint.setColor(Color.BLUE);
            canvas.drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);

            // write the document content
            String targetPdf = "/sdcard/pdffromlayout.pdf";
            File filePath;
            filePath = new File(targetPdf);
            try {
                document.writeTo(new FileOutputStream(filePath));

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
            }

            // close the document
            document.close();
            Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show();

            //openGeneratedPDF();
        }
    }

    private File savebitmap2(Bitmap bmp) {
        String temp = "UrineResultHistory";

        OutputStream outStream = null;
        String path = Environment.getExternalStorageDirectory()
                .toString();
        new File(path + "/SplashItTemp2").mkdirs();
        File file = new File(path + "/SplashItTemp2", temp + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(path + "/SplashItTemp2", temp + ".png");
        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            file.setReadable(true, false);

            Uri imageUri = FileProvider.getUriForFile(
                    getApplicationContext(),
                    "com.gtsr.gtsr.provider", //(use your app signature + ".provider" )
                    file);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "Sharing Status.."));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {

        }
    }

    /* private void openGeneratedPDF(){
        File file = new File("/sdcard/pdffromlayout.pdf");
        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(getApplicationContext(), "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }*/
    @Override
    public void onBackPressed() {
        loadBackAction();
        super.onBackPressed();

    }
}