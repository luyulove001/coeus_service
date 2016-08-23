package net.tatans.coeus.service.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Yuriy on 2016/8/23.
 */

public class TatansSwitchPreference extends SwitchPreference {
    /**
     * Construct a new SwitchPreference with the given style options.
     *
     * @param context      The Context that will style this preference
     * @param attrs        Style attributes that differ from the default
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that
     *                     supplies default values for the view, used only if
     *                     defStyleAttr is 0 or can not be found in the theme. Can be 0
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TatansSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Construct a new SwitchPreference with the given style options.
     *
     * @param context      The Context that will style this preference
     * @param attrs        Style attributes that differ from the default
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     */
    public TatansSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Construct a new SwitchPreference with the given style options.
     *
     * @param context The Context that will style this preference
     * @param attrs   Style attributes that differ from the default
     */
    public TatansSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Construct a new SwitchPreference with default style options.
     *
     * @param context The Context that will style this preference
     */
    public TatansSwitchPreference(Context context) {
        super(context);
    }
    @Override
    public boolean isChecked() {
        Log.d("Yuriy","super.isChecked():"+super.isChecked());
        return super.isChecked();
    }
    @Override
    protected boolean callChangeListener(Object newValue) {
        Log.d("Yuriy","newValue:"+newValue);
        Log.d("Yuriy","key:"+super.getKey());
        return super.callChangeListener(newValue);
    }

    //预设的值
    @Override
    protected boolean persistBoolean(boolean value) {
        Log.d("Yuriy","value:"+value);
        return super.persistBoolean(value);
    }

    @Override
    protected boolean getPersistedBoolean(boolean defaultReturnValue) {
        Log.d("Yuriy","defaultReturnValue:"+defaultReturnValue);
        return super.getPersistedBoolean(defaultReturnValue);
    }
    @Override
    protected void notifyChanged() {
        Log.d("Yuriy","notifyChanged:");
        super.notifyChanged();
    }
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        Log.d("Yuriy","onSetInitialValue:"+restoreValue+":"+defaultValue);
        super.onSetInitialValue(restoreValue, defaultValue);
    }

    @Override
    public String getKey() {
        Log.d("Yuriy","getKey:"+super.getKey());
        return super.getKey();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        Log.d("Yuriy","getSharedPreferences:");
        return super.getSharedPreferences();
    }
}
