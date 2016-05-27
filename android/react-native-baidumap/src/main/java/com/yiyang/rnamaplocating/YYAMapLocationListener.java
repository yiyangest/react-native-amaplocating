package com.yiyang.rnamaplocating;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by yiyang on 16/5/26.
 */
public class YYAMapLocationListener implements AMapLocationListener {
    private AMapLocationCallback mCallback;

    public YYAMapLocationListener(AMapLocationCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            Log.e("AMapLocating", "received amaplocation is null");
            return;
        }

        if (aMapLocation.getErrorCode() == 0) {
            Log.d("AMapLocating", "location info: " + aMapLocation.getLatitude() + " - " + aMapLocation.getLongitude() + " / " + aMapLocation.getLocationDetail());
            if (mCallback != null) {
                mCallback.onSuccess(aMapLocation);
            }
        } else {
            Log.e("AMapLocating", "locating error code: " + aMapLocation.getErrorCode() + " - error info: " + aMapLocation.getErrorInfo());
            if (mCallback != null) {
                mCallback.onFailure(aMapLocation);
            }
        }
    }

    public interface AMapLocationCallback {
        void onSuccess(AMapLocation aMapLocation);
        void onFailure(AMapLocation aMapLocation);
    }
}
