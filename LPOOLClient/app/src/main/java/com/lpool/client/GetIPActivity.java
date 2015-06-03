package com.lpool.client;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class GetIPActivity extends ActionBarActivity {

    private String ip_to_connect;
    private EditText ipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_ip);

        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ip_to_connect = "";
        ipText = (EditText) findViewById(R.id.ipField);
        updateIPLabel();

        ipText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                ip_to_connect = ipText.getText().toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ip_to_connect = ipText.getText().toString();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_i, menu);
        return true;
    }

    public void connectToServerIp(View v)
    {
        ip_to_connect = ipText.getText().toString();
        if(isValidIP(ip_to_connect)) {
            ShotActivity.setServerIP(ip_to_connect);
            startActivity(new Intent(GetIPActivity.this, ShotActivity.class));
        }
        else
            Toast.makeText(this, "Connection Failed." + '\n' + "The IP entered is invalid.", Toast.LENGTH_SHORT).show();
    }

    public void testAbout(View v)
    {
        startActivity(new Intent(GetIPActivity.this, InstructionsActivity.class));
    }

    public void readIPFromQR(View v)
    {
        IntentIntegrator.initiateScan(this, IntentIntegrator.QR_CODE_TYPES, "Please point the camera to the QR Code on the server application");
    }

    private void updateIPLabel()
    {
        ipText = (EditText) findViewById(R.id.ipField);
        if(ipText != null && ip_to_connect != null)
            ipText.setText(ip_to_connect);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String text = result.getContents().toString();

                if(isValidIP(text)) {
                    ShotActivity.setServerIP(text);
                    startActivity(new Intent(GetIPActivity.this, ShotActivity.class));
                }
                else
                    Toast.makeText(this, "Scanned: " + text, Toast.LENGTH_LONG).show();
                ip_to_connect = text;
                updateIPLabel();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean isValidIP(String ip)
    {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if(ip.endsWith(".")) {
                return false;
            }

            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings are currently unavailable", Toast.LENGTH_LONG).show();
            return true;
        } else if(id == R.id.action_instructions) {
            testAbout(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        /*int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            setContentView(R.layout.activity_get_ip);
        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_get_ip_landscape);
        }

        updateIPLabel();*/
    }
}
