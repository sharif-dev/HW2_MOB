package com.example.shakedetect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static final int RESULT_ENABLE = 1;
    DevicePolicyManager deviceManger;
    ComponentName compName;
    String angular_value = "15";
    EditText angular_value_editText;
    Switch sleep_mode_switch, shake_switch, clock_swich;
    TextView sleepText, shakeText, clockText;
    static final String degree_id = "degree_id";
    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        //////////////////////////////////////////////////////// clock

        clock_swich = findViewById(R.id.feature1Switch);
        clockText = findViewById(R.id.Clock_ID);


        //////////////////////////////////////////////////////// shake

        shake_switch = findViewById(R.id.shakeSwitch);
        shakeText = findViewById(R.id.shake_ID);


        shake_switch.setChecked(sharedPrefs.getBoolean("SaveShakeSwitch", false));
        shake_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("MySharedPref", MODE_PRIVATE).edit();
                if (isChecked) {
                    startService(new Intent(MainActivity.this, ShakeService.class));
                    editor.putBoolean("SaveShakeSwitch", true);
                } else {
                    stopService(new Intent(MainActivity.this, ShakeService.class));
                    editor.putBoolean("SaveShakeSwitch", false);
                }
                editor.apply();
            }
        });


        //////////////////////////////////////////////////////// sleep

        angular_value_editText = findViewById(R.id.angular_value_id);
        sleep_mode_switch = findViewById(R.id.sleepmodeSwitch);
        sleepText = findViewById(R.id.sleepMode_ID);

        deviceManger = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, DeviceAdmin.class);


        if (sharedPrefs.getBoolean("SaveSleepModeSwitch", false))
            angular_value_editText.setText(sharedPrefs.getString("lastDegree", ""));

        sleep_mode_switch.setChecked(sharedPrefs.getBoolean("SaveSleepModeSwitch", false));

        sleep_mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("MySharedPref", MODE_PRIVATE).edit();
                angular_value = angular_value_editText.getText().toString();
                if (isChecked) {
                    if (!angular_value.isEmpty()) {
                        boolean active = deviceManger.isAdminActive(compName);
                        if (active) {
                            Intent intent = new Intent(MainActivity.this, SleepModeService.class);
                            intent.putExtra(degree_id, angular_value);
                            startService(intent);
                        } else {
                            alertDialog();
                        }
                        editor.putBoolean("SaveSleepModeSwitch", true);
                        editor.putString("lastDegree", angular_value);
                    }else {
                        sleep_mode_switch.setChecked(false);
                        Toast.makeText(getApplicationContext() , "enter angular value" , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    deviceManger.removeActiveAdmin(compName); // felan
                    stopService(new Intent(MainActivity.this, SleepModeService.class));
                    editor.putBoolean("SaveSleepModeSwitch", false);
                }
                editor.apply();
            }
        });


    }


    public void enablePhone() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        startActivityForResult(intent, RESULT_ENABLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_ENABLE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(this, SleepModeService.class);
                intent.putExtra(degree_id, angular_value);
                startService(intent);
            } else {
                sleep_mode_switch.setChecked(false);
            }
        } else {
            sleep_mode_switch.setChecked(false);
        }
    }

    private void alertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Allow app to control your lock screens");
        dialog.setTitle("Sleep Mode alert");
        dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                enablePhone();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

}
