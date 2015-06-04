package com.lpool.client.GameController;

/**
 * Created by André on 04/06/2015.
 */
public interface GameState {

    public static enum Value {
        WAIT,
        SHOOT,
        PLACE_BALL
    }

    public Value getValue();
    public void interrupt();
    public void start();
    public Boolean isActive();
}
