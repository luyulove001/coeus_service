package net.tatans.coeus.service.tools;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


public class TatansService extends AccessibilityServiceSubject {


	@Override
	public void notifyInterrupt() {
		for(TatansServiceImp watcher : observers){
			watcher.onInterrupt();
		}
	}

	@Override
	public void notifyInit() {
		for(TatansServiceImp watcher : observers){
			watcher.onInit();
		}
	}

	@Override
	public void unbind(Intent intent) {
		for(TatansServiceImp watcher : observers){
			watcher.onUnbind(intent);
		}
	}

	@Override
	public void notifyAccessibilityEvent(AccessibilityEvent acbEvent, AccessibilityNodeInfo acbNodeInfo) {
		if ((acbEvent.getPackageName()).equals("")){

		}
		for(TatansServiceImp watcher : observers){
			watcher.onAccessibilityEvent(acbEvent,acbNodeInfo);
		}
	}
}