package com.javadroid.fakecall.ads.applovin;

import static com.javadroid.fakecall.config.SettingAll.interval_inter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdk;
import com.javadroid.fakecall.manager.LanguageManager;

import java.util.Locale;

public class Inter {
    private static AppLovinAd currentAd;
    public static AppLovinInterstitialAdDialog interstitialAdZone;
    private static boolean loadingIklanapzone=true;
    private static Integer hitungapzone=0;

    public static void LOAD(Activity activity){
        AppLovinSdk.getInstance( activity ).getAdService().loadNextAd( AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd ad)
            {
                currentAd = ad;
            }

            @Override
            public void failedToReceiveAd(int errorCode) {

            }
        } );
        interstitialAdZone = AppLovinInterstitialAd.create( AppLovinSdk.getInstance( activity ), activity );
    }

    public static void SHOWADS(Activity activity){
        if ( currentAd != null ) {
            interstitialAdZone.showAndRender( currentAd );
        }
        LOAD(activity);
    }
    public static void SHOW(Activity activity) {
        hitungapzone++;
        if (loadingIklanapzone) {
            AppLovinSdk.getInstance( activity ).getAdService().loadNextAd( AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                @Override
                public void adReceived(AppLovinAd ad)
                {
                    currentAd = ad;
                }

                @Override
                public void failedToReceiveAd(int errorCode) {

                }
            } );
            interstitialAdZone = AppLovinInterstitialAd.create( AppLovinSdk.getInstance( activity ), activity );
            loadingIklanapzone = false;
        }
        if (hitungapzone % interval_inter == 0) {
            if ( currentAd != null ) {
                interstitialAdZone.showAndRender( currentAd );
                loadingIklanapzone = true;
            }
        }
    }
}
