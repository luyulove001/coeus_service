//package net.tatans.coeus.braodcastReciver;
//
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.media.AudioManager;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.view.WindowManager;
//
//import net.tatans.coeus.service.FxService;
//import net.tatans.coeus.service.InCallAccessibilityService;
//import net.tatans.coeus.service.MainActivity;
//import net.tatans.coeus.util.PhoneUtil;
//
///**
// * Created by John on 2016/1/5.
// */
//public class PhoneBroadcastReceiver extends BroadcastReceiver implements
//        SensorEventListener {
//
//    TelephonyManager tManager;
//    AudioManager audioManager;
//    SensorManager sensorManager;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if ("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())) {
//            String EXTRA_PHONE_NUMBER = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//            Intent i = new Intent(context, MainActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.putExtra("EXTRA_PHONE_NUMBER", EXTRA_PHONE_NUMBER);
//            context.startActivity(i);
//        } else {
//            tManager = (TelephonyManager) context
//                    .getSystemService(Service.TELEPHONY_SERVICE);
//            audioManager = (AudioManager) context
//                    .getSystemService(Context.AUDIO_SERVICE);
//            sensorManager = (SensorManager) context
//                    .getSystemService(Context.SENSOR_SERVICE);
//            // 如果是来电
//            switch (tManager.getCallState()) {
//                case TelephonyManager.CALL_STATE_RINGING:
//                    Log.d("myTag", "comming");
//                    InCallAccessibilityService.flag = false;
//                    break;
//
//                // 通话过程
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    InCallAccessibilityService.flag = true;
//                    InCallAccessibilityService.closed = true;
//                    sensorManager.registerListener(this,
//                            sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
//                            SensorManager.SENSOR_DELAY_NORMAL);
//                    firstSensor = 0;
//                    Log.d("myTag", "online");
//                    break;
//
//                // 挂断
//                case TelephonyManager.CALL_STATE_IDLE:
//                    InCallAccessibilityService.flag = true;
//                    InCallAccessibilityService.closed = true;
//                    sensorManager.unregisterListener(this);
//                    Log.d("myTag", "hangup");
//                    if (MainActivity.lockLayer != null && MainActivity.activity != null) {
//                        MainActivity.lockLayer.unlock();
//                        MainActivity.activity.finish();
//                    }
//                    break;
//            }
//        }
//    }
//
//    private int firstSensor = 0;
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        float[] its = event.values;
//        if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
//            if (audioManager.isWiredHeadsetOn() || audioManager.isBluetoothScoOn() || its[0] == 0.0) {
//                audioManager.setSpeakerphoneOn(false);
//                if (MainActivity.lockLayer != null && MainActivity.activity != null) {
//                    MainActivity.lockLayer.unlock();
//                    MainActivity.activity.finish();
//                }
//                FxService.getInstance().removeAnswerView();
//                FxService.getInstance().interrupt(450);
//                FxService.getInstance().interrupt(600);
//                WindowManager.LayoutParams wmParams = FxService.getInstance().initParams(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
//                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//                FxService.getInstance().createView(wmParams);
//                firstSensor = 1;
//                Log.d("myTag", "222222");
//            } else if (firstSensor == 1) {
//                audioManager.setSpeakerphoneOn(true);
//                FxService.interrupt(200);
//                Log.d("myTag", "1111");
//            }
//                                                            /*if(firstSensor==1){
//                                                                                audioManager.setSpeakerphoneOn(true);
//                                                            }else{
//                                                                                audioManager.setSpeakerphoneOn(false);
//                                                                                firstSensor = 1;
//                                                            }*/
//        }
//
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }
//
//}
