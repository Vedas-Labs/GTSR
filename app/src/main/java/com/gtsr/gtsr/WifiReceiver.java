package com.gtsr.gtsr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by wave on 10/6/2018.
 */

public class WifiReceiver extends BroadcastReceiver {
    String ssid = "";
    boolean isoNLINE = false;
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
              //  Toast.makeText(context, "Wifi enabled", Toast.LENGTH_LONG).show();
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
              //  Toast.makeText(context, "Mobile data enabled", Toast.LENGTH_LONG).show();
            }
        }
        else {
           // Toast.makeText(context, "No internet is available", Toast.LENGTH_LONG).show();
        }


        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (info != null && info.isConnected()) {

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            ssid = wifiInfo.getSSID();
            Boolean isConnectedStatus = info != null && info.isConnected();
            Log.e("ssidforwifi", "call" + ssid + isConnectedStatus);
            /*if(OfflineDataController.isConnected != isConnectedStatus){
                if (isOnline()) {
                    OfflineDataController.isConnected = isConnectedStatus;
                    Log.e("isConnectedCalled", "" + OfflineDataController.isConnected);
                    OfflineDataController.getInstance().sycnOfflineData();
                    OfflineDataController.isConnected=false;
                }
            }*/
        }
    }
    public boolean isOnline() {
        try {
            Process p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            Log.e("checkreachabilty", "call" + reachable);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.e("checkreachabilty", "call" + "false");
        return false;
    }
}
