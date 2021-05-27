package org.lineageos.setupwizard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManagerPolicyConstants;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

public class NavSettingsActivity extends BaseSetupWizardActivity implements RadioGroup.OnCheckedChangeListener {

    private String[] mSelectorTitle;
    private int[] mIds = {R.id.gesture_navigation, R.id.two_button_navigation, R.id.three_button_navigation};
    private ImageView mPreviewView;
    private RadioGroup mRadioGroup;

    private AlertDialog mLoadingDialog;
    private IOverlayManager mOverlayManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));
        this.mPreviewView = findViewById(R.id.gesture_view);
        this.mRadioGroup = findViewById(R.id.selector_items);
        this.mSelectorTitle = getResources().getStringArray(R.array.gesture_title_array);
//        migrateOverlaySensitivityToSettings(this.mOverlayManager);
        for (int i = 0; i < mIds.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(mIds[i]);
            radioButton.setText(mSelectorTitle[i]);
            radioButton.setPadding(getResources().getDimensionPixelSize(R.dimen.setup_nav_settings_radio_group_padding_left) / 2,
                    getResources().getDimensionPixelSize(R.dimen.setup_nav_settings_radio_group_padding_top),
                    0,
                    getResources().getDimensionPixelSize(R.dimen.setup_nav_settings_radio_group_padding_bottom));
            this.mRadioGroup.addView(radioButton, ViewGroup.LayoutParams.MATCH_PARENT, 156);
        }
        ViewGroup.LayoutParams mPreviewViewLayoutParams = this.mPreviewView.getLayoutParams();
        mPreviewViewLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.setup_nav_settings_image_height) * (getResources().getDisplayMetrics().heightPixels / 1920);
        this.mPreviewView.setLayoutParams(mPreviewViewLayoutParams);
        this.mPreviewView.setImageResource(isGestureNavigationEnabled() ? R.drawable.system_nav_fully_gestural : is2ButtonNavigationEnabled() ? R.drawable.system_nav_2_button : R.drawable.system_nav_3_button);
        this.mRadioGroup.setPadding(getResources().getDimensionPixelSize(R.dimen.setup_nav_settings_radio_group_padding_left),
                0,
                getResources().getDimensionPixelSize(R.dimen.setup_nav_settings_radio_group_padding_right),
                0);
        this.mRadioGroup.check(isGestureNavigationEnabled() ? R.id.gesture_navigation : is2ButtonNavigationEnabled() ? R.id.two_button_navigation : R.id.three_button_navigation);
        this.mRadioGroup.setOnCheckedChangeListener(this);
        this.mLoadingDialog = createLoadingDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.onWindowFocusChanged(false);
        if (this.mLoadingDialog.isShowing()) {
            this.mLoadingDialog.dismiss();
        }
    }

    boolean is2ButtonNavigationEnabled() {
        return WindowManagerPolicyConstants.NAV_BAR_MODE_2BUTTON == getResources().getInteger(
                com.android.internal.R.integer.config_navBarInteractionMode);
    }

    boolean isGestureNavigationEnabled() {
        return WindowManagerPolicyConstants.NAV_BAR_MODE_GESTURAL == getResources().getInteger(
                com.android.internal.R.integer.config_navBarInteractionMode);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.setup_nav_settings;
    }

    @Override
    protected int getTitleResId() {
        return R.string.setup_nav_settings;
    }

    @Override
    protected int getIconResId() {
        return R.drawable.ic_gesture;
    }

    void migrateOverlaySensitivityToSettings(IOverlayManager overlayManager) {
        if (isGestureNavigationEnabled()) {
            return;
        }

        OverlayInfo info = null;
        try {
            info = overlayManager.getOverlayInfo(WindowManagerPolicyConstants.NAV_BAR_MODE_GESTURAL_OVERLAY, UserHandle.USER_CURRENT);
        } catch (RemoteException e) { /* Do nothing */ }
        if (info != null && !info.isEnabled()) {
            // Enable the default gesture nav overlay. Back sensitivity for left and right are
            // stored as separate settings values, and other gesture nav overlays are deprecated.
            setCurrentSystemNavigationMode(overlayManager, WindowManagerPolicyConstants.NAV_BAR_MODE_GESTURAL_OVERLAY);
            Settings.Secure.putFloat(getContentResolver(),
                    Settings.Secure.BACK_GESTURE_INSET_SCALE_LEFT, 1.0f);
            Settings.Secure.putFloat(getContentResolver(),
                    Settings.Secure.BACK_GESTURE_INSET_SCALE_RIGHT, 1.0f);
        }
    }

    static void setCurrentSystemNavigationMode(IOverlayManager overlayManager, String overlayPackage) {
        try {
            overlayManager.setEnabledExclusiveInCategory(overlayPackage, UserHandle.USER_CURRENT);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private AlertDialog createLoadingDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.setup_nav_applying));
        progressDialog.setCancelable(false);
        progressDialog.create();
        return progressDialog;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.gesture_navigation:
                this.mLoadingDialog.show();
                setCurrentSystemNavigationMode(mOverlayManager, WindowManagerPolicyConstants.NAV_BAR_MODE_GESTURAL_OVERLAY);
                break;
            case R.id.two_button_navigation:
                this.mLoadingDialog.show();
                setCurrentSystemNavigationMode(mOverlayManager, WindowManagerPolicyConstants.NAV_BAR_MODE_2BUTTON_OVERLAY);
                break;
            case R.id.three_button_navigation:
                this.mLoadingDialog.show();
                setCurrentSystemNavigationMode(mOverlayManager, WindowManagerPolicyConstants.NAV_BAR_MODE_3BUTTON_OVERLAY);
                break;
        }
    }
}
