package net.tatans.coeus.service.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.utils.FieldUtils;
import net.tatans.coeus.service.R;
import net.tatans.coeus.util.DataCleanManager;
import net.tatans.coeus.util.FileSizeUtil;
import net.tatans.coeus.util.TatansDefaultSetting;

/**
 * Created by Yuriy on 2016/8/24.
 */

public class TatansViewPreference extends Preference {
    private static String TAG="TatansViewPreference";
    private static Preference dependency ;
    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of Preference allows subclasses to use their own base style
     * when they are inflating. For example, a {@link }
     * constructor calls this version of the super class constructor and
     * supplies {@code android.R.attr.checkBoxPreferenceStyle} for
     * <var>defStyleAttr</var>. This allows the theme's checkbox preference
     * style to modify all of the base preference attributes as well as the
     * {@link } class's attributes.
     *
     * @param context      The Context this is associated with, through which it can
     *                     access the current theme, resources,
     *                     {@link }, etc.
     * @param attrs        The attributes of the XML tag that is inflating the
     *                     preference.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that
     *                     supplies default values for the view, used only if
     *                     defStyleAttr is 0 or can not be found in the theme. Can be 0
     *                     to not look for defaults.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TatansViewPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of Preference allows subclasses to use their own base style
     * when they are inflating. For example, a {@link }
     * constructor calls this version of the super class constructor and
     * supplies {@code android.R.attr.checkBoxPreferenceStyle} for
     * <var>defStyleAttr</var>. This allows the theme's checkbox preference
     * style to modify all of the base preference attributes as well as the
     * {@link } class's attributes.
     *
     * @param context      The Context this is associated with, through which it can
     *                     access the current theme, resources,
     *                     {@link }, etc.
     * @param attrs        The attributes of the XML tag that is inflating the
     *                     preference.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @see #(Context, AttributeSet)
     */
    public TatansViewPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Constructor that is called when inflating a Preference from XML. This is
     * called when a Preference is being constructed from an XML file, supplying
     * attributes that were specified in the XML file. This version uses a
     * default style of 0, so the only attribute values applied are those in the
     * Context's Theme and the given AttributeSet.
     *
     * @param context The Context this is associated with, through which it can
     *                access the current theme, resources, {@link },
     *                etc.
     * @param attrs   The attributes of the XML tag that is inflating the
     *                preference.
     * @see #(Context, AttributeSet, int)
     */
    public TatansViewPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor to create a Preference.
     *
     * @param context The Context in which to store Preference values.
     */
    public TatansViewPreference(Context context) {
        super(context);
    }

    @Override
    public CharSequence getSummary() {
        if (isEnabled()){
            return TatansApplication.getContext().getString(R.string.summary_pref_clean_data);
        }else {
            if (FileSizeUtil.isFileEmpty(super.getKey())){
                return TatansApplication.getContext().getString(R.string.summary_pref_clean_data_empty);
            }else {
                return TatansApplication.getContext().getString(R.string.summary_pref_clean_data_tatans_setting);
            }
        }
    }

    @Override
    public CharSequence getTitle() {
        return super.getTitle()+FileSizeUtil.getFileOrFilesSize(super.getKey());
    }

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        dependency=dependency;
        if (FileSizeUtil.isFileEmpty(getKey())){
            super.onDependencyChanged(dependency, true);
        }else {
            super.onDependencyChanged(dependency,disableDependent);
        }
    }

    @Override
    protected void onClick() {
        DataCleanManager.cleanUserData(super.getKey());
        super.notifyChanged();
        super.onDependencyChanged(dependency,true);
        super.onClick();
    }
}
