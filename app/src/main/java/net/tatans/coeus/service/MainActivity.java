package net.tatans.coeus.service;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.util.LockLayer;
import net.tatans.coeus.util.PhoneUtil;

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener{
    private LinearLayout lyt_full;
    private TextView tv_main_number, tv_main_end, tv_main_more;
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
        tv_main_more = (TextView) lock.findViewById(R.id.tv_main_more);
        tv_main_number.setText(queryNameByNum(getIntent().getStringExtra("EXTRA_PHONE_NUMBER"), MainActivity.this));
        lyt_full = (LinearLayout) lock.findViewById(R.id.lyt_full);
        lyt_full.setContentDescription(queryNameByNum(getIntent().getStringExtra("EXTRA_PHONE_NUMBER"), MainActivity.this));
        tv_main_end.setText("挂断");
        tv_main_end.setContentDescription("挂断。按钮");
        tv_main_end.setOnClickListener(this);
        tv_main_more.setText("更多");
        tv_main_more.setContentDescription("更多。按钮");
        tv_main_more.setOnClickListener(this);
        lyt_full = (LinearLayout) lock.findViewById(R.id.lyt_full);
        lyt_full.setOnTouchListener(this);
        tv_main_number.setOnTouchListener(this);
        tv_main_end.setOnTouchListener(this);
        tv_main_more.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_end:
                endCall();
                break;
            case R.id.tv_main_more:
                removeFxView();
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
     */
    public String queryNumberName(String incomingNumber) {
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

    public static String queryNameByNum(String num, Context context) {
        Cursor cursorOriginal =
                context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                        ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + num + "'", null, null);
        if (null != cursorOriginal) {
            if (cursorOriginal.getCount() > 1) {
                return null;
            } else {
                if (cursorOriginal.moveToFirst()) {
                    return cursorOriginal.getString(cursorOriginal.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
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
                endCall();
            }
            return false;
        }
    }

    private void endCall() {
        PhoneUtil.endCall(MainActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeFxView();
            }
        }, 500);
    }

    /**
     * 移除悬浮窗口
     */
    public void removeFxView() {
        lockLayer.unlock();
        finish();
    }
}
