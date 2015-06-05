package com.lpool.client.GameController;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

        states[GameState.Value.WAIT.ordinal()] = new WaitState(this);
        states[GameState.Value.SHOOT.ordinal()] = new ShootState(this);
        states[GameState.Value.PLACE_BALL.ordinal()] = new PlaceBallState(this);

        currentState = states[GameState.Value.WAIT.ordinal()];
        currentState.start();

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
        connector.sendTCPMessage("" + Connector.ProtocolCmd.JOIN.ordinal() + " " + '\n');
    }


    public void temp_funct(View v) {
        // TODO remove
        sendTCPMessage("" + Connector.ProtocolCmd.JOIN.ordinal() + " " + '\n');
        // TODO remove
        switch (currentState.getValue()) {
            case WAIT:
                currentState.interrupt();
                currentState = states[GameState.Value.SHOOT.ordinal()];
                currentState.start();
                break;
            case SHOOT:
                currentState.interrupt();
                currentState = states[GameState.Value.PLACE_BALL.ordinal()];
                currentState.start();
                break;
            case PLACE_BALL:
                currentState.interrupt();
                currentState = states[GameState.Value.WAIT.ordinal()];
                currentState.start();
                break;
        }
    }

    public void getMessage(String message) {
        System.out.println("Got message " + message);
        GameCommand cmd = new GameCommand(message);
        if(cmd.getCmd() == null) return;

        System.out.println("Received command " + cmd.getCmd());

        if(!currentState.isSameAsCmd(cmd.getCmd())) {
            currentState.interrupt();
            switch (cmd.getCmd()) {
                case WAIT:
                    currentState = states[GameState.Value.WAIT.ordinal()];
                    break;
                case PLAY:
                    currentState = states[GameState.Value.SHOOT.ordinal()];
                    break;
                case BIH:
                    currentState = states[GameState.Value.PLACE_BALL.ordinal()];
                    break;
                case END:
                    currentState = states[GameState.Value.WAIT.ordinal()];
                    break;
                default:
                    currentState = states[GameState.Value.WAIT.ordinal()];
                    break;
            }
            currentState.start();
        }

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
        if(value != currentState.getValue()) {
            currentState.interrupt();
            currentState = states[value.ordinal()];
            currentState.start();
        }
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

    protected void onPause() {
        super.onPause();
        currentState.onPause();
    }

    protected void onResume() {
        super.onResume();
        currentState.onResume();
    }

    public void terminate() {
        currentState.interrupt();
        connector.disconnect();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        terminate();
        /*Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);*/
    }
}
