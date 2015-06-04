package com.lpool.client.GameController;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lpool.client.Network.Connector;
import com.lpool.client.R;
import com.lpool.client.ToUpdate.StrengthButton;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by André on 04/06/2015.
 */
public class ShootState implements GameState, SensorEventListener {

    private Value value = Value.SHOOT;
    private Boolean active;
    private LinearLayout own_layout;
    private ControllerActivity caller;

    private static final int FPS = 20;
    private SensorManager sensorManager;
    private Sensor senAccelerometer;
    private boolean shooting = false;
    private float angle = (float)Math.PI;
    private float gravity[] = new float[3];
    private float accelerometerLast = 0;
    private long lastSensorReadTime = System.currentTimeMillis();

    private Button fire;
    private StrengthButton strengthButtonAnim;
    private RelativeLayout strength;

    public ShootState(ControllerActivity caller) {
        this.caller = caller;
        own_layout = (LinearLayout) caller.findViewById(R.id.fireLayout);
    }

    // TODO cenas de interacao

    private void initializeSensors() {
        sensorManager = (SensorManager) caller.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initializeAngleSender() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!shooting && active) {
                    caller.sendUDPMessage(Connector.ProtocolCmd.ANGLE.ordinal() + " " + angle + " " + '\n');
                }
            }
        }, 100, 1000 / FPS);
    }

    private void initializeElements() {
        fire = (Button) caller.findViewById(R.id.fireButton);
        strength = (RelativeLayout) caller.findViewById(R.id.strengthBar);
        strength.setY(700);

        //final ShootState caller = this;

        /*fire.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d("", "changed");
                strengthButtonAnim.stop();
                strengthButtonAnim.reset();
                strengthButtonAnim.disable();
                strengthButtonAnim = new StrengthButton(caller, fire, strength, fire.getY(), 0, 4);
                strength.setY(fire.getY());
            }
        });
        strengthButtonAnim = new StrengthButton(caller, fire, strength, fire.getY(), 0, 4);*/

        final ImageView cueBallPlace = (ImageView) caller.findViewById(R.id.cueBallPlacable);
        final RelativeLayout placeBall = (RelativeLayout) caller.findViewById(R.id.tableandball);

        placeBall.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                cueBallPlace.setX(event.getX());
                cueBallPlace.setY(event.getY());
                return true;
            }
        });

        initializeAiming();
    }

    private void initializeAiming() {
        final ImageView cueBall = (ImageView) caller.findViewById(R.id.cueBall);
        final RelativeLayout horizontal = (RelativeLayout) caller.findViewById(R.id.horizontalLine);
        final RelativeLayout vertical = (RelativeLayout) caller.findViewById(R.id.verticalLine);

        final View touchView = caller.findViewById(R.id.center);
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float pointX = event.getX();
                float pointY = event.getY();
                float centerX = cueBall.getX() + cueBall.getWidth() / 2;
                float centerY = cueBall.getY() + cueBall.getHeight() / 2;
                float radius = (float) 0.65 * cueBall.getWidth() / 2;

                float dist = (float) Math.sqrt((pointX - centerX) * (pointX - centerX) + (pointY - centerY) * (pointY - centerY));

                if (dist < radius) {
                    vertical.setX(pointX - vertical.getWidth() / 2);
                    horizontal.setY(pointY - horizontal.getHeight() / 2);
                } else {
                    float closest_x = centerX + radius * (pointX - centerX) / dist;
                    float closest_y = centerY + radius * (pointY - centerY) / dist;
                    vertical.setX(closest_x - vertical.getWidth() / 2);
                    horizontal.setY(closest_y - horizontal.getHeight() / 2);
                }

                return true;
            }
        });
    }

    public void startShot() {

    }

    public void fireBall(float relativeStrength) {

    }

    public boolean onTouchEvent(MotionEvent event){

        /*if (action == MotionEvent.ACTION_DOWN) {
            startTime = System.currentTimeMillis();
            shooting = true;
            return true;
        }*/
        return true;
    }

    protected void onResume() {
        //super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        // unregister listener
        //super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event)
    {
        System.out.println("new angle");
        if (shooting)
            return;
        System.out.println("valid shot");
        float[] g = new float[3];
        g = event.values.clone();

        float norm_Of_g = (float)Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

        final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        float linear_acceleration[] = new float[3];
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        accelerometerLast = (float)Math.sqrt(linear_acceleration[0] * linear_acceleration[0] + linear_acceleration[1] * linear_acceleration[1] + linear_acceleration[2] * linear_acceleration[2]);

        // Normalize the accelerometer vector
        g[0] = g[0] / norm_Of_g;
        g[1] = g[1] / norm_Of_g;
        g[2] = g[2] / norm_Of_g;

        float rotation = (float)(Math.atan2(g[0], g[1]) - Math.PI / 2);
        angle -= (float)(rotation * Math.abs(rotation) * (System.currentTimeMillis() - lastSensorReadTime) * 0.02f);
        lastSensorReadTime = System.currentTimeMillis();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void interrupt() {
        sensorManager.unregisterListener(this);
        own_layout.setVisibility(View.INVISIBLE);
        active = false;
    }

    public void start() {
        own_layout.setVisibility(View.VISIBLE);
        initializeSensors();
        initializeAngleSender();
        initializeElements();
        sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        active = true;
    }

    public Boolean isActive() {
        return active;
    }

    public Value getValue() {
        return value;
    }
}
