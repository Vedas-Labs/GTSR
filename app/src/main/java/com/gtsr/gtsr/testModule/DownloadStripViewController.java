package com.gtsr.gtsr.testModule;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gtsr.gtsr.AlertShowingDialog;
import com.gtsr.gtsr.HomeActivity;
import com.gtsr.gtsr.InstructionpageViewController;
import com.gtsr.gtsr.LanguagesKeys;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.RefreshShowingDialog;
import com.gtsr.gtsr.dataController.ApiCallDataController;
import com.gtsr.gtsr.dataController.LanguageTextController;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCFile;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCTestAnalysis;

import com.spectrochips.spectrumsdk.FRAMEWORK.SCFileHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.TestFactors;
import com.spectrochips.spectrumsdk.FRAMEWORK.UartService;
import com.spectrochips.spectrumsdk.MODELS.IntensityChart;
import com.spectrochips.spectrumsdk.MODELS.SpectroDeviceDataController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Abhilash on 3/20/2019.
 */

public class DownloadStripViewController extends AppCompatActivity {
    StripAdapter stripAdapter;
    BloodStripAdapter bloodStripAdapter;
    RecyclerView recyclerView,bloodRecyclerView;
    EditText searchBox;
    int selectedPosition = -1;
    RefreshShowingDialog getfilesDialogue, syncingDialog, syncingDialog1;
    Button next;
    ArrayList<SCFile> scFilesArray = new ArrayList<>();
    ArrayList<SCFile> bloodFilesArray = new ArrayList<>();
    TextView eject_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadjsonfiles);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
        getfilesDialogue = new RefreshShowingDialog(DownloadStripViewController.this, "Strip Ejecting..");
        syncingDialog = new RefreshShowingDialog(DownloadStripViewController.this, "Configure Settings..");
        syncingDialog1 = new RefreshShowingDialog(DownloadStripViewController.this, "Configuring..");

        loadArray();
        loadRecyclerView();
        loadBloodRecyclerview();
        fetchJsonFiles();
        SpectroDeviceDataController.getInstance().fillContext(getApplicationContext());
        SCTestAnalysis.getInstance().startTestProcess();
        SCTestAnalysis.getInstance().initializeService();
        SCTestAnalysis.getInstance().fillContext(getApplicationContext());


        fetchJsonFiles();
        String isClickedReturnStrip=getString();
        Log.e("setOnClickListener","call"+isClickedReturnStrip);
        if(isClickedReturnStrip==null){
            syncingDialog1.showAlert();
            loadDummyCommands();
        }else{
            clearPreferenceData();
            syncingDialog1.showAlert();
            loadDummyCommands();
        }
    }
    private void loadArray() {
        SCFile scFile = new SCFile();
        scFile.setId("VEDA1234");
        scFile.setAddedDate("2020/10/16");
        scFile.setCategory("Urine");
        scFile.setFilename("VEDA_Urine Test Strip");


        SCFile scFile1 = new SCFile();
        scFile1.setId("VEDA1234");
        scFile1.setAddedDate("2020/10/16");
        scFile1.setCategory("Urine");
        scFile1.setFilename("Roche Combur 10 Test Ux Urinalysis Strip");

        scFilesArray.add(scFile);
        scFilesArray.add(scFile1);

        SCFile scFile2 = new SCFile();
        scFile2.setId("VEDA1234");
        scFile2.setAddedDate("2020/10/16");
        scFile2.setCategory("Blood");
        scFile2.setFilename("Roche Accu-Chek Active Blood Glucose Test Strip");

        bloodFilesArray.add(scFile2);

    }

    protected void onResume() {
        super.onResume();
        selectedPosition = -1;
        stripAdapter.notifyDataSetChanged();
        fetchJsonFiles();
    }
private void loadDummyCommands(){
    SCTestAnalysis.getInstance().isTestingCal = true;
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            if (SCConnectionHelper.getInstance().isConnected) {
                SCTestAnalysis.getInstance().sendString("$SUV0#");
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (SCConnectionHelper.getInstance().isConnected) {
                        SCTestAnalysis.getInstance().sendString("$CAL#");
                    }
                }
            }, 3000 * 1);
        }
    }, 2000 * 1);
}
    private void fetchJsonFiles() {
        SCConnectionHelper.getInstance().activateScanNotification(new SCConnectionHelper.ScanDeviceInterface() {
            @Override
            public void onSuccessForConnection(String msg) {

            }

            @Override
            public void onSuccessForScanning(ArrayList<BluetoothDevice> deviceArray, boolean msg) {

            }

            @Override
            public void onFailureForConnection(String error) {
                Log.e("onFailureForConnection", "msssasnasn");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertShowingDialog(DownloadStripViewController.this, "Device Disconnected", LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                    }
                });
            }

            @Override
            public void uartServiceClose(String error) {

            }

            @Override
            public void onBLEStatusChange(int state) {

            }
        });
        SCFileHelper.getInstance().fetchJsonfiles(new SCFileHelper.DownloadJsonFiles() {
            @Override
            public void onSuccessForLoadJson(final ArrayList<SCFile> jsonObject) {
                Log.e("onSuccessForLoadJson", "msssasnasn" + jsonObject.size());
                /*scFilesArray = jsonObject;
                loadRecyclerView();*/
                //stripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailureForLoadJson() {
                Toast.makeText(getApplicationContext(), "Fail to load json files.", Toast.LENGTH_SHORT).show();
            }
        });

        SCTestAnalysis.getInstance().ejectTesting(new SCTestAnalysis.EjectInterface() {
                @Override
                public void ejectStrip(boolean bool) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getfilesDialogue.hideRefreshDialog();
                            Toast.makeText(getApplicationContext(), "Strip Ejected.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            @Override
            public void startTestForEjectTest(boolean bool) {

            }

            @Override
            public void stoptestForEjectTest(boolean bool) {

            }

            @Override
            public void insertStrip(boolean bool) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(getApplicationContext(), "Insert Strip.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        SCTestAnalysis.getInstance().startTestAnalysis(new SCTestAnalysis.TeststaResultInterface() {
            @Override
            public void onSuccessForTestComplete(ArrayList<TestFactors> results, String msg, ArrayList<IntensityChart> intensityChartsArray) {

            }

            @Override
            public void getRequestAndResponse(final String receivedString) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("ReceivedBytes", "call" + receivedString);
                        if (SCTestAnalysis.getInstance().isTestingCal) {
                            if (receivedString.contains("^EOF#")) {
                                SCTestAnalysis.getInstance().isTestingCal = false;
                                syncingDialog1.hideRefreshDialog();

                               /* getfilesDialogue.showAlert();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (SCConnectionHelper.getInstance().isConnected) {
                                            SCTestAnalysis.getInstance().ejectStripCommand();
                                        }
                                    }
                                }, 1000 * 1);*/
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailureForTesting(String error) {
                Log.e("onFailureForTesting", "call");
            }
        });

        SCFileHelper.getInstance().getJsonFiles();
    }

    @OnClick(R.id.back)
    public void bkAction() {
        SCConnectionHelper.getInstance().scanDeviceInterface = null;
        SCConnectionHelper.getInstance().disconnectWithPeripheral();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SCConnectionHelper.getInstance().scanDeviceInterface = null;
        SCConnectionHelper.getInstance().disconnectWithPeripheral();
        finish();
        /*SCTestAnalysis.getInstance().removereceiver();
        SCTestAnalysis.getInstance().unRegisterReceiver();
        SCConnectionHelper.getInstance().disconnectWithPeripheral();
        SCConnectionHelper.getInstance().mBluetoothAdapter=null;
        finish();*/
    }

    @OnClick(R.id.file)
    public void fileAction() {
        if (ActivityCompat.checkSelfPermission(DownloadStripViewController.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openFolder();
        }
    }
private void loadBloodRecyclerview(){
    bloodStripAdapter = new BloodStripAdapter(getApplicationContext());
    bloodRecyclerView = (RecyclerView) findViewById(R.id.listtwo);
    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    bloodRecyclerView.setLayoutManager(horizontalLayoutManager);
    bloodRecyclerView.setAdapter(bloodStripAdapter);
    bloodRecyclerView.setHasFixedSize(true);
}
    private void loadRecyclerView() {
        stripAdapter = new StripAdapter(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.humanlist);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(stripAdapter);
        recyclerView.setHasFixedSize(true);

        next = (Button) findViewById(R.id.next);
        eject_text = (TextView) findViewById(R.id.textview);
        SpannableString content = new SpannableString("Eject Strip Tray");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        eject_text.setText(content);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition > -1) {
                    Log.e("DialogInterface", "click");
                    syncingDialog.showAlert();
                    loadFileWithFileName("VEDA_UrineTest.json", scFilesArray.get(selectedPosition).getCategory(), scFilesArray.get(selectedPosition).getAddedDate());
                    // showAlert(scFilesArray.get(selectedPosition).getFilename(), scFilesArray.get(selectedPosition).getCategory(), scFilesArray.get(selectedPosition).getAddedDate());
                } else {
                    new AlertShowingDialog(DownloadStripViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SELECT_A_STRIP_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }

            }
        });
        RelativeLayout back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SCConnectionHelper.getInstance().disconnectWithPeripheral();
                finish();
                //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });

        eject_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getfilesDialogue.showAlert();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (SCConnectionHelper.getInstance().isConnected) {
                            SCTestAnalysis.getInstance().ejectStripCommand();
                        }
                    }
                }, 1000 * 1);
            }
        });
    }


    public class StripAdapter extends RecyclerView.Adapter<StripAdapter.ViewHolder> {
        Context ctx;

        public StripAdapter(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.downloadjsonlist, parent, false);
            ViewHolder myViewHolder = new ViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(scFilesArray.get(position).getFilename());
            if(position==1){
                holder.name.setTextColor(Color.parseColor("#cccccc"));
                holder.testParams.setVisibility(View.GONE);
            }
            holder.btnConnect.setVisibility(View.GONE);
            if (selectedPosition == position) {
                Log.e("if", "" + position);
                holder.image.setVisibility(View.VISIBLE);
            } else {
                Log.e("else", "" + position);
                holder.image.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(position==0){
                       if (selectedPosition != position) {
                           selectedPosition = position;
                           notifyDataSetChanged();
                       } else {
                           selectedPosition = -1;
                           notifyDataSetChanged();
                       }
                   }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (scFilesArray.size() > 0) {
                return scFilesArray.size();
            } else {
                return 0;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name,testParams;
            ImageView image;
            Button btnConnect;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.testName);
                image = (ImageView) itemView.findViewById(R.id.image);
                btnConnect = itemView.findViewById(R.id.btn_connect);
                testParams=itemView.findViewById(R.id.testParams);

                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {

            }
        }
    }

    public class BloodStripAdapter extends RecyclerView.Adapter<BloodStripAdapter.ViewHolder> {
        Context ctx;

        public BloodStripAdapter(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.downloadjsonlist, parent, false);
            ViewHolder myViewHolder = new ViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(bloodFilesArray.get(position).getFilename());
            holder.btnConnect.setVisibility(View.GONE);
            holder.testParams.setVisibility(View.GONE);
            holder.name.setTextColor(Color.parseColor("#cccccc"));

           /* if (selectedPosition == position) {
                Log.e("if", "" + position);
                holder.image.setVisibility(View.VISIBLE);
            } else {
                Log.e("else", "" + position);
                holder.image.setVisibility(View.GONE);
            }*/

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  /*  if (selectedPosition != position) {
                        selectedPosition = position;
                        notifyDataSetChanged();
                    } else {
                        selectedPosition = -1;
                        notifyDataSetChanged();
                    }*/
                }
            });
        }

        @Override
        public int getItemCount() {
            if (bloodFilesArray.size() > 0) {
                return bloodFilesArray.size();
            } else {
                return 0;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name,testParams;
            ImageView image;
            Button btnConnect;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.testName);
                image = (ImageView) itemView.findViewById(R.id.image);
                btnConnect = itemView.findViewById(R.id.btn_connect);
                testParams=itemView.findViewById(R.id.testParams);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {

            }
        }
    }

    private void showAlert(final String jsonFiles, final String category, final String date) {
        String key = "Would you like to configure the 'XXXXXXXX' into the SpectroDevice?";
        String str1 = key.replaceFirst("'XXXXXXXX'", jsonFiles);
        System.out.println(str1);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Configure File");
        alertDialogBuilder.setMessage(str1)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.e("DialogInterface", "click");
                        // SCTestAnalysis.getInstance().fillContext(getApplicationContext());
                        syncingDialog.showAlert();
                        loadFileWithFileName(jsonFiles, category, date);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));

        Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#FF0012"));
    }

    private void loadFileWithFileName(final String fileNmae, final String category, final String date) {
        SCTestAnalysis.getInstance().getDeviceSettings(fileNmae, category, "");
        syncingDialog.hideRefreshDialog();
        syncingSettings(fileNmae, category, date);
    }

    public void syncingSettings(final String fileNmae, final String category, final String date) {
        if (SCTestAnalysis.getInstance().canDo()) {
            syncingDialog.showAlert();
            synSettingWithDevice(fileNmae);
        }
    }

    private void showFileAlert(JSONObject jsonObject, String filename, final String category, final String date) {
        String key = "Would you like to configure the 'XXXXXXXX' into the SpectroDevice?";
        String str1 = key.replaceFirst("'XXXXXXXX'", filename);
        System.out.println(str1);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Configure File");
        alertDialogBuilder.setMessage(str1)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.e("fileDialogInterface", "click");
                        SCTestAnalysis.getInstance().setDeviceSettings(jsonObject);
                        // next.setVisibility(View.GONE);
                        syncingDialog.showAlert();
                        synSettingWithDevice(filename);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));

        Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#FF0012"));
    }

    public void synSettingWithDevice(String filename) {
        SCTestAnalysis.getInstance().syncSettingsWithDevice(new SCTestAnalysis.SyncingInterface() {
            @Override
            public void isSyncingCompleted(boolean error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        syncingDialog.hideRefreshDialog();
                        Intent intent = new Intent(getApplicationContext(), SampleReadyActivity.class);
                        intent.putExtra("filename", filename);
                        intent.putExtra("category", "Urine");
                        intent.putExtra("addedDate", "");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void gettingDarkSpectrum(boolean isgetting) {

            }
        });

    }

    private static final int FILE_PICKER_REQUEST_CODE = 8001;

    public void openFolder() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    try {
                        Log.e("filepath", "call" + getFilePath(getApplicationContext(), uri));
                        String path = getFilePath(getApplicationContext(), uri);
                        String array[] = path.split("/");
                        String name = array[array.length - 1];
                        Log.e("name", "call" + name);
                        if (path.contains(".json")) {
                            ReadFile(uri, name);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please select json file", Toast.LENGTH_LONG).show();
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("resultcode", "call" + resultCode);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri content_describer = data.getData();
            BufferedReader reader = null;
            try {
                // open the user-picked file for reading:
                InputStream in = getContentResolver().openInputStream(content_describer);
                // now read the content:
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                String filename = getFileName(getApplicationContext(), content_describer);
                if (filename != null) {
                    if (filename.contains(".json")) {
                        ReadFile(builder, filename);
                    } else {
                        Toast.makeText(DownloadStripViewController.this, "Please select json file", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        if (DEBUG)
            Log.d(" ccccccc" + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static final boolean DEBUG = false; // Set to true to enable logging

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isLocalStorageDocument(Uri uri) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getFileName(@NonNull Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        String filename = null;

        if (mimeType == null && context != null) {
            String path = getPath(context, uri);
            if (path == null) {
                filename = getName(uri.toString());
            } else {
                File file = new File(path);
                filename = file.getName();
            }
        } else {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                filename = returnCursor.getString(nameIndex);
                returnCursor.close();
            }
        }
        Log.e("filename", "call" + filename);
        return filename;
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf('/');
        return filename.substring(index + 1);
    }

    public String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {


            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public void ReadFile(StringBuilder builder, final String filename) {
        BufferedReader reader = null;
       /* try {
            // open the user-picked file for reading:
            InputStream in = getContentResolver().openInputStream(uri);
            // now read the content:
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }*/
        // Do something with the content in
        try {
            final JSONObject jsonObj = new JSONObject(builder.toString());
            Log.e("dataobject", "call" + jsonObj);
            Log.e("localfilename", "call" + filename);
            Log.e("fileDialogInterface", "click");
            SCTestAnalysis.getInstance().setDeviceSettings(jsonObj);
            // next.setVisibility(View.GONE);
            syncingDialog.showAlert();
            synSettingWithDevice(filename);
            //showFileAlert(jsonObj,filename,"Urine","");
        } catch (Throwable t) {
            Log.e("MyApp", "Could not parse malformed JSON");
            Toast.makeText(getApplicationContext(), "Not a valid file", Toast.LENGTH_LONG).show();
        }
       /* } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } *//*catch (JSONException e) {
            e.printStackTrace();
        }*//* finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

    }
    public String  getString() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = prefs.getString("isRetrunstrip", null);
        return json;
    }
    private void clearPreferenceData(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}

