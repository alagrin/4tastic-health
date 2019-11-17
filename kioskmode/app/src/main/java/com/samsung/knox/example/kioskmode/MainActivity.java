package com.samsung.knox.example.kioskmode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.samsung.android.knox.AppIdentity;
import com.samsung.android.knox.EnterpriseDeviceManager;
import com.samsung.android.knox.application.ApplicationPolicy;
import com.samsung.android.knox.datetime.DateTimePolicy;
import com.samsung.android.knox.kiosk.KioskMode;
import com.samsung.android.knox.kiosk.KioskSetting;
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;
import com.samsung.android.knox.location.LatLongPoint;
import com.samsung.android.knox.location.LocationPolicy;
import com.samsung.android.knox.nfc.NfcPolicy;
import com.samsung.knox.example.kioskmode.ui.login.LoginActivity;
import com.samsung.knox.example.kioskmode.DateTime;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;


import static com.samsung.android.knox.application.ApplicationPolicy.PERMISSION_POLICY_STATE_GRANT;

/**
 * This activity displays the main UI of the application. This is a simple application to enable
 * and/or disable the use of certain apps using the Samsung Knox SDK.
 * Read more about the Knox SDK here:
 * https://seap.samsung.com/sdk
 * <p>
 * Please insert valid KPE key to {@link Constants}.
 * </p>
 *
 * @author s.veloso@partner.samsung.com
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    static final int DEVICE_ADMIN_ADD_RESULT_ENABLE = 1;

    private Button mToggleAdminBtn;
    private Button mToggleDefaultKioskBtn;
    private Button mToggleCustomKioskBtn;
    private ComponentName mDeviceAdmin;
    private DevicePolicyManager mDPM;
    private SampleKioskReceiver mSampleKioskReceiver;
    private Utils mUtils;
    private Button mToggleLogViewBtn;
    private Button mToggleDateTime;
    private Button mToggleLocationBtn;
    private Button mToggleNFCBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //...called when the activity is starting. This is where most initialization should go.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView logView = (TextView) findViewById(R.id.logview_id);
        logView.setMovementMethod(new ScrollingMovementMethod());
        mToggleAdminBtn = (Button) findViewById(R.id.toggleAdminBtn);
        Button activateKPEBtn = (Button) findViewById(R.id.activateLicenceBtn);
        Button grantPermissonsBtn = (Button) findViewById(R.id.grantPermissionsBtn);
        mToggleDefaultKioskBtn = (Button) findViewById(R.id.toggleDefaultKioskBtn);
        mToggleCustomKioskBtn = (Button) findViewById(R.id.toggleCustomKioskButton) ;
        mToggleLogViewBtn = (Button) findViewById(R.id.toggleLogViewBtn);
        mToggleDateTime = (Button) findViewById(R.id.toggleDateTime);
        mToggleLocationBtn = (Button) findViewById(R.id.toggleLocation);
        mToggleNFCBtn = (Button) findViewById(R.id.toggleNFC);
        mDeviceAdmin = new ComponentName(MainActivity.this, SampleAdminReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mUtils = new Utils(logView, TAG);

        // Check if device supports Knox SDK
        mUtils.checkApiLevel(24, this);

        mToggleAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {toggleAdmin();
            }
        });
        activateKPEBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateKPELicense();
            }
        });
        grantPermissonsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){grantPermissions();
            }
        });
        mToggleDefaultKioskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleKiosk();
            }
        });
        mToggleCustomKioskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptCustomKioskSetting();
            }
        });
        mToggleLogViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {launchLoginView();}
        });
        mToggleDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {processTimeData();}
        });
        mToggleLocationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){processLocation();}
        });
        mToggleNFCBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){processNFC();}
        });

        mSampleKioskReceiver = new SampleKioskReceiver();
        mSampleKioskReceiver.setMainActivityHandler(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KioskMode.ACTION_ENABLE_KIOSK_MODE_RESULT);
        intentFilter.addAction(KioskMode.ACTION_DISABLE_KIOSK_MODE_RESULT);
        registerReceiver(mSampleKioskReceiver, intentFilter);
    }

    /*
    * If Admin is activated, deactivate this app as device administrator with no explanation.
    * If Admin is deactivated, present a dialog to activate device administrator for this application.
    */
    private void toggleAdmin() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        boolean adminActive = dpm.isAdminActive(mDeviceAdmin);

        if (adminActive) { // If Admin is activated
            mUtils.log(getResources().getString(R.string.deactivating_admin));
            // Deactivate this application as device administrator
            dpm.removeActiveAdmin(new ComponentName(this, SampleAdminReceiver.class));
            finish();
        } else { // If Admin is deactivated
            mUtils.log(getResources().getString(R.string.activating_admin));
            // Ask the user to add a new device administrator to the system
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
            // Start the add device admin activity
            startActivityForResult(intent, DEVICE_ADMIN_ADD_RESULT_ENABLE);
        }
    }

    /*
    * Handle result of device administrator activation request
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DEVICE_ADMIN_ADD_RESULT_ENABLE) {
            switch (resultCode) {
                // End user cancels the request
                case Activity.RESULT_CANCELED:
                    mUtils.log(getResources().getString(R.string.admin_cancelled));
                    break;
                // End user accepts the request
                case Activity.RESULT_OK:
                    mUtils.log(getResources().getString(R.string.admin_activated));
                    refreshButtons();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mSampleKioskReceiver);
    }

    /*
    *  Update button state.
    */
    public void refreshButtons() {
        boolean adminState = mDPM.isAdminActive(mDeviceAdmin);
        boolean kioskState = EnterpriseDeviceManager.getInstance(this.getApplicationContext()).getKioskMode().isKioskModeEnabled();

        if (!adminState) {
            mToggleAdminBtn.setText(getString(R.string.activate_admin));
        } else {
            mToggleAdminBtn.setText(getString(R.string.deactivate_admin));
        }

        if (!kioskState) {
            mToggleDefaultKioskBtn.setText(getString(R.string.enter_default_kiosk));
            mToggleCustomKioskBtn.setText(getString(R.string.enter_custom_kiosk));
        } else {
            mToggleDefaultKioskBtn.setText(getString(R.string.leave_default_kiosk));
            mToggleCustomKioskBtn.setText(getString(R.string.leave_custom_kiosk));
        }
    }

    /*
     * Note that embedding your license key in code is unsafe and is done here for
     * demonstration purposes only.
     * Please visit https://seap.samsung.com/license-keys/about. for more details about license
     * keys.
     *
     */
    private void activateKPELicense() {
        // Instantiate the KnoxEnterpriseLicenseManager class to use the activateLicense method
        KnoxEnterpriseLicenseManager kpeManager = KnoxEnterpriseLicenseManager.getInstance(this.getApplicationContext());

        try {
            // KPE License Activation TODO Add license key to Constants.java
            kpeManager.activateLicense(Constants.KPE_LICENSE_KEY);
            mUtils.log(getResources().getString(R.string.license_progress));

        } catch (Exception e) {
            mUtils.processException(e, TAG);
        }
    }

    /*
     * The Knox SDK camera restriction method is protected by the following permission:
     * "com.samsung.android.knox.permission.KNOX_HW_CONTROL". The following
     * demonstrates the use of the Knox SDK applyRuntimePermissions method to grant the above
     * permission to this application. Failing to grant this permission will result in a
     * SecurityException.
     */
    private void grantPermissions() {

        List<String> runtimePermissions = new ArrayList<>();
        // Permissions TODO Add permssions to be used by your application
        runtimePermissions.add(getString(R.string.permission_3));
        // Instantiate the EnterpriseDeviceManager class
        EnterpriseDeviceManager enterpriseDeviceManager = EnterpriseDeviceManager.getInstance(this.getApplicationContext());
        // Get the ApplicationPolicy class where the applyRuntimePermissions method lives
        ApplicationPolicy applicationPolicy = enterpriseDeviceManager.getApplicationPolicy();

        AppIdentity appIdentity = new AppIdentity(getApplicationContext().getPackageName(), null);
        try {
            // Grant runtime permissions to list of permissions provided
            int ret = applicationPolicy.applyRuntimePermissions(appIdentity, runtimePermissions,
                    PERMISSION_POLICY_STATE_GRANT);
            if (ret == 0) {

                for (String permission : runtimePermissions) {
                    mUtils.log(getResources().getString(R.string.permissions_granted, permission));
                }

            } else {
                mUtils.log(getResources().getString(R.string.permissions_failed));
            }

        } catch (Exception e) {
            mUtils.processException(e, TAG);
        }
    }

    /*
    * Enable/disable Kiosk Mode depending on current Kiosk state.
    */
    private void toggleKiosk () {
        try {
            EnterpriseDeviceManager enterpriseDeviceManager = EnterpriseDeviceManager.getInstance(this.getApplicationContext()); // Instantiate the EnterpriseDeviceManager class
            KioskMode kioskMode = enterpriseDeviceManager.getKioskMode(); // Get the KioskMode object where the enable/disableKioskMode method lives

            boolean kioskState = kioskMode.isKioskModeEnabled();

            if (kioskState) { // If in Kiosk Mode, disable Kiosk Mode
                mUtils.log(getString(R.string.leaving_kiosk));
                removeShortcutFromKioskMode();
                kioskMode.disableKioskMode();
            } else { // If not in Kiosk Mode, enable Kiosk Mode with the current package as the home package
                mUtils.log(getString(R.string.entering_kiosk));
                kioskMode.enableKioskMode();
            }
        } catch (SecurityException e) {
            mUtils.processException(e, TAG);
        }
    }

    /*
    * Enable/disable Kiosk Mode depending on current Kiosk state - with further user configuration.
    */
    private void toggleCustomKiosk (KioskSetting kioskSetting) {
        try {
            EnterpriseDeviceManager enterpriseDeviceManager = EnterpriseDeviceManager.getInstance(this.getApplicationContext()); // Instantiate the EnterpriseDeviceManager class
            KioskMode kioskMode = enterpriseDeviceManager.getKioskMode(); // Get the KioskMode object where the enable/disableKioskMode method lives

            boolean kioskState = kioskMode.isKioskModeEnabled();

            if (kioskState) { // If in Kiosk Mode, disable Kiosk Mode
                mUtils.log(getString(R.string.leaving_kiosk));
                removeShortcutFromKioskMode();
                kioskMode.disableKioskMode(kioskSetting);
            } else { // If not in Kiosk Mode, enable Kiosk Mode with provided Kiosk Mode settings
                mUtils.log(getString(R.string.entering_kiosk));
                kioskMode.enableKioskMode(kioskSetting);
            }
        } catch (SecurityException e) {
            mUtils.processException(e, TAG);
        }
    }

    /*
    * Prompt the user to configure settings for entering Kiosk Mode.
    *
    * For example, the user can disable changing settings and expanding the status bar
    * in Kiosk Mode.
    */
    private void promptCustomKioskSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Build a dialog to show the user
        builder.setTitle(getString(R.string.configure_kiosk_setting)); // WIth the title "Configure Certificate"
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.prompt_kiosk_setting, (ViewGroup) findViewById(R.id.kiosk_config_group), false);

        final CheckBox chkSettingsChanges = (CheckBox) viewInflated.findViewById(R.id.chkSettingsChange);
        final CheckBox chkStatusBar = (CheckBox) viewInflated.findViewById(R.id.chkAllowStatusBar);
        final CheckBox chkStatusBarExpansion = (CheckBox) viewInflated.findViewById(R.id.chkStatusBarExpansion);
        final CheckBox chkSystemBar = (CheckBox) viewInflated.findViewById(R.id.chkAllowSystemBar);
        final CheckBox chkTaskManager = (CheckBox) viewInflated.findViewById(R.id.chkAllowTaskManager);
        final CheckBox chkHomekey = (CheckBox) viewInflated.findViewById(R.id.chkAllowHomekey);
        final CheckBox chkAirCommand = (CheckBox) viewInflated.findViewById(R.id.chkAllowAirCommand);
        final CheckBox chkAirView = (CheckBox) viewInflated.findViewById(R.id.chkAllowAirview);
        final CheckBox chkMultiwindow = (CheckBox) viewInflated.findViewById(R.id.chkAllowMultiwindow);
        final CheckBox chkSmartclip = (CheckBox) viewInflated.findViewById(R.id.chkAllowSmartClip);
        final CheckBox chkNavBar = (CheckBox) viewInflated.findViewById(R.id.chkAllowNavBar);
        final CheckBox chkWipeRecentTasks = (CheckBox) viewInflated.findViewById(R.id.chkWipeRecentTasks);
        final CheckBox chkClearNotifications = (CheckBox) viewInflated.findViewById(R.id.chkClearNotifications);

        builder.setView(viewInflated);
        builder.setPositiveButton(getString(R.string.option_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KioskSetting kioskSetting = new KioskSetting();
                kioskSetting.settingsChanges = chkSettingsChanges.isChecked();
                kioskSetting.statusBar = chkStatusBar.isChecked();
                kioskSetting.statusBarExpansion = chkStatusBarExpansion.isChecked();
                kioskSetting.systemBar = chkSystemBar.isChecked();
                kioskSetting.taskManager = chkTaskManager.isChecked();
                kioskSetting.homeKey = chkHomekey.isChecked();
                kioskSetting.airCommand = chkAirCommand.isChecked();
                kioskSetting.airView = chkAirView.isChecked();
                kioskSetting.multiWindow = chkMultiwindow.isChecked();
                kioskSetting.smartClip = chkSmartclip.isChecked();
                kioskSetting.navigationBar = chkNavBar.isChecked();
                kioskSetting.wipeRecentTasks = chkWipeRecentTasks.isChecked();
                kioskSetting.clearAllNotifications = chkClearNotifications.isChecked();

                toggleCustomKiosk(kioskSetting);
            }
        });

        builder.setNegativeButton(getString(R.string.option_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /*
    * Add this sample app's shortcut to Kiosk Mode.
    */
    public void addShortcutToKioskMode() {
        EnterpriseDeviceManager enterpriseDeviceManager = EnterpriseDeviceManager.getInstance(this.getApplicationContext()); // Instantiate the EnterpriseDeviceManager class
        ApplicationPolicy applicationPolicy = enterpriseDeviceManager.getApplicationPolicy(); // Get the ApplicationPolicy object where the addHomeShortcut method lives
        KioskMode kioskMode = enterpriseDeviceManager.getKioskMode(); // Get the KioskMode object where the getKioskHomePackage method lives

        try {
            String kioskHomePackage = kioskMode.getKioskHomePackage(); // Get the Kiosk home package

            boolean result = applicationPolicy.addHomeShortcut(getPackageName(), kioskHomePackage); // Add the sample app shortcut to the Kiosk home package
            if (result) {
                mUtils.log(getString(R.string.added_app_to_kiosk));
            } else {
                mUtils.log(getString(R.string.failed_add_app_to_kiosk));
            }
        } catch (SecurityException e) {
            mUtils.processException(e, TAG);
        }
    }

    /*
    * Remove this sample app's shortcut from Kiosk Mode.
    */
    public void removeShortcutFromKioskMode() {
        EnterpriseDeviceManager enterpriseDeviceManager = EnterpriseDeviceManager.getInstance(this.getApplicationContext()); // Instantiate the EnterpriseDeviceManager class
        ApplicationPolicy applicationPolicy = enterpriseDeviceManager.getApplicationPolicy(); // Get the ApplicationPolicy object where the removeHomeShortcut method lives
        KioskMode kioskMode = enterpriseDeviceManager.getKioskMode(); // Get the KioskMode object where the getKioskHomePackage method lives

        try {
            String kioskHomePackage = kioskMode.getKioskHomePackage(); // Get the Kiosk home package

            boolean result = applicationPolicy.deleteHomeShortcut(getPackageName(), kioskHomePackage); // Add the sample app shortcut to the Kiosk home package
            if (result) {
                mUtils.log(getString(R.string.removed_app_from_kiosk));
            } else {
                mUtils.log(getString(R.string.failed_remove_app_kiosk));
            }
        } catch (SecurityException e) {
            mUtils.processException(e, TAG);
        }
    }

    public void launchLoginView() {
        mUtils.log("launched login activity");
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i); // brings up the second activity
    }

    public void processTimeData() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(this.getApplicationContext());
        DateTimePolicy policy = edm.getDateTimePolicy();
        Date date = policy.getDateTime();
        String test = date.toString();
        mUtils.log(test);
    }

    public void processLocation(){
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(this.getApplicationContext());
        LocationPolicy policy = edm.getLocationPolicy();

        //setGPS
        policy.setGPSStateChangeAllowed(false);
        mUtils.log("GPS forced on");

        // turn on GPS
        policy.startGPS(true);
        mUtils.log("GPS started");

        mUtils.log("Android Last Location Captured");
        // to be done:
        // get Android location
    }

    public void processNFC(){
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(this.getApplicationContext());
        NfcPolicy policy = edm.getNfcPolicy();

        policy.allowNFCStateChange(false);
        mUtils.log("NFC forced on");

        policy.startNFC(true);
        mUtils.log("NFC started");

        mUtils.log("Identification Verified w/ NFC Reader");

        // sample NFC Code references:
        // https://medium.com/@ssaurel/create-a-nfc-reader-application-for-android-74cf24f38a6f

        // https://developer.android.com/guide/topics/connectivity/nfc/nfc

        // to be done:
        // allow nfc state change
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        String s = adapter.toString();
        mUtils.log(s);
    }

}
