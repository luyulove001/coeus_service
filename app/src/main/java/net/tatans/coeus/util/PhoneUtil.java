package net.tatans.coeus.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PhoneUtil {

    public static String TAG = PhoneUtil.class.getSimpleName();

    /**
     * 挂断
     *
     * @param context
     */
    public static void endCallByTelephony(Context context) {
        try {
            Object telephonyObject = getTelephonyObject(context);
            if (null != telephonyObject) {
                Class telephonyClass = telephonyObject.getClass();

                Method endCallMethod = telephonyClass.getMethod("endCall");
                endCallMethod.setAccessible(true);

                endCallMethod.invoke(telephonyObject);
                Log.e("antony", "挂断");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * 挂断
     *
     * @param context
     */
    public static void endCallByTelecom(Context context) {
        try {
            Object telecomObject = getTelecomObject(context);
            Log.e("antony", "null != telecomObject--" + (null != telecomObject));
            if (null != telecomObject) {
                Class telecomObjectClass = telecomObject.getClass();
                Method endCallMethod = telecomObjectClass.getMethod("endCall");
                endCallMethod.setAccessible(true);
                endCallMethod.invoke(telecomObject);
                Log.e("antony", "挂断");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static Object getTelecomObject(Context context) {
        Object telecomObject = null;
        try {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            // Will be used to invoke hidden methods with reflection
            // Get the current object implementing ITelephony interface
            Class telManager = telecomManager.getClass();
            Method getITelecom = telManager.getDeclaredMethod("getTelecomService");
            getITelecom.setAccessible(true);
            telecomObject = getITelecom.invoke(telecomManager);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return telecomObject;
    }

    private static Object getTelephonyObject(Context context) {
        Object telephonyObject = null;
        try {
            // ��ʼ��iTelephony
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            // Will be used to invoke hidden methods with reflection
            // Get the current object implementing ITelephony interface
            Class telManager = telephonyManager.getClass();
            Method getITelephony = telManager
                    .getDeclaredMethod("getITelephony");
            getITelephony.setAccessible(true);
            telephonyObject = getITelephony.invoke(telephonyManager);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return telephonyObject;
    }

    /**
     * 通过反射接听
     *
     * @param context
     */
    private static void answerRingingCallWithReflect(Context context) {
        try {
            Object telephonyObject = getTelephonyObject(context);
            if (null != telephonyObject) {
                Class telephonyClass = telephonyObject.getClass();
                Method endCallMethod = telephonyClass
                        .getMethod("answerRingingCall");
                endCallMethod.setAccessible(true);

                endCallMethod.invoke(telephonyObject);
                // ITelephony iTelephony = (ITelephony) telephonyObject;
                // iTelephony.answerRingingCall();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过广播接听（模拟插入耳机）
     *
     * @param context
     */
    private static void answerRingingCallWithBroadcast(Context context) {
        AudioManager localAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        // �ж��Ƿ�����˶���
        boolean isWiredHeadsetOn = localAudioManager.isWiredHeadsetOn();
        if (!isWiredHeadsetOn) {
            Intent headsetPluggedIntent = new Intent(Intent.ACTION_HEADSET_PLUG);
            headsetPluggedIntent.putExtra("state", 1);
            headsetPluggedIntent.putExtra("microphone", 0);
            headsetPluggedIntent.putExtra("name", "");
            context.sendBroadcast(headsetPluggedIntent);

            Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
                    KeyEvent.KEYCODE_HEADSETHOOK);
            meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
            context.sendOrderedBroadcast(meidaButtonIntent, null);

            Intent headsetUnpluggedIntent = new Intent(
                    Intent.ACTION_HEADSET_PLUG);
            headsetUnpluggedIntent.putExtra("state", 0);
            headsetUnpluggedIntent.putExtra("microphone", 0);
            headsetUnpluggedIntent.putExtra("name", "");
            context.sendBroadcast(headsetUnpluggedIntent);
        } else {
            Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
                    KeyEvent.KEYCODE_HEADSETHOOK);
            meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
            context.sendOrderedBroadcast(meidaButtonIntent, null);
        }
    }

    /**
     * 接听
     *
     * @param context
     */
    public static void answerRingingCall(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) { // 2.3��2.3����ϵͳ
            answerRingingCallWithBroadcast(context);
        } else {
            answerRingingCallWithReflect(context);
        }
    }

    /**
     * 打电话
     *
     * @param context
     * @param phoneNumber
     */
    public static void callPhone(Context context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + phoneNumber));
                context.startActivity(callIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 调用打电话界面
     *
     * @param context
     * @param phoneNumber
     */
    public static void dialPhone(Context context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + phoneNumber));
                context.startActivity(callIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}