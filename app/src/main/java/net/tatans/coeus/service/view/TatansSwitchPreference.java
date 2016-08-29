package net.tatans.coeus.service.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.util.Log;

import net.tatans.coeus.util.TatansDefaultSetting;

/**
 * Created by Yuriy on 2016/8/23.
 */

public class TatansSwitchPreference extends SwitchPreference {
    private static String TAG="TatansDefaultSetting";
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
    protected boolean callChangeListener(Object newValue) {
        Log.d(TAG, "callChangeListener0: "+newValue);
        if (newValue.equals(true)){
            Log.d(TAG, "callChangeListener: "+true+"---"+super.getKey());
            TatansDefaultSetting.modifyDefaultAppXml(super.getKey(),super.getKey());
        }else {
            Log.d(TAG, "callChangeListener: "+false+"---"+"no");
            TatansDefaultSetting.modifyDefaultAppXml(super.getKey(),"no");
        }
        return super.callChangeListener(newValue);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        Log.d(TAG, "onSetInitialValue: "+defaultValue);
        super.onSetInitialValue(restoreValue, TatansDefaultSetting.isContainsValue(super.getKey()));
    }
}
