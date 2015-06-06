package com.lpool.client.GameController;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lpool.client.Network.Connector;
import com.lpool.client.R;

import org.w3c.dom.Text;

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
    //private boolean shooting = false;
    private boolean paused = false;
    private boolean interrupted = true;
    private boolean shot_active = false;
    private float angle = (float)Math.PI;
    private float gravity[] = new float[3];
    private float accelerometerLast = 0;
    private long lastSensorReadTime = System.currentTimeMillis();
    private float spinX = 0;
    private float spinY = 0;

    private Button fire;
    private StrengthButton strengthButtonAnim;
    private RelativeLayout strength;

    public ShootState(ControllerActivity caller) {
        this.caller = caller;
        own_layout = (LinearLayout) caller.findViewById(R.id.fireLayout);
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) caller.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initializeAngleSender() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!paused && !interrupted && active && !shot_active) {
                    caller.sendUDPMessage(Connector.ProtocolCmd.ANGLE.ordinal() + " " + angle + " " + '\n');
                }
            }
        }, 100, 1000 / FPS);
    }

    private void initializeElements() {
        fire = (Button) caller.findViewById(R.id.fireButton);
        strength = (RelativeLayout) caller.findViewById(R.id.strengthBar);

        caller.runOnUiThread(new Runnable() {
            public void run() {
                strength.setY(700);
            }
        });

        final ShootState shooter = this;
        strengthButtonAnim = new StrengthButton(caller, shooter, fire, strength, fire.getY(), own_layout.getY(), 5);
        fire.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                caller.runOnUiThread(new Runnable() {
                    public void run() {
                        strength.setY(fire.getY());
                    }
                });
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
                float finalX;
                float finalY;

                if (dist < radius) {
                    finalX = pointX - vertical.getWidth() / 2;
                    finalY = pointY - horizontal.getHeight() / 2;
                } else {
                    float closest_x = centerX + radius * (pointX - centerX) / dist;
                    float closest_y = centerY + radius * (pointY - centerY) / dist;
                    finalX = closest_x - vertical.getWidth() / 2;
                    finalY = closest_y - horizontal.getHeight() / 2;
                }

                spinX = (finalX + vertical.getWidth()/2 - centerX)/radius;
                spinY = -(finalY + horizontal.getHeight()/2 - centerY)/radius;

                final float xVal = finalX;
                final float yVal = finalY;

                caller.runOnUiThread(new Runnable() {
                    public void run() {
                        vertical.setX(xVal);
                        horizontal.setY(yVal);
                    }
                });
                return true;
            }
        });
    }

    public void startShot() {
        //shooting = true;
        shot_active = true;
    }

    public void stopShot() {
        //shooting = false;
        shot_active = false;
    }

    public void fireBall(float relativeStrength) {
        //shooting = false;
        caller.sendTCPMessage("" + Connector.ProtocolCmd.FIRE.ordinal() + " " + relativeStrength + " " + spinX + " " + spinY + " " + '\n');
        System.out.println("Force: " + relativeStrength);
        System.out.println("Spin: (" + spinX + " == " + spinY + ")");
        shot_active = false;
        strengthButtonAnim.resetPosition();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void onResume() {
        sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        //shooting = false;
        paused = false;
    }

    public Boolean isSameAsCmd(Connector.ProtocolCmd cmd) {
        return (cmd == Connector.ProtocolCmd.PLAY);
    }

    public void onPause() {
        sensorManager.unregisterListener(this);
        //shooting = true;
        paused = true;
    }

    public void onSensorChanged(SensorEvent event) {

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
        if(!shot_active && !paused && !interrupted && active)
            angle -= (float) (rotation * Math.abs(rotation) * (System.currentTimeMillis() - lastSensorReadTime) * 0.02f);
        lastSensorReadTime = System.currentTimeMillis();

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void interrupt() {
        //shooting = true;
        interrupted = true;
        sensorManager.unregisterListener(this);

        caller.runOnUiThread(new Runnable() {
            public void run() {
                own_layout.setVisibility(View.INVISIBLE);
            }
        });

        active = false;
    }

    public void start() {
        final ImageView cueBall = (ImageView) caller.findViewById(R.id.cueBall);
        final RelativeLayout horizontal = (RelativeLayout) caller.findViewById(R.id.horizontalLine);
        final RelativeLayout vertical = (RelativeLayout) caller.findViewById(R.id.verticalLine);
        caller.runOnUiThread(new Runnable() {
            public void run() {
                own_layout.setVisibility(View.VISIBLE);
                vertical.setX(cueBall.getX()+cueBall.getWidth()/2);
                horizontal.setY(cueBall.getY()+cueBall.getHeight()/2);
            }
        });
        initializeSensors();
        initializeAngleSender();
        initializeElements();
        sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        active = true;
        //shooting = false;
        interrupted = false;
        shot_active = false;

        update_ball_type(caller.ball_type());
    }

    private void update_ball_type(int type) {
        final LinearLayout layout = (LinearLayout) caller.findViewById(R.id.ball_type_layout);
        final ImageView img = (ImageView) caller.findViewById(R.id.ball_type_img);
        final TextView txt = (TextView) caller.findViewById(R.id.ball_type_txt);

        if(type == -1) {
            caller.runOnUiThread(new Runnable() {
                public void run() {
                    layout.setVisibility(View.INVISIBLE);
                    img.setVisibility(View.INVISIBLE);
                    txt.setText(caller.getResources().getString(R.string.balltype_none));
                }
            });
            return;
        }

        Connector.BallType btype = Connector.BallType.values()[type];
        switch (btype) {
            case NONE:
                caller.runOnUiThread(new Runnable() {
                    public void run() {
                        layout.setVisibility(View.VISIBLE);
                        img.setVisibility(View.INVISIBLE);
                        txt.setText(caller.getResources().getString(R.string.balltype_none));
                    }
                });
                break;
            case STRIPE:
                caller.runOnUiThread(new Runnable() {
                    public void run() {
                        layout.setVisibility(View.VISIBLE);
                        img.setVisibility(View.VISIBLE);
                        img.setImageResource(R.mipmap.stripe);
                        txt.setText(caller.getResources().getString(R.string.balltype_stripe));
                    }
                });
                break;
            case SOLID:
                caller.runOnUiThread(new Runnable() {
                    public void run() {
                        layout.setVisibility(View.VISIBLE);
                        img.setVisibility(View.VISIBLE);
                        img.setImageResource(R.mipmap.solid);
                        txt.setText(caller.getResources().getString(R.string.balltype_solid));
                    }
                });
                break;
            case BLACK:
                caller.runOnUiThread(new Runnable() {
                    public void run() {
                        layout.setVisibility(View.VISIBLE);
                        img.setVisibility(View.VISIBLE);
                        img.setImageResource(R.mipmap.black);
                        txt.setText(caller.getResources().getString(R.string.balltype_black));
                    }
                });
                break;

        }
    }

    public Boolean isActive() {
        return active;
    }

    public Value getValue() {
        return value;
    }
}
