package com.maple.imageselector;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.maple.imageselector.pojo.FolderUnit;
import com.maple.imageselector.pojo.ImageUnit;
import com.maple.imageselector.util.BroadCaster.BroadCasterObserver;
import com.maple.imageselector.util.ImageLoader;
import com.maple.imageselector.util.ImageLoader.Type;

/**
 * @author yuanweinan
 *
 */
public class ImageFolderGridViewAdapter extends BaseAdapter{
	
	private File mFile;
	private List<ImageUnit> mImages = new ArrayList<ImageUnit>();
	private FolderUnit mFolder;
	private Context mContext;
	
	public ImageFolderGridViewAdapter(Context context, FolderUnit folder) {
		mFolder = folder;
		mContext = context;
		mFile = new File(folder.mFolderDir);
		List<String> images = Arrays.asList(mFile.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		
		for (String name:images) {
			ImageUnit unit = new ImageUnit();
			unit.mImageName = name;
			mImages.add(unit);
		}
	}
	
	public void setItemIsSelected(int pos, boolean isSelected) {
		ImageUnit unit = (ImageUnit) getItem(pos);
		if (unit != null) {
			unit.mIsSelected = isSelected;
			unit.broadCast(ImageUnit.SELECTED_CHANGED, -1);
		}
	}
	
	@Override
	public int getCount() {
		return mFolder.mCount;
	}

	@Override
	public Object getItem(int arg0) {
		if (null == mImages || arg0 < 0 || arg0 >= mImages.size() ) {
			return null;
		}
		return mImages.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	/**
	 * 返回指定位置图片路径
	 * @param position
	 * @return
	 */
	public String getImagePath(int position) {
		return mFolder.mFolderDir + "/" + mImages.get(position).mImageName;
	}
	

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		 ViewHolder viewHolder = null;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.imagefolder_gridview_item, null, false);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (ImageView) view.findViewById(R.id.gridview_imageview);
			viewHolder.mCheckSelectView = view.findViewById(R.id.gridview_imageview_select);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		ImageUnit unit = mImages.get(arg0);
		viewHolder.bind(unit);
		viewHolder.mImageView.setImageResource(R.drawable.ic_launcher);
		viewHolder.mCheckSelectView.setVisibility(unit.mIsSelected ? View.VISIBLE : View.GONE);
//		//异步加载图片LRU
		ImageLoader.getInstance(3,Type.LIFO).loadImage(mFolder.mFolderDir + "/" + unit.mImageName, viewHolder.mImageView);
		return view;
	}

	/**
	 * @author yuanweinan
	 *
	 */
	private class ViewHolder implements BroadCasterObserver {
		public ImageView mImageView;
		public View mCheckSelectView;
		
		private ImageUnit mImageInfo;
		
		private void bind(ImageUnit imageInfo) {
			if (mImageInfo != null) {
				mImageInfo.unRegisterObserver(this);
			}
			imageInfo.registerObserver(this);
			mImageInfo = imageInfo;
		}

		@Override
		public void onBgChange(int msgId, int param, Object... objects) {
			switch (msgId) {
			case ImageUnit.SELECTED_CHANGED:
				mCheckSelectView.setVisibility(mImageInfo.mIsSelected ? View.VISIBLE : View.GONE);
				break;

			default:
				break;
			}
		}
	}
	
}
