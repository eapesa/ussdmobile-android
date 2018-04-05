package com.ph.eapesa.ussdpoc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by elixa.pesa on 03/04/2018.
 */

public class UssdActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = UssdActivity.class.getSimpleName();
    private TextView textViewResponse;
    private EditText editTextRequest;
    private Button buttonSend;
    private Button buttonRefresh;

    private int REQUEST_CODE_PERMISSION = 500;
    private final String ussdNum = "*121*4*1#";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ussd);

        initializeLayout();
        initializeUssd();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            Log.d(TAG, "CALL_PHONE permission granted");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ussd_button_send:
                Log.d(TAG, "Send button clicked.");
                break;
            case R.id.ussd_button_refresh:
                Log.d(TAG, "Refresh button clicked.");
                refreshVisibility("onLoading", View.INVISIBLE);
                initializeUssd();
                break;
        }
    }

    private void initializeLayout() {
        textViewResponse = (TextView) findViewById(R.id.ussd_textview_response);
        editTextRequest = (EditText) findViewById(R.id.ussd_editview_request);
        buttonSend = (Button) findViewById(R.id.ussd_button_send);
        buttonRefresh = (Button) findViewById(R.id.ussd_button_refresh);

        buttonSend.setOnClickListener(this);
        buttonRefresh.setOnClickListener(this);
    }

    private void initializeUssd() {
        TelephonyManager ussdManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        Log.d(TAG, "Contacting USSD number: " + ussdNum);

        if (ActivityCompat.checkSelfPermission(UssdActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UssdActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_PERMISSION);
            return;
        }

        ussdManager.sendUssdRequest(ussdNum, new TelephonyManager.UssdResponseCallback() {
            @Override
            public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                Log.d(TAG, "[SUCCESS] REQUEST: " + request);
                Log.d(TAG, "[SUCCESS] RESPONSE: " + response.toString());

                // TODO: Show result in responseTextView and make edittext and button visible
                refreshVisibility("onSuccess", View.VISIBLE);
                textViewResponse.setText(response);

                super.onReceiveUssdResponse(telephonyManager, request, response);
            }

            @Override
            public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                Log.d(TAG, "[FAILED] REQUEST: " + request);
                Log.d(TAG, "[FAILED] RESPONSE: " + String.valueOf(failureCode));

                // TODO: Show error result in textView
                refreshVisibility("onFailed", View.VISIBLE);
                textViewResponse.setText(translateUssdError(failureCode));

                super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
            }
        }, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.d(TAG, "NOTE: Check for what is this handler...");
                return true;
            }
        }));
    }

    private String translateUssdError(int errorCode) {
        if (errorCode == TelephonyManager.USSD_RETURN_FAILURE) {
            return "USSD returned failed. Error code -1.";
        } else if (errorCode == TelephonyManager.USSD_ERROR_SERVICE_UNAVAIL) {
            return "USSD service unavailable. Error code -2.";
        } else {
            return "Unknown USSD error";
        }
    }

    private void refreshVisibility(String type, int visibility) {
        if (type == "onSuccess") {
            textViewResponse.setVisibility(visibility);
            editTextRequest.setVisibility(visibility);
            buttonSend.setVisibility(visibility);
            // NOTE: below should be hidden on success
            buttonRefresh.setVisibility(negateVisibility(visibility));
        } else if (type == "onFailed") {
            textViewResponse.setVisibility(visibility);
            buttonRefresh.setVisibility(visibility);
            // NOTE: below should be hidden on failed
            editTextRequest.setVisibility(negateVisibility(visibility));
            buttonSend.setVisibility(negateVisibility(visibility));
        } else {
            textViewResponse.setVisibility(visibility);
            editTextRequest.setVisibility(visibility);
            buttonSend.setVisibility(visibility);
            buttonRefresh.setVisibility(visibility);
        }
    }

    private int negateVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            return View.INVISIBLE;
        } else {
            return View.VISIBLE;
        }
    }
}
