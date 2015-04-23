package com.lpool.client;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class GetIPActivity extends ActionBarActivity {

    private String ip_to_connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_ip);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_i, menu);
        return true;
    }

    public void connectToServerIp(View v)
    {
        EditText et1 = (EditText) findViewById(R.id.ipField);
        ip_to_connect = et1.getText().toString();
        if(isValidIP(ip_to_connect)) {
            ShotActivity.setServerIP(ip_to_connect);
            startActivity(new Intent(GetIPActivity.this, ShotActivity.class));
        }
        else
            Toast.makeText(this, "Connection Failed." + '\n' + "The IP entered is invalid.", Toast.LENGTH_SHORT).show();
    }

    public void readIPFromQR(View v)
    {
        IntentIntegrator integrator = new IntentIntegrator(GetIPActivity.this);
        integrator.addExtra("SCAN_WIDTH", 640);
        integrator.addExtra("SCAN_HEIGHT", 480);
        integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
        integrator.addExtra("PROMPT_MESSAGE", "Scan the code on the server PC");    // Initial scanning message
        integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String contents;
        if (result != null) {
            contents = result.getContents();
            if (contents == null) {
                Toast.makeText(this, "Unable to read QR Code", Toast.LENGTH_SHORT).show();
                return;
            }
            EditText et1 = (EditText) findViewById(R.id.ipField);
            et1.setText(contents);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
