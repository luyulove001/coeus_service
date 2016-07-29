package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.service.activity.TatansServiceApplication;
import net.tatans.coeus.util.FloatView;

import java.util.List;

/**
 * Created by xzb on 2016/6/27
 * .
 */
public class EasySettingControl extends TatansService {
	@Override
	public void onAccessibilityEvent(AccessibilityService accessibilityService,AccessibilityEvent event, AccessibilityNodeInfo rowNode) {
		easySettingApplication((Application) TatansServiceApplication.getContext(), rowNode);
		this.processAccessibilityEvent(event);
	}
	//针对小米手机的默认短信设置
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
	/**
	 * xzb   针对小米手机不让进入老人机的方法,极简模式覆盖窗口
	 * @param application
	 * @param accessibilityNodeInfo
	 */
	public void easySettingApplication(Application application, final AccessibilityNodeInfo accessibilityNodeInfo) {
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
