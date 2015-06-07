package com.lpool.client.GameController;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lpool.client.Network.Connector;
import com.lpool.client.R;

/**
 * Created by André on 05/06/2015.
 */
public class PlaceBallState implements GameState {

    private Value value = Value.PLACE_BALL;
    private Boolean active;
    private LinearLayout own_layout;
    private ControllerActivity caller;
    private float ballX = (float)0.5;
    private float ballY = (float)0.5;

    private static final float xProportionLimit = (float)0.051;
    private static final float yProportionLimit = (float)0.091;

    public PlaceBallState(ControllerActivity caller) {
        this.caller = caller;
        own_layout = (LinearLayout) caller.findViewById(R.id.placeBallLayout);
    }

    private void initializeElements() {
        final ImageView ball = (ImageView) caller.findViewById(R.id.cueBallPlacable);
        final ImageView table = (ImageView) caller.findViewById(R.id.poolTable);

        table.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                final float x = event.getX();
                final float y = event.getY();
                System.out.println("Touched " + x + " " + y);


                if((x - ball.getWidth()/2) >= table.getX() + xProportionLimit*table.getWidth()
                        && (x + ball.getWidth()/2) <= table.getX()+table.getWidth()-xProportionLimit*table.getWidth()
                        && (y-ball.getHeight()/2) >= table.getY()+yProportionLimit*table.getHeight()
                        && (y+ball.getHeight()/2) <= table.getY()+table.getHeight()-yProportionLimit*table.getHeight()
                        )
                {
                    ballX = (x-ball.getWidth()/2-table.getX()-xProportionLimit*table.getWidth())/(table.getWidth()-ball.getWidth()-xProportionLimit*table.getWidth()*2);
                    ballY = (y-ball.getHeight()/2-table.getY()-yProportionLimit*table.getHeight())/(table.getHeight()-ball.getHeight()-yProportionLimit*table.getHeight()*2);

                    caller.runOnUiThread(new Runnable() {
                        public void run() {
                            ball.setX(x-ball.getWidth()/2);
                            ball.setY(y-ball.getHeight()/2);
                        }
                    });

                    moveCueBall();

                }

                /*if(x >= 0 + xProportionLimit*table.getWidth() &&
                        x <= table.getX() + table.getWidth() - ball.getWidth() - xProportionLimit*table.getWidth() &&
                        y >= 0 + yProportionLimit*table.getHeight() &&
                        y <= table.getY() + table.getHeight() - ball.getHeight() - yProportionLimit*table.getHeight()) {

                    ballX = (x)/(table.getWidth() - ball.getWidth());
                    ballY = (y)/(table.getHeight() - ball.getHeight());

                    ballX = (x+ball.getWidth()/2)/(table.getWidth()-ball.getWidth());
                    ballY = (y+ball.getHeight()/2)/(table.getHeight()-ball.getHeight());


                    caller.runOnUiThread(new Runnable() {
                        public void run() {
                            ball.setX(ballX*table.getWidth()-ball.getWidth()/2);
                            ball.setY(ballY*table.getHeight()-ball.getHeight()/2);
                        }
                    });

                    moveCueBall();
                }*/
                return true;
            }
        });

        final Button placeButton = (Button) caller.findViewById(R.id.placeBallButton);
        placeButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                placeCueBall(v);
            }
        });
    }

    private void moveCueBall() {
        System.out.println("Moving to: " + ballX + " " + ballY);
        caller.sendTCPMessage("" + Connector.ProtocolCmd.MOVECB.ordinal() + " " + ballX + " " + ballY + " " + '\n');
    }

    public void placeCueBall(View v) {
        System.out.println("Placing ball on: " + ballX + " " + ballY);
        caller.sendTCPMessage("" + Connector.ProtocolCmd.PLACECB.ordinal() + " " + ballX + " " + ballY + " " + '\n');
    }

    public void onPause() {}

    public void onResume() {}

    public Boolean isSameAsCmd(Connector.ProtocolCmd cmd) {
        return (cmd == Connector.ProtocolCmd.BIH);
    }

    public Value getValue() {
        return value;
    }

    public Boolean isActive() {
        return active;
    }

    public void interrupt() {
        caller.runOnUiThread(new Runnable() {
            public void run() {
                own_layout.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void start() {
        caller.runOnUiThread(new Runnable() {
            public void run() {
                own_layout.setVisibility(View.VISIBLE);
            }
        });
        ballX = (float)0.5;
        ballY = (float)0.5;
        final ImageView cueBallPlace = (ImageView) caller.findViewById(R.id.cueBallPlacable);
        final RelativeLayout placeBall = (RelativeLayout) caller.findViewById(R.id.tableandball);
        initializeElements();
        caller.runOnUiThread(new Runnable() {
            public void run() {
                cueBallPlace.setX(ballX*placeBall.getWidth()-cueBallPlace.getWidth()/2);
                cueBallPlace.setY(ballY*placeBall.getHeight()-cueBallPlace.getHeight()/2);
            }
        });
        // TODO reset ball position
    }
}
