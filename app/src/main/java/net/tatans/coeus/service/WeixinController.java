package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.tatans.coeus.IWetchatInput;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.service.activity.TatansServiceApplication;
import net.tatans.coeus.util.FloatView;
import net.tatans.coeus.util.OnHomeKeyEven;

import java.util.List;

/**
 * Created by newre on 2016/4/26.
 */
public class WeixinController extends TatansService{
    private static final String TAG = "WeixinController";

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
    public void onInit() {
        super.onInit();
        bindService(TatansApplication.getContext());
    }

    @Override
    public void onUnbind(Intent intent) {
        resumeSystemStatus();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityService accessibilityService, AccessibilityEvent event, AccessibilityNodeInfo rowNode) {
        super.onAccessibilityEvent( accessibilityService,  event,  rowNode);
        Log.i("ServiceConnection", "popSoftKeyboardOnPay called,iWetchatInput:"+iWetchatInput );
        startHomeKeyPressed(accessibilityService);
        if(event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            //对微信支付密码重新布局
            Log.i("ServiceConnection", "popSoftKeyboardOnPay called,iWetchatInput:"+iWetchatInput );
            popSoftKeyboardOnPay((Application) TatansServiceApplication.getContext(), rowNode);
        }
    }

    @Override
    public void onHomeKeyPressed(OnHomeKeyEven onHomeKeyEven) {
        resumeSystemStatus();
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
        Log.i("ServiceConnection", "popSoftKeyboardOnPay called,iWetchatInput:"+iWetchatInput );
        //当不能adb模拟按键事件，直接结束
        try {
            if(iWetchatInput==null){
                return ;
            }
            Log.i("ServiceConnection", "popSoftKeyboardOnPay called,iWetchatInput:"+iWetchatInput );
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
        final List<AccessibilityNodeInfo> beginPay = rowNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/da");
        if(beginPay==null||beginPay.size()==0){
            return ;
        }
        if((payMoney!=null&&payMoney.size()>0)){
            View weixinView = FloatView.createFloatView(application, R.layout.wechat_pay);
            setListener(weixinView);
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
        weixinView.findViewById(R.id.wechat_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "wechat_down called" );
                resumeSystemStatus();
            }
        });
    }

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
        TatansApplication.getContext().unbindService(conn);
    }
}
