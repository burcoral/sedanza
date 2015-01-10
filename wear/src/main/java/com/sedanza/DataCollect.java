package com.sedanza;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by anthony on 1/10/15.
 */
public class DataCollect implements SensorEventListener {

    private static String TAG = " DataCollect";
    private static final int ACCELEROMETER_FREQUENCY = 20; // Number of skips

    Context mContext;
    public DataCollect(Context c){
        mContext = c;
    }

    public void start() {
        SensorManager mSensorManager = ((SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE));
        Sensor mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor mAccelarationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mSensorManager.registerListener(this, mAccelarationSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }


    private String currentTimeStr() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(c.getTime());
    }

    private int aMeasurementCount = 0;
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            String msg = "Count: " + event.values[0];

            if (aMeasurementCount++ % ACCELEROMETER_FREQUENCY == 0)
                recordAccelerometer(event.values);

        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            String msg = "Count: " + (int) event.values[0];
            Log.d(TAG, msg);
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            String msg = "Detected at " + currentTimeStr();
            Log.d(TAG, msg);

            stepCount++;

        } else {
            Log.d(TAG, "Unknown sensor type");
        }
    }

    int stepCount = 0;

    float accelerometerSum = 0.0f;
    int accelerometerCount = 0;
    private void recordAccelerometer(float[] arr){
        accelerometerSum += Math.abs(arr[0]) + Math.abs(arr[1]) + Math.abs(arr[2]);
        accelerometerCount += 3;
    }

    public float getAccelerometerAvg(){
        return accelerometerSum / accelerometerCount;
    }

    public void clearAccelerometer(){
        accelerometerSum = 0.0f;
        accelerometerCount = 0;
    }
}
