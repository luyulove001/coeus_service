package net.tatans.coeus.service.tools;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义水质监测的目标对象
 */
public abstract class AccessibilityServiceSubject {
	/**
	 * 用来保存注册的观察者对象
	 */
	protected List<TatansServiceImp> observers = new ArrayList<TatansServiceImp>();
	/**
	 * 注册观察者对象
	 * @param observer 观察者对象
	 */
	public void attach(TatansServiceImp observer) {
		observers.add(observer);
	}
	/**
	 * 删除观察者对象
	 * @param observer 观察者对象
	 */
	public void detach(TatansServiceImp observer) {
		observers.remove(observer);
	}
	/**
	 * 通知相应的观察者对象
	 */
	public abstract void notifyInterrupt();
	/**
	 * 通知相应的观察者对象
	 */
	public abstract void notifyInit();

	public abstract void unbind(Intent intent);
	/**
	 * 通知相应的观察者对象
	 */
	public abstract void notifyAccessibilityEvent(AccessibilityEvent acbEvent, AccessibilityNodeInfo acbNodeInfo);

}

