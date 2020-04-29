package com.example.shakedetect;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Gyroscope {


    public interface Listener
    {
        void onRotatio(float rx,float ry,float rz);
    }

    private Listener listener;


    public void  setListener(Listener L)
    {
        listener =  L;
    }


    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    Gyroscope(Context context)
    {
        sensorManager=(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorEventListener=new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent)
            {

                if(listener!=null)
                {
                    listener.onRotatio(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]);

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {

            }
        };
    }


    public  void  register()
    {
        sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void unregister(){
        sensorManager.unregisterListener(sensorEventListener);
    }
}
