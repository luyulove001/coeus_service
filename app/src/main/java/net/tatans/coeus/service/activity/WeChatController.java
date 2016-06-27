package net.tatans.coeus.service.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import net.tatans.coeus.network.speaker.Speaker;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.service.tools.AccessibilityServiceSubject;
import net.tatans.coeus.service.tools.TatansServiceImp;

import java.util.List;

/**
 * Created by xzb on 2016/6/27.
 */
public class WeChatController implements TatansServiceImp {
	private static String sPackage;
	private static final String TAG = "WeChatController";
	private static int currentVolume ;
	private static boolean avoidRepeat1=true;
	private static boolean avoidRepeat2=true;
	private AudioManager mAudioManager ;
	public static boolean isRecording = false;

	private AccessibilityNodeInfo speakButton;
	private static boolean firstLoad=true;

	private Speaker speaker;
	@Override
	public void onInit() {
		sPackage="com.tencent.mm";
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
	public void onAccessibilityEvent(AccessibilityEvent event, AccessibilityNodeInfo rowNode) {
		Log.e("tencent", "onAccessibilityEvent tencent"+event.getPackageName());
		if (event.getPackageName().equals(sPackage)) {
			popVoiceButton((Application) TatansServiceApplication.getContext(), rowNode);
		}
	}

	public void popVoiceButton(final Application application,AccessibilityNodeInfo accessibilityNodeInfo) {
		speaker = Speaker.getInstance(application);
		final AccessibilityNodeInfo rowNode = accessibilityNodeInfo;
		final Application app = application;
		mAudioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
//        List<AccessibilityNodeInfo> linearlayoutNodeInfos=rowNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c6x");
		List<AccessibilityNodeInfo> holdNodeInfo = rowNode.findAccessibilityNodeInfosByText("按住 说话");
		List<AccessibilityNodeInfo> releaseNodeInfo = rowNode.findAccessibilityNodeInfosByText("松开 结束");
		if ((null == holdNodeInfo || holdNodeInfo.size() == 0)&&(null == releaseNodeInfo || releaseNodeInfo.size() == 0) ){
			return;
		}
		if(null != holdNodeInfo &&holdNodeInfo.size() > 0){
			speakButton = holdNodeInfo.get(0);
		}
		if(null != releaseNodeInfo &&releaseNodeInfo.size() > 0){
			speakButton = releaseNodeInfo.get(0);
		}

		if(firstLoad){
			currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			firstLoad=false;
		}

		Log.d(TAG, "speakButton.getText():" + speakButton.getText().toString() + "---speakButton:" + speakButton);

		if (speakButton.getText().toString().equals("松开 结束") && avoidRepeat1) {
			avoidRepeat2 = true;
			avoidRepeat1 = false;
			Log.d(TAG, "it works :松开 结束" + currentVolume);
			//TODO 打断talkback的声音
			currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
			isRecording = true;
		}
		if (speakButton.getText().toString().equals("按住 说话") && avoidRepeat2) {
			avoidRepeat2 = false;
			avoidRepeat1 = true;
			Log.d(TAG, "按住 说话 :" + currentVolume);
			long time = 150;
			while(time<3350){
				time+=20;
				mhandler.sendEmptyMessageDelayed(0, time);
			}
			//TODO
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);//恢复talkback的声音
			isRecording = false;
		}
	}

	private Handler mhandler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==0){
				speaker.speech("  ");
				TatansToast.showAndCancel("");
				Log.d(TAG, "说话 :");
			}
		}
	};
}
