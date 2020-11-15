package com.or.mobilesecutiry1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Date;

public class MainActivity<m_sensorEventListener> extends AppCompatActivity {
    public static final String KEY_MSP_shake  = "isShake";
    public static final String KEY_MSP_taps  = "countOfTaps";

        private MaterialButton main_BTN_click;
        private SensorManager mSensorManager;
        private float mAccel; // acceleration apart from gravity
        private float mAccelCurrent; // current acceleration including gravity
        private float mAccelLast; // last acceleration including gravity
        private Boolean isShake = false;
        private  int curBrightnessValue;
        private MySheredP msp;
        private  int countOfTaps =0;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            getSupportActionBar().hide();
            main_BTN_click = findViewById(R.id.main_BTN_click);


            msp = new MySheredP(this);
            getFromMSP();
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            mAccel = 0.00f;
            mAccelCurrent = SensorManager.GRAVITY_EARTH;
            mAccelLast = SensorManager.GRAVITY_EARTH;


            main_BTN_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkAll())
                        openPage();
                }
            });


            View rootView= MainActivity.this.findViewById(android.R.id.content);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  countOfTaps++;
                }
            });
        }


        public boolean checkAll(){
            if(isBrightness()&&isEvenDate()&&isLandscape()&&isTaps()&&isShake==true)
                return true;
            return false;
        }

        public boolean isTaps(){
            BatteryManager bm = (BatteryManager) MainActivity.this.getSystemService(BATTERY_SERVICE);
            int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
              if(countOfTaps>=batLevel/10)
                   return true;
              return false;
        }
    @Override
    protected void onStart() {
        super.onStart();
        getFromMSP();
    }

    public boolean isBrightness() {
        try {
            curBrightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (curBrightnessValue ==0)
            return true;
        return false;

    }



public boolean isLandscape() {
    int orientation = getResources().getConfiguration().orientation;
    if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        return true;
     else
        return false;
}

    @Override
    protected void onDestroy() {
        super.onDestroy();
            putOnMSP();

    }

    @Override
    protected void onStop() {
        super.onStop();
       putOnMSPFalse();
    }

    public void openPage() {
            startActivity(new Intent(MainActivity.this, open.class));
        }

        private final SensorEventListener mSensorListener = new SensorEventListener() {

            public void onSensorChanged(SensorEvent se) {
                float x = se.values[0];
                float y = se.values[1];
                float z = se.values[2];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta; // perform low-cut filter
                if (mAccel > 12) {
                    isShake= true;
                    putOnMSP();
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        @Override
        protected void onResume() {
            super.onResume();
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }



    @Override
        protected void onPause() {
            mSensorManager.unregisterListener(mSensorListener);
            super.onPause();
        }



        private boolean isEvenDate(){
            Date currentTime = Calendar.getInstance().getTime();
            if(currentTime.getMinutes()%2==0)
                return true;
        return false;
        }


    private void getFromMSP(){
        String data  = msp.getString(KEY_MSP_shake, "false");
        if(data.equals("true"))
            isShake = true;
        else
            isShake = false;
        String taps  = msp.getString(KEY_MSP_taps, "0");
          countOfTaps =   Integer.parseInt(taps);

    }

    private void putOnMSP(){

        msp.putString(KEY_MSP_shake,isShake.toString());
        msp.putString(KEY_MSP_taps,countOfTaps+"");
    }

    private void putOnMSPFalse(){

        msp.putString(KEY_MSP_shake,"false");
        msp.putString(KEY_MSP_taps,"0");
    }
}
