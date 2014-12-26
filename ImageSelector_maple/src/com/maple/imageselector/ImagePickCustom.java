package com.maple.imageselector;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.maple.imageselector.pojo.FolderUnit;

/**
 * 图片选择器（悬浮窗）
 * 
 * @author yuanweinan
 * @date 14-12-19
 *
 */
public class ImagePickCustom {

	private static WindowManager sWindowManager;
	private WindowManager.LayoutParams mParams;
	private static ImagePickCustom sInstance;
	private Context mContext;
	private static View sView;

	private GridView mGridView;
	private ImageFolderGridViewAdapter mCurGridViewAdapter;
	private ListView mListView;
	private View mBackView;
	private View mQuitView;
	private View mOkView;
	private ImagePickSelectListener mImageSelectedListener;
	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<FolderUnit> mImageFloders = new ArrayList<FolderUnit>();
	private HashSet<String> mDirPaths = new HashSet<String>();
	//所有图片总量
//	private int mTotalCount = 0;
	
	private int mSelectedPos = -1;
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			mListView.setAdapter(new ImageFolderListAdapter(mImageFloders, mContext));
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					mBackView.setVisibility(View.VISIBLE);
					mGridView.setVisibility(View.VISIBLE);
					mOkView.setVisibility(View.VISIBLE);
					mListView.setVisibility(View.GONE);
					mQuitView.setVisibility(View.GONE);
					mCurGridViewAdapter = new ImageFolderGridViewAdapter(mContext, mImageFloders.get(arg2));
					mGridView.setAdapter(mCurGridViewAdapter);
					mGridView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							mCurGridViewAdapter.setItemIsSelected(mSelectedPos, false);
							mSelectedPos = arg2;
							mCurGridViewAdapter.setItemIsSelected(mSelectedPos, true);
						}
					});
				}
			});
		}
	};
	
	
	private ImagePickCustom(Context context) {
		mContext = context;
		sWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams();
		mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		mParams.flags = flags;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.width = LayoutParams.MATCH_PARENT;
		mParams.height = LayoutParams.MATCH_PARENT;
		mParams.gravity = Gravity.CENTER;

		sView = LayoutInflater.from(context).inflate(
				R.layout.imagefolder_view, null);
		mGridView = (GridView) sView.findViewById(R.id.imagepick_gridView);
		mListView = (ListView) sView.findViewById(R.id.imagepick_listview);
		mQuitView = sView.findViewById(R.id.quit);
		mBackView = sView.findViewById(R.id.back);
		mOkView = sView.findViewById(R.id.ok);
		
		mQuitView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mSelectedPos < 0 ||mGridView == null) {
				} else {
				mCurGridViewAdapter.setItemIsSelected(mSelectedPos, false);
				mSelectedPos = -1;
				}
				dismiss();
			}
		});
		
		mBackView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mListView.setVisibility(View.VISIBLE);
				mGridView.setVisibility(View.GONE);
				mQuitView.setVisibility(View.VISIBLE);
				mBackView.setVisibility(View.GONE);
				mOkView.setVisibility(View.GONE);
				if (mSelectedPos < 0 ||mGridView == null) {
					return;
				}
				mCurGridViewAdapter.setItemIsSelected(mSelectedPos, false);
				mSelectedPos = -1;
			}
		});
		
		mOkView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (mSelectedPos <0 || mGridView == null) {
					return;
				}
				
				String path = ((ImageFolderGridViewAdapter)mGridView.getAdapter()).getImagePath(mSelectedPos);
				mImageSelectedListener.selectedImage(path);
				mCurGridViewAdapter.setItemIsSelected(mSelectedPos, false);
				mSelectedPos = -1;
				dismiss();
			}
		});
		
		
		
		sWindowManager.addView(sView, mParams);

	}

	public static void show(Context context, ImagePickSelectListener listener) {
			sInstance = new ImagePickCustom(context);
			sInstance.scanImageData();
			sInstance.mImageSelectedListener = listener;
	}
	
	public static void dismiss(){
		if (sWindowManager == null) {
			return;
		}
		sWindowManager.removeView(sView);
		sView = null;
		sWindowManager = null;
		sInstance = null;
	}
	
	/**
	 * 扫描sdcard
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void scanImageData() {

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.i("maple", "暂无外部存储");
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {

				 String firstImage = null;
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = mContext
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					FolderUnit imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new FolderUnit(dirPath, path);
					}

					String[] temp =  parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg")) {
								return true;
							}
							return false;
						}
					});
					//避免空指针
					if (temp == null) {
						continue;
					}
					int picSize = temp.length;
//					mTotalCount += picSize;

					imageFloder.mCount = picSize;
					mImageFloders.add(imageFloder);
				}
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}
	
	/**
	 * 图片选择结果监听器
	 * @author yuanweinan
	 *
	 */
	public interface ImagePickSelectListener {
		public void selectedImage(String path);
	}
}
