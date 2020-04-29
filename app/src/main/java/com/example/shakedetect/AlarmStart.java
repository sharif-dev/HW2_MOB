package com.example.shakedetect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

public class AlarmStart extends AppCompatActivity {



    private Gyroscope gyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_start);


        speed_rotate x=speed_rotate.getInstance();
        final int a=x.speed_Rotate;
        System.out.println("hellooooo " + a);

        final MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.sound1);
        mediaPlayer.start();

        gyroscope=new Gyroscope(this);


        new CountDownTimer(1000*60*10, 1000)
        {
            public void onTick(long millisUntilFinished)
            {

            }

            public void onFinish()
            {
                mediaPlayer.stop();
            }
        }.start();



        Thread threadcontinue=new Thread()
        {
            public void run()
            {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override

                    public void onCompletion(MediaPlayer mediaPlayer)
                    {
                        mediaPlayer.start();
                    }
                });
            }

        };
        threadcontinue.start();




        Button button=(Button) findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mediaPlayer.stop();
                Intent i = new Intent(AlarmStart.this,MainActivity.class);
                startActivity(i);
            }
        });

        final Context finalContext = null;
        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotatio(float rx, float ry, float rz) {


                if(rz > a)
                {
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    mediaPlayer.stop();
                    Intent i = new Intent(AlarmStart.this,MainActivity.class);
                    startActivity(i);
                }
                else if(rz < -a){


                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                    mediaPlayer.stop();
                    Intent i = new Intent(AlarmStart.this,MainActivity.class);
                    startActivity(i);

                }

            }
        });

    }



    protected void onResume(){
        super.onResume();


        gyroscope.register();
    }


    protected  void onPause(){
        super.onPause();


        gyroscope.unregister();

    }
}
