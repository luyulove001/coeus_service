package net.tatans.coeus.service.tools;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public interface TatansServiceImp {
	/**
	 * 被通知的方法
	 * @param subject 传入被观察的目标对象
	 */
	public void update(AccessibilityServiceSubject subject);
	public void onInit();
	public void onInterrupt();
	public void onUnbind(Intent intent);
	public void onAccessibilityEvent(AccessibilityEvent acbEvent, AccessibilityNodeInfo acbNodeInfo);

}