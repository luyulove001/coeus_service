package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.service.activity.EasySettingControl;
import net.tatans.coeus.service.activity.TatansServiceApplication;
import net.tatans.coeus.service.activity.WeChatController;
import net.tatans.coeus.service.tools.TatansService;


/**
  * ClassName :TatansAccessibilityService
  * @author: Yuriy
  * Created time : 2016/6/24 14:44.
  */
public class TatansAccessibilityService extends AccessibilityService {
    protected TatansService tatansService;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        tatansService = new TatansService();
        tatansService.attach(new EasySettingControl());
        tatansService.attach(new WeChatController());
        tatansService.notifyInit();
    }

    @SuppressLint("NewApi")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
        if (rowNode == null) {
            return;
        } else {
            tatansService.notifyAccessibilityEvent(this,event,rowNode);
        }
    }
    @Override
    public void onInterrupt() {
        tatansService.notifyInterrupt();
    }
    @Override
    public boolean onUnbind(Intent intent) {
        tatansService.unbind(intent);
        TatansServiceApplication.rmContentPackage();
        tatansService.clear();
        return super.onUnbind(intent);
    }


}
