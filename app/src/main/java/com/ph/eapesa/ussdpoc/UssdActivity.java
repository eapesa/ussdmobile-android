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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by elixa.pesa on 03/04/2018.
 */

public class UssdActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = UssdActivity.class.getSimpleName();

    private int REQUEST_CODE_PERMISSION = 500;

    private TextView textViewResponse;
    private EditText editTextRequest;
    private Button buttonSend;
    private Button buttonRefresh;

//    private final String ussdNum = "*121*4*1#";
    private final String ussdNum = "*118*07#";

    private UssdSender ussdSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ussd);

        initializeLayout();

        ussdSender = new UssdSender(this);
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
                String query = editTextRequest.getText().toString();
                Log.d(TAG, "EDIT TEXT: " + query);
                sendUssd("custom", query);
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
        String ussdString = new String("*118*07#");
        sendUssd("init", ussdString);
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

    private String multiplyString(String text, int count) {
        String textAll = "";
        for (int i = 0; i < count; i++) {
            textAll += text;
        }
        return textAll;
    }

    private void sendUssd(String type, String text) {
        final String TYPE = type;
        ussdSender.sendUssd(text, new UssdSender.Listener() {
            @Override
            public void onRequirePermission() {
                ActivityCompat.requestPermissions(UssdActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_PERMISSION);
                return;
            }

            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "[SUCCESS] RESPONSE: " + response.toString());
                // TODO: Show result in responseTextView and make edittext and button visible
                refreshVisibility("onSuccess", View.VISIBLE);
                textViewResponse.setText(response);
            }

            @Override
            public void onError(int failureCode) {
                Log.d(TAG, "[FAILED] RESPONSE: " + String.valueOf(failureCode));
                // TODO: Show error result in textView

                if (TYPE == "init") {
                    refreshVisibility("onSuccess", View.VISIBLE);
                    textViewResponse.setText("INITIALIZATION SUCCESSFUL!");
                } else {
                    refreshVisibility("onFailed", View.VISIBLE);
                    textViewResponse.setText(translateUssdError(failureCode));
                }
            }
        });
    }
}
