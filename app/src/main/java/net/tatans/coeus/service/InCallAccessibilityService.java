package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


/**
 * Created by John on 2016/1/5.
 */
public class InCallAccessibilityService extends AccessibilityService {
                    public static Boolean flag = false;
                    public static Boolean closed = true;

                    @Override
                    protected void onServiceConnected() {
                                        super.onServiceConnected();
                                        Log.d("myTag", "connected");
                    }

                    @SuppressLint("NewApi")
                    @Override
                    public void onAccessibilityEvent(AccessibilityEvent event) {
                                        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
                                        if (rowNode == null) {
                                                            return;
                                        } else {
                                            if ("com.android.dialer".equals(event.getPackageName()))
                                                            recycle(rowNode);
                                        }
                        this.processAccessibilityEnvent(event);

                    }

    private void processAccessibilityEnvent(AccessibilityEvent event) {

        Log.d("test", event.eventTypeToString(event.getEventType()));
        if (event.getSource() == null) {
            Log.d("test", "the source = null");
        } else {
//            Log.d("test", "event = " + event.toString());
            processKillApplication(event);
        }
    }

                    @Override
                    public void onInterrupt() {

                    }

                    @SuppressLint("InlinedApi")
                    public void recycle(AccessibilityNodeInfo info) {
                                        //通话界面第一次进来，作点击事件，打开拨号键盘
                                        CharSequence ContentDescription = info.getContentDescription();
                                        if(ContentDescription != null &&ContentDescription.equals("*")){
                                                            closed=false;
                                        }
                                        if (ContentDescription != null && ContentDescription.equals("拨号键盘")) {
                                                            if (info.isClickable() && flag &&closed) {

                                                                                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                                                flag = false;
                                                                                return;
                                                            }
                                        }

                                        if (info.getChildCount() == 0) {

                                        } else {
                                                            for (int i = info.getChildCount() - 1; i >= 0; i--) {
                                                                                if (info.getChild(i) != null) {
                                                                                                    recycle(info.getChild(i));
                                                                                }
                                                            }
                                        }
                    }


    private void processKillApplication(AccessibilityEvent event) {

        if (event.getSource() != null) {
            if (event.getPackageName().equals("com.android.settings")) {
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

}
