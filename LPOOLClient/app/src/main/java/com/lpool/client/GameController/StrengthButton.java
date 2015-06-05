package com.lpool.client.GameController;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by André on 04/06/2015.
 */
public class StrengthButton implements View.OnTouchListener {

    private ControllerActivity caller_activity;
    private ShootState shooter;
    private Button trigger;
    private RelativeLayout strength_bar;
    private float maxY;
    private float minY;
    private float delta;
    private float relativeForce;
    private Thread animation_thread;
    private boolean  anim_running = false;

    private int startColor[];
    private int endColor[];
    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;

    public StrengthButton(ControllerActivity activity, ShootState shooter, Button trigger, RelativeLayout strength_bar, float maxY, float minY, float delta) {
        this.caller_activity = activity;
        this.shooter = shooter;
        this.trigger = trigger;
        this.strength_bar = strength_bar;
        this.maxY = maxY;
        this.minY = minY;
        this.delta = delta;
        relativeForce = 0;

        startColor = new int[] {0, 255, 0};
        endColor = new int[] {255, 0, 0};

        trigger.setOnTouchListener(this);
    }

    public void start() {
        anim_running = true;
        makeVisible();
        if(minY < maxY) {
            animation_thread = new Thread("StrengthAnimThread") {
                public void run(){
                    while(strength_bar.getY() > minY && anim_running) {
                        final float interpolation = (maxY-strength_bar.getY())/(maxY-minY);
                        relativeForce = interpolation;
                        final float newY = strength_bar.getY() - delta;
                        final int newRed = Math.round(linear_interpolation(startColor[RED], endColor[RED], interpolation));
                        final int newGreen = Math.round(linear_interpolation(startColor[GREEN], endColor[GREEN], interpolation));
                        final int newBlue =  Math.round(linear_interpolation(startColor[BLUE], endColor[BLUE], interpolation));
                        final int newColor = Color.rgb(newRed, newGreen, newBlue);

                        if(anim_running) {
                            caller_activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    strength_bar.setBackgroundColor(newColor);
                                    strength_bar.setY(newY);
                                }
                            });
                        }

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(strength_bar.getY() <= minY) {
                        caller_activity.runOnUiThread(new Runnable() {
                            public void run() {
                                strength_bar.setY(minY);
                            }
                        });
                        relativeForce = 1;
                    }
                }
            };
            animation_thread.start();
        }
    }

    public void restart() {
        stop();
        start();
    }

    public void stop() {
        anim_running = false;
        makeInvisible();
        if(animation_thread != null)
            animation_thread.interrupt();

        caller_activity.runOnUiThread(new Runnable() {
            public void run() {
                strength_bar.setY(maxY);
            }
        });
    }

    public static float linear_interpolation(float min, float max, float pos){
        return (1 - pos) * min + pos * max;
    }

    public void resetPosition() {
        caller_activity.runOnUiThread(new Runnable() {
            public void run() {
                strength_bar.setY(maxY);
            }
        });
        makeInvisible();
    }

    public boolean onTouch(View v, MotionEvent event) {

        switch ( event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                restart();
                shooter.startShot();
                break;
            case MotionEvent.ACTION_UP:
                stop();
                float x = event.getX();
                float y = event.getY();
                if(x >= 0 && x <= trigger.getWidth() && y >= 0 && y <= trigger.getHeight())
                    shooter.fireBall(relativeForce);
                else
                    shooter.stopShot();
                makeInvisible();
                caller_activity.runOnUiThread(new Runnable() {
                    public void run() {
                        strength_bar.setY(maxY);
                    }
                });
                break;
        }
        return true;
    }

    private void makeInvisible() {
        caller_activity.runOnUiThread(new Runnable() {
            public void run() {
                strength_bar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void makeVisible() {
        caller_activity.runOnUiThread(new Runnable() {
            public void run() {
                strength_bar.setVisibility(View.VISIBLE);
            }
        });
    }
}
