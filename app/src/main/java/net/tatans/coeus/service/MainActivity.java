package net.tatans.coeus.service;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.network.speaker.Speaker;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.util.LockLayer;
import net.tatans.coeus.util.PhoneUtil;

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener{
    private LinearLayout lyt_full;
    private TextView tv_main_number, tv_main_end;
    private GestureDetector mDetector;//屏幕监控
    private View lock;
    public static LockLayer lockLayer;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lock = View.inflate(this, R.layout.activity_main, null);
        lockLayer = new LockLayer(this);
        lockLayer.setLockView(lock);
        lockLayer.lock();//是否开启锁屏
        activity=this;
        initView();
    }

    private void initView() {
        mDetector = new GestureDetector(this, new mOnGestureListener());
        tv_main_number = (TextView) lock.findViewById(R.id.tv_main_number);
        tv_main_end = (TextView) lock.findViewById(R.id.tv_main_end);
        tv_main_number.setText(queryNumberName(getIntent().getStringExtra("EXTRA_PHONE_NUMBER")));
        tv_main_end.setText("挂断");
        tv_main_end.setContentDescription("挂断。按钮");
        tv_main_end.setOnClickListener(this);
        lyt_full = (LinearLayout) lock.findViewById(R.id.lyt_full);
        lyt_full.setOnTouchListener(this);
        tv_main_number.setOnTouchListener(this);
        tv_main_end.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_end:
                PhoneUtil.endCall(MainActivity.this);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mDetector.onTouchEvent(event);
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
    }
}
