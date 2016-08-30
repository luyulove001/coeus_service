package net.tatans.coeus.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class FloatViewControl {
    private Context mContext;
    private WindowManager mWindowManager;
    private View mFloatView;
    private LayoutParams wmParams;
    private static FloatViewControl mLockLayer;
    private boolean isShow;

    public static synchronized FloatViewControl getInstance(Context mContext) {
        if (mLockLayer == null) {
            mLockLayer = new FloatViewControl(mContext);
        }
        return mLockLayer;
    }

    public FloatViewControl(Context mContext) {
        this.mContext = mContext;
        init();
    }

    private void init() {
        isShow = false;
        mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wmParams = new LayoutParams();
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.width = LayoutParams.MATCH_PARENT;
        wmParams.height = LayoutParams.MATCH_PARENT;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.flags =
//                LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
//                LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        wmParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public synchronized void show() {
        if (mFloatView != null && !isShow) {
            mWindowManager.addView(mFloatView, wmParams);
        }
        isShow = true;
    }

    public synchronized void hide() {
        if (mWindowManager != null && isShow) {
            mWindowManager.removeView(mFloatView);
        }
        isShow = false;
    }

    public synchronized void update() {
        if (mFloatView != null && !isShow) {
            mWindowManager.updateViewLayout(mFloatView, wmParams);
        }
    }

    public synchronized void setLockView(View v) {
        mFloatView = v;
    }
}
