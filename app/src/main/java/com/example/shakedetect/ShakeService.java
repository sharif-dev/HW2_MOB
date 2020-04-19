package com.example.shakedetect;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ShakeService extends Service {
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 10) {
                turnOnScreen();
            }

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI, new Handler());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(sensorEventListener);
        super.onDestroy();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @SuppressLint("InvalidWakeLockTag")
    public void turnOnScreen(){
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn == false)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
            wl.acquire();
            wl.release();
        }
        else{
            Toast.makeText(getApplicationContext() , "SHAKED" , Toast.LENGTH_SHORT).show();

        }
    }

}