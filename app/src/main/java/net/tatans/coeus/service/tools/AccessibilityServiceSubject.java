package net.tatans.coeus.service.tools;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuriy on 2016/6/24.
 */
public abstract class AccessibilityServiceSubject {
	/**
	 * 用来保存注册的对象
	 */
	protected List<TatansServiceImp> observers = new ArrayList<TatansServiceImp>();

	public void attach(TatansServiceImp observer) {
		observers.add(observer);
	}

	public void detach(TatansServiceImp observer) {
		observers.remove(observer);
	}
	public void clear() {
		observers.clear();
	}

	public abstract void notifyInterrupt();

	public abstract void notifyInit();

	public abstract void unbind(Intent intent);

	public abstract void notifyAccessibilityEvent(AccessibilityService accessibilityService, AccessibilityEvent acbEvent, AccessibilityNodeInfo acbNodeInfo);

}

