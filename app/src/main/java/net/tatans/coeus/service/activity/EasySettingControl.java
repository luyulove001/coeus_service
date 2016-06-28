package net.tatans.coeus.service.activity;

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
import net.tatans.coeus.service.R;
import net.tatans.coeus.service.tools.AccessibilityServiceSubject;
import net.tatans.coeus.service.tools.TatansServiceImp;
import net.tatans.coeus.util.FloatView;

import java.util.List;

/**
 * Created by xzb on 2016/6/27.
 */
public class EasySettingControl implements TatansServiceImp {
	private static String sPackage;

	@Override
	public void onInit() {
		sPackage="com.android.settings";
		TatansServiceApplication.setContentPackage(sPackage);
	}

	@Override
	public void onInterrupt() {
		TatansLog.d("onInterrupt()1");
	}

	@Override
	public void onUnbind(Intent intent) {
		TatansLog.d("onUnbind()1");
	}

	@Override
	public void onAccessibilityEvent(AccessibilityService accessibilityService,AccessibilityEvent event, AccessibilityNodeInfo rowNode) {
		Log.e("setting", "onAccessibilityEvent setting"+event.getPackageName());
		if (event.getPackageName().equals(sPackage)) {
			easySettingApplication((Application) TatansServiceApplication.getContext(), rowNode);
		}
	}

	/**
	 * xzb   极简模式覆盖窗口
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
