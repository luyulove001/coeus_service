package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Yuriy on 2016/7/22.
 */

public abstract class TatansService {
    public void onInit(){  };
    public void onInterrupt(){};
    public void onUnbind(Intent intent){};
    public void onAccessibilityEvent(AccessibilityService accessibilityService, AccessibilityEvent acbEvent, AccessibilityNodeInfo acbNodeInfo){};
}
