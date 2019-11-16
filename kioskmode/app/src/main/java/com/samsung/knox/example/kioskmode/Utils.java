package com.samsung.knox.example.kioskmode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.TextView;

import com.samsung.android.knox.EnterpriseDeviceManager;

/**
 * This is a Utility class that is composed of helper methods not completely relevant to the main feature of this app.
 */
public class Utils {

    private TextView textView;
    private String TAG;

    public Utils(TextView view, String className) {
        textView = view;
        TAG = className;
    }

    /** Check Knox API level on device, if it does not meet minimum requirement, the end user
     * cannot use the application */
    public void checkApiLevel(int apiLevel, final Context context) {
        if(EnterpriseDeviceManager.getAPILevel() < apiLevel) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);
            String msg = context.getResources().getString(R.string.api_level_message, EnterpriseDeviceManager.getAPILevel(),apiLevel);
            builder.setTitle(R.string.app_name)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("CLOSE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            })
                    .show();
        }
    }

    /** Log results to a textView in application UI */
    public void log(String text) {
        textView.append(text);
        textView.append("\n\n");
        textView.invalidate();
        Log.d(TAG,text);
    }

    /** Process the exception */
    public void processException(Exception ex, String TAG) {
        if (ex != null) {
            // present the exception message
            String msg = ex.getClass().getCanonicalName() + ": " + ex.getMessage();
            textView.append(msg);
            textView.append("\n\n");
            textView.invalidate();
            Log.e(TAG, msg);
        }
    }
}