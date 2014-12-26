package com.maple.imageselector.pojo;

import com.maple.imageselector.util.BroadCaster;


/**
 * @author yuanweinan
 *
 */
public class ImageUnit extends BroadCaster {
	public static final int SELECTED_CHANGED = 0x01;
	
	public String mImageName;
	public boolean mIsSelected = false;
}
