package com.ph.eapesa.ussdpoc;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = MainActivity.class.getSimpleName();
    private Button buttonSync;
    private Button buttonOriginal;
    private Button buttonDynamicMenu;
    private Button buttonDsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOriginal = (Button) findViewById(R.id.main_button_original);
        buttonDynamicMenu = (Button) findViewById(R.id.main_button_dynamicmenu);
        buttonDsMenu = (Button) findViewById(R.id.main_button_dsmenu);

        buttonOriginal.setOnClickListener(this);
        buttonDynamicMenu.setOnClickListener(this);
        buttonDsMenu.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            switch (v.getId()) {
                case R.id.main_button_original:
                    Intent originalIntent = new Intent(MainActivity.this, UssdActivity.class);
                    startActivity(originalIntent);
                    break;
                case R.id.main_button_dynamicmenu:
                    Intent dynamicMenuIntent = new Intent(MainActivity.this, DynamicMenuActivity.class);
                    startActivity(dynamicMenuIntent);
                    break;
                case R.id.main_button_dsmenu:
                    Intent dsMenuIntent = new Intent(MainActivity.this, DialstringMenuActivity.class);
                    startActivity(dsMenuIntent);
                    break;
            }
        } else {
            CharSequence invalidSdkVersion = "Your SDK version is below the required.";
            Toast errorToast = Toast.makeText(getApplicationContext(), invalidSdkVersion,
                    Toast.LENGTH_SHORT);
            errorToast.show();
        }
    }
}
