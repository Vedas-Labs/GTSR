package com.gtsr.gtsr.testModule;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gtsr.gtsr.R;
import com.spectrochips.spectrumsdk.DeviceConnectionModule.Commands;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCConnectionHelper;
import com.spectrochips.spectrumsdk.FRAMEWORK.SCTestAnalysis;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ScanViewControllerActivity extends AppCompatActivity {
    BluetoothDevice bluetoothDevice;
    public static final int ACTION_REQUEST_ENABLE_BT = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static ArrayList<BluetoothDevice> devicesArray = new ArrayList<BluetoothDevice>();
    int selectedPosition = -1;
    devicesAdapter adapter;
    RecyclerView recyclerView;
    Button btnNext;
    String deviceStatus="CONNECT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        btnNext = findViewById(R.id.btn_next);
        btnNext.setVisibility(View.GONE);
        activateNotification();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition > -1) {
                    startActivity(new Intent(ScanViewControllerActivity.this, DownloadStripViewController.class));
                }
            }
        });
        loadRecyclerView();
    }

    private void loadRecyclerView() {
        adapter = new devicesAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        ImageView back = (ImageView) findViewById(R.id.home);
        ImageView refresh = (ImageView) findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devicesArray.clear();
                SCConnectionHelper.getInstance().disconnectWithPeripheral();
                selectedPosition=-1;
                adapter.notifyDataSetChanged();
                btnNext.setVisibility(View.GONE);
                SCConnectionHelper.getInstance().startScan(true);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SCConnectionHelper.getInstance().startScan(false);
            }
        });
    }

    public void activateNotification() {
        SCConnectionHelper.getInstance().activateScanNotification(new SCConnectionHelper.ScanDeviceInterface() {
            @Override
            public void onSuccessForConnection(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("onSuccessForConnection", "call");
                        deviceStatus="Connected";
                        if(deviceStatus.equals("Connected")){
                           /* btnConnect.setBackgroundResource(R.drawable.btn_gradient);
                            btnConnect.setText("Connected");*/
                            btnNext.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onSuccessForScanning(final ArrayList<BluetoothDevice> devcies, boolean msg) {
                Log.e("onSuccessForScanning", "call" + devcies.size());
                devicesArray = devcies;
                if (devicesArray.size() > 0) {
                  // btnNext.setVisibility(View.VISIBLE);
                    loadRecyclerView();
                    adapter.notifyDataSetChanged();
                } else {
                    btnNext.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailureForConnection(String error) {
                Log.e("onFailureForConnection", "call");
                SCConnectionHelper.getInstance().disconnectWithPeripheral();
                deviceStatus="Connect";
                btnNext.setVisibility(View.GONE);
                SCConnectionHelper.getInstance().startScan(true);
                // showMessage("Device not connected.");
            }

            @Override
            public void uartServiceClose(String error) {
                //  showMessage("Not support Ble Service");
            }

            @Override
            public void onBLEStatusChange(int state) {

            }
        });
    }

    public class devicesAdapter extends RecyclerView.Adapter<devicesAdapter.ViewHolder> {
        Context ctx;
        public devicesAdapter(Context ctx) {
            this.ctx = ctx;
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

            if(bluetoothDevice!=null) {
                Log.e("bluetoothDevice", "call"+selectedPosition);
                if (devicesArray.get(position).getAddress().equals(bluetoothDevice.getAddress())/*bluetoothDevice.getAddress().equals(devicesArray.get(selectedPosition).getAddress()) && deviceStatus.equals("Connected")*/) {
                    Log.e("aaaaaaa", "call"+devicesArray.get(selectedPosition).getAddress());
                  //  if (deviceStatus.equals("Connected")&&) {
                      holder.  btnConnect.setBackgroundResource(R.drawable.btn_gradient);
                       holder. btnConnect.setText("Connected");
                  //  }
                }
            }
            holder.btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition=holder.getAdapterPosition();
                    SCConnectionHelper.getInstance().startScan(false);
                    bluetoothDevice = devicesArray.get(position);
                    SCTestAnalysis.getInstance().mService.connect(bluetoothDevice);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(ScanViewControllerActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == ACTION_REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        } else {
            SCConnectionHelper.getInstance().startScan(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SCConnectionHelper.getInstance().disconnectWithPeripheral();
        finish();
       // startActivity(new Intent(ScanViewControllerActivity.this, SelectTestStripActivity.class));
        // SCConnectionHelper.getInstance().startScan(false);

    }

}
