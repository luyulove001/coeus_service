package net.tatans.coeus.util;

import android.nfc.Tag;
import android.os.Environment;
import android.util.Log;

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
 * Created by Yuriy on 2016/8/24.
 */

public class TatansDefaultSetting {
    public static final String FILE_NAME = Environment.getExternalStorageDirectory().toString()  + "/tatans/data/defaultSettingApp.xml";
    private static Map<String,String> mapDefaultSettingApp;
    private static String TAG="TatansDefaultSetting";

    public static void modifyDefaultAppXml(String key,String value){
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
            Log.e(TAG, "DocumentException: "+e.toString());
        }
    }
    /**
     * 保存XML文档
     * @param doc
     * @throws IOException
     */
    private static void saveDocument(Document doc)  {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(new FileOutputStream(FILE_NAME),format);
            writer.write(doc);
            writer.close();
        }catch (IOException e){
            Log.d(TAG, "IOException: " + e.toString());
        }
    }
    public static boolean isContainsValue(String string){
        if (null==mapDefaultSettingApp){
            readTatansManifest();
        }
        Log.d(TAG, "isContainsValue key:"+string);
        return mapDefaultSettingApp.containsValue(string);
    }

    private static void readTatansManifest(){
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
            Log.e(TAG, "DocumentException: "+e.toString());
        }
    }
}
