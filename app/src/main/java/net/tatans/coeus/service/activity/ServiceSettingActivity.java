package net.tatans.coeus.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
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
public class ServiceSettingActivity extends Activity{

    private static final String FILE_NAME = Environment.getExternalStorageDirectory().toString()  + "/tatans/data/defaultSettingApp.xml";

    private Map<String,String> mapDefaultSettingApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference);
        }
    }
}
