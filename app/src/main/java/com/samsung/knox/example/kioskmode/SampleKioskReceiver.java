package com.samsung.knox.example.kioskmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.samsung.android.knox.kiosk.KioskMode;

/**
 * This BroadcastReceiver handles Kiosk Mode activation results.

 * @author s.veloso@partner.samsung.com
 */
public class SampleKioskReceiver extends BroadcastReceiver {

    private MainActivity mMainActivity;

    public void setMainActivityHandler (MainActivity main) {
        mMainActivity = main;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int extra = intent.getIntExtra(KioskMode.EXTRA_KIOSK_RESULT, -1);

        if (action.equals(KioskMode.ACTION_ENABLE_KIOSK_MODE_RESULT)) {
            switch (extra) {
                case 0:
                    Toast.makeText(context, context.getString(R.string.activated_kiosk_successfully), Toast.LENGTH_SHORT).show();
                    mMainActivity.addShortcutToKioskMode();
                    mMainActivity.finish();
                    break;
                case -1:
                    Toast.makeText(context, context.getString(R.string.kiosk_already_enabled), Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(context, context.getString(R.string.kiosk_enabled_by_other_admin), Toast.LENGTH_SHORT).show();
                    break;
                case -3:
                    Toast.makeText(context, context.getString(R.string.kiosk_package_not_found), Toast.LENGTH_SHORT).show();
                    break;
                case -4:
                    Toast.makeText(context, context.getString(R.string.kiosk_error_busy), Toast.LENGTH_SHORT).show();
                    break;
                case -2000:
                    Toast.makeText(context, context.getString(R.string.kiosk_unknown_error), Toast.LENGTH_SHORT).show();
                    break;
            }
        } else if (action.equals(KioskMode.ACTION_DISABLE_KIOSK_MODE_RESULT)) {
            switch (extra) {
                case 0:
                    Toast.makeText(context, context.getString(R.string.exited_kiosk_successfully), Toast.LENGTH_SHORT).show();
                    mMainActivity.finish();
                    break;
                case -1:
                    Toast.makeText(context, context.getString(R.string.kiosk_already_enabled), Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(context, context.getString(R.string.kiosk_enabled_by_other_admin), Toast.LENGTH_SHORT).show();
                    break;
                case -3:
                    Toast.makeText(context, context.getString(R.string.kiosk_package_not_found), Toast.LENGTH_SHORT).show();
                    break;
                case -4:
                    Toast.makeText(context, context.getString(R.string.kiosk_error_busy), Toast.LENGTH_SHORT).show();
                    break;
                case -2000:
                    Toast.makeText(context, context.getString(R.string.kiosk_unknown_error), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}