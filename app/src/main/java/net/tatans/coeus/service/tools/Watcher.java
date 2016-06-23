package net.tatans.coeus.service.tools;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.network.tools.TatansLog;

/**
 * 具体的观察者实现
 */
public class Watcher implements TatansServiceImp {
	private static String sPackage;

	public void update(AccessibilityServiceSubject subject) {
	}

	@Override
	public void onInit() {
		sPackage="com.qihoo360.mobilesafe";
		TatansLog.d("onInit()1");
	}
	public void setContentPackage(String string){

	}
	public void getContentPackage(){

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
		//TatansLog.d(""+event.getPackageName());
	}
}
