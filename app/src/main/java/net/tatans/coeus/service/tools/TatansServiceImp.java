package net.tatans.coeus.service.tools;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
/**
 * Created by Yuriy on 2016/6/24.
 */
public interface TatansServiceImp {
	/**
	 * 被通知的方法
	 */
	public void onInit();
	public void onInterrupt();
	public void onUnbind(Intent intent);
	public void onAccessibilityEvent(AccessibilityEvent acbEvent, AccessibilityNodeInfo acbNodeInfo);

}