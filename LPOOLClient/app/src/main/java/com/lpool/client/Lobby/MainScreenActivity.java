package com.lpool.client.Lobby;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lpool.client.GameController.ControllerActivity;
import com.lpool.client.Network.Utilities;
import com.lpool.client.R;

/**
 * Created by André on 03/06/2015.
 */
public class MainScreenActivity extends ActionBarActivity {

    private String server_ip;
    private int server_port;
    private EditText server_ip_text;
    private EditText server_port_text;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        server_ip = "";
        server_port = 0;
        server_ip_text = (EditText) findViewById(R.id.ipField);
        server_port_text = (EditText) findViewById(R.id.portField);
        updateLabels();

        server_ip_text.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                server_ip = server_ip_text.getText().toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                server_ip = server_ip_text.getText().toString();
            }
        });

        server_port_text.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(server_port_text.getText() != null && !server_port_text.getText().equals("") )
                    server_port = Integer.parseInt(server_port_text.getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(server_port_text.getText() != null && !server_port_text.getText().equals("") )
                    server_port = Integer.parseInt(server_port_text.getText().toString());
            }
        });
    }

    private void updateLabels() {
        server_ip_text = (EditText) findViewById(R.id.ipField);
        if(server_ip_text != null && server_ip != null)
            server_ip_text.setText(server_ip);
        server_port_text = (EditText) findViewById(R.id.portField);
        if(server_port_text != null && server_port != 0)
            server_port_text.setText("" + server_port);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_i, menu);
        return true;
    }

    public void connectToServer(View v)
    {
        server_ip = server_ip_text.getText().toString();
        if(Utilities.isValidIP(server_ip) && Utilities.isValidPort(server_port)) {
            Intent intent = new Intent(MainScreenActivity.this, ControllerActivity.class);
            Bundle params = new Bundle();
            params.putInt("port", server_port);
            params.putString("ip", server_ip);
            intent.putExtras(params);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "Connection Failed." + '\n' + "The IP entered is invalid.", Toast.LENGTH_SHORT).show();
    }

    public void openInstructions(View v) {
        startActivity(new Intent(MainScreenActivity.this, InstructionsActivity.class));
    }

    public void readInfoFromQR(View v) {
        IntentIntegrator.initiateScan(this, IntentIntegrator.QR_CODE_TYPES,
                "Please point the camera to the QR Code on the server application." +
                        '\n' + "     If the camera stays black hit the back button and try again.");
    }

    @Override           // Get QR code scanning result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String text = result.getContents().toString();

                String[] lines = text.split(System.getProperty("line.separator"));

                if(lines.length == 2) {
                    String ip = lines[0];
                    String port_str = lines[1];
                    int port = Integer.parseInt(port_str);

                    if (Utilities.isValidIP(ip) && Utilities.isValidPort(port)) {
                        server_ip = ip;
                        server_port = port;
                        updateLabels();
                        connectToServer(null);
                    } else
                        Toast.makeText(this, "Scanned: " + text, Toast.LENGTH_LONG).show();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // TODO Menu de settings maybe?
            Toast.makeText(this, "Settings are currently unavailable", Toast.LENGTH_LONG).show();
            return true;
        } else if(id == R.id.action_instructions) {
            openInstructions(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
}
