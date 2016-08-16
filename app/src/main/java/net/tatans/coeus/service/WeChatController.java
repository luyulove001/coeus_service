package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.IWetchatInput;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansSpeaker;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.service.activity.TatansServiceApplication;
import net.tatans.coeus.util.FloatView;
import net.tatans.coeus.util.OnHomeKeyEven;

import java.util.List;

/**
 * Created by xzb on 2016/6/27.
 * 对微信中的录音键（“按住说话”进行控制，按住时系统静音）
 */
public class WeChatController extends TatansService {
	private static String sPackage;
	private static final String TAG = "WeChatController";
	private static int currentVolume ;
	private static boolean avoidRepeat1=true;
	private static boolean avoidRepeat2=true;
	private AudioManager mAudioManager ;
	public static boolean isRecording = false;

	private AccessibilityNodeInfo speakButton;
	private static boolean firstLoad=true;

	private TatansSpeaker speaker;

    private static View floatView;

	@Override
	public void onInit() {
		super.onInit();
		bindService(TatansApplication.getContext());
	}

	@Override
	public void onUnbind(Intent intent) {
		resumeSystemStatus();
	}

	private class KeyCode{
		private final static String KEYCODE_0="7";
		private final static String KEYCODE_1="8";
		private final static String KEYCODE_2="9";
		private final static String KEYCODE_3="10";
		private final static String KEYCODE_4="11";
		private final static String KEYCODE_5="12";
		private final static String KEYCODE_6="13";
		private final static String KEYCODE_7="14";
		private final static String KEYCODE_8="15";
		private final static String KEYCODE_9="16";
		private final static String KEYCODE_del="67";
	}

	@Override
	public void onAccessibilityEvent(AccessibilityService accessibilityService,AccessibilityEvent event, AccessibilityNodeInfo rowNode) {
        startHomeKeyPressed(accessibilityService);
        Log.i("ServiceConnection", "00000000000000000");
        if(event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            Log.i("ServiceConnection", "-----------------");
            //对录音的控制
            popVoiceButton((Application) TatansServiceApplication.getContext(), rowNode);
            //对微信支付密码重新布局
            Log.i("ServiceConnection", "popSoftKeyboardOnPay called,iWetchatInput:"+iWetchatInput );
            popSoftKeyboardOnPay((Application) TatansServiceApplication.getContext(), rowNode);
        }
	}

	@Override
	public void onHomeKeyPressed(OnHomeKeyEven onHomeKeyEven) {
		resumeSystemStatus();
        Log.i("ServiceConnection", "onHomeKeyPressed");
		super.stopHomeKeyPressed();
	}


	private static IWetchatInput iWetchatInput;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("ServiceConnection", "onServiceConnected() called");
			iWetchatInput = IWetchatInput.Stub.asInterface(service);
			Log.i("ServiceConnection", "popSoftKeyboardOnPay called,iWetchatInput:"+iWetchatInput );
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			//This is called when the connection with the service has been unexpectedly disconnected,
			//that is, its process crashed. Because it is running in our same process, we should never see this happen.
			Log.i("ServiceConnection", "onServiceDisconnected() called");
		}
	};

	public void popSoftKeyboardOnPay(Application application, AccessibilityNodeInfo accessibilityNodeInfo){
		Log.i("popSoftKeyboardOnPay", "popSoftKeyboardOnPay called,iWetchatInput:"+iWetchatInput );
		//当不能adb模拟按键事件，直接结束
        destroyView();
		try {
			if(iWetchatInput==null){
				return ;
			}
			Log.i("popSoftKeyboardOnPay", "popSoftKeyboardOnPay called,iWetchatInput:"+iWetchatInput );
			if((iWetchatInput!=null&&!iWetchatInput.canSimulatorKeyEvent())){
				return ;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (accessibilityNodeInfo==null){
			return ;
		}
		final AccessibilityNodeInfo rowNode = accessibilityNodeInfo;
		final List<AccessibilityNodeInfo> payMoney = rowNode.findAccessibilityNodeInfosByText("请输入支付密码，以验证身份");
        //根据不同版本做分析
//		final List<AccessibilityNodeInfo> beginPay = rowNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e9");
		final List<AccessibilityNodeInfo> beginPay1 = rowNode.findAccessibilityNodeInfosByText("开启付款");
		final List<AccessibilityNodeInfo> beginPay2 = rowNode.findAccessibilityNodeInfosByText("微信安全支付");
		if((beginPay1==null||beginPay1.size()==0)||(beginPay1==null||beginPay1.size()==0)){
            Log.i("popSoftKeyboardOnPay", "floatView,beginPay:" );
			return ;
		}
        Log.i("popSoftKeyboardOnPay", "floatView,payMoney:"+payMoney );
		if((payMoney!=null&&payMoney.size()>0)){
            floatView = FloatView.createFloatView(application, R.layout.wechat_pay);
			Log.i("popSoftKeyboardOnPay", "floatView,createFloatView:"+floatView );
			setListener(floatView);
		}
	}

    private void destroyView() {
        Log.i("popSoftKeyboardOnPay", "floatView,floatView:"+floatView );
        if(floatView!=null){
            FloatView.destoryView();
        }
    }


    private void setListener(View weixinView) {
		weixinView.findViewById(R.id.wechat_1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Log.i(TAG, "inputPassword called,iWetchatInput:"+iWetchatInput );
					iWetchatInput.inputPassword(KeyCode.KEYCODE_1);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		weixinView.findViewById(R.id.wechat_2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					iWetchatInput.inputPassword(KeyCode.KEYCODE_2);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		weixinView.findViewById(R.id.wechat_3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					iWetchatInput.inputPassword(KeyCode.KEYCODE_3);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		weixinView.findViewById(R.id.wechat_4).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					iWetchatInput.inputPassword(KeyCode.KEYCODE_4);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		weixinView.findViewById(R.id.wechat_5).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					iWetchatInput.inputPassword(KeyCode.KEYCODE_5);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		weixinView.findViewById(R.id.wechat_6).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					iWetchatInput.inputPassword(KeyCode.KEYCODE_6);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		weixinView.findViewById(R.id.wechat_7).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					iWetchatInput.inputPassword(KeyCode.KEYCODE_7);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		weixinView.findViewById(R.id.wechat_8).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					iWetchatInput.inputPassword(KeyCode.KEYCODE_8);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		weixinView.findViewById(R.id.wechat_9).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					iWetchatInput.inputPassword(KeyCode.KEYCODE_9);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		weixinView.findViewById(R.id.wechat_0).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					iWetchatInput.inputPassword(KeyCode.KEYCODE_0);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		weixinView.findViewById(R.id.wechat_del).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					iWetchatInput.inputPassword(KeyCode.KEYCODE_del); //退格键
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
//		weixinView.findViewById(R.id.wechat_down).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//                resumeSystemStatus();
//			}
//		});
	}



	public void popVoiceButton(final Application application,AccessibilityNodeInfo accessibilityNodeInfo) {
		speaker = TatansSpeaker.create();
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


	private void bindService(Context context){
		Intent mIntent = new Intent();
		mIntent.setAction("android.intent.action.AIDLService");//你定义的service的action
//        mIntent.setPackage("com.example.yqw.customView01");//这里你需要设置你应用的包名
		mIntent.setPackage("net.tatans.coeus.setting");//这里你需要设置你应用的包名
		Log.i("ServiceConnection", "bindService called" );
		context.bindService(mIntent, conn, Context.BIND_AUTO_CREATE);
		Log.i("ServiceConnection", "bindService called,iWetchatInput:"+iWetchatInput );
	}


	public void resumeSystemStatus(){
		FloatView.destoryView();
        try {
            TatansApplication.getContext().unbindService(conn);
        } catch (IllegalArgumentException e) {

        }

	}
}
