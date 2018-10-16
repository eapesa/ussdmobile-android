package com.example.ussd.poc;

import android.Manifest;
import android.os.Bundle;
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

    private final int REQUEST_CODE_PERMISSION = 500;

    private TextView textViewResponse;
    private EditText editTextRequest;
    private Button buttonSendCustom;
    private Button buttonSend03;
    private Button buttonSend07;
    private Button buttonCounted;
    private Button buttonRefresh;

    private UssdSender ussdSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ussd);
        initializeLayout();
        ussdSender = new UssdSender(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            Log.d(TAG, "CALL_PHONE permission granted");
        }
    }

    @Override
    public void onClick(View view) {
        String ussdPrefix03 = new String("*118*03#");
        String ussdPrefix07 = new String("*118*07#");
        switch (view.getId()) {
            case R.id.ussd_button_03:
                Log.d(TAG,"CLICKED 03");
                clearEditText();
                sendUssd(ussdPrefix03);
                break;
            case R.id.ussd_button_07:
                Log.d(TAG, "CLICKED 07");
                clearEditText();
                sendUssd(ussdPrefix07);
                break;
            case R.id.ussd_button_counted:
                Log.d(TAG, "CLICKED MEASURED TEXT");
                int count = Integer.parseInt(editTextRequest.getText().toString());
                String measuredQuery = countedString(count);
                textViewResponse.setText("SENT (" + count +" chars): " + measuredQuery);
                sendUssd(measuredQuery);
                break;
            case R.id.ussd_button_custom:
                Log.d(TAG, "CLICKED CUSTOM");
                String query = editTextRequest.getText().toString();
                sendUssd(query);
                break;
            case R.id.ussd_button_refresh:
                Log.d(TAG, "Refresh button clicked.");
                clearEditText();
                break;
        }
    }

    private void initializeLayout() {
        textViewResponse = (TextView) findViewById(R.id.ussd_textview_response);
        editTextRequest = (EditText) findViewById(R.id.ussd_editview_request);
        buttonSend03 = (Button) findViewById(R.id.ussd_button_03);
        buttonSend07 = (Button) findViewById(R.id.ussd_button_07);
        buttonSendCustom = (Button) findViewById(R.id.ussd_button_custom);
        buttonRefresh = (Button) findViewById(R.id.ussd_button_refresh);
        buttonCounted = (Button) findViewById(R.id.ussd_button_counted);

        buttonSend03.setOnClickListener(this);
        buttonSend07.setOnClickListener(this);
        buttonSendCustom.setOnClickListener(this);
        buttonRefresh.setOnClickListener(this);
        buttonCounted.setOnClickListener(this);
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

    private void clearEditText() {
        textViewResponse.setText("");
    }

    private String multiplyString(String text, int count) {
        String textAll = "";
        for (int i = 0; i < count; i++) {
            textAll += text;
        }
        return textAll;
    }

    private String countedString(int count) {
        String textAll = "";
        String baseChars = "0123456789";
        int tens = count / 10;
        int excess = count % 10;

        for (int i = 0; i < tens; i++) {
            textAll += baseChars;
        }

        for (int j = 0; j < excess; j++) {
            textAll += String.valueOf(j);
        }

        return new String("*118*07*" + textAll + "#");
    }

    private void sendUssd(String text) {
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
                textViewResponse.setText(response.toString());
            }

            @Override
            public void onError(int failureCode) {
                Log.d(TAG, "[FAILED] RESPONSE: " + String.valueOf(failureCode));
                // TODO: Show error result in textView
                textViewResponse.setText(translateUssdError(failureCode));
            }
        });
    }
}
