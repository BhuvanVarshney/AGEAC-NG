package com.research.ageac.nongamified;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChecker {


    Context context;
    ConnectionChecker(Context context)
    {
        this.context = context;
    }

    boolean isInternetConnected()
    {
        boolean isConnectedToInternet = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null)
        {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null)
            {
                if(networkInfo.getState()== NetworkInfo.State.CONNECTED){
                    isConnectedToInternet = true;
                }
            }
        }
        return isConnectedToInternet;
    }
}
