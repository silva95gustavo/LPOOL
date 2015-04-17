package com.lpool.client;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ShotActivity extends ActionBarActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor senAccelerometer;
    private Sensor senMagnetometer;

    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shot);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
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
        boolean success = false;
        float orientation[] = new float[3];
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                SensorManager.getOrientation(R, orientation);
            }
        }

        if (!success)
            return;

        TextView tv1 = (TextView) findViewById(R.id.textView1);
        tv1.setText(String.valueOf(orientation[0]));

        TextView tv2 = (TextView) findViewById(R.id.textView2);
        tv2.setText(String.valueOf(orientation[1]));

        TextView tv3 = (TextView) findViewById(R.id.textView3);
        tv3.setText(String.valueOf(orientation[2]));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("resumed with delay " + SensorManager.SENSOR_DELAY_FASTEST);
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, senMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
