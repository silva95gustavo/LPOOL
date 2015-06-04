package com.lpool.client.GameController;

import android.view.View;
import android.widget.RelativeLayout;

import com.lpool.client.R;

/**
 * Created by André on 04/06/2015.
 */
public class WaitState implements GameState {

    private Value value = Value.WAIT;
    private Boolean active;
    private RelativeLayout own_layout;
    private ControllerActivity caller;

    public WaitState(ControllerActivity caller) {
        this.caller = caller;
        own_layout = (RelativeLayout) caller.findViewById(R.id.waitLayout);
        active = false;
    }

    public void start() {
        own_layout.setVisibility(View.VISIBLE);
        active = true;
    }

    public void interrupt() {
        own_layout.setVisibility(View.INVISIBLE);
        active = false;
    }

    public Value getValue() {
        return value;
    }

    public Boolean isActive() {
        return active;
    }
}
