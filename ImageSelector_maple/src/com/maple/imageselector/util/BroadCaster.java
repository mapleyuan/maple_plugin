package com.maple.imageselector.util;

import java.util.ArrayList;

/**
 * @author yuanweinan
 *
 */
public class BroadCaster {

	private ArrayList<BroadCasterObserver> mObservers;

	private Object mLockerForBroad = new Object();

	public static interface BroadCasterObserver {

		public void onBgChange(int msgId, int param, Object... objects);

	}

	public void registerObserver(BroadCasterObserver oberver) {
		if (oberver == null) {
			return;
		}
		synchronized (mLockerForBroad) {

			if (mObservers == null) {
				mObservers = new ArrayList<BroadCasterObserver>();
			}
			try {
				if (mObservers.indexOf(oberver) < 0) {
					mObservers.add(oberver);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean unRegisterObserver(BroadCasterObserver observer) {

		synchronized (mLockerForBroad) {
			if (null == mObservers) {
				return false;
			}

			return mObservers.remove(observer);
		}
	}

	public void clearAllObserver() {

		synchronized (mLockerForBroad) {
			if (mObservers != null) {
				mObservers.clear();
				mObservers = null;
			}
		}
	}

	public void broadCast(int msgId, int param, Object... objects) {

		synchronized (mLockerForBroad) {
			if (mObservers == null) {
				return;
			}

			ArrayList<BroadCasterObserver> clone = (ArrayList<BroadCasterObserver>) mObservers
					.clone();
			for (BroadCasterObserver broadCasterObserver : clone) {
				if (broadCasterObserver != null) {
					broadCasterObserver.onBgChange(msgId, param, objects);
				}
			}
		}
	}

	public ArrayList<BroadCasterObserver> getObserver() {
		return mObservers;
	}
}
