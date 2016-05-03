package net.tatans.coeus.util;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class LockLayer {
	private Context mContext;
	private WindowManager mWindowManager;
	private View mLockView;
	private LayoutParams wmParams;
	private static LockLayer mLockLayer;
	private boolean isLocked;

	public static synchronized LockLayer getInstance(Context mContext) {
		if (mLockLayer == null) {
			mLockLayer = new LockLayer(mContext);
		}
		return mLockLayer;
	}

	public LockLayer(Context mContext) {
		this.mContext = mContext;
		init();
	}

	private void init() {
		isLocked = false;
		mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		wmParams = new LayoutParams();
		wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;//�ؼ�����
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.width = LayoutParams.MATCH_PARENT;
		wmParams.height = LayoutParams.MATCH_PARENT;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.flags = 1280;
	}

	public synchronized void lock() {
		if (mLockView != null && !isLocked) {
			mWindowManager.addView(mLockView, wmParams);
		}
		isLocked = true;
	}

	public synchronized void unlock() {
		if (mWindowManager != null && isLocked) {
			mWindowManager.removeView(mLockView);
		}
		isLocked = false;
	}

	public synchronized void update() {
		if (mLockView != null && !isLocked) {
			mWindowManager.updateViewLayout(mLockView, wmParams);
		}
	}

	public synchronized void setLockView(View v) {
		mLockView = v;
	}
}
