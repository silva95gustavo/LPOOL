package ga.lpool.client.GameController;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ga.lpool.client.Network.Connector;
import ga.lpool.client.R;

/**
 * Created by André on 04/06/2015.
 */
public class WaitState implements GameState {

    private Value value = Value.WAIT;
    private Boolean active;
    private RelativeLayout own_layout;
    private ControllerActivity caller;
    private Thread anim = null;

    public WaitState(ControllerActivity caller) {
        this.caller = caller;
        own_layout = (RelativeLayout) caller.findViewById(R.id.waitLayout);
        active = false;
    }

    private void startTextAnim() {
        if(anim != null)
            anim.interrupt();

        final TextView txt = (TextView) caller.findViewById(R.id.wait_text_dots);
        final String dot1 = caller.getResources().getString(R.string.wait_text_dot1);
        final String dot2 = caller.getResources().getString(R.string.wait_text_dot2);
        final String dot3 = caller.getResources().getString(R.string.wait_text_dot3);
        anim = new Thread( new Runnable() {
            public void run() {
                while(active) {
                    caller.runOnUiThread(new Runnable() {
                        public void run() {
                            txt.setText(dot1);
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch(InterruptedException e) {}
                    caller.runOnUiThread(new Runnable() {
                        public void run() {
                            txt.setText(dot2);
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch(InterruptedException e) {}
                    caller.runOnUiThread(new Runnable() {
                        public void run() {
                            txt.setText(dot3);
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch(InterruptedException e) {}
                }
            }
        });
        anim.start();
    }

    private void stopTextAnim() {
        if(anim != null) {
            anim.interrupt();
            anim = null;
        }
    }

    public void start() {
        caller.runOnUiThread(new Runnable() {
            public void run() {
                own_layout.setVisibility(View.VISIBLE);
            }
        });
        startTextAnim();
        active = true;
    }

    public void interrupt() {
        stopTextAnim();
        caller.runOnUiThread(new Runnable() {
            public void run() {
                own_layout.setVisibility(View.INVISIBLE);
            }
        });
        active = false;
    }

    public Value getValue() {
        return value;
    }

    public Boolean isActive() {
        return active;
    }

    public void onPause() {stopTextAnim();}

    public void onResume() {startTextAnim();}

    public Boolean isSameAsCmd(Connector.ProtocolCmd cmd) {
        return (cmd == Connector.ProtocolCmd.WAIT);
    }
}
