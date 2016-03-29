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
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.LinearLayout;

import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.util.PhoneUtil;

import java.util.List;

public class FxService extends AccessibilityService implements View.OnClickListener {

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    Button btn_endCall, btn_answer, btn_slide;
    private static final String TAG = "FxService";
    private static String PHONE_STATE = "IDLE";
    private PhoneStateListener phoneStateListener;
    private boolean isAnswer = false;
    private String name, callCardTelocation, phoneNumber;

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
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        //设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.MATCH_PARENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(id, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        initKbView();
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    private void initKbView() {
        btn_endCall = (Button) mFloatLayout.findViewById(R.id.btn_endCall);
        btn_answer = (Button) mFloatLayout.findViewById(R.id.btn_answer);
        btn_slide = (Button) mFloatLayout.findViewById(R.id.btn_slide);
        btn_endCall.setOnClickListener(this);
        btn_answer.setOnClickListener(this);
        btn_answer.setContentDescription("双击接听");
        btn_endCall.setContentDescription("双击挂断");
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
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "TYPE_VIEW_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "TYPE_VIEW_FOCUSED";
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                eventText = "TYPE_VIEW_LONG_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                eventText = "TYPE_VIEW_SELECTED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                eventText = "TYPE_VIEW_TEXT_CHANGED";
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                eventText = "TYPE_NOTIFICATION_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                eventText = "TYPE_ANNOUNCEMENT";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                eventText = "TYPE_VIEW_HOVER_ENTER";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                eventText = "TYPE_VIEW_HOVER_EXIT";
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                eventText = "TYPE_VIEW_SCROLLED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                eventText = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                eventText = "TYPE_WINDOW_CONTENT_CHANGED";
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

    private void answerCall() {
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
        super.onDestroy();
        unregisterReceiver(phoneStateListener);
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
                        createFloatView(R.layout.kb_answer);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.e("hg", "电话状态……OFFHOOK");
                        PHONE_STATE = "OFFHOOK";
                        removeFxView();
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