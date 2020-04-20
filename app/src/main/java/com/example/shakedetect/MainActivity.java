package com.example.shakedetect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

//import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPrefs = getSharedPreferences("com.example.shakedetect", MODE_PRIVATE);
        aSwitch=findViewById(R.id.switch1);
        aSwitch.setChecked(sharedPrefs.getBoolean("NameOfThingToSave", false));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(MainActivity.this, ShakeService.class));
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.shakedetect", MODE_PRIVATE).edit();
                    editor.putBoolean("NameOfThingToSave", true);
                    editor.apply();
                } else {
                    stopService(new Intent(MainActivity.this, ShakeService.class));
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.shakedetect", MODE_PRIVATE).edit();
                    editor.putBoolean("NameOfThingToSave", false);
                    editor.apply();
                }
            }
        });

    }
}
