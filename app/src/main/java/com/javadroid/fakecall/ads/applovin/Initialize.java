package com.javadroid.fakecall.ads.applovin;

import android.content.Context;

import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;

public class Initialize {
    public static void SDK(Context context){
        AppLovinSdk.initializeSdk(context);
        AppLovinPrivacySettings.setHasUserConsent(true, context);
    }
}
