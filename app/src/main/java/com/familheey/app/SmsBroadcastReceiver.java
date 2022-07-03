package com.familheey.app;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_PREFIX_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;


public class SmsBroadcastReceiver extends BroadcastReceiver {
    public SmsBroadcastReceiverListener smsBroadcastReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras=intent.getExtras();
        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);

        if (intent.getAction() == SmsRetriever.SMS_RETRIEVED_ACTION) {
            Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (smsRetrieverStatus.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    ComponentName name = consentIntent.resolveActivity(context.getPackageManager());

                    Log.e("test", "onReceive: "+name.getPackageName() + " " + name.getClassName());
                    if (name.getPackageName().equalsIgnoreCase("com.google.android.gms") &&
                            name.getClassName().equalsIgnoreCase("com.google.android.gms.auth.api.phone.ui.UserConsentPromptActivity")) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            consentIntent.removeFlags(FLAG_GRANT_READ_URI_PERMISSION);
                            consentIntent.removeFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                            consentIntent.removeFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                            consentIntent.removeFlags(FLAG_GRANT_PREFIX_URI_PERMISSION);
                        }
                        smsBroadcastReceiverListener.onSuccess(consentIntent);
                    }

                    break;
                case CommonStatusCodes.TIMEOUT:
                    smsBroadcastReceiverListener.onFailure();
                    break;
            }
        }
    }

    public interface SmsBroadcastReceiverListener {
        void onSuccess(Intent intent);

        void onFailure();
    }
}
