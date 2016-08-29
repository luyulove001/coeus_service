package net.tatans.coeus.braodcastReciver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.service.FxService;
import net.tatans.coeus.service.InCallControl;
import net.tatans.coeus.service.activity.MainActivity;
import net.tatans.coeus.util.Const;

public class PhoneBroadcastReceiver extends BroadcastReceiver {

    TelephonyManager tManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())) {
            Const.EXTRA_PHONE_NUMBER = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(TatansApplication.getContext(), FxService.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("EXTRA_PHONE_NUMBER", Const.EXTRA_PHONE_NUMBER);
                    TatansLog.e("onReceive --- isDestory:" + FxService.isDestroy);
                    if (!FxService.isDestroy) {
//                                TatansApplication.getContext().startActivity(i);
                        i.putExtra("isCalling", true);
                        TatansApplication.getContext().startService(i);
                        FxService.interrupt(0);
                    }
                }
            }, 800);
        } else {
            tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            // 如果是来电
            if (tManager.getCallState() == Const.PHONE_STATE)
                return;
            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("antony", "coming");
                    InCallControl.flag = false;
                    Const.PHONE_STATE = TelephonyManager.CALL_STATE_RINGING;
                    break;

                // 通话过程
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    InCallControl.flag = true;
                    InCallControl.closed = true;
                    Log.d("antony", "online");
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent i = new Intent(TatansApplication.getContext(), FxService.class);
//                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            i.putExtra("EXTRA_PHONE_NUMBER", Const.EXTRA_PHONE_NUMBER);
//                            TatansLog.e("onReceive --- isDestory:" + FxService.isDestroy);
//                            if (!FxService.isDestroy && Const.PHONE_STATE != TelephonyManager.CALL_STATE_OFFHOOK) {
////                                TatansApplication.getContext().startActivity(i);
//                                i.putExtra("isCalling", true);
//                                TatansApplication.getContext().startService(i);
//                                FxService.interrupt(0);
//                            }
//                        }
//                    }, 800);
                    Const.PHONE_STATE = TelephonyManager.CALL_STATE_OFFHOOK;
                    break;

                // 挂断
                case TelephonyManager.CALL_STATE_IDLE:
                    InCallControl.flag = true;
                    InCallControl.closed = true;
                    Log.d("antony", "hangup");
                    Const.PHONE_STATE = TelephonyManager.CALL_STATE_IDLE;
                    break;
            }
        }
    }

}
