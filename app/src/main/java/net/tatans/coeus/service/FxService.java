package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.util.PhoneUtil;

import java.util.List;

public class FxService extends AccessibilityService implements View.OnClickListener, OnTouchListener {

    //定义浮动窗口布局
    LinearLayout mFloatLayout,lyt_full;
    LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    LinearLayout btn_endCall, btn_answer;
    private static final String TAG = "FxService";
    private static String PHONE_STATE = "IDLE";
    private boolean isAnswer = false;
    private String name, callCardTelocation, phoneNumber;
    private TelephonyManager telephonyManager;
    private TextView tv_number;
    private Speaker mSpeaker;
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取到系统提供的 电话的服务
        telephonyManager = (TelephonyManager) getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneLinstener(),
                PhoneStateListener.LISTEN_CALL_STATE);
        mSpeaker = Speaker.getInstance(this);
    }

    /**
     * 创建数字键盘悬浮窗界面
     */
    private void createFloatView(int id) {
        wmParams = new LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
        wmParams.type = LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.MATCH_PARENT;
        wmParams.height = LayoutParams.MATCH_PARENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(id, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        initKbView();
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    private GestureDetector mDetector;//屏幕监控
    private void initKbView() {
        mDetector = new GestureDetector(this, new mOnGestureListener());
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
                removeFxView();
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
        isAnswer = true;
        interrupt();
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
        Log.e(TAG, name + " -- " + phoneNumber + " -- " + callCardTelocation);
    }

    private String getNodeInfoText(List<AccessibilityNodeInfo> list) {
        String name = "";
        if (list.size() == 0) System.out.print("ID找不到");
        for (AccessibilityNodeInfo n : list) {
//            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            name = n.getText().toString();
            Log.e(TAG, name);
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
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    PHONE_STATE = "OFFHOOK";
                    removeFxView();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //查询该号码对应的名字
                    final String numbername = queryNumberName(incomingNumber);
                    PHONE_STATE = "RINGING";
                    createFloatView(R.layout.kb_answer);
                    tv_number.setText(numbername);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            interrupt();
                        }
                    }, 100);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSpeaker.speech(numbername);
                            Log.e("antony", "speech");
                        }
                    }, 200);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    removeFxView();
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

        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + incomingNumber);
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri,
                new String[]{"display_name"},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            String phoneName = cursor.getString(0);
            cursor.close();
            return phoneName;
        }
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
                removeFxView();
            } else if (e2.getY() - e1.getY() > 120) {
                removeFxView();
                PhoneUtil.endCall(FxService.this);
            }
            return false;
        }
    }
}