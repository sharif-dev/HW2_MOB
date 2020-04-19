package com.example.shakedetect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ToggleButton toggleButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPrefs = getSharedPreferences("com.example.shakedetect", MODE_PRIVATE);
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setChecked(sharedPrefs.getBoolean("NameOfThingToSave", false));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
