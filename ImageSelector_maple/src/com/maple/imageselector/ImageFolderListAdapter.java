package com.maple.imageselector;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maple.imageselector.pojo.FolderUnit;
import com.maple.imageselector.util.ImageLoader;
import com.maple.imageselector.util.ImageLoader.Type;


/**
 * @author yuanweinan
 *
 */
public class ImageFolderListAdapter extends BaseAdapter{

	private List<FolderUnit> mImageFolders;
	private Context mContext;
	
     public ImageFolderListAdapter(List<FolderUnit> folders, Context context) {
    	 mImageFolders = folders;
    	 mContext = context;
     }
	
	@Override
	public int getCount() {
		return mImageFolders.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mImageFolders.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View contentView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (contentView == null) {
		contentView = LayoutInflater.from(mContext).inflate(R.layout.imagefolder_list_item, null, false);
		viewHolder = new ViewHolder();
		viewHolder.mImageView = (ImageView) contentView.findViewById(R.id.imagefolder_list_item_image);
		viewHolder.mNameTextView = (TextView) contentView.findViewById(R.id.imagefolder_list_item_name);
		viewHolder.mCountTextView = (TextView) contentView.findViewById(R.id.imagefolder_list_item_count);
		contentView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) contentView.getTag();
		}
		FolderUnit folder = mImageFolders.get(arg0);
		viewHolder.mImageView.setImageResource(R.drawable.ic_launcher);
		//异步加载图片LRU
		ImageLoader.getInstance(3,Type.LIFO).loadImage(folder.mFirstImagePath, viewHolder.mImageView);
		viewHolder.mNameTextView.setText(folder.getName());;
		viewHolder.mCountTextView.setText(folder.mCount + "");
		
		return contentView;
	}
	
	/**
	 * @author yuanweinan
	 *
	 */
	private class ViewHolder {
		public ImageView mImageView;
		public TextView mNameTextView;
		public TextView mCountTextView;
	}

}
