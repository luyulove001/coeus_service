package net.tatans.coeus.service.activity;

import net.tatans.coeus.network.tools.CrashHandler;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansLog;
import net.tatans.coeus.network.tools.TatansSpeaker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/31.
 */
public class TatansServiceApplication extends TatansApplication{
    private static TatansSpeaker mSpeaker;
    private static List<String> al_contentPackage = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.initTatans("service");
        TatansLog.TAG="TatansService";
        TatansLog.d("TatansServiceApplication");
        setAppSpeaker();
        mSpeaker = TatansSpeaker.create();
    }
    /*
     * Created by Yuriy on 2016/6/24.
     */
    public static void setContentPackage(String s){
        al_contentPackage.add(s);
    }
    /*
     * Created by Yuriy on 2016/6/24.
     */
    public static void rmContentPackage(){
        al_contentPackage.clear();
    }
    /*
     * Created by Yuriy on 2016/6/24.
     */
    public static String getContentPackage(int s){
        return al_contentPackage.get(s);
    }
    public static void speech(String str){
        mSpeaker.speech(str);
    }

    public static void stopAllSound(){
        mSpeaker.stop();
    }
}