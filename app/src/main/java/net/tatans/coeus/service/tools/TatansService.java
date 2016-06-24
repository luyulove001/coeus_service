package net.tatans.coeus.service.tools;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.service.activity.TatansServiceApplication;

/**
 * Created by Yuriy on 2016/6/24.
 */
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
		if (null==acbEvent.getPackageName()){
			return;
		}
		for (int i=0;i<observers.size();i++){
			if (acbEvent.getPackageName().equals(TatansServiceApplication.getContentPackage(i))){
				TatansServiceImp watcher = observers.get(i);
				watcher.onAccessibilityEvent(acbEvent,acbNodeInfo);
			}
		}
	}
}