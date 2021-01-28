package com.gtsr.gtsr.testModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.RefreshShowingDialog;
import com.gtsr.gtsr.loginModule.LoginActivity;
import com.skyfishjy.library.RippleBackground;

import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCTestAnalysis;
import com.spectrochips.spectrumsdk.FRAMEWORK.SpectroCareSDK;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PairDeviceViewController extends AppCompatActivity {
    RelativeLayout relativeScanning, relativeConnect;
    RippleBackground rippleBackground;
    RefreshShowingDialog refreshShowingDialog, inserDialogue, ejectDialogue;
    BluetoothDevice bluetoothDevice;
    private ArrayList<BluetoothDevice> devicesArray = new ArrayList<BluetoothDevice>();
    int selectedPosition = -1;
    devicesAdapter adapter;
    RecyclerView recyclerView;
    Button btnTestNow, btnStripTray;
    boolean isConnected = false;
    BluetoothAdapter bluetoothAdapter;
    int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairdevice);
        ejectDialogue = new RefreshShowingDialog(PairDeviceViewController.this, "Strip Ejecting..");
        refreshShowingDialog = new RefreshShowingDialog(PairDeviceViewController.this);
        inserDialogue = new RefreshShowingDialog(PairDeviceViewController.this, "Strip tray returning..");
        String isClickedReturnStrip = getString();
        Log.e("isClickedReturnStrip", "call" + isClickedReturnStrip);
        SpectroCareSDK.getInstance().fillContext(getApplicationContext());
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            SCConnectionHelper.getInstance().initializeAdapterAndServcie();
            bluetoothAdapter = SCConnectionHelper.getInstance().mBluetoothAdapter;
        }
        SCTestAnalysis.getInstance().initializeService();
        loadRecyclerView();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activeNotifications();
        if (SCConnectionHelper.getInstance().mBluetoothAdapter == null) {
            finish();
        } else if (!SCConnectionHelper.getInstance().mBluetoothAdapter.isEnabled()) {
            // Request for BLE turn on
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            startScan();
        }
        ///////////////////
        SCConnectionHelper.getInstance().disconnectWithPeripheral();
        if (bluetoothDevice != null) {
            bluetoothDevice = null;
        }
        devicesArray.clear();
        isConnected=false;
        selectedPosition = -1;
        adapter.notifyDataSetChanged();
        btnTestNow.setVisibility(View.VISIBLE);
        init();
    }

    protected void onPause() {
        super.onPause();
        SCConnectionHelper.getInstance().scanDeviceInterface = null;
        SCConnectionHelper.getInstance().startScan(false);

    }

    private void init() {
        // iv = (ImageView) findViewById(R.id.animation);
        relativeScanning = (RelativeLayout) findViewById(R.id.relativeScanning);
        relativeScanning.setVisibility(View.VISIBLE);
        relativeConnect = (RelativeLayout) findViewById(R.id.relativeConnect);
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        btnTestNow = findViewById(R.id.btn_next);
        btnStripTray = findViewById(R.id.btn_striptray);
        btnTestNow.setVisibility(View.GONE);
        btnStripTray.setVisibility(View.GONE);
        // SCTestAnalysis.getInstance().isInsertStrip=false;
        SCTestAnalysis.getInstance().loadInterface();

        btnTestNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginActivity.isGtsrSelected) {
                    String isClickedReturnStrip = getString();
                    Log.e("setOnClickListener", "call" + isClickedReturnStrip);
                    if (isClickedReturnStrip != null) {
                        if (isClickedReturnStrip.contains("true")) {
                            ejectDialogue.showAlert();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SCTestAnalysis.getInstance().sendString("$SUV0#");
                                }
                            }, 1000 * 2);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (SCConnectionHelper.getInstance().isConnected) {
                                        SCTestAnalysis.getInstance().ejectStripCommand();
                                    }
                                }
                            }, 1000 * 2);
                        }
                    } else {
                        bluetoothDevice = null;
                        isConnected = false;
                        startActivity(new Intent(PairDeviceViewController.this, DownloadStripViewController.class));
                    }
                }else{
                    startActivity(new Intent(PairDeviceViewController.this, SelectTestStripActivity.class));
                }
            }
        });
        btnStripTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SCConnectionHelper.getInstance().isConnected) {
                    inserDialogue.showAlert();
                    saveSrting("true");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                SCTestAnalysis.getInstance().sendString("$SUV0#");
                        }
                    }, 1000 * 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SCTestAnalysis.getInstance().insertStripCommand();
                        }
                    }, 1000 * 2);
                }

            }
        });
        RelativeLayout back = findViewById(R.id.home);
        RelativeLayout refresh = findViewById(R.id.refresh);
        RelativeLayout back1 = findViewById(R.id.home1);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = false;
                SCConnectionHelper.getInstance().disconnectWithPeripheral();
                bluetoothDevice = null;
                devicesArray.clear();
                selectedPosition = -1;
                adapter.notifyDataSetChanged();
                btnTestNow.setVisibility(View.GONE);
                btnStripTray.setVisibility(View.GONE);
                startScan();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SCConnectionHelper.getInstance().startScan(false);
                finish();
            }
        });
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SCConnectionHelper.getInstance().startScan(false);
                finish();
            }
        });
    }

    private void loadRecyclerView() {
        adapter = new devicesAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void loadEjectInterface() {
        SCTestAnalysis.getInstance().ejectTesting(new SCTestAnalysis.EjectInterface() {
            @Override
            public void ejectStrip(boolean bool) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ejectDialogue.hideRefreshDialog();
                        SCTestAnalysis.getInstance().ejectTesting(null);
                        Toast.makeText(getApplicationContext(), "Strip Ejected.", Toast.LENGTH_SHORT).show();
                        bluetoothDevice = null;
                        isConnected = false;
                        //clearPreferenceData();
                        startActivity(new Intent(PairDeviceViewController.this, DownloadStripViewController.class));
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
                        inserDialogue.hideRefreshDialog();
                        SCConnectionHelper.getInstance().disconnectWithPeripheral();
                        finish();
                        // Toast.makeText(getApplicationContext(), "Insert Strip.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void activeNotifications() {
        loadEjectInterface();
        SCConnectionHelper.getInstance().activateScanNotification(new SCConnectionHelper.ScanDeviceInterface() {
            @Override
            public void onSuccessForConnection(String msg) {
                Log.e("onSuccessForConnection", "call");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("onSuccessForConnection", "call");
                        refreshShowingDialog.hideRefreshDialog();
                        isConnected = true;
                        if(LoginActivity.isGtsrSelected) {
                            btnTestNow.setVisibility(View.VISIBLE);
                            btnStripTray.setVisibility(View.VISIBLE);
                        }else{
                            btnTestNow.setVisibility(View.VISIBLE);
                            btnStripTray.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();

                    }
                });
            }

            @Override
            public void onSuccessForScanning(final ArrayList<BluetoothDevice> devcies, boolean msg) {
                Log.e("onSuccessForScanning", "size" + devcies.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*devicesArray = devcies;
                        adapter.notifyDataSetChanged();*/
                        if (devcies.size() > 0) {
                            devicesArray = devcies;
                            relativeScanning.setVisibility(View.GONE);
                            relativeConnect.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        } else {
                            btnTestNow.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onFailureForConnection(String error) {
                Log.e("onFailureForConnection", "call");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // adapter.notifyDataSetChanged();
                        refreshShowingDialog.hideRefreshDialog();
                        Log.e("onFailureForConnection", "call");
                        isConnected = false;
                        //   deviceStatus = "Connect";
                        btnTestNow.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void uartServiceClose(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onBLEStatusChange(int state) {
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        //Indicates the local Bluetooth adapter is off.
                        //   setEmptyText("<bluetooth is disabled>");
                        devicesArray.clear();
                        adapter.notifyDataSetChanged();
                        // menu.findItem(R.id.ble_scan).setEnabled(false);
                        Log.e("BLE_Status", "OFF");
                        break;

                    case BluetoothAdapter.STATE_TURNING_ON:
                        //Indicates the local Bluetooth adapter is turning on. However local clients should wait for STATE_ON before attempting to use the adapter.
                        break;

                    case BluetoothAdapter.STATE_ON:
                        //Indicates the local Bluetooth adapter is on, and ready for use.
                        Log.e("BLE Status", "ON");
                        // setEmptyText("<use SCAN to refresh devices>");
//                        if (menu != null)
//                            menu.findItem(R.id.ble_scan).setEnabled(true);
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                        //Indicates the local Bluetooth adapter is turning off. Local clients should immediately attempt graceful disconnection of any remote links.
                        break;
                }
            }
        });

    }

    @SuppressLint("StaticFieldLeak") // AsyncTask needs reference to this fragment
    private void startScan() {
        if (SCConnectionHelper.getInstance().getScanState() != SCConnectionHelper.ScanState.NONE)
            return;
        SCConnectionHelper.getInstance().setScanState(SCConnectionHelper.ScanState.LESCAN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationEnableStatus();
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                SCConnectionHelper.getInstance().setScanState(SCConnectionHelper.ScanState.NONE);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.location_permission_title);
                builder.setMessage(R.string.location_permission_message);
                builder.setPositiveButton(android.R.string.ok,
                        (dialog, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0));
                builder.show();
                return;
            }
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean locationEnabled = false;
            try {
                locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ignored) {
            }
            try {
                locationEnabled |= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ignored) {
            }
            if (!locationEnabled)
                SCConnectionHelper.getInstance().setScanState(SCConnectionHelper.ScanState.DISCOVERY);
        }
        devicesArray.clear();
        adapter.notifyDataSetChanged();
        if (SCConnectionHelper.getInstance().getScanState() == SCConnectionHelper.ScanState.LESCAN) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void[] params) {
                    SCConnectionHelper.getInstance().startScan(true);
                    return null;
                }
            }.execute();
        } else {
            bluetoothAdapter.startDiscovery();
        }
    }

    private void checkLocationEnableStatus() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(this)
                    .setMessage("Please enable location services to scan bluetooth devices")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ignore requestCode as there is only one in this fragment
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new Handler(Looper.getMainLooper()).postDelayed(this::startScan, 1); // run after onResume to avoid wrong empty-text
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getText(R.string.location_denied_title));
            builder.setMessage(getText(R.string.location_denied_message));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
    }

    public class devicesAdapter extends RecyclerView.Adapter<devicesAdapter.ViewHolder> {
        Context ctx;

        // ArrayList<BluetoothDevice> devicesArray;
        public devicesAdapter(Context ctx/*,ArrayList<BluetoothDevice> devcies*/) {
            this.ctx = ctx;
            //  this.devicesArray=devcies;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name, id;
            ImageView image;
            Button btnConnect;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.testName);
                image = (ImageView) itemView.findViewById(R.id.image);
                id = itemView.findViewById(R.id.txt_id);
                btnConnect = itemView.findViewById(R.id.btn_connect);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.scanlist, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            BluetoothDevice device = devicesArray.get(position);
            holder.name.setText(device.getName());
            holder.id.setText(device.getAddress());
            holder.btnConnect.setBackgroundResource(R.drawable.btn_gray);
            holder.btnConnect.setText("Connect");
            if (isConnected) {
                selectedPosition = -1;
                if (devicesArray.get(position).getAddress().equals(bluetoothDevice.getAddress())) {
                    holder.btnConnect.setBackgroundResource(R.drawable.btn_gradient);
                    holder.btnConnect.setText("Connected");
                    btnTestNow.setVisibility(View.VISIBLE);
                    btnStripTray.setVisibility(View.VISIBLE);
                }
            } else {
                holder.btnConnect.setBackgroundResource(R.drawable.btn_gray);
                holder.btnConnect.setText("Connect");
                btnTestNow.setVisibility(View.GONE);
                SCConnectionHelper.getInstance().disconnectWithPeripheral();
            }
            if (selectedPosition == position) {
                selectedPosition = position;
                refreshShowingDialog.showAlert();
                Log.e("selectedpos", "call" + holder.getAdapterPosition());
                bluetoothDevice = devicesArray.get(position);
                /*SCTestAnalysis.getInstance().mService.connect(bluetoothDevice);
                SCConnectionHelper.getInstance().startScan(false);
*/
                SCConnectionHelper.getInstance().stopScan();
                SCTestAnalysis.getInstance().mService.connect(bluetoothDevice);
            }
            holder.btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isConnected = false;
                    if (selectedPosition != position) {
                        selectedPosition = position;
                        notifyDataSetChanged();
                    } else {
                        selectedPosition = -1;
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (devicesArray.size() > 0) {
                return devicesArray.size();
            } else {
                return 0;
            }
        }
    }

    public void saveSrting(String isRetrunstrip) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("isRetrunstrip", isRetrunstrip);
        Log.e("isRetrunstrip", "call" + isRetrunstrip);
        editor.apply();
        editor.commit();
    }

    public String getString() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = prefs.getString("isRetrunstrip", null);
        return json;
    }

    private void clearPreferenceData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        // if(SCConnectionHelper.getInstance().scanner!=null)
        SCConnectionHelper.getInstance().stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // clearPreferenceData();
        SCConnectionHelper.getInstance().stopScan();
    }

    @Override
    public void onBackPressed() {
        Log.e("isRetrunstrip", "call");
        super.onBackPressed();
        SCConnectionHelper.getInstance().startScan(false);
        //clearPreferenceData();
        finish();
    }
}