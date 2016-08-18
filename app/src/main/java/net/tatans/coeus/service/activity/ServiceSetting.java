package net.tatans.coeus.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Button;

import net.tatans.coeus.service.R;
import net.tatans.coeus.util.Const;
import net.tatans.coeus.util.HomeWatcher;
import net.tatans.coeus.util.OnHomeKeyEven;
import net.tatans.coeus.util.OnHomePressedListener;
import net.tatans.rhea.network.event.OnClick;
import net.tatans.rhea.network.view.ContentView;
import net.tatans.rhea.network.view.TatansIoc;
import net.tatans.rhea.network.view.ViewIoc;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Yuriy on 2016/7/28.
 */
@ContentView(R.layout.service_setting)
public class ServiceSetting extends Activity{

    private static String TAG="QQQQ";
    @ViewIoc(R.id.bt_global)
    Button btnErLeMe;

    @ViewIoc(R.id.bt_single)
    Button btnQiHoo360;

    @ViewIoc(R.id.bt_tiris)
    Button btnQQ;

    private static final String FILE_NAME = Environment.getExternalStorageDirectory().toString()  + "/tatans/data/defaultSettingApp.xml";
    private Map<String,String> mapDefaultSettingApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TatansIoc.inject(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    private void readTatansManifest(){
        mapDefaultSettingApp = new HashMap<String,String>();
        SAXReader reader = new SAXReader();
        Document document= null;
        try {
            document = reader.read(new File(FILE_NAME));
            Element node = document.getRootElement();
            List nodes = node.elements("string");
            for (Iterator it = nodes.iterator(); it.hasNext();) {
                Element elm = (Element) it.next();
                mapDefaultSettingApp.put(elm.attributeValue("name"),elm.getText());
            }
        } catch (DocumentException e) {
            Log.e("TatansMainService", "DocumentException: "+e.toString());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (null==mapDefaultSettingApp){
            readTatansManifest();
        }
        if(mapDefaultSettingApp.containsValue(Const.ELE)){
            btnErLeMe.setText("饿了么,天坦预置，已开启。");
        }else{
            btnErLeMe.setText("饿了么,天坦预置,已关闭。");
        }
        if(mapDefaultSettingApp.containsValue(Const.QIHOO_360)){
            btnErLeMe.setText("360手机助手,天坦预置,已开启。");
        }else{
            btnQiHoo360.setText("360手机助手,天坦预置,已关闭。");
        }
        if(mapDefaultSettingApp.containsValue(Const.QIHOO_360)){
            btnQQ.setText("QQ,天坦预置,已开启。");
        }else{
            btnQQ.setText("QQ,天坦预置,已关闭。");
        }
    }

    @OnClick(R.id.bt_global)
    public void btnGlobal(){
        if(mapDefaultSettingApp.containsValue(Const.ELE)){
            btnErLeMe.setText("饿了么,天坦预置,已关闭。");
            modifyDefaultAppXml(Const.ELE,"no");
        }else{
            btnErLeMe.setText("饿了么,天坦预置,已开启。");
            modifyDefaultAppXml(Const.ELE,Const.ELE);
        }
    }

    @OnClick(R.id.bt_single)
    public void btnSingle(){
        if(mapDefaultSettingApp.containsValue(Const.QIHOO_360)){
            btnQiHoo360.setText("360手机助手,天坦预置,已关闭。");
            modifyDefaultAppXml(Const.QIHOO_360,"no");
        }else{
            btnQiHoo360.setText("360手机助手,天坦预置,已开启。");
            modifyDefaultAppXml(Const.QIHOO_360,Const.QIHOO_360);
        }
    }

    @OnClick(R.id.bt_tiris)
    public void btTiris(){
        if(mapDefaultSettingApp.containsValue(Const.QIHOO_360)){
            btnQQ.setText("QQ,天坦预置,已关闭。");
            modifyDefaultAppXml(Const.QIHOO_360,"no");
        }else{
            btnQQ.setText("QQ,天坦预置,已开启。");
            modifyDefaultAppXml(Const.QIHOO_360,Const.QIHOO_360);
        }
    }
    private void modifyDefaultAppXml(String key,String value){
        mapDefaultSettingApp = new HashMap<String,String>();
        SAXReader reader = new SAXReader();
        Document document= null;
        try {
            document = reader.read(new File(FILE_NAME));
            Element node = document.getRootElement();
            List nodes = node.elements("string");
            for (Iterator it = nodes.iterator(); it.hasNext();) {
                Element elm = (Element) it.next();
                if (elm.attributeValue("name").equals(key)){
                    elm.setText(value);
                    saveDocument(document);
                }
                mapDefaultSettingApp.put(elm.attributeValue("name"),elm.getText());
            }
        } catch (DocumentException e) {
            Log.e("TatansMainService", "DocumentException: "+e.toString());
        }
    }
    /**
     * 保存XML文档
     * @param doc
     * @throws IOException
     */
    private void saveDocument(Document doc)  {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(new FileOutputStream(FILE_NAME),format);
            writer.write(doc);
            writer.close();
        }catch (IOException e){
            Log.d(TAG, "IOException: " + e.toString());
        }
    }
}
