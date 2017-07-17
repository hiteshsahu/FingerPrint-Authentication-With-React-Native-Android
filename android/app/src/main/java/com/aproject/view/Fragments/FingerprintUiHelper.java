/*
 *
 * Copyright (C)  2017 HiteshSahu.com- All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * Written by Hitesh Sahu <hiteshkrsahu@Gmail.com>, 2017.
 *
 */

package com.aproject.view.Fragments;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aproject.R;
import com.aproject.utils.PreferenceHelper;

import static android.hardware.fingerprint.FingerprintManager.FINGERPRINT_ERROR_LOCKOUT;

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 */
@TargetApi(Build.VERSION_CODES.M)
public class FingerprintUiHelper extends FingerprintManager.AuthenticationCallback {

    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 1300;
    private final FingerprintManager mFingerprintManager;
    private final ImageView mIcon;
    private final TextView mErrorTextView;
    private final Callback mCallback;
    private CancellationSignal mCancellationSignal;
//    private int failureCount;

    private boolean mSelfCancelled;
    private Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            mErrorTextView.setTextColor(
                    mErrorTextView.getResources().getColor(R.color.hint_color, null));
            mErrorTextView.setText(
                    mErrorTextView.getResources().getString(R.string.fingerprint_hint));
            mIcon.setImageResource(R.drawable.ic_fp_40px);
        }
    };

    FingerprintUiHelper(FingerprintManager fingerprintManager,
                        ImageView icon, TextView errorTextView, Callback callback) {
        mFingerprintManager = fingerprintManager;
        mIcon = icon;
        mErrorTextView = errorTextView;
        mCallback = callback;
//        failureCount = 0;
    }

    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable()) {
            return;
        }
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        mFingerprintManager
                .authenticate(cryptoObject, mCancellationSignal, 0 /* flags */, this, null);
        mIcon.setImageResource(R.drawable.ic_fp_40px);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mIcon.setImageResource(R.drawable.ic_fingerprint_success);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.success_color, null));
        mErrorTextView.setText(
                mErrorTextView.getResources().getString(R.string.fingerprint_success));
        mIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCallback.onAuthenticationSuccess();
            }
        }, SUCCESS_DELAY_MILLIS);
    }

    @Override
    public void onAuthenticationError(int errMsgId, final CharSequence errString) {
        if (!mSelfCancelled) {
            showError(errString);

            if (errMsgId == FINGERPRINT_ERROR_LOCKOUT) {
//                mIcon.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mCallback.onAuthenticationError("");
//                    }
//                }, ERROR_TIMEOUT_MILLIS);

                PreferenceHelper.getPrefernceHelperInstace().setBoolean(mIcon.getContext(), "LOCK_FINGERPRINT", false);

                Toast.makeText(mIcon.getContext(), "YOU HAVE EXCEED MAX NUMBER OF ATTEMPTS \n Please Use Your Credentials to LogIn", Toast.LENGTH_SHORT).show();

                mCallback.maxAttemptExceed();


            }
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, final CharSequence helpString) {
        showError(helpString);
//        mIcon.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mCallback.onAuthenticationError(helpString.toString());
//            }
//        }, ERROR_TIMEOUT_MILLIS);
    }

    @Override
    public void onAuthenticationFailed() {
        showError(mIcon.getResources().getString(
                R.string.fingerprint_not_recognized));

//        mIcon.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mCallback.onAuthenticationError(mIcon.getResources().getString(
//                        R.string.fingerprint_not_recognized));
//            }
//        }, ERROR_TIMEOUT_MILLIS);

        //Toast.makeText(getActivity(), "Error : " + getActivity().getString(R.string.fingerprint_not_supported), Toast.LENGTH_LONG).show();

//        failureCount++;
//        if (failureCount == 3) {
//            mIcon.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mCallback.onAuthenticationError("Max Attempts Crossed");
//                }
//            }, ERROR_TIMEOUT_MILLIS);
//        }
    }

    private void showError(CharSequence error) {
        mIcon.setImageResource(R.drawable.ic_fingerprint_error);
        mErrorTextView.setText(error);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.warning_color, null));
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mErrorTextView.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);

    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    public boolean isFingerprintAuthAvailable() {
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        return mFingerprintManager.isHardwareDetected()
                && mFingerprintManager.hasEnrolledFingerprints();
    }

    public interface Callback {

        void onAuthenticationSuccess();

        void onAuthenticationError(String errorMessage);

        void maxAttemptExceed();

    }
}
