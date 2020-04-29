package com.example.shakedetect;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.widget.Toast;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        Toast.makeText(context, "Alarm......", Toast.LENGTH_LONG).show();
//        final MediaPlayer mediaPlayer=MediaPlayer.create(context,R.raw.sound1);
//        mediaPlayer.start();
        Intent i = new Intent();
        i.setClassName("com.example.shakedetect", "com.example.shakedetect.AlarmStart");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
//
//       Gyroscope gyroscope=new Gyroscope(context);



    }
}