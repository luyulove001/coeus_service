package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.service.activity.TatansServiceApplication;
import net.tatans.coeus.util.FloatViewControl;
import net.tatans.coeus.util.NumberAddressQueryUtils;
import net.tatans.coeus.util.PhoneUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FxService extends AccessibilityService implements View.OnClickListener, OnTouchListener {

    //定义浮动窗口布局
    private View mAnswerLayout, mFloatView;
    private LinearLayout lytFull;
    private LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private static WindowManager mWindowManager;
    private String numberName;
    private static final String TAG = "FxService";
    private TelephonyManager telephonyManager;
    private TextView tvNumber;
    public static boolean isDestroy;
    private AudioManager audioManager;
    private SensorManager sensorManager;
    private SensorProximity sensor;
    private boolean isCalling;
    private FloatViewControl mControl;
    private GestureDetector mDetector;//屏幕监控

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取到系统提供的 电话的服务
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
        sensorManager = (SensorManager) getApplication().getSystemService(Context.SENSOR_SERVICE);
        sensor = new SensorProximity();
        telephonyManager.listen(new MyPhoneLinstener(),
                PhoneStateListener.LISTEN_CALL_STATE);
        wmParams = new LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        mDetector = new GestureDetector(this, new mOnGestureListener());
        mControl = new FloatViewControl(getApplicationContext());
        copyDB();
        isDestroy = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            isCalling = intent.getBooleanExtra("isCalling", false);
            if (isCalling) {
                removeAllView();
                String number = intent.getStringExtra("EXTRA_PHONE_NUMBER");
                numberName = queryNumberName(number);
                createFloatView(R.layout.activity_main);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 创建悬浮窗界面
     */
    private void createFloatView(int id) {
        interrupt();
        mControl.hide();
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatView = inflater.inflate(id, null);
        mFloatView.setContentDescription("。");
        if (id == R.layout.kb_answer)
            initRingingView();
        else if (id == R.layout.activity_main)
            initOffHookView();
        mControl.setLockView(mFloatView);
        mControl.show();
    }

    /**
     * 初始化接听界面
     */
    private void initOffHookView() {
        TextView tvMainNumber = (TextView) mFloatView.findViewById(R.id.tv_main_number);
        TextView tvMainEnd = (TextView) mFloatView.findViewById(R.id.tv_main_end);
        TextView tvMainMore = (TextView) mFloatView.findViewById(R.id.tv_main_more);
        lytFull = (LinearLayout) mFloatView.findViewById(R.id.lyt_full);
        tvMainMore.setText("更多");
        tvMainMore.setContentDescription("更多。按钮");
        if (numberName != null || !"".equals(numberName)) {
            tvMainNumber.setText(numberName);
            lytFull.setContentDescription(numberName);
        }
        tvMainEnd.setText("挂断");
        tvMainEnd.setContentDescription("挂断。按钮");
        tvMainEnd.setOnClickListener(this);
        tvMainMore.setOnClickListener(this);
        lytFull = (LinearLayout) mFloatView.findViewById(R.id.lyt_full);
        lytFull.setOnTouchListener(this);
        tvMainNumber.setOnTouchListener(this);
        tvMainEnd.setOnTouchListener(this);
        tvMainMore.setOnTouchListener(this);
    }

    /**
     * 接听界面初始化
     */
    private void initRingingView() {
        LinearLayout btnEndCall = (LinearLayout) mFloatView.findViewById(R.id.btn_endCall);
        LinearLayout btnAnswer = (LinearLayout) mFloatView.findViewById(R.id.btn_answer);
        tvNumber = (TextView) mFloatView.findViewById(R.id.tv_number);
        lytFull = (LinearLayout) mFloatView.findViewById(R.id.lyt_full);
        btnEndCall.setOnClickListener(this);
        btnAnswer.setOnClickListener(this);
        btnAnswer.setContentDescription("点击接听。按钮");
        btnEndCall.setContentDescription("点击挂断。按钮");
        lytFull.setOnTouchListener(this);
        btnAnswer.setOnTouchListener(this);
        btnEndCall.setOnTouchListener(this);
        tvNumber.setOnTouchListener(this);
    }

    /**
     * 接听界面
     */
    private void createView() {
        mControl.hide();
        //设置window type
        wmParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.MATCH_PARENT;
        wmParams.height = LayoutParams.MATCH_PARENT;
        wmParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mAnswerLayout = (LinearLayout) inflater.inflate(R.layout.activity_main, null);
        TextView tv_main_number = (TextView) mAnswerLayout.findViewById(R.id.tv_main_number);
        TextView tv_main_end = (TextView) mAnswerLayout.findViewById(R.id.tv_main_end);
        TextView tv_main_more = (TextView) mAnswerLayout.findViewById(R.id.tv_main_more);
        lytFull = (LinearLayout) mAnswerLayout.findViewById(R.id.lyt_full);
        tv_main_more.setText("更多");
        tv_main_more.setContentDescription("更多。按钮");
        if (numberName != null || !"".equals(numberName)) {
            tv_main_number.setText(numberName);
            lytFull.setContentDescription(numberName);
        }
        tv_main_end.setText("挂断");
        tv_main_end.setContentDescription("挂断。按钮");
        tv_main_end.setOnClickListener(this);
        tv_main_more.setOnClickListener(this);
        lytFull = (LinearLayout) mAnswerLayout.findViewById(R.id.lyt_full);
        lytFull.setOnTouchListener(this);
        tv_main_number.setOnTouchListener(this);
        tv_main_end.setOnTouchListener(this);
        tv_main_more.setOnTouchListener(this);
        //添加mFloatLayout
        mWindowManager.addView(mAnswerLayout, wmParams);
        mAnswerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_end:
            case R.id.btn_endCall:
                endCall();
                break;
            case R.id.btn_answer:
                answerCall();
                break;
            case R.id.tv_main_more:
                removeAnswerView();
                mControl.hide();
                break;
        }
    }

    private void endCall() {
        PhoneUtil.endCallByTelecom(FxService.this);
        isCalling = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeAnswerView();
                mControl.hide();
            }
        }, 800);
    }

    public void answerCall() {
        //5.1接听方法
        try {
            Runtime.getRuntime().exec("input keyevent " +
                    Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
        } catch (IOException e) {
            String enforcedPerm = "android.permission.CALL_PRIVILEGED";
            Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                    Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_HEADSETHOOK));
            Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                    Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_HEADSETHOOK));
            sendOrderedBroadcast(btnDown, enforcedPerm);
            sendOrderedBroadcast(btnUp, enforcedPerm);
        }
        createView();
        interrupt(500);
    }

    private void interrupt() {
        try {
            AccessibilityManager accessibilityManager = (AccessibilityManager) TatansApplication.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
            accessibilityManager.interrupt();
            TatansToast.cancel();
            Log.e("antony", "打断");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void interrupt(int score) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    AccessibilityManager accessibilityManager = (AccessibilityManager) TatansApplication.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
                    accessibilityManager.interrupt();
                    TatansToast.cancel();
                    Log.e("antony", "打断");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, score);
    }


    @Override
    public void onDestroy() {
        telephonyManager.listen(null, PhoneStateListener.LISTEN_CALL_STATE);
        isDestroy = true;
        TatansLog.e("onDestroy --- isDestory:" + isDestroy);
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        isDestroy = true;
        TatansLog.e("onUnbind --- isDestory:" + isDestroy);
        return super.onUnbind(intent);
    }

    /**
     * 移除悬浮窗口
     */
    public void removeAnswerView() {
        if (mAnswerLayout != null) {
            mWindowManager.removeView(mAnswerLayout);
            mAnswerLayout = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }


    public class MyPhoneLinstener extends PhoneStateListener {
        // 当电话拨打的状态改变的时候会调用的回调方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.i(TAG, incomingNumber);
            if (isDestroy) return;
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    removeFxView();
                    TatansServiceApplication.stopAllSound();
                    sensorManager.registerListener(sensor,
                            sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                            SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    removeAllView();
                    //查询该号码对应的名字
                    numberName = queryNumberName(incomingNumber);
                    createFloatView(R.layout.kb_answer);
                    interrupt();
                    tvNumber.setText(numberName);
                    TatansServiceApplication.speech("来电:" + numberName + "。来电:" + numberName);
                    Log.e("antony", "speech");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    sensorManager.unregisterListener(sensor);
                    removeAllView();
                    TatansServiceApplication.stopAllSound();
                    break;
            }
        }
    }

    private void removeAllView() {
        removeAnswerView();
        mControl.hide();
    }

    /**
     * 通过内容提供者 查询当前手机号码所对应的人名
     *
     * @param incomingNumber
     */
    public String queryNumberName(String incomingNumber) {
//        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + incomingNumber);
//        String city = PhoneUtil.mobileNumber(incomingNumber);
        String city = NumberAddressQueryUtils.queryNumber(incomingNumber);
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER};
        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(incomingNumber));
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri,
                projection,
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            String phoneName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));// 缓存的名称与电话号码，如果它的存在
            if (TextUtils.isEmpty(phoneName))
                continue;
            return phoneName;
        }
        cursor.close();

        return incomingNumber + "\n" + city;
    }

    /**
     * 手势控制接听挂断
     */
    private class mOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // 向上的手势
            if (e1.getY() - e2.getY() > 120) {
                answerCall();
            } else if (e2.getY() - e1.getY() > 120) {
                endCall();
            }
            return false;
        }
    }

    private void copyDB() {
        try {
            String path = getApplicationContext().getFilesDir()
                    .getAbsolutePath() + "address.db";   //data/data目录
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
            } else {
                InputStream is = getAssets().open("address.db");
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SensorProximity implements SensorEventListener {

        private int firstSensor = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] its = event.values;
            if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (audioManager.isWiredHeadsetOn() || audioManager.isBluetoothScoOn() || its[0] == 0.0) {
                    //靠近手机，设置扬声器外放false
                    audioManager.setSpeakerphoneOn(false);
                    FxService.interrupt(450);
                    FxService.interrupt(600);
                    firstSensor = 1;
                } else if (firstSensor == 1) {
                    //离开手机，设置扬声器外方true
                    audioManager.setSpeakerphoneOn(true);
                    FxService.interrupt(200);
                } else {
                    //默认听筒
                    audioManager.setSpeakerphoneOn(false);
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}