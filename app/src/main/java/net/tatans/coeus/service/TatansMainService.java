package net.tatans.coeus.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import net.tatans.coeus.service.activity.TatansServiceApplication;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
  * ClassName :TatansMainService
  * @author: Yuriy
  * Created time : 2016/6/24 14:44.
  */
public class TatansMainService extends AccessibilityService {
    private Iterator<String> iteTatansService;
    private Map<String,String> hm_TatansService;
    private String serviceNamePackage;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        hm_TatansService = new HashMap<String, String>();
        readTatansManifest();
        invoTatansServiceMethod("onInit",null);
        startService(new Intent(getApplication(), FxService.class));
    }
    private void readTatansManifest(){
        SAXReader reader = new SAXReader();
        Document document= null;
        try {
            InputStream isTatansmanifest = getAssets().open("tatansmanifest.xml");
            document = reader.read(isTatansmanifest);
            Element node = document.getRootElement();
            Attribute attrPackage = node.attribute("package");
            serviceNamePackage=attrPackage.getText();
            List nodes = node.elements("tatans-service");
            for (Iterator it = nodes.iterator();it.hasNext();) {
                Element elm = (Element) it.next();
                hm_TatansService.put(elm.element("service-package").getText(),elm.element("service").getText());
            }
        } catch (DocumentException e) {
            Log.e("TatansMainService", "DocumentException: "+e.toString());
        }catch (IOException e) {
            Log.e("TatansMainService", "IOException: "+e.toString());
        }
    }
    private void invoTatansServiceMethod(String sMethod,AccessibilityService accessibilityService, AccessibilityEvent acbEvent, AccessibilityNodeInfo acbNodeInfo){
        String strTatansServiceName = hm_TatansService.get(acbEvent.getPackageName());
        if (strTatansServiceName==null){
            return;
        }
        try {
            Class cls = Class.forName(serviceNamePackage+"."+strTatansServiceName);
            Method m = cls.getDeclaredMethod(sMethod,AccessibilityService.class,AccessibilityEvent.class,AccessibilityNodeInfo.class);
            m.invoke(cls.newInstance(),accessibilityService,acbEvent,acbNodeInfo);
        }catch (Exception e){
        //    Log.e("TatansMainService", "IOException: "+e.toString());
        }
    }
    private void invoTatansServiceMethod(String sMethod,Intent intent){
        Collection<String> c_tatamsService = hm_TatansService.values();
        iteTatansService = c_tatamsService.iterator();
        while (iteTatansService.hasNext()){
            try {
                Class cls = Class.forName(serviceNamePackage+"."+iteTatansService.next());
                if(null==intent){
                    Method m = cls.getDeclaredMethod(sMethod);
                    m.invoke(cls.newInstance());
                }else {
                    Method m = cls.getDeclaredMethod(sMethod,Intent.class);
                    m.invoke(cls.newInstance(),intent);
                }
            }catch (Exception e){
              //  Log.e("TatansMainService", "IOException: "+e.toString());
            }
        }
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
        if (rowNode == null) {
            return;
        } else {
            invoTatansServiceMethod("onAccessibilityEvent",this,event,rowNode);
        }
    }
    @Override
    public void onInterrupt() {
        invoTatansServiceMethod("onInterrupt",null);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        invoTatansServiceMethod("onUnbind",intent);
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
