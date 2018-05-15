package com.ph.eapesa.ussdpoc;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by elixa.pesa on 10/05/2018.
 */

public class DialstringMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = DialstringMenuActivity.class.getSimpleName();
    private Button buttonMenu1, buttonMenu2, buttonMenu3;
    private Button buttonMenu2_1, buttonMenu2_2, buttonMenu3_1;
    private Button buttonMenu2_1_1, buttonMenu2_1_2;
    private LinearLayout menuGroup2, menuGroup3, menuGroup2Sub1;
    private TextView ussdResponse;

    private UssdSender ussdSender;

    private boolean menuGroup2State = false;
    private boolean menuGroup3State = false;
    private boolean menuGroup2Sub1State = false;
    private final int REQUEST_CODE_PERMISSION = 500;
    private final String DSTRING = "*118*07*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsmenu);

        buttonMenu1 = (Button) findViewById(R.id.dsmenu_button_1);
        buttonMenu2 = (Button) findViewById(R.id.dsmenu_button_2);
        buttonMenu3 = (Button) findViewById(R.id.dsmenu_button_3);
        buttonMenu2_1 = (Button) findViewById(R.id.dsmenu_button_2_1);
        buttonMenu2_2 = (Button) findViewById(R.id.dsmenu_button_2_2);
        buttonMenu3_1 = (Button) findViewById(R.id.dsmenu_button_3_1);
        buttonMenu2_1_1 = (Button) findViewById(R.id.dsmenu_button_2_2_1);
//        buttonMenu2_1_2 = (Button) findViewById(R.id.dsmenu_button_2_2_2);

        buttonMenu1.setOnClickListener(this);
        buttonMenu2.setOnClickListener(this);
        buttonMenu3.setOnClickListener(this);
        buttonMenu2_1.setOnClickListener(this);
        buttonMenu2_2.setOnClickListener(this);
        buttonMenu3_1.setOnClickListener(this);
        buttonMenu2_1_1.setOnClickListener(this);
//        buttonMenu2_1_2.setOnClickListener(this);

        menuGroup2 = (LinearLayout) findViewById(R.id.dsmenu_button2_group);
        menuGroup3 = (LinearLayout) findViewById(R.id.dsmenu_button3_group);
        menuGroup2Sub1 = (LinearLayout) findViewById(R.id.dsmenu_button2sub1_group);

        ussdResponse = (TextView) findViewById(R.id.dsmenu_textview_result);

        ussdSender = new UssdSender(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dsmenu_button_1:
                Log.d(TAG, "CLICKED MENU 1");
                sendUssd(DSTRING + "1#");
                break;
            case R.id.dsmenu_button_2:
                Log.d(TAG, "CLICKED MENU 2");
                if (!menuGroup2State) {
                    menuGroup2.setVisibility(View.VISIBLE);
                    buttonMenu2.setBackgroundColor(Color.LTGRAY);
                } else {
                    menuGroup2.setVisibility(View.GONE);
                    buttonMenu2.setBackgroundColor(Color.TRANSPARENT);
                }
                menuGroup2State = !menuGroup2State;
                break;
            case R.id.dsmenu_button_3:
                Log.d(TAG, "CLICKED MENU 3");
                if (!menuGroup3State) {
                    menuGroup3.setVisibility(View.VISIBLE);
                    buttonMenu3.setBackgroundColor(Color.LTGRAY);
                } else {
                    menuGroup3.setVisibility(View.GONE);
                    buttonMenu3.setBackgroundColor(Color.TRANSPARENT);
                }
                menuGroup3State = !menuGroup3State;
                break;
            case R.id.dsmenu_button_2_1:
                Log.d(TAG, "CLICKED MENU 2-1");
                sendUssd(DSTRING + "2*1#");
                break;
            case R.id.dsmenu_button_2_2:
                Log.d(TAG, "CLICKED MENU 2-2");
                if (!menuGroup2Sub1State) {
                    menuGroup2Sub1.setVisibility(View.VISIBLE);
                    buttonMenu2_2.setBackgroundColor(Color.parseColor("#E8E8E8"));
                } else {
                    menuGroup2Sub1.setVisibility(View.GONE);
                    buttonMenu2_2.setBackgroundColor(Color.TRANSPARENT);
                }
                menuGroup2Sub1State = !menuGroup2Sub1State;
                break;
            case R.id.dsmenu_button_3_1:
                Log.d(TAG, "CLICKED MENU 3-1");
                sendUssd(DSTRING + "3*1#");
                break;
            case R.id.dsmenu_button_2_2_1:
                Log.d(TAG, "CLICKED MENU 2-2-1");
                sendUssd(DSTRING + "2*2*1#");
                break;
        }
    }

    private void sendUssd(String text) {
        ussdSender.sendUssd(text, new UssdSender.Listener() {
            @Override
            public void onRequirePermission() {
                ActivityCompat.requestPermissions(DialstringMenuActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_PERMISSION);
                return;
            }

            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "[SUCCESS] RESPONSE: " + response.toString());
                // TODO: Show result in responseTextView and make edittext and button visible
                ussdResponse.setText(response.toString());
            }

            @Override
            public void onError(int failureCode) {
                Log.d(TAG, "[FAILED] RESPONSE: " + String.valueOf(failureCode));
                // TODO: Show error result in textView
                ussdResponse.setText(translateUssdError(failureCode));
            }
        });
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
}
