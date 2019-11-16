/**
 * DISCLAIMER: PLEASE TAKE NOTE THAT THE SAMPLE APPLICATION AND
 * SOURCE CODE DESCRIBED HEREIN IS PROVIDED FOR TESTING PURPOSES ONLY.
 * <p>
 * Samsung expressly disclaims any and all warranties of any kind,
 * whether express or implied, including but not limited to the implied warranties and conditions
 * of merchantability, fitness for com.samsung.knoxsdksample particular purpose and non-infringement.
 * Further, Samsung does not represent or warrant that any portion of the sample application and
 * source code is free of inaccuracies, errors, bugs or interruptions, or is reliable,
 * accurate, complete, or otherwise valid. The sample application and source code is provided
 * "as is" and "as available", without any warranty of any kind from Samsung.
 * <p>
 * Your use of the sample application and source code is at its own discretion and risk,
 * and licensee will be solely responsible for any damage that results from the use of the sample
 * application and source code including, but not limited to, any damage to your computer system or
 * platform. For the purpose of clarity, the sample code is licensed “as is” and
 * licenses bears the risk of using it.
 * <p>
 * Samsung shall not be liable for any direct, indirect or consequential damages or
 * costs of any type arising out of any action taken by you or others related to the sample application
 * and source code.
 */
package com.samsung.knox.example.kioskmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.knox.license.EnterpriseLicenseManager;
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;

/**
 * This BroadcastReceiver handles KPE activation results. It has to be registered in manifest file like so:
 * <p>
 * <pre>
 *     <code>
 *      <receiver android:name=".SampleLicenseReceiver">
 *          <intent-filter>*
 *              <action android:name="com.samsung.android.knox.intent.action.LICENSE_STATUS"
 *          </intent-filter>
 *      </receiver>
 *      </code>
 * </pre>
 *
 * @author v.okunev, s.veloso
 */

public class SampleLicenseReceiver extends BroadcastReceiver {

    private int DEFAULT_ERROR_CODE = -1;

    private void showToast(Context context, int msg_res) {
        Toast.makeText(context, context.getResources().getString(msg_res), Toast.LENGTH_SHORT).show();
    }

    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int msg_res = -1;

        if (intent == null) {
            // No intent action is available
            showToast(context, R.string.no_intent);
            return;
        } else {
            String action = intent.getAction();
            if (action == null) {
                // No intent action is available
                showToast(context, R.string.no_intent_action);
                return;
            }  else if (action.equals(KnoxEnterpriseLicenseManager.ACTION_LICENSE_STATUS)) {
                // KPE activation result Intent is obtained
                int errorCode = intent.getIntExtra(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE, DEFAULT_ERROR_CODE);

                if (errorCode == KnoxEnterpriseLicenseManager.ERROR_NONE) {
                    // KPE activated successfully
                    showToast(context, R.string.kpe_activated_successfully);
                    Log.d("SampleLicenseReceiver", context.getString(R.string.kpe_activated_successfully));
                    return;
                } else {
                    //KPE activation failed
                    switch (errorCode) {
                        case KnoxEnterpriseLicenseManager.ERROR_INTERNAL:
                            msg_res = R.string.err_kpe_internal;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_INTERNAL_SERVER:
                            msg_res = R.string.err_kpe_internal_server;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE:
                            msg_res = R.string.err_kpe_licence_invalid_license;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_INVALID_PACKAGE_NAME:
                            msg_res = R.string.err_kpe_invalid_package_name;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_LICENSE_TERMINATED:
                            msg_res = R.string.err_kpe_licence_terminated;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_NETWORK_DISCONNECTED:
                            msg_res = R.string.err_kpe_network_disconnected;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_NETWORK_GENERAL:
                            msg_res = R.string.err_kpe_network_general;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_NOT_CURRENT_DATE:
                            msg_res = R.string.err_kpe_not_current_date;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_NULL_PARAMS:
                            msg_res = R.string.err_kpe_null_params;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_UNKNOWN:
                            msg_res = R.string.err_kpe_unknown;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_USER_DISAGREES_LICENSE_AGREEMENT:
                            msg_res = R.string.err_kpe_user_disagrees_license_agreement;
                            break;
                        default:
                            // Unknown error code
                            String errorStatus = intent.getStringExtra(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
                            String msg = context.getResources().getString(R.string.err_kpe_code_unknown, Integer.toString(errorCode), errorStatus);
                            showToast(context, msg);
                            Log.d("SampleLicenseReceiver", msg);
                            return;
                    }

                    // Display KPE error message
                    showToast(context, msg_res);
                    Log.d("SampleLicenseReceiver", context.getString(msg_res));
                    return;
                }
            }
        }
    }
}