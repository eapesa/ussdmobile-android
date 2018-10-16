package com.example.ussd.poc;

import android.Manifest;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by elixa.pesa on 15/05/2018.
 */

public class DynamicMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = DynamicMenuActivity.class.getSimpleName();
    private final int REQUEST_CODE_PERMISSION = 500;
    private final String INIT_MENU = new String("*118*07*1#");

    private UssdSender ussdSender;

    private TextView responseTextView;
    private TextView headerTextView;
    private LinearLayout container;

    private Dialstring ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamicmenu);

        container = (LinearLayout) findViewById(R.id.dynamicmenu_container_menu);
        headerTextView = (TextView) findViewById(R.id.dynamicmenu_textview_menu);
        responseTextView = (TextView) findViewById(R.id.dynamicmenu_textview_result);

        ussdSender = new UssdSender(this);
        ds = new Dialstring(new String("*118*07*1"));

        sendUssd(INIT_MENU);
    }

    private void sendUssd(String text) {
        ussdSender.sendUssd(text, new UssdSender.Listener() {
            @Override
            public void onRequirePermission() {
                ActivityCompat.requestPermissions(DynamicMenuActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_PERMISSION);
                return;
            }

            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "[SUCCESS] RESPONSE: " + response.toString());
                // TODO: Show result in responseTextView and make edittext and button visible
                classifyResponse(response.toString());
            }

            @Override
            public void onError(int failureCode) {
                Log.d(TAG, "[FAILED] RESPONSE: " + String.valueOf(failureCode));
                // TODO: Show error result in textView
                responseTextView.setText(translateUssdError(failureCode));
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

    private void classifyResponse(String code) {
        String[] syntax = code.split(":");
        String type = syntax[0];
        if (type.equals("text")) {
            headerTextView.setText(ds.getHeader());
            responseTextView.setText(syntax[1]);
        } else {
            headerTextView.setText(ds.getHeader());
            String[] menuLabels = syntax[1].split("\\|");
            for (int i = 0; i < menuLabels.length; i++) {
                Button menuButton = new Button(this);
                menuButton.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                menuButton.setId(i);
                menuButton.setText(menuLabels[i]);
                menuButton.setTag(romanToIntString(menuLabels[i].split("-")[1]));
                menuButton.setOnClickListener(this);
                container.addView(menuButton);
            }
        }
    }

    private String romanToIntString(String code) {
        switch (code) {
            case "I":
                return "1";
            case "II":
                return "2";
            case "III":
                return "3";
            case "IV":
                return "4";
            case "V":
                return "5";
            case "VI":
                return "6";
            case "VII":
                return "7";
            case "VIII":
                return "8";
            case "IX":
                return "9";
            case "X":
                return "10";
        }
        return "0";
    }

    @Override
    public void onClick(View v) {
        Button pressedButton = (Button) v;
        String headerText = pressedButton.getText().toString();
        String menuId = v.getTag().toString();
        ds.update(menuId, headerText);
        String finalDs = ds.getDialstring();
        sendUssd(finalDs);

        if(container.getChildCount() > 0) {
            container.removeAllViews();
        }
    }

    private class Dialstring {
        private String ds;
        private String dsOld;
        private String header;
        private String headerOld;
        public Dialstring(String code) {
            ds = code;
            header = "Initial Menu";
        }
        public void update(String code, String headerString) {
            dsOld = ds;
            ds = ds + "*" + code;
            if (header.equals("Initial Menu")) {
                header = headerString;
            } else {
                header = header + " > " + headerString;
            }
        }
        public void revert() {
            ds = dsOld;
            header = headerOld;
        }
        public String getDialstring() {
            return ds + "#";
        }
        public String getHeader() {
            return header;
        }
    }
}
