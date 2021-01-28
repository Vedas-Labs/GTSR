package com.gtsr.gtsr.dataController;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by ADMIN on 05-06-2019.
 */

public class ClientController {

    private static ClientController ourInstance;
    public Context context;

    public static ClientController getInstance() {
        if (ourInstance == null) {
            ourInstance = new ClientController();
        }
        return ourInstance;
    }

    public void fillContext(Context context1) {
        context = context1;
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
