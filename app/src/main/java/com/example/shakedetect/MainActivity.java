package com.example.shakedetect;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.TimedText;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    static final int RESULT_ENABLE = 1;
    DevicePolicyManager deviceManger;
    ComponentName compName;
    String angular_value = "15";
    EditText angular_value_editText;
    Switch sleep_mode_switch, shake_switch, clock_swich;
    TextView sleepText, shakeText, clockText;
    static final String degree_id = "degree_id";
    SharedPreferences sharedPrefs;
    SharedPreferences sharedPreferences;
    Button button;
    EditText rotateSpeedTex;
    String speedRotate;
    speed_rotate x = speed_rotate.getInstance();
    EditText shakeSensitivityEditText ;
    String shakeSensitivityNum ;
    static final String sensitivity = "sensitivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);





        //////////////////////////////////////////////////////// clock

        clock_swich = findViewById(R.id.feature1Switch);
        clockText = findViewById(R.id.Clock_ID);
        button=(Button) findViewById(R.id.SetAlarm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        rotateSpeedTex=findViewById(R.id.rotate_speed);

        clock_swich.setChecked(sharedPrefs.getBoolean("AlarmclockSwich", false));
        rotateSpeedTex.setText(sharedPrefs.getString("lastrotateSpeedNum", ""));
        clockText.setText(sharedPrefs.getString("TimeTex", "Alarm"));






        //////////////////////////////////////////////////////// shake

//        shake_switch = findViewById(R.id.shakeSwitch);
//        shakeText = findViewById(R.id.shake_ID);
//
//
//
//        shake_switch.setChecked(sharedPrefs.getBoolean("SaveShakeSwitch", false));
//        shake_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SharedPreferences.Editor editor = getSharedPreferences("MySharedPref", MODE_PRIVATE).edit();
//                if (isChecked) {
//                    startService(new Intent(MainActivity.this, ShakeService.class));
//                    editor.putBoolean("SaveShakeSwitch", true);
//                } else {
//                    stopService(new Intent(MainActivity.this, ShakeService.class));
//                    editor.putBoolean("SaveShakeSwitch", false);
//                }
//                editor.apply();
//            }
//        });


        shake_switch = findViewById(R.id.shakeSwitch);
        shakeText = findViewById(R.id.shake_ID);
        shakeSensitivityEditText = findViewById(R.id.shakeSensitivity);

        if (sharedPrefs.getBoolean("SaveShakeSwitch", false)) {
            shakeSensitivityEditText.setText(sharedPrefs.getString("lastshakeSensitivityNum", ""));
        }
        shake_switch.setChecked(sharedPrefs.getBoolean("SaveShakeSwitch", false));
        shake_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("MySharedPref", MODE_PRIVATE).edit();
                shakeSensitivityNum = shakeSensitivityEditText.getText().toString();

                if (isChecked) {

                    if (!shakeSensitivityNum.isEmpty()) {
                        Intent intent_for_shake = new Intent(MainActivity.this, ShakeService.class);
                        intent_for_shake.putExtra(sensitivity, shakeSensitivityNum);
                        startService(intent_for_shake);
                        editor.putBoolean("SaveShakeSwitch", true);
                        editor.putString("lastshakeSensitivityNum", shakeSensitivityNum);
                    } else {
                        clock_swich.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Enter Shake Sensitivity", Toast.LENGTH_SHORT).show();
                    }

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

    private void showTimePicker() {

        DialogFragment dialogFragment=new TimePickerFragment();
        dialogFragment.show(getSupportFragmentManager(),"timePicker");

    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


        SharedPreferences.Editor editor = getSharedPreferences("MySharedPref", MODE_PRIVATE).edit();
        clock_swich.setChecked(false);
        editor.putBoolean("AlarmclockSwich", false);
        editor.apply();

        System.out.println("hours"+hourOfDay);
        System.out.println("minute"+minute);

        final Calendar c=Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
        c.set(Calendar.MINUTE,minute);
        c.set(Calendar.SECOND,0);
        updateTimeText(hourOfDay,minute);
//        sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);


//        clock_swich.setChecked(sharedPrefs.getBoolean("AlarmclockSwich", false));
//        shake_switch.setChecked(sharedPrefs.getBoolean("SaveShakeSwitch", false));
        clock_swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = getSharedPreferences("MySharedPref", MODE_PRIVATE).edit();
                speedRotate = rotateSpeedTex.getText().toString();


                if (isChecked)
                {
                    if(!speedRotate.isEmpty())
                    {

                        x.speed_Rotate=Integer.parseInt(speedRotate);
                        System.out.println(speedRotate);
                        editor.putString("lastrotateSpeedNum",speedRotate );
//                        editor.putBoolean("AlarmclockSwich", true);
                        startAlarm(c);
                    }
                    else
                    {
                        clock_swich.setChecked(false);
                        Toast.makeText(getApplicationContext() , "enter speed rotate" , Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
//                    editor.putBoolean("AlarmclockSwich", false);
                    cancelAlarm();


                }
                editor.putBoolean("AlarmclockSwich", isChecked);
                editor.apply();

            }
        });


    }

    private void updateTimeText(int hourOfDay,int minute) {

        String timeTex="";
        SharedPreferences.Editor editor = getSharedPreferences("MySharedPref", MODE_PRIVATE).edit();

        if(minute<10){
            timeTex +=Integer.toString(hourOfDay)+" : "+"0"+Integer.toString(minute);

        }
        else{
            timeTex +=Integer.toString(hourOfDay)+" : "+Integer.toString(minute);

        }

        editor.putString("TimeTex",timeTex );
        editor.apply();
        clockText.setText(timeTex);


    }


    private void startAlarm(Calendar c){

        Intent i=new Intent(MainActivity.this,AlertReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),0,i,0);
        AlarmManager alarmManager=(AlarmManager)getSystemService((ALARM_SERVICE));
        alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis() ,pendingIntent);

    }


    private void cancelAlarm() {

        Intent i=new Intent(MainActivity.this,AlertReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),0,i,0);
        AlarmManager alarmManager=(AlarmManager)getSystemService((ALARM_SERVICE));
        alarmManager.cancel(pendingIntent);


    }



}
