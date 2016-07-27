package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MessageSetControl extends TatansService {

    @Override
    public void onAccessibilityEvent(AccessibilityService accessibilityService, AccessibilityEvent acbEvent, AccessibilityNodeInfo acbNodeInfo) {
        this.processAccessibilityEvent(acbEvent);
    }

    private void processAccessibilityEvent(AccessibilityEvent event) {
        Log.d("test", event.eventTypeToString(event.getEventType()));
        if (event.getSource() == null) {
            Log.d("test", "the source = null");
        } else {
            processKillApplication(event);
        }
    }

    private void processKillApplication(AccessibilityEvent event) {

        if (event.getSource() != null) {
            List<AccessibilityNodeInfo> stop_nodes = event.getSource().findAccessibilityNodeInfosByText("默认短信");
            if (stop_nodes != null && !stop_nodes.isEmpty()) {
                List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText("确定");
                if (ok_nodes != null && !ok_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for (int i = 0; i < ok_nodes.size(); i++) {
                        node = ok_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button")) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            Log.d("action", "click ok");
                        }
                    }
                }
                List<AccessibilityNodeInfo> xiayibu_nodes = event.getSource().findAccessibilityNodeInfosByText("下一步");
                if (xiayibu_nodes != null && !xiayibu_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for (int i = 0; i < xiayibu_nodes.size(); i++) {
                        node = xiayibu_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button")) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            FxService.interrupt(0);
                            Log.d("action", "click ok");
                        }
                    }
                }
            }
        }
    }
}
