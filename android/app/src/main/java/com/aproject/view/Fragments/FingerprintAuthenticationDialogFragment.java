/*
 *
 * Copyright (C)  2017 HiteshSahu.com- All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * Written by Hitesh Sahu <hiteshkrsahu@Gmail.com>, 2017.
 *
 */

package com.aproject.view.Fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aproject.MainActivity;
import com.aproject.R;
import com.aproject.modules.BiometricModule;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 */
public class FingerprintAuthenticationDialogFragment extends DialogFragment
        implements FingerprintUiHelper.Callback {

    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private MainActivity mActivity;
    private SharedPreferences mSharedPreferences;
    private BiometricModule biometricModule;

    public FingerprintAuthenticationDialogFragment(BiometricModule biometricModule) {
        this.biometricModule = biometricModule;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.sign_in));
        View rootView = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        rootView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        rootView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mFingerprintUiHelper = new FingerprintUiHelper(
                mActivity.getSystemService(FingerprintManager.class),
                (ImageView) rootView.findViewById(R.id.fingerprint_icon),
                (TextView) rootView.findViewById(R.id.fingerprint_status), this);

        // If fingerprint authentication is not available, switch immediately to the backup
        if (!mFingerprintUiHelper.isFingerprintAuthAvailable()) {
            Toast.makeText(getActivity(), "Error : " + getActivity().getString(R.string.fingerprint_not_supported), Toast.LENGTH_LONG).show();
            onAuthenticationError(getActivity().getString(R.string.fingerprint_not_supported));
            dismiss();

        }
        return rootView;
    }

    /**
     * Sets the crypto object to be passed in when authenticating with fingerprint.
     */
    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }

    @Override
    public void onAuthenticationSuccess() {
        //  authentication was successful.
        biometricModule.authenticationSuccess(true /* withFingerprint */, mCryptoObject);
        dismiss();
    }

    @Override
    public void onAuthenticationError(String errorMessage) {
        biometricModule.authenticationFailed(errorMessage);
    }

    @Override
    public void maxAttemptExceed() {
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFingerprintUiHelper.startListening(mCryptoObject);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
