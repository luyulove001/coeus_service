package net.tatans.coeus.service.tools;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.network.tools.TatansLog;

/**
 * Created by Yuriy on 2016/6/23.
 */

public class Tans extends Watcher{
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event, AccessibilityNodeInfo rowNode) {
        super.onAccessibilityEvent(event, rowNode);
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void onInterrupt() {
        super.onInterrupt();
    }

    @Override
    public void onUnbind(Intent intent) {
        super.onUnbind(intent);
    }
}
