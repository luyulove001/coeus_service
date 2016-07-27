package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


/**
 * Created by cly on 2016/6/29.
 */
public class InCallControl extends TatansService {
    public static Boolean flag = false;
    public static Boolean closed = true;

    @Override
    public void onAccessibilityEvent(AccessibilityService accessibilityService, AccessibilityEvent acbEvent, AccessibilityNodeInfo acbNodeInfo) {
        AccessibilityNodeInfo rowNode = accessibilityService.getRootInActiveWindow();
        recycle(rowNode);
        /*AccessibilityNodeInfo rowNode = accessibilityService.getRootInActiveWindow();
        if (rowNode == null) {
            return;
        } else {
            if (mPackageName.equals(acbEvent.getPackageName()))
                recycle(rowNode);
        }*/
    }

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
}
