package com.yiyang.rnamaplocating;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.SystemClock;
import com.facebook.react.modules.core.DeviceEventManagerModule;


/**
 * Created by yiyang on 16/4/11.
 */
public class ReactMapLocationModule extends ReactContextBaseJavaModule {

    private AMapLocationClient mClient;

    @Override
    public String getName() {
        return "YYAMapLocationObserver";
    }

    YYAMapLocationListener.AMapLocationCallback aMapLocationCallback = new YYAMapLocationListener.AMapLocationCallback() {
        @Override
        public void onSuccess(AMapLocation aMapLocation) {
            getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("yyAMapLocationDidChange", locationToMap(aMapLocation));
        }

        @Override
        public void onFailure(AMapLocation aMapLocation) {
            emitError("unable to locate, errorCode = " + aMapLocation.getErrorCode());
        }
    };

    public ReactMapLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void getCurrentPosition(ReadableMap options, final Callback success, Callback error) {
        AMapLocationClientOption option = defaultOption();
        AMapLocationClient client = new AMapLocationClient(getReactApplicationContext());
        client.setLocationOption(option);
        AMapLocation lastLocation = client.getLastKnownLocation();
        if (lastLocation != null) {
            long locationTime = lastLocation.getTime();
            if (locationTime > 0 && (SystemClock.currentTimeMillis() - locationTime < 1000)) {

                success.invoke(locationToMap(lastLocation));
                return;
            }
        }
        new SingleUpdateRequest(client, success, error).invoke();
    }

    @ReactMethod
    public void startObserving(ReadableMap options) {
        AMapLocationClientOption option = defaultOption();

        if (mClient == null) {
            mClient = new AMapLocationClient(getReactApplicationContext().getApplicationContext());
            mClient.setLocationOption(option);
            YYAMapLocationListener listener = new YYAMapLocationListener(aMapLocationCallback);
            mClient.setLocationListener(listener);
        } else {
            mClient.setLocationOption(option);
        }

        if (!mClient.isStarted()) {
            mClient.startLocation();
        }
    }

    @ReactMethod
    public void stopObserving() {
        if (mClient != null) {
            mClient.stopLocation();
        }
    }

    public static AMapLocationClientOption defaultOption() {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setInterval(30 * 1000);
        return option;
    }

    private static WritableMap locationToMap(AMapLocation location) {
        if (location == null) {
            return null;
        }

        WritableMap map = Arguments.createMap();
        WritableMap coords = Arguments.createMap();
        coords.putDouble("latitude", location.getLatitude());
        coords.putDouble("longitude", location.getLongitude());
        coords.putString("address", location.getAddress());
        coords.putDouble("accuracy", location.getAccuracy());
        coords.putDouble("heading", location.getBearing());
        map.putMap("coords", coords);
        map.putDouble("timestamp", location.getTime());

        return map;

    }


    private void emitError(String error) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("yyAMapLocationError", error);
    }

    private static class SingleUpdateRequest {
        private final Callback mSuccess;
        private final Callback mError;
        private final AMapLocationClient mClient;

        private final YYAMapLocationListener mListenr;

        private final YYAMapLocationListener.AMapLocationCallback mCallback = new YYAMapLocationListener.AMapLocationCallback() {
            @Override
            public void onSuccess(AMapLocation aMapLocation) {
                mSuccess.invoke(locationToMap(aMapLocation));
                mClient.unRegisterLocationListener(mListenr);
                mClient.stopLocation();
            }

            @Override
            public void onFailure(AMapLocation aMapLocation) {
                mError.invoke("locating failed: " + aMapLocation.getErrorCode());
                mClient.unRegisterLocationListener(mListenr);
                mClient.stopLocation();
            }
        };

        private SingleUpdateRequest(AMapLocationClient client, Callback success, Callback error) {
            this.mClient = client;
            mSuccess = success;
            mError = error;
            mListenr = new YYAMapLocationListener(mCallback);
            mClient.setLocationListener(mListenr);
        }

        public void invoke() {
            if (mClient == null) {
                return ;
            }
            mClient.startLocation();
        }
    }
}
