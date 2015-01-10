package com.sedanza;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity implements SensorEventListener {

    private static String TAG = " MainActivity";

    private TextView mTextView;
    private TextView mTextViewStepCount;
    private TextView mTextViewStepDetect;
    private TextView mTextViewLinearAcceleration;
    private TextView mTextViewAcceleration;

    Sensor mStepCountSensor;
    Sensor mStepDetectSensor;
    Sensor mAccelarationSensor;
    Sensor mLinearAccelarationSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextViewStepDetect = (TextView) stub.findViewById(R.id.step_detect);
                mTextViewStepCount = (TextView) stub.findViewById(R.id.step_count);
                mTextViewAcceleration = (TextView) stub.findViewById(R.id.acceleration);
                mTextViewLinearAcceleration = (TextView) stub.findViewById(R.id.linear_acceleration);
                getStepCount();
            }
        });
    }

    @Override
    protected  void onResume()
    {
        super.onResume();
    }
    private void getStepCount() {
        SensorManager mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mAccelarationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAccelarationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mSensorManager.registerListener(this, mAccelarationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mLinearAccelarationSensor, SensorManager.SENSOR_DELAY_NORMAL);
      }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }


    private String currentTimeStr() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(c.getTime());
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            String tmp = event.values[0] + "/" + event.values[1] + "/" + event.values[2];
            String msg = "Accelerometer: " + tmp;
            mTextViewAcceleration.setText(msg);
        }else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            String tmp = event.values[0] + "/" + event.values[1] + "/" + event.values[2];
            String msg = "Linear Acceleration: " + tmp;
            mTextViewLinearAcceleration.setText(msg);
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            String msg = "Step Count: " + (int) event.values[0];
           mTextViewStepCount.setText(msg);
            Log.d(TAG, msg);
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            String msg = "Detected at " + currentTimeStr();
            mTextViewStepDetect.setText(msg);
            Log.d(TAG, msg);
        } else {
            Log.d(TAG, "Unknown sensor type");
        }
    }

}
