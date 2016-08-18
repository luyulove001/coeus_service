package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.util.HomeWatcher;
import net.tatans.coeus.util.OnHomeKeyEven;
import net.tatans.coeus.util.OnHomePressedListener;

/**
 * Created by Yuriy on 2016/7/22.
 */

public abstract class TatansService implements OnHomePressedListener {
    private static HomeWatcher mHomeWatcher;

    public void onInit(){  }

    public void onInterrupt(){}

    public void onUnbind(Intent intent){}

    public void onAccessibilityEvent(AccessibilityService accessibilityService, AccessibilityEvent acbEvent, AccessibilityNodeInfo acbNodeInfo){
    }

    protected void startHomeKeyPressed(AccessibilityService accessibilityService){
        mHomeWatcher = HomeWatcher.getInstance(accessibilityService);
        mHomeWatcher.setOnHomePressedListener(this);
        mHomeWatcher.startWatch();
    }

    @Override
    public void onHomeKeyPressed(OnHomeKeyEven onHomeKeyEven) {

    }
    protected void stopHomeKeyPressed(){
        mHomeWatcher.stopWatch();
    }
}
