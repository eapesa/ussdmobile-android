package com.example.ussd.poc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by elixa.pesa on 17/04/2018.
 */

public class UssdSender {
    private String TAG = UssdSender.class.getSimpleName();

    private TelephonyManager ussdManager;

    private Context context;

    public UssdSender(final Context context) {
        this.context = context;
        ussdManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public interface Listener {
        void onRequirePermission();
        void onSuccess(String response);
        void onError(int failureCode);
    }

    public void sendUssd(String text, final Listener listener) {
        boolean isGranted = (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED);
        Log.d(TAG, "[PERMISSION] GRANTED? " + isGranted);
        if (isGranted) {
            ussdManager.sendUssdRequest(text, new TelephonyManager.UssdResponseCallback() {
                @Override
                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                    super.onReceiveUssdResponse(telephonyManager, request, response);
                    Log.d(TAG, "[SUCCESS] USSD SENDER REQUEST: " + request);
                    Log.d(TAG, "[SUCCESS] USSD SENDER RESPONSE: " + response);
                    listener.onSuccess(response.toString());
                }

                @Override
                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                    Log.d(TAG, "[FAILED] USSD SENDER REQUEST: " + request);
                    Log.d(TAG, "[FAILED] USSD SENDER RESPONSE: " + failureCode);
                    listener.onError(failureCode);
                }
            }, new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Log.d(TAG, "NOTE: Check for what is this handler...");
                    return true;
                }
            }));
        } else {
            listener.onRequirePermission();
        }

    }
}
