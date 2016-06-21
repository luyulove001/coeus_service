package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.network.speaker.Speaker;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.util.PhoneUtil;

import java.util.List;

public class FxService extends AccessibilityService implements View.OnClickListener, OnTouchListener {

    //定义浮动窗口布局
    private LinearLayout mFloatLayout, mAnswerLayout;
    private LinearLayout lyt_full;
    private LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;
    private LinearLayout btn_endCall, btn_answer;
    private TextView tv_main_number, tv_main_end;
    private String numbername;
    private static final String TAG = "FxService";
    private static String PHONE_STATE = "IDLE";
    private boolean isAnswer = false;
    private String name, callCardTelocation, phoneNumber;
    private TelephonyManager telephonyManager;
    private TextView tv_number;
    private Speaker mSpeaker;
    private Handler handler = new Handler();
    private boolean isCalling;
    PhoneBroadcastReceiver pbReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取到系统提供的 电话的服务
        telephonyManager = (TelephonyManager) getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneLinstener(),
                PhoneStateListener.LISTEN_CALL_STATE);
        mSpeaker = Speaker.getInstance(this);
        wmParams = new LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        mDetector = new GestureDetector(this, new mOnGestureListener());
        pbReceiver = new PhoneBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(pbReceiver, filter);
    }

    /**
     * 创建数字键盘悬浮窗界面
     */
    private void createFloatView(int id, LayoutParams wmParams) {
        removeFxView();
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(id, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        initKbView();
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    /**
     * 创建数字键盘悬浮窗界面
     */
    public void createView(LayoutParams wmParams) {
        removeFxView();
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mAnswerLayout = (LinearLayout) inflater.inflate(R.layout.activity_main, null);
        tv_main_number = (TextView) mAnswerLayout.findViewById(R.id.tv_main_number);
        tv_main_end = (TextView) mAnswerLayout.findViewById(R.id.tv_main_end);
        if (numbername != null || !"".equals(numbername))
            tv_main_number.setText(queryNumberName(numbername));
        tv_main_end.setText("挂断");
        tv_main_end.setContentDescription("挂断。按钮");
        tv_main_end.setOnClickListener(this);
        lyt_full = (LinearLayout) mAnswerLayout.findViewById(R.id.lyt_full);
        lyt_full.setOnTouchListener(this);
        tv_main_number.setOnTouchListener(this);
        tv_main_end.setOnTouchListener(this);
        //添加mFloatLayout
        mWindowManager.addView(mAnswerLayout, wmParams);
        mAnswerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    /**
     * 设置windowsManager属性， 参数为WindowsManager.LayoutParams中的属性
     *
     * @param type   LayoutParams.TYPE_
     * @param flags  LayoutParams.FLAG_
     * @param width
     * @param height
     */
    public LayoutParams initParams(int type, int flags, int width, int height) {
        //设置window type
        wmParams.type = type;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = flags;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //设置悬浮窗口长宽数据
        wmParams.width = width;
        wmParams.height = height;
        return wmParams;
    }

    private GestureDetector mDetector;//屏幕监控
    private void initKbView() {
        btn_endCall = (LinearLayout) mFloatLayout.findViewById(R.id.btn_endCall);
        btn_answer = (LinearLayout) mFloatLayout.findViewById(R.id.btn_answer);
        tv_number = (TextView) mFloatLayout.findViewById(R.id.tv_number);
        lyt_full= (LinearLayout) mFloatLayout.findViewById(R.id.lyt_full);
        btn_endCall.setOnClickListener(this);
        btn_answer.setOnClickListener(this);
        btn_answer.setContentDescription("点击接听。按钮");
        btn_endCall.setContentDescription("点击挂断。按钮");
        lyt_full.setOnTouchListener(this);
        btn_answer.setOnTouchListener(this);
        btn_endCall.setOnTouchListener(this);
        tv_number.setOnTouchListener(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        String eventText = "";
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                eventText = "TYPE_WINDOW_STATE_CHANGED";
                System.out.println(PHONE_STATE);
                if ("OFFHOOK".equals(PHONE_STATE) && mFloatLayout == null) {
//                    createFloatView(R.layout.float_layout);
                } else if ("RINGING".equals(PHONE_STATE) && mFloatLayout == null && !isAnswer) {
//                    createFloatView(R.layout.kb_answer);
                } else if ("IDLE".equals(PHONE_STATE)) {
                    removeFxView();
                }
                break;
        }
        System.out.println(eventText);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_endCall:
                removeFxView();
                PhoneUtil.endCall(FxService.this);
                break;
            case R.id.btn_answer:
                answerCall();
                break;
            case R.id.tv_main_end:
                removeFxView();
                PhoneUtil.endCall(FxService.this);
                break;
        }
    }

    public  void answerCall() {
        try {
            Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
            intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
            sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
        } catch (Exception e2) {
            Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
            mediaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
            sendOrderedBroadcast(mediaButtonIntent, null);
        }
        createView(initParams(LayoutParams.TYPE_SYSTEM_ERROR, LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        isAnswer = true;
        interrupt(500);
    }

    private void interrupt() {
        try {
            AccessibilityManager accessibilityManager = (AccessibilityManager) TatansApplication.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
            accessibilityManager.interrupt();
            TatansToast.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void interrupt(int score) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AccessibilityManager accessibilityManager = (AccessibilityManager) TatansApplication.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
                    accessibilityManager.interrupt();
                    TatansToast.cancel();
                    TatansLog.e("打断");
                }
            }, score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clickByID() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            System.out.println("rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.incallui:id/name");
        name = getNodeInfoText(nodeInfoList);
        nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.incallui:id/phoneNumber");
        phoneNumber = getNodeInfoText(nodeInfoList);
        nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.incallui:id/callCardTelocation");
        callCardTelocation = getNodeInfoText(nodeInfoList);
        TatansLog.e(name + " -- " + phoneNumber + " -- " + callCardTelocation);
    }

    private String getNodeInfoText(List<AccessibilityNodeInfo> list) {
        String name = "";
        if (list.size() == 0) System.out.print("ID找不到");
        for (AccessibilityNodeInfo n : list) {
//            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            name = n.getText().toString();
            TatansLog.e(name);
        }
        return name;
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        return super.onKeyEvent(event);
    }

    @Override
    public void onDestroy() {
        telephonyManager.listen(null, PhoneStateListener.LISTEN_CALL_STATE);
        unregisterReceiver(pbReceiver);
        super.onDestroy();
    }

    /**
     * 移除悬浮窗口
     */
    public void removeFxView() {
        if (mFloatLayout != null) {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
            mFloatLayout = null;
        } else {
            TatansLog.d("mFloatLayout为空");
        }
    }

    public void removeAnswerView(){
        if (mAnswerLayout != null){
            mWindowManager.removeView(mAnswerLayout);
            mAnswerLayout = null;
        } else {
            TatansLog.d("mAnswerLayout为空");
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
            TatansLog.i(incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    PHONE_STATE = "OFFHOOK";
//                    removeFxView();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //查询该号码对应的名字
                    numbername = queryNumberName(incomingNumber);
                    PHONE_STATE = "RINGING";
                    isCalling = true;
                    createFloatView(R.layout.kb_answer, initParams(LayoutParams.TYPE_PHONE, LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    tv_number.setText(numbername);
                    interrupt(100);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSpeaker.speech(numbername);
                            TatansLog.e("speech");
                        }
                    }, 500);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    removeFxView();
                    removeAnswerView();
                    isCalling = false;
                    PHONE_STATE = "IDLE";
                    isAnswer = false;
                    break;
            }
        }
    }

    /**
     * 通过内容提供者 查询当前手机号码所对应的人名
     *
     * @param incomingNumber
     */
    public String queryNumberName(String incomingNumber) {
//        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + incomingNumber);
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER };
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

        return incomingNumber;
    }

    /**
     * 手势控制暂停播放
     * @author SiLiPing
     */
    private class mOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // 向上的手势
            if (e1.getY() - e2.getY() > 120) {
                answerCall();
            } else if (e2.getY() - e1.getY() > 120) {
                removeFxView();
                PhoneUtil.endCall(FxService.this);
            }
            return false;
        }
    }
    public class PhoneBroadcastReceiver extends BroadcastReceiver implements
            SensorEventListener {

        TelephonyManager tManager;
        AudioManager audioManager;
        SensorManager sensorManager;

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())) {
                String EXTRA_PHONE_NUMBER = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                numbername = EXTRA_PHONE_NUMBER;
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("EXTRA_PHONE_NUMBER", EXTRA_PHONE_NUMBER);
                isCalling = true;
                context.startActivity(i);
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
                        TatansLog.d("comming");
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
                        TatansLog.d("online");
                        break;

                    // 挂断
                    case TelephonyManager.CALL_STATE_IDLE:
                        InCallAccessibilityService.flag = true;
                        InCallAccessibilityService.closed = true;
                        sensorManager.unregisterListener(this);
                        TatansLog.d("hangup");
                        if (MainActivity.lockLayer != null && MainActivity.activity != null) {
                            MainActivity.lockLayer.unlock();
                            MainActivity.activity.finish();
                        }
                        break;
                }
            }
        }

        private int firstSensor = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] its = event.values;
            if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (audioManager.isWiredHeadsetOn() || audioManager.isBluetoothScoOn() || its[0] == 0.0) {
                    audioManager.setSpeakerphoneOn(false);
                    if (MainActivity.lockLayer != null && MainActivity.activity != null) {
                        MainActivity.lockLayer.unlock();
                        MainActivity.activity.finish();
                    }
                    if (isCalling) {
                        removeAnswerView();
                        interrupt(450);
                        interrupt(600);
                        WindowManager.LayoutParams wmParams = initParams(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        createView(wmParams);
                    }
                    firstSensor = 1;
                    TatansLog.d("222222");
                } else if (firstSensor == 1) {
                    audioManager.setSpeakerphoneOn(true);
                    FxService.interrupt(200);
                    TatansLog.d("1111");
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }
}
