package com.example.shakedetect;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Objects;

public class ShakeService extends Service {
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    int shakeSensitivity = 10;

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
            if (mAccel > shakeSensitivity) {
                turnOnScreen();
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            shakeSensitivity = Integer.parseInt(Objects.requireNonNull(intent.getExtras().getString(MainActivity.sensitivity)));
            SharedPreferences.Editor editor = getSharedPreferences("ShakePref", MODE_PRIVATE).edit();
            editor.putInt("ShakeAmount", shakeSensitivity);
            editor.apply();
        } else {
            SharedPreferences shake_pref = getSharedPreferences("ShakePref", MODE_PRIVATE);
            shakeSensitivity = shake_pref.getInt("ShakeAmount", 10);
        }
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
    public void turnOnScreen() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn == false) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
            wl.acquire();
            wl.release();
        } else {
            Toast.makeText(getApplicationContext(), "SHAKED", Toast.LENGTH_SHORT).show();
        }
    }

}