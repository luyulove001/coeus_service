package net.tatans.coeus.service.activity;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.service.tools.AccessibilityServiceSubject;
import net.tatans.coeus.service.tools.TatansServiceImp;

/**
 * Created by Yuriy on 2016/6/24.
 */
public class Demo1 implements TatansServiceImp {
	private static String sPackage;

	public void update(AccessibilityServiceSubject subject) {

	}
	@Override
	public void onInit() {
		sPackage="com.qihoo360.mobilesafe";
		TatansLog.d("onInit()1");
		TatansServiceApplication.setContentPackage("com.qihoo360.mobilesafe");
	}

	@Override
	public void onInterrupt() {
		TatansLog.d("onInterrupt()1");
	}

	@Override
	public void onUnbind(Intent intent) {
		TatansLog.d("onUnbind()1");
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event, AccessibilityNodeInfo rowNode) {
		/*if (event.getPackageName().equals(sPackage)){

		}*/
		TatansLog.d("1111111"+event.getPackageName());
	}
}
