package com.maple.imageselector.pojo;

/**
 * @author yuanweinan
 * 
 */
public class FolderUnit {

	public String mFolderDir;
	public String mFirstImagePath;
	public int mCount;
	private String mName;

	public FolderUnit(String folderDir, String firstImagePath, int count) {
		mFolderDir = folderDir;
		mFirstImagePath = firstImagePath;
		mCount = count;
	}

	public FolderUnit(String folderDir, String firstImagePath) {
		mFolderDir = folderDir;
		mFirstImagePath = firstImagePath;
	}

	/**
	 * @return
	 */
	public String getName() {
		if (mName == null || mName.length() <= 0) {
			int lastIndexOf = mFolderDir.lastIndexOf("/");
			mName = mFolderDir.substring(lastIndexOf + 1);
		}
		return mName;
	}
}
