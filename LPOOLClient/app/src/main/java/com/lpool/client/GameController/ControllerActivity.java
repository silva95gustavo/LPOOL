package com.lpool.client.GameController;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lpool.client.Network.Connector;
import com.lpool.client.Network.Receiver;
import com.lpool.client.Network.Utilities;
import com.lpool.client.R;

/**
 * Created by André on 03/06/2015.
 */
public class ControllerActivity extends Activity implements Receiver{

    private GameState currentState;
    private GameState states[];

    private Connector connector;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        states = new GameState[3];

        currentState = new WaitState(this);
        currentState.start();

        states[GameState.Value.WAIT.ordinal()] = currentState;
        states[GameState.Value.SHOOT.ordinal()] = new ShootState(this);
        //states[GameState.Value.PLACE_BALL.ordinal()] = new PlaceBallState(this);

        Bundle b = getIntent().getExtras();
        int server_port = b.getInt("port");
        String server_ip = b.getString("ip");

        if(!Utilities.isValidPort(server_port) || server_ip == null || !Utilities.isValidIP(server_ip))
        {
            Toast.makeText(this, "Invalid server parameters", Toast.LENGTH_SHORT).show();
            finish();
        }

        connector = new Connector(server_ip, server_port);
        connector.addReceiver(this);
    }


    public void temp_funct(View v) {
        // TODO remove
        switch (currentState.getValue()) {
            case WAIT:
                currentState.interrupt();
                currentState = states[GameState.Value.SHOOT.ordinal()];
                currentState.start();
                break;
            case SHOOT:
                currentState.interrupt();
                currentState = states[GameState.Value.WAIT.ordinal()];
                currentState.start();
                break;
            case PLACE_BALL:
                break;
        }
    }

    public void getMessage(String message) {
        // TODO continuar
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setState(GameState newState) {
        currentState.interrupt();
        currentState = newState;
        newState.start();
    }

    public GameState.Value currentValue() {
        return currentState.getValue();
    }

    public void switchStateValue(GameState.Value value) {
        // TODO
    }

    public Boolean sendUDPMessage(String message) {
        if(connector != null) {
            return connector.sendUDPMessage(message);
        }
        return false;
    }

    public Boolean sendTCPMessage(String message) {
        if(connector != null) {
            return connector.sendTCPMessage(message);
        }
        return false;
    }
}
