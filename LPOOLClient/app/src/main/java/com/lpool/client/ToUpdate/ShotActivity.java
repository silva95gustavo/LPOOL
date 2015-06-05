package com.lpool.client.ToUpdate;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.view.MotionEventCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lpool.client.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


public class ShotActivity extends Activity implements SensorEventListener {

    private static String serverIP = "192.168.206.1";
    private static final int serverPort = 6900;
    private static final int FPS = 20;

    private SensorManager sensorManager;
    private Sensor senAccelerometer;
    private Sensor senMagnetometer;

    float[] mGravity;
    float[] mGeomagnetic;

    private volatile Socket socket;

    // UDP
    private DatagramSocket datagramSocket;

    public float angle = (float)Math.PI;
    private int counter = 0;
    private long startTime = System.currentTimeMillis();
    private long lastSensorReadTime = System.currentTimeMillis();
    private boolean shooting = false;
    private float accelerometerLast = 0;
    private float gravity[] = new float[3];
    private float fireAngle;

    private Button fire;
    private StrengthButton strengthButtonAnim;
    private RelativeLayout strength;

    public enum ProtocolCmd {
        ANGLE, // angle
        FIRE, // force[0, 1] x-spin[-1, 1] y-spin[-1, 1]
        PING,
        PONG,
        JOIN,
        QUIT,
        KICK,
        BIH,
        ACKBIH,
        PLACECB // x-pos[0, 1] y-pos[0, 1]
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        initializeSensors();
        initializeConnection();
        initializeAngleSender();
        initializeElements();
    }

    private void initializeConnection() {
        new Thread(new ClientThread()).start();
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initializeAngleSender() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (socket == null || datagramSocket == null)
                    return;
                try {
                    if(!shooting) {
                        String data = new String(ProtocolCmd.ANGLE.ordinal() + " " + angle + " " + counter++ + '\n');
                        byte[] msg = data.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(serverIP), serverPort);
                        datagramSocket.send(sendPacket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 100, 1000/FPS);
    }

    private void initializeElements() {
        fire = (Button) findViewById(R.id.fireButton);
        strength = (RelativeLayout) findViewById(R.id.strengthBar);
        strength.setY(700);

        final ShotActivity caller = this;

        fire.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
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
        strengthButtonAnim = new StrengthButton(caller, fire, strength, fire.getY(), 0, 4);

        final ImageView cueBallPlace = (ImageView) findViewById(R.id.cueBallPlacable);
        final RelativeLayout placeBall = (RelativeLayout) findViewById(R.id.tableandball);

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

    public void fireButtonClick(View v) {

    }

    private void initializeAiming() {
        final ImageView cueBall = (ImageView) findViewById(R.id.cueBall);
        final RelativeLayout horizontal = (RelativeLayout) findViewById(R.id.horizontalLine);
        final RelativeLayout vertical = (RelativeLayout) findViewById(R.id.verticalLine);

        final View touchView = findViewById(R.id.center);
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

    class beatThread implements Runnable {
        @Override
        public void run() {
            while (counter>0)
            {
                try {
                    System.out.println("Receiving...");
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = br.readLine();
                    System.out.println("Received...");
                    if (str == null) {
                        System.out.println("received null");
                        break;
                    }
                    else if(str.equals(ProtocolCmd.PING.ordinal() + "" + '\n'))
                    {
                        System.out.println("Got PING");
                        try {
                            String data = new String("" + ProtocolCmd.PONG.ordinal() + '\n');
                            byte[] msg = data.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(serverIP), serverPort);
                            datagramSocket.send(sendPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    //stopMe();
                }
            }
        }
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIP);
                socket = new Socket(serverAddr, serverPort);
                datagramSocket = new DatagramSocket(serverPort);
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (shooting)
            return;
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

        if (socket == null)
            return;

        TextView tv1 = (TextView) findViewById(R.id.textView1);
        //tv1.setText(String.valueOf(rotation));

        TextView tv2 = (TextView) findViewById(R.id.textView2);
        //tv2.setText(String.valueOf(angle));

        TextView tv3 = (TextView) findViewById(R.id.textView3);
        //tv3.setText(String.valueOf(event.values[2]));

    }

    public void startShot() {
        shooting = true;
        this.fireAngle = this.angle;
    }

    public void fireBall(float relativeStrength) {
        strengthButtonAnim.reset();
        shooting = false;
        if (socket == null)
            return;

        long difference = System.currentTimeMillis() - startTime;

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            pw.println("" + ProtocolCmd.FIRE.ordinal() + " " + (float)relativeStrength);
            System.out.println("Firing...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        strengthButtonAnim.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            startTime = System.currentTimeMillis();
            shooting = true;
            return true;
        }
        else if (action == MotionEvent.ACTION_UP)
        {
            shooting = false;
            if (socket == null)
                return true;

            long difference = System.currentTimeMillis() - startTime;

            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                pw.println("" + ProtocolCmd.FIRE.ordinal() + " " + (float)difference / 1000);
                //pw.println("" + ProtocolCmd.FIRE.ordinal() + " " + accelerometerLast);
                System.out.println("Firing...");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        else
            return super.onTouchEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public static String getServerIP() {
        return serverIP;
    }

    public static void setServerIP(String serverIP) {
        ShotActivity.serverIP = serverIP;
    }
}
