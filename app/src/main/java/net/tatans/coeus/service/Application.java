package net.tatans.coeus.service;

import net.tatans.coeus.network.tools.CrashHandler;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansLog;

/**
 * Created by Administrator on 2016/3/31.
 */
public class Application extends TatansApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.initTatans("countdown");
        TatansLog.TAG="countdown";
        TatansLog.d("Application");
    }
}
