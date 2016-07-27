package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.ListView;

import net.tatans.coeus.adapter.SettingSpeedAdapter;
import net.tatans.coeus.network.tools.TatansPreferences;
import net.tatans.coeus.service.activity.TatansServiceApplication;
import net.tatans.coeus.util.FloatView;

import java.util.List;

public class SeeMoreController extends TatansService {
    private String[] speedArray = new String[]{"1", "2", "3", "4", "5"};

    @Override
    public void onUnbind(Intent intent) {
        FloatView.destoryView();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityService accessibilityService, AccessibilityEvent event, AccessibilityNodeInfo acbNodeInfo) {
        Log.e("SeeMoreController", "onAccessibilityEvent setting"+event.getPackageName());
        if(event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            displayVolumeControllerOnProgressbar(accessibilityService,(Application) TatansServiceApplication.getContext(),acbNodeInfo,event);
          /*  if (event.getPackageName().toString().equals(sPackage)) {
                displayVolumeControllerOnProgressbar(accessibilityService,(Application) TatansServiceApplication.getContext(),acbNodeInfo,event);
            }else{//对按home键进行处理
                resumeSystemStatus();
            }*/
        }

    }


    public void displayVolumeControllerOnProgressbar(final AccessibilityService accessibilityService, Application application, AccessibilityNodeInfo accessibilityNodeInfo, final AccessibilityEvent event){
        final AccessibilityNodeInfo rowNode = accessibilityNodeInfo;
//        final List<AccessibilityNodeInfo> voiceSetting = rowNode.findAccessibilityNodeInfosByViewId("android:id/list");
//        Log.d("SeeMoreController", " voiceSetting: performAction  AccessibilityEvent：" + event+voiceSetting.size());
//        if(voiceSetting.size()>0){
//            voiceSetting.get(0).getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            Log.d("SeeMoreController", " voiceSetting: performAction  AccessibilityEvent：" + event);
//        }
        final List<AccessibilityNodeInfo> vb_setup_seekBar = rowNode.findAccessibilityNodeInfosByViewId("com.xingmu.tts:id/vb_setup_seekBar");
        final List<AccessibilityNodeInfo> comfirm = rowNode.findAccessibilityNodeInfosByViewId("android:id/button1");
        if(vb_setup_seekBar.size()>0&&comfirm.size()>0){
            Log.d("SeeMoreController","vb_setup_seekBar:");
            //当滑动条出现
            View see_more_tts = FloatView.createFloatView(application, R.layout.see_more_tts); //渲染布局
            ListView lv_main = (ListView)see_more_tts.findViewById(R.id.lv_main);
            String playSpeed = (String) TatansPreferences.get("playSpeed", "1");
            SettingSpeedAdapter listAdapter = new SettingSpeedAdapter(application, speedArray,playSpeed);
            lv_main.setAdapter(listAdapter);
            lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //先调到最大
                    for(int i=1;i<=5;i++){
                        vb_setup_seekBar.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                    }
                    //再到指定位置
                    for(int i = 1; i<= Integer.valueOf(speedArray[position]); i++){
                        vb_setup_seekBar.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    }
                    comfirm.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    TatansPreferences.put("playSpeed", speedArray[position]);
                    exitVoiceSpeedSetting(accessibilityService);
//                    event.getSource().performAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }
            });
        }else{
            FloatView.destoryView();
        }
    }

    public void exitVoiceSpeedSetting(AccessibilityService accessibilityService) {
        FloatView.destoryView();
        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    public  void resumeSystemStatus() {
        FloatView.destoryView();
    }

    public  void handlerHomeAction(AccessibilityService accessibilityService) {
        exitVoiceSpeedSetting(accessibilityService);
    }
    public  void handlerBackAction(AccessibilityService accessibilityService) {
        exitVoiceSpeedSetting(accessibilityService);
    }
}
