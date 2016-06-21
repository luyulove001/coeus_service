package net.tatans.coeus.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.network.speaker.Speaker;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.service.R;

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener{
    private LinearLayout mFloatLayout, lyt_full;
    private LinearLayout btn_endCall, btn_answer;
    private TelephonyManager telephonyManager;
    private TextView tv_number;
    private Speaker mSpeaker;
    private Handler handler = new Handler();
    private GestureDetector mDetector;//屏幕监控
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    private View lock;
    private LockLayer lockLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TatansLog.d("aaaaaaaaaaaaaaaaaa");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lock = View.inflate(this, R.layout.kb_answer, null);
        lockLayer = new LockLayer(this);
        lockLayer.setLockView(lock);
        lockLayer.lock();//是否开启锁屏
        initKbView();
    }

    private void initKbView() {
        telephonyManager = (TelephonyManager) getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneLinstener(),
                PhoneStateListener.LISTEN_CALL_STATE);
//        mSpeaker = Speaker.getInstance(this);
        mDetector = new GestureDetector(this, new mOnGestureListener());
        btn_endCall = (LinearLayout) lock.findViewById(R.id.btn_endCall);
        btn_answer = (LinearLayout) lock.findViewById(R.id.btn_answer);
        tv_number = (TextView) lock.findViewById(R.id.tv_number);
        lyt_full = (LinearLayout) lock.findViewById(R.id.lyt_full);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_endCall:
                removeFxView();
                PhoneUtil.endCall(MainActivity.this);
                break;
            case R.id.btn_answer:
                answerCall();
                removeFxView();
                break;
        }
    }

    @Override
    public void onDestroy() {
        telephonyManager.listen(null, PhoneStateListener.LISTEN_CALL_STATE);
        super.onDestroy();
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }


    public class MyPhoneLinstener extends PhoneStateListener {
        // 当电话拨打的状态改变的时候会调用的回调方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.i("antony", incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    removeFxView();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //查询该号码对应的名字
                    final String numbername = queryNumberName(incomingNumber);
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
//                            mSpeaker.speech(numbername);
                            Log.e("antony", "speech");
                        }
                    }, 200);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    removeFxView();
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
                PhoneUtil.endCall(MainActivity.this);
            }
            return false;
        }
    }

    /**
     * 移除悬浮窗口
     */
    public void removeFxView() {
        lockLayer.unlock();
        finish();
        TatansLog.d("挂断");
    }
}
