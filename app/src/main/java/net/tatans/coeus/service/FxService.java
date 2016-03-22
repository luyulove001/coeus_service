package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.LinearLayout;

import net.tatans.coeus.util.PhoneUtil;

import java.util.List;

public class FxService extends AccessibilityService implements View.OnClickListener {

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    Button btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_0, btn_star, btn_pound, btn_endCall, btn_answer;
    private static final String TAG = "FxService";
    private static String PHONE_STATE = "IDLE";
    private PhoneStateListener phoneStateListener;
    private boolean isAnswer = false;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate");
        phoneStateListener = new PhoneStateListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(phoneStateListener, filter);
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
        if (id == R.layout.float_layout)
            wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        else if (id == R.layout.kb_answer)
            wmParams.flags = LayoutParams.FLAG_LOCAL_FOCUS_MODE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;
        //设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.MATCH_PARENT;
        wmParams.height = LayoutParams.MATCH_PARENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(id, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        if (id == R.layout.float_layout) initFloatView();
        else if (id == R.layout.kb_answer) initKbView();
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    private void initKbView() {
        btn_endCall = (Button) mFloatLayout.findViewById(R.id.btn_endCall);
        btn_answer = (Button) mFloatLayout.findViewById(R.id.btn_answer);
        btn_endCall.setOnClickListener(this);
        btn_answer.setOnClickListener(this);
    }

    private void initFloatView() {
        //浮动窗口按钮
        btn_1 = (Button) mFloatLayout.findViewById(R.id.btn_1);
        btn_2 = (Button) mFloatLayout.findViewById(R.id.btn_2);
        btn_3 = (Button) mFloatLayout.findViewById(R.id.btn_3);
        btn_4 = (Button) mFloatLayout.findViewById(R.id.btn_4);
        btn_5 = (Button) mFloatLayout.findViewById(R.id.btn_5);
        btn_6 = (Button) mFloatLayout.findViewById(R.id.btn_6);
        btn_7 = (Button) mFloatLayout.findViewById(R.id.btn_7);
        btn_8 = (Button) mFloatLayout.findViewById(R.id.btn_8);
        btn_9 = (Button) mFloatLayout.findViewById(R.id.btn_9);
        btn_0 = (Button) mFloatLayout.findViewById(R.id.btn_0);
        btn_star = (Button) mFloatLayout.findViewById(R.id.btn_star);
        btn_pound = (Button) mFloatLayout.findViewById(R.id.btn_pound);
        btn_endCall = (Button) mFloatLayout.findViewById(R.id.btn_endCall);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_0.setOnClickListener(this);
        btn_star.setOnClickListener(this);
        btn_pound.setOnClickListener(this);
        btn_endCall.setOnClickListener(this);
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
                    createFloatView(R.layout.kb_answer);
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
            case R.id.btn_1:
                clickByID("com.android.incallui:id/one");
                break;
            case R.id.btn_2:
                clickByID("com.android.incallui:id/two");
                break;
            case R.id.btn_3:
                clickByID("com.android.incallui:id/three");
                break;
            case R.id.btn_4:
                clickByID("com.android.incallui:id/four");
                break;
            case R.id.btn_5:
                clickByID("com.android.incallui:id/five");
                break;
            case R.id.btn_6:
                clickByID("com.android.incallui:id/six");
                break;
            case R.id.btn_7:
                clickByID("com.android.incallui:id/seven");
                break;
            case R.id.btn_8:
                clickByID("com.android.incallui:id/eight");
                break;
            case R.id.btn_9:
                clickByID("com.android.incallui:id/nine");
                break;
            case R.id.btn_0:
                clickByID("com.android.incallui:id/zero");
                break;
            case R.id.btn_star:
                clickByID("com.android.incallui:id/star");
                break;
            case R.id.btn_pound:
                clickByID("com.android.incallui:id/pound");
                break;
            case R.id.btn_endCall:
                removeFxView();
                PhoneUtil.endCall(FxService.this);
                break;
            case R.id.btn_answer:
                answerCall();
                removeFxView();
//                createFloatView(R.layout.float_layout);
                break;
        }
    }

    private void answerCall() {
        try {
            Log.e("Sandy", "for version 4.1 or larger");
            Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
            intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
            sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
        } catch (Exception e2) {
            Log.d("Sandy", "", e2);
            Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
            mediaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
            sendOrderedBroadcast(mediaButtonIntent, null);
        }
        isAnswer = true;
    }

    private void clickByID(String btn_id) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            System.out.println("rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(btn_id);
        if (list.size() == 0) System.out.println("ID没找到");
        for (AccessibilityNodeInfo n : list) {
            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        return super.onKeyEvent(event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 移除悬浮窗口
     */
    private void removeFxView() {
        if (mFloatLayout != null) {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
            mFloatLayout = null;
        }
    }

    private class PhoneStateListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                Log.e("hg", "呼出……OUTING");
                PHONE_STATE = "OUTGOING_CALL";
            }
            if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Service.TELEPHONY_SERVICE);
                switch (tm.getCallState()) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.e("hg", "电话状态……RINGING");
                        PHONE_STATE = "RINGING";
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.e("hg", "电话状态……OFFHOOK");
                        PHONE_STATE = "OFFHOOK";
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.e("hg", "电话状态……IDLE");
                        PHONE_STATE = "IDLE";
                        removeFxView();
                        isAnswer = false;
                        break;
                }
            }
        }
    }
}