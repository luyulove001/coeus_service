package net.tatans.coeus.service;

import net.tatans.coeus.network.tools.CrashHandler;
import net.tatans.coeus.network.tools.TatansApplication;

/**
 * Created by Administrator on 2016/3/31.
 */
public class Application extends TatansApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.initTatans("countdown");
    }
}
