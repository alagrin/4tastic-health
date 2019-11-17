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
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

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

public class SampleTagReceiver  {

    private int DEFAULT_ERROR_CODE = -1;

    private void showToast(Context context, int msg_res) {
        Toast.makeText(context, context.getResources().getString(msg_res), Toast.LENGTH_SHORT).show();
    }

    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //@Override
    //public void onReceive(Context context, Intent intent) {
    public void onReceive(Intent intent){
        //Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //byte[] b = tag.getId();
        //String s = new String(b);
        Log.println(1,"yes", "got here");
        //mUtils.log(s);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                    Log.w("Myapp", messages[i].toString());
                    //mUtils.log(messages[i].toString());

                }
                // Process the messages array

            }
        }
    }
}