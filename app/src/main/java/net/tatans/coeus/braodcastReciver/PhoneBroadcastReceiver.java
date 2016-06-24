package net.tatans.coeus.braodcastReciver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.service.FxService;
import net.tatans.coeus.service.InCallAccessibilityService;
import net.tatans.coeus.service.TatansAccessibilityService;
import net.tatans.coeus.service.activity.MainActivity;

/**
  * ClassName :PhoneBroadcastReceiver
  * explain :监听电话
  * @author: syf
  * Created time : 2016/6/7 13:25.
  */
public class PhoneBroadcastReceiver extends BroadcastReceiver implements
        SensorEventListener {

    TelephonyManager tManager;
    AudioManager audioManager;
    SensorManager sensorManager;
    String EXTRA_PHONE_NUMBER;
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())) {
            EXTRA_PHONE_NUMBER = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(TatansApplication.getContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("EXTRA_PHONE_NUMBER", EXTRA_PHONE_NUMBER);
                    if (!FxService.isDestroy)
                    TatansApplication.getContext().startActivity(i);
                }
            },200);

        } else {
            tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            // 如果是来电
            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("myTag", "comming");
                    InCallAccessibilityService.flag = false;
                    if (MainActivity.lockLayer != null && MainActivity.activity != null) {
                        MainActivity.lockLayer.unlock();
                        MainActivity.activity.finish();
                    }
                    break;

                // 通话过程
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    InCallAccessibilityService.flag = true;
                    InCallAccessibilityService.closed = true;
                    sensorManager.registerListener(this,
                            sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                            SensorManager.SENSOR_DELAY_NORMAL);
                    firstSensor = 0;
                    Log.d("myTag", "online");
                    break;

                // 挂断
                case TelephonyManager.CALL_STATE_IDLE:
                    InCallAccessibilityService.flag = true;
                    InCallAccessibilityService.closed = true;
                    sensorManager.unregisterListener(this);
                    Log.d("myTag", "hangup");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (MainActivity.lockLayer != null && MainActivity.activity != null) {
                                MainActivity.lockLayer.unlock();
                                MainActivity.activity.finish();
                            }
                        }
                    }, 1500);
                    break;
            }
        }
    }

    private int firstSensor = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] its = event.values;
        if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            //靠近手机，设置扬声器外放false
            if (audioManager.isWiredHeadsetOn() || audioManager.isBluetoothScoOn() || its[0] == 0.0) {
                audioManager.setSpeakerphoneOn(false);
                FxService.interrupt(450);
                FxService.interrupt(600);
                firstSensor = 1;
            } else if (firstSensor == 1) {
                //离开手机，设置扬声器外方true
                audioManager.setSpeakerphoneOn(true);
                FxService.interrupt(200);
            }else{
                //默认听筒
                audioManager.setSpeakerphoneOn(false);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
