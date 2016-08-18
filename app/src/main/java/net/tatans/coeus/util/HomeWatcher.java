package net.tatans.coeus.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import net.tatans.coeus.util.OnHomeKeyEven;

/**
 * Home键监听封装
 * 
 * @author Yuriy
 * 
 */
public class HomeWatcher {

	static final String TAG = "HomeWatcher";
	private Context mContext;
	private IntentFilter mFilter;
	private OnHomePressedListener mListener;
	private InnerRecevier mRecevier;
	private static HomeWatcher mHomeWatcher;

	public static synchronized HomeWatcher getInstance(Context context){
		if(mHomeWatcher==null){
			mHomeWatcher = new HomeWatcher(context);
		}
		return mHomeWatcher;
	}
	public HomeWatcher(Context context) {
		mContext = context;
		mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	}

	/**
	 * 设置监听
	 * 
	 * @param listener
	 */
	public void setOnHomePressedListener(OnHomePressedListener listener) {
		mListener = listener;
		mRecevier = new InnerRecevier();
	}

	/**
	 * 开始监听，注册广播
	 */
	public void startWatch() {
		if (mRecevier != null) {
			mContext.registerReceiver(mRecevier, mFilter);
		}
	}

	/**
	 * 停止监听，注销广播
	 */
	public void stopWatch() {
		if (mRecevier != null) {
			try {
				mContext.unregisterReceiver(mRecevier);
			} catch (IllegalArgumentException e) {

			}
		}
	}

	/**
	 * 广播接收者
	 */
	class InnerRecevier extends BroadcastReceiver {
		static final String SYSTEM_DIALOG_REASON_KEY = "reason";
		static final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
		static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
		static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				if (reason != null) {
					Log.e(TAG, "action:" + action + ",reason:" + reason);
					if (mListener != null) {
						if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
							// 短按home键
							mListener.onHomeKeyPressed(OnHomeKeyEven.KEYCODE_SHORT_PRESS);
						} else if (reason
								.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
							// 长按home键
							mListener.onHomeKeyPressed(OnHomeKeyEven.KEYCODE_LONG_PRESS);
						}
					}
				}
			}
		}
	}
}