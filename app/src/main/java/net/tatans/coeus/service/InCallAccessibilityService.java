package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by John on 2016/1/5.
 */
public class InCallAccessibilityService extends AccessibilityService {


                    public static Boolean flag = false;

                    @Override
                    protected void onServiceConnected() {
                                        super.onServiceConnected();
                    }

                    @SuppressLint("NewApi")
                    @Override
                    public void onAccessibilityEvent(AccessibilityEvent event) {
                                        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
                                        if (rowNode == null) {
                                                            return;
                                        } else {
                                                            recycle(rowNode);
                                        }

                    }

                    @Override
                    public void onInterrupt() {

                    }

                    @SuppressLint("InlinedApi")
                    public void recycle(AccessibilityNodeInfo info) {
                                        //通话界面第一次进来，作点击事件，打开拨号键盘
                                        CharSequence ContentDescription = info.getContentDescription();
                                        if (ContentDescription != null && ContentDescription.equals("拨号键盘")) {
                                                            if (info.isClickable() && flag) {
                                                                                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                                                flag = false;
                                                                                return;
                                                            }
                                        }

                                        if (info.getChildCount() == 0) {

                                        } else {
                                                            for (int i = 0; i < info.getChildCount(); i++) {
                                                                                if (info.getChild(i) != null) {
                                                                                                    recycle(info.getChild(i));
                                                                                }
                                                            }
                                        }
                    }


}
