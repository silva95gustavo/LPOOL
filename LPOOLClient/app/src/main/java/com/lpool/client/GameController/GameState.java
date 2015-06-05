package com.lpool.client.GameController;

import com.lpool.client.Network.Connector;

/**
 * Created by André on 04/06/2015.
 */
public interface GameState {

    public static enum Value {
        WAIT,
        SHOOT,
        PLACE_BALL,
        END
    }

    public Value getValue();
    public void interrupt();
    public void start();
    public Boolean isActive();
    public void onPause();
    public void onResume();
    public Boolean isSameAsCmd(Connector.ProtocolCmd cmd);
}
