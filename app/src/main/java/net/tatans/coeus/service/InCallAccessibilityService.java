package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.service.activity.Demo1;
import net.tatans.coeus.service.activity.Demo2;
import net.tatans.coeus.service.tools.TatansService;
import net.tatans.coeus.util.FloatView;

import java.util.List;


/**
  * ClassName :TatansAccessibilityService
  * explain :拨号盘自动打开处理
  * @author: syf
  * Created time : 2016/6/7 14:44.
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

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                if (event.getPackageName().equals("com.android.settings")) {
                    Log.e("acess", "onAccessibilityEvent ");
                    easySettingApplication(getApplication(), getRootInActiveWindow());
                }
        }
        this.processAccessibilityEnvent(event);
    }

 /**
   * Purpose:分发检测到的事件
   * explain:
   * @param event
   * @author: syf
   * Created time : 2016/6/7 14:46.
   */
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
        TatansLog.e("----------");
    }

    @SuppressLint("InlinedApi")
    public void recycle(AccessibilityNodeInfo info) {
        //通话界面第一次进来，作点击事件，打开拨号键盘
        CharSequence ContentDescription = info.getContentDescription();
        if (ContentDescription != null && ContentDescription.equals("*")) {
            closed = false;
        }
        if (ContentDescription != null && ContentDescription.equals("拨号键盘")) {
            if (info.isClickable() && flag && closed) {

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

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * xzb   极简模式覆盖窗口
     * @param application
     * @param accessibilityNodeInfo
     */
    public void easySettingApplication(android.app.Application application, final AccessibilityNodeInfo accessibilityNodeInfo) {
        if (accessibilityNodeInfo != null) {
            final List<AccessibilityNodeInfo> ok_nodes = accessibilityNodeInfo.findAccessibilityNodeInfosByText("极简模式设置");
            if (!ok_nodes.isEmpty()) {
                AccessibilityNodeInfo node;
                for (int i = 0; i < ok_nodes.size(); i++) {
                    node = ok_nodes.get(i);
                    if (node.getClassName().equals("android.widget.TextView") && node.isEnabled()) {
                        WindowManager.LayoutParams lp = FloatView.createDefaultLayoutParams();
                        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        final View settingView = FloatView.createFloatView(application, R.layout.easysetting, lp);
                        settingView.findViewById(R.id.out).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FloatView.destoryView();
                                accessibilityNodeInfo.getContentDescription();
                                List<AccessibilityNodeInfo> rl_item = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("miui:id/up");
                                Log.e("**", "onClick rl_item" + rl_item.size());
                                rl_item.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        });
                        settingView.findViewById(R.id.in).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FloatView.destoryView();
                            }
                        });

                    }
                }
            }

        }
    }

}
