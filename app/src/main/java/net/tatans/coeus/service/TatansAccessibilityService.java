package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.service.activity.EasySettingControl;
import net.tatans.coeus.service.activity.TatansServiceApplication;
import net.tatans.coeus.service.activity.WeChatController;
import net.tatans.coeus.service.tools.TatansService;
import net.tatans.coeus.service.activity.Demo1;
import net.tatans.coeus.service.activity.Demo2;
import net.tatans.coeus.util.FloatView;

import java.util.List;


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
        tatansService.attach(new Demo1());
        tatansService.attach(new Demo2());
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
            tatansService.notifyAccessibilityEvent(event,rowNode);
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
