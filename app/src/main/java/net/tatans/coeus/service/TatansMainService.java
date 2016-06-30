package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.service.activity.TatansServiceApplication;
import net.tatans.coeus.service.tools.TatansService;


/**
  * ClassName :TatansMainService
  * @author: Yuriy
  * Created time : 2016/6/24 14:44.
  */
public class TatansMainService extends AccessibilityService {
    protected TatansService tatansService;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        tatansService = new TatansService();
        tatansService.attach(new EasySettingControl());
        tatansService.attach(new WeChatController());
        tatansService.attach(new InCallControl());
        tatansService.attach(new MessageSetControl());
        tatansService.attach(new SeeMoreController());
        tatansService.notifyInit();
        startService(new Intent(getApplication(), FxService.class));
    }

    @SuppressLint("NewApi")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        TatansLog.d("111111111111111111");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
//        FxService.isDestroy = true;
    }
}
