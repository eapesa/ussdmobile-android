package com.ph.eapesa.ussdpoc;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private Button buttonSync;
//    private KeyPairGenerator keyPairGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        generateKeys();

        buttonSync = (Button) findViewById(R.id.main_button_sync);
        buttonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent ussdIntent = new Intent(MainActivity.this, UssdActivity.class);
                    startActivity(ussdIntent);
                } else {
                    CharSequence invalidSdkVersion = "Your SDK version is below the required.";
                    Context context = getApplicationContext();
                    Toast errorToast = Toast.makeText(context, invalidSdkVersion, Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        });
    }


}
