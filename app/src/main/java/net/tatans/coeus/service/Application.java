package net.tatans.coeus.service;

import net.tatans.coeus.network.speaker.Speaker;
import net.tatans.coeus.network.tools.CrashHandler;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansLog;

/**
 * Created by Administrator on 2016/3/31.
 */
public class Application extends TatansApplication{
    private static Speaker mSpeaker;
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.initTatans("service");
        TatansLog.TAG="myTag";
        TatansLog.d("Application");
        mSpeaker = Speaker.getInstance(this);
    }

    public static void speech(String str){
        mSpeaker.speech(str);
    }

    public static void stopAllSound(){
        mSpeaker.stopAllSound();
    }
}
