package net.tatans.coeus.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import net.tatans.coeus.service.FxService;

/**
 * 解锁View
 *
 * @author SiLiPing
 *         Created by Lion on 2015/12/8.
 */
public class PullDoorView extends LinearLayout {


	private Context mContext;
	private Scroller mScroller;
	private int mScreenWidth = 0;
	private int mScreenHeigh = 0;
	private int mLastDownY = 0;
	private int mCurryY;
	private int mDelY;
//	private MediaPlayer mediaPlayer;
	private boolean mCloseFlag = false;
	private static Handler mainHandler = null; // 与主Activity通信的Handler对象
	private static Handler exitHandler = null; // 与Service通信的Handler对象
	private ImageView mImgView;

	public PullDoorView(Context context) {
		super(context, null);
		mContext = context;
//		setupView();
	}

	/**
	 * 该方法必须存在，否则锁屏app将崩溃
	 *
	 * @param context
	 * @param attrs
	 */
	public PullDoorView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		mContext = context;
//		setupView();
	}


	private void setupView() {
		// 这个Interpolator你可以设置别的 我这里选择的是有弹跳效果的Interpolator
		Interpolator polator = new BounceInterpolator();
		mScroller = new Scroller(mContext, polator);

		// 获取屏幕分辨率
		WindowManager wm = (WindowManager) (mContext.getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenHeigh = dm.heightPixels;
		mScreenWidth = dm.widthPixels;

		// 这里你一定要设置成透明背景,不然会影响你看到底层布局
		this.setBackgroundColor(Color.argb(0, 0, 0, 0));
		mImgView = new ImageView(mContext);
		mImgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		addView(mImgView);
//		mediaPlayer = new MediaPlayer().create(LockApplication.getInstance(), R.raw.kp);

	}
	// 设置推动门背景
	public void setBgImage(int id) {
		mImgView.setImageResource(id);
	}

	// 设置推动门背景
	public void setBgImage(Drawable drawable) {
		mImgView.setImageDrawable(drawable);
	}

	// 推动门的动画
	public void startBounceAnim(int startY, int dy, int duration) {
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_UP:
				mCurryY = (int) event.getY();
				mDelY = mCurryY - mLastDownY;
				/**
				 * 判断t/f做是否可以向上滑动解锁
				 */
					if (mDelY < 0) {
						if (Math.abs(mDelY) > mScreenHeigh / 2.5) {
							// 向上滑动超过半个屏幕高的时候 开启向上消失动画
							startBounceAnim(this.getScrollY(), mScreenHeigh, 300);
							FxService service=new FxService();
							service.answerCall();
							service.removeFxView();
							mCloseFlag = true;
						} else {
							// 向上滑动未超过半个屏幕高的时候 开启向下弹动动画
							startBounceAnim(this.getScrollY(), -this.getScrollY(), 1000);
						}
					}

				break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {

		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			// 不要忘记更新界面
			postInvalidate();
		} else {
			if (mCloseFlag) {
//				mediaPlayer.start();
				this.setVisibility(View.GONE);
			}
		}
	}

	public static void setMainHandler(Handler handler) {
		mainHandler = handler;// activity所在的Handler对象
	}
}
