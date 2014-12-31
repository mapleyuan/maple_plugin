package com.maple.util;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 自定义toast, 用法同{@link Toast}
 * 
 * @author yuanweinan
 *
 */
public class ToastCustom {

	public static final long LENGTH_SHORT = 2000;
	public static final long LENGTH_LONG = 3500;

	private long mDuration;
	private WindowManager mWindowManager;
	private View mView;
	private View mOldView;
	private static Toast mToast;
	private WindowManager.LayoutParams mParams;
	private Timer mTimer;
	private static ToastCustom sInstance;

	@SuppressLint("ShowToast")
	private ToastCustom(Context context, String text, long duration) {
		if (duration == Toast.LENGTH_SHORT) {
			duration = LENGTH_SHORT;
		} else {
			duration = LENGTH_LONG;
		}
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		mView = mToast.getView();
		mOldView = mView;
		mTimer = new Timer();
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.windowAnimations = android.R.style.Animation_Toast;
		// mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		mParams.setTitle("Toast");
		mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;

		mDuration = duration;
	}

	private static ToastCustom getInstance(Context context, String text,
			long duration) {
		if (sInstance == null) {
			synchronized (ToastCustom.class) {
				if (sInstance == null) {
					sInstance = new ToastCustom(context, text, duration);
				}
			}
		}
		return sInstance;
	}

	/**
	 * 构建Toast
	 * 
	 * @param context
	 * @param textId
	 * @param duration
	 *            显示时长，单位毫秒
	 * @return
	 */
	public static ToastCustom makeText(Context context, int textId,
			long duration) {
		return makeText(context, context.getResources().getString(textId),
				duration);
	}

	/**
	 * 构建Toast
	 * 
	 * @param context
	 * @param text
	 * @param duration
	 *            显示时长，单位毫秒
	 * @return
	 */
	public static ToastCustom makeText(Context context, String text,
			long duration) {
		ToastCustom toast = getInstance(context, text, duration);
		mToast.setText(text);
		return toast;
	}

	/**
	 * 显示Toast
	 */
	public void show() {
		// clear the oldview
		cancelOldView();
		mParams.y = mToast.getYOffset();
		mWindowManager.addView(mView, mParams);
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mWindowManager.removeView(mView);
			}
		}, mDuration);
	}

	/**
	 * 取消Toast
	 */
	public void cancel() {
		mWindowManager.removeView(mView);
		mTimer.cancel();
	}

	public void cancelOldView() {
		if (mOldView != null && mOldView.getParent() != null) {
			mWindowManager.removeView(mOldView);
			mTimer.cancel();
			mTimer = new Timer();
		}
	}
}
