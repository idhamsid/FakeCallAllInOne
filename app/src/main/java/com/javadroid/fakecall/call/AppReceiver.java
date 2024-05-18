package com.javadroid.fakecall.call;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.javadroid.fakecall.call.fb.VideoCall;
import com.javadroid.fakecall.call.fb.SuaraCall;

public class AppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (Tools.template_call) {
            case 1:
                switch (Tools.type_call) {
                    case 2: {
                        Intent intent2 = new Intent(context, com.javadroid.fakecall.call.wa.SuaraCall.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent2);
                        break;
                    }
                    case 1:
                    default: {
                        Intent intent2 = new Intent(context, com.javadroid.fakecall.call.wa.VideoCall.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent2);
                        break;
                    }
                }
                break;
            case 2:
                switch (Tools.type_call) {
                    case 2: {
                        Intent intent2 = new Intent(context, SuaraCall.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent2);
                        break;
                    }
                    case 1:
                    default: {
                        Intent intent2 = new Intent(context, VideoCall.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent2);
                        break;
                    }
                }
                break;
            case 3:
                switch (Tools.type_call) {
                    case 2: {
                        Intent intent2 = new Intent(context, com.javadroid.fakecall.call.tele.SuaraCall.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent2);
                        break;
                    }
                    case 1:
                    default: {
                        Intent intent2 = new Intent(context, com.javadroid.fakecall.call.tele.VideoCall.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent2);
                        break;
                    }
                }
                break;
        }

    }

}
