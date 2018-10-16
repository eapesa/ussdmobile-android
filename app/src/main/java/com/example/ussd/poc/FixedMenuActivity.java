package com.example.ussd.poc;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by elixa.pesa on 15/05/2018.
 */

public class FixedMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = FixedMenuActivity.class.getSimpleName();
    private final int REQUEST_CODE_PERMISSION = 500;
    private final String INIT_MENU = new String("*118*07*0#");

    private TextView headerTextView, responseTextView;
    private LinearLayout containerLayout;

    private UssdSender ussdSender;
    private Dialstring ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixedmenu);

        headerTextView = (TextView) findViewById(R.id.fixedmenu_textview_menu);
        responseTextView = (TextView) findViewById(R.id.fixedmenu_textview_result);
        containerLayout = (LinearLayout) findViewById(R.id.fixedmenu_container_menu);

        ussdSender = new UssdSender(this);
        ds = new Dialstring(new String("*118*07*1"));
        headerTextView.setText(ds.getHeader());
        sendUssd(INIT_MENU);
    }

    private void sendUssd(String text) {
        ussdSender.sendUssd(text, new UssdSender.Listener() {
            @Override
            public void onRequirePermission() {
                ActivityCompat.requestPermissions(FixedMenuActivity.this,
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
            responseTextView.setText(syntax[1]);
        } else {
            buildMenu(syntax[1].split("\\|"), containerLayout);
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

    private void buildMenu(String[] menu, LinearLayout container) {
        for (String m : menu) {
            String[] submenu = m.split(">", 2);
            String label = submenu[0];
            String id = String.valueOf(romanToIntString(label.split("-")[1]));

            MenuButton menuButton = new MenuButton(this, label);
            menuButton.setTextSize(20);

            if (submenu.length > 1) {
                String sublabels = submenu[1];
                menuButton.setTag(id + ":" + sublabels);
            }

            menuButton.setOnClickListener(this);
            container.addView(menuButton);
        }
    }

    @Override
    public void onClick(View v) {
        Button pressedButton = (Button) v;
        String headerText = pressedButton.getText().toString();
        String[] menuTag = v.getTag().toString().split(":");

        ds.update(menuTag[0], headerText);
        headerTextView.setText(ds.getHeader());

        if (menuTag.length == 1) {
            pressedButton.setEnabled(false);
            String finalDs = ds.getDialstring();
            sendUssd(finalDs);
        } else {
            if(containerLayout.getChildCount() > 0) {
                containerLayout.removeAllViews();
            }
            buildMenu(new String[]{menuTag[1]}, containerLayout);
        }
    }

    private class MenuButton extends android.support.v7.widget.AppCompatButton {
        private String buttonLabel;
        public MenuButton(Context context, String label) {
            super(context);
            buttonLabel = label;
            this.setText(buttonLabel);
            this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            this.setTag(romanToIntString(buttonLabel.split("-")[1]));
        }

        public void setTextSize(int textSize) {
            this.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
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
