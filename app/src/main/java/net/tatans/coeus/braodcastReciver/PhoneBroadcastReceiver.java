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
import android.telephony.TelephonyManager;
import android.util.Log;

import net.tatans.coeus.service.InCallAccessibilityService;

/**
 * Created by John on 2016/1/5.
 */
public class PhoneBroadcastReceiver extends BroadcastReceiver implements
          SensorEventListener {

                    TelephonyManager tManager;
                    AudioManager audioManager;
                    SensorManager sensorManager;

                    @Override
                    public void onReceive(Context context, Intent intent) {
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
                                                                                break;
                                        }
                    }

                    private int firstSensor = 0;

                    @Override
                    public void onSensorChanged(SensorEvent event) {
                                        float[] its = event.values;
                                        if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                                                            if (audioManager.isWiredHeadsetOn()||audioManager.isBluetoothScoOn()||its[0] == 0.0) {
                                                                                audioManager.setSpeakerphoneOn(false);
                                                                                firstSensor = 1;
                                                                                Log.d("myTag", "222222");
                                                            } else if (firstSensor == 1) {
                                                                                audioManager.setSpeakerphoneOn(true);
                                                                                Log.d("myTag", "1111");
                                                            }
                                                            /*if(firstSensor==1){
                                                                                audioManager.setSpeakerphoneOn(true);
                                                            }else{
                                                                                audioManager.setSpeakerphoneOn(false);
                                                                                firstSensor = 1;
                                                            }*/
                                        }

                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }

}
