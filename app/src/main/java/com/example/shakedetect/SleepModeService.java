package com.example.shakedetect;
import android.annotation.SuppressLint;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Objects;

@SuppressLint("Registered")
public class SleepModeService extends Service implements SensorEventListener {

    static int degree;
    DevicePolicyManager deviceManger;
    SensorManager sensorManager;
    Sensor accelerometer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getExtras() != null) {
            setDegree(Integer.parseInt((Objects.requireNonNull(intent.getExtras().getString(MainActivity.degree_id)))));
            SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
            editor.putInt("Input_Degree", getDegree());
            editor.apply();
        }else{
            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            setDegree(prefs.getInt("Input_Degree", 15));
        }

        deviceManger = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] g = event.values.clone();
        double norm_Of_g = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

        g[0] = (float) (g[0] / norm_Of_g);
        g[1] = (float) (g[1] / norm_Of_g);
        g[2] = (float) (g[2] / norm_Of_g);
        int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));
        if (inclination < getDegree() || inclination > (180 - getDegree())) {
            lockPhone();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void lockPhone() {
        deviceManger.lockNow();
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

}
