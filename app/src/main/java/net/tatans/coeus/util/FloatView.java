package net.tatans.coeus.util;

import android.app.Application;
import android.graphics.PixelFormat;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;


/**
 * Created by newre on 2016/4/25.
 */
public class FloatView {

    //定义浮动窗口布局
    static View mFloatLayout;
    static WindowManager mWindowManager;

    public static View createFloatView(Application application, @LayoutRes int resource) {
        return createFloatView(application,resource,createDefaultLayoutParams());
    }

    public static View createFloatView(Application application, @LayoutRes int resource,WindowManager.LayoutParams wmParams) {
        if(mWindowManager==null)
            //获取WindowManagerImpl.CompatModeWrapper
            mWindowManager = (WindowManager) application.getSystemService(application.WINDOW_SERVICE);

        destoryView();

        /*// 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;*/

        //获取浮动窗口视图所在布局
        LayoutInflater inflater = LayoutInflater.from(application);
        mFloatLayout = inflater.inflate(resource, null);
        mWindowManager.addView(mFloatLayout,wmParams);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return mFloatLayout;
    }

    public static  WindowManager.LayoutParams createDefaultLayoutParams(){
        //创建浮动窗口设置布局参数的对象
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags =
////          LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
////          LayoutParams.FLAG_NOT_TOUCHABLE
//        ;
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;

        //调整悬浮窗显示的停靠位置为左侧置顶
//        wmParams.gravity = /*Gravity.LEFT |*/ Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        //设置悬浮窗口长宽数据
//        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        return wmParams;
    }

    public static void destoryView() {
        try {
            if (mFloatLayout != null && mWindowManager != null)
                mWindowManager.removeView(mFloatLayout);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
}
