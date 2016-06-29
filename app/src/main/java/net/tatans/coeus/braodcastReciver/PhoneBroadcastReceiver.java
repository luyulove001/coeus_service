package net.tatans.coeus.braodcastReciver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.service.FxService;
import net.tatans.coeus.service.InCallControl;
import net.tatans.coeus.service.activity.MainActivity;

public class PhoneBroadcastReceiver extends BroadcastReceiver {

    TelephonyManager tManager;
    String EXTRA_PHONE_NUMBER;
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())) {
            EXTRA_PHONE_NUMBER = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.e("antony", "OUTGOING_CALL");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(TatansApplication.getContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("EXTRA_PHONE_NUMBER", EXTRA_PHONE_NUMBER);
                    if (!FxService.isDestroy) {
                        Log.e("antony", FxService.isDestroy + "--" + i);
                        TatansApplication.getContext().startActivity(i);
                        FxService.interrupt(0);
                    }
                }
            }, 800);

        } else {
            tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            // 如果是来电
            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("antony", "coming");
                    InCallControl.flag = false;
                    break;

                // 通话过程
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    InCallControl.flag = true;
                    InCallControl.closed = true;
                    Log.d("antony", "online");
                    break;

                // 挂断
                case TelephonyManager.CALL_STATE_IDLE:
                    InCallControl.flag = true;
                    InCallControl.closed = true;
                    Log.d("antony", "hangup");
                    break;
            }
        }
    }

}
