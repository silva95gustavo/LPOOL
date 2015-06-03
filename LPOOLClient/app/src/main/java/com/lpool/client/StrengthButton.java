package com.lpool.client;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.concurrent.Callable;

/**
 * Created by André on 02/06/2015.
 */
public class StrengthButton implements View.OnTouchListener {
    private Button button;
    private RelativeLayout layout;
    private float minY;
    private float maxY;
    private float delta;
    private Thread animation_thread;
    private boolean  anim_running = true;
    private ShotActivity caller;
    private Boolean enabled = true;
    private float relativeForce;

    private int startColor[];
    private int endColor[];
    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;

    public StrengthButton(ShotActivity caller, Button button, RelativeLayout layout, float maxY, float minY, float delta) {
        this.caller = caller;
        this.button = button;
        this.layout = layout;
        this.maxY = maxY;
        this.minY = minY;
        this.delta = delta;
        relativeForce = 0;

        startColor = new int[] {0, 255, 0};
        endColor = new int[] {255, 0, 0};

        button.setOnTouchListener(this);
    }

    public float linear_interpolation(float min, float max, float pos){
        return (1 - pos) * min + pos * max;
    }

    public void start() {
        if(minY < maxY) {
            animation_thread = new Thread("StrengthAnimThread") {
                public void run(){
                    while(layout.getY() > minY && anim_running) {
                        final float interpolation = (maxY-layout.getY())/(maxY-minY);
                        relativeForce = interpolation;
                        final float newY = layout.getY() - delta;
                        final int newRed = Math.round(linear_interpolation(startColor[RED], endColor[RED], interpolation));
                        final int newGreen = Math.round(linear_interpolation(startColor[GREEN], endColor[GREEN], interpolation));
                        final int newBlue =  Math.round(linear_interpolation(startColor[BLUE], endColor[BLUE], interpolation));
                        final int newColor = Color.rgb(newRed, newGreen, newBlue);

                        caller.runOnUiThread(new Runnable() {
                            public void run() {
                                layout.setBackgroundColor(newColor);
                                layout.setY(newY);
                            }
                        });

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(layout.getY() <= minY)
                        layout.setY(minY);
                }
            };
        } else {
            animation_thread = new Thread("StrengthAnimThread") {
                public void run(){
                    while(layout.getY() < maxY && anim_running) {
                        layout.setY(layout.getY()+delta);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(layout.getY() >= maxY)
                        layout.setY(maxY);
                }
            };
        }
        animation_thread.start();
    }

    public void stop() {
        if(animation_thread != null)
            animation_thread.interrupt();
    }

    public void reset() {
        anim_running = false;
        stop();
        layout.setY(maxY);
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
        anim_running = false;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public boolean onTouch(View v, MotionEvent event) {

        switch ( event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                if(enabled && anim_running) {
                    anim_running = true;
                    layout.setY(maxY);
                    caller.startShot();
                    start();
                }
                break;
            case MotionEvent.ACTION_UP:
                if(anim_running && enabled)
                    caller.fireBall(relativeForce);
                anim_running = false;
                stop();
                break;
        }
        return true;
    }
}
