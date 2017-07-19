/*
 *
 * Copyright (C)  2017 HiteshSahu.com- All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * Written by Hitesh Sahu <hiteshkrsahu@Gmail.com>, 2017.
 *
 */

package com.aproject.modules;

/**
 * Created by Hitesh.Sahu on 7/12/2017.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.aproject.domain.storage.Decrypter;
import com.aproject.domain.storage.Encryptor;
import com.aproject.utils.AppConstants;
import com.aproject.utils.PreferenceHelper;
import com.aproject.utils.RootUtil;
import com.aproject.view.Fragments.FingerprintAuthenticationDialogFragment;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.aproject.utils.AppConstants.DEFAULT_KEY_NAME;
import static com.aproject.utils.AppConstants.DIALOG_FRAGMENT_TAG;
import static com.aproject.utils.PreferenceHelper.getPrefernceHelperInstace;

public class BiometricModule extends ReactContextBaseJavaModule {
    private static final String TAG = BiometricModule.class.getSimpleName();
    private static final String BIOMETRIC_MODULE_NAME = "FingerPrintAndroid";
    private static final String SAMPLE_ALIAS = "MYALIAS";
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private SharedPreferences mSharedPreferences;
    private Context AppContext;
    private Activity activityContext;
    private KeyguardManager keyguardManager;
    private FingerprintManagerCompat fingerprintManager;
    private Cipher defaultCipher;
    private Cipher cipherNotInvalidated;
    private Callback errorCallback;
    private Callback successCallback;
    private Encryptor encryptor;
    private Decrypter decryptor;
    private JSONObject jsonObject;

    public BiometricModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.AppContext = reactContext;

        encryptor = new Encryptor();

        try {
            decryptor = new Decrypter();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
                IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return BIOMETRIC_MODULE_NAME;
    }

    @ReactMethod
    public void storeUserSettings(String key, boolean value) {

        if (key.equalsIgnoreCase(AppConstants.LOCK_FINGERPRINT)) {
            Toast.makeText(AppContext, "FingerPrint is Unlocked \n After restarting the application you will be able to use Fingerprints", Toast.LENGTH_SHORT).show();
        }
        getPrefernceHelperInstace().setBoolean(AppContext, key, value);
    }

    private boolean isSensorAvialable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(AppContext, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED &&
                    AppContext.getSystemService(FingerprintManager.class).isHardwareDetected();
        } else {
            return FingerprintManagerCompat.from(AppContext).isHardwareDetected();
        }
    }

    @ReactMethod
    public void isFingerPrintSupported(Callback isSUpportedHw) {
        isSUpportedHw.invoke(isSensorAvialable());
    }

    @ReactMethod
    public void retrieveUserSettings(String key, Callback successCallbackUserSettings) {

        if (key.equalsIgnoreCase(AppConstants.LOCK_FINGERPRINT)) {
            successCallbackUserSettings.invoke(PreferenceHelper.getPrefernceHelperInstace().getBoolean(AppContext,
                    key, true));
        } else {
            successCallbackUserSettings.invoke(PreferenceHelper.getPrefernceHelperInstace().getBoolean(AppContext,
                    key, false));
        }

    }


    public void storeCredentials() {
        //Encrypt JSON
        String encryptedJSON = encryptText(jsonObject.toString());
        Log.e(TAG, "encryptText : " + encryptedJSON);

        //Store data in Internal file system
        writeToFile(encryptedJSON);
    }

    @ReactMethod
    public void retrieveCredentials(Callback errorCallbackCred, Callback successCallbackCred) {

        //Read encrypted JSON from file system
        String encryptedJSONFromFile = readFromFile();

        //decrypt data from file system
        String decryptedJSON = decryptText(encryptedJSONFromFile);

        if (null != decryptedJSON) {
            try {
                JSONObject decryptedJSONObject = new JSONObject(decryptedJSON);
                successCallbackCred.invoke(decryptedJSONObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                errorCallbackCred.invoke("Failed to retrieve Credentials");
            }
        } else {
            errorCallbackCred.invoke("Failed to retrieve Credentials");
        }
    }

    @ReactMethod
    public void authenticateUser(String userName, String passWord, Callback errorCallback,
                                 Callback successCallback) {
        activityContext = getCurrentActivity();
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
        String errorMessage = null;

        //Check if device is not Rooted
        if (!RootUtil.isDeviceRooted()) {

            fingerprintManager = FingerprintManagerCompat.from(AppContext);
            keyguardManager = (KeyguardManager) AppContext.getSystemService(Context.KEYGUARD_SERVICE);
            //keyguardManager = AppContext.getSystemService(KeyguardManager.class);
            // fingerprintManager = AppContext.getSystemService(FingerprintManager.class);

            jsonObject = new JSONObject();

            try {
                jsonObject.put(AppConstants.USER_NAME, userName);
                jsonObject.put(AppConstants.PASS_WORD, passWord);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Check whether the device has a Fingerprint sensor.
            if (!fingerprintManager.isHardwareDetected()) {
                errorMessage = "Your Device does not have a Fingerprint Sensor";
                errorCallback.invoke(errorMessage);
            } else {
                // Checks whether fingerprint permission is set on manifest
                if (ActivityCompat.checkSelfPermission(AppContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    errorMessage = "Fingerprint authentication permission not enabled";
                    errorCallback.invoke(errorMessage);
                } else {
                    // Check whether at least one fingerprint is registered
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        errorMessage = "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint";

                        errorCallback.invoke(errorMessage);

                    } else {
                        // Checks whether lock screen security is enabled or not
                        if (!keyguardManager.isKeyguardSecure()) {

                            errorMessage = "Secure lock screen hasn't set up.\n"
                                    + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint";

                            errorCallback.invoke(errorMessage);
                        } else {

                            //Generate key
                            generateKey();

                            //initialize cipher and pass it to FingerPrint scanner
                            if (cipherInit()) {

                                //Create dialog
                                FingerprintAuthenticationDialogFragment fragment
                                        = new FingerprintAuthenticationDialogFragment(BiometricModule.this);
                                //Assign crypto Object
                                fragment.setCryptoObject(new FingerprintManagerCompat.CryptoObject(defaultCipher));
                                //Start Authentication
                                fragment.show(activityContext.getFragmentManager(), DIALOG_FRAGMENT_TAG);

                            }
                        }
                    }
                }
            }
        } else {
            errorMessage = "Fingerprint authentication feature does not work on rooted devices";
            errorCallback.invoke(errorMessage);

            //clear stored credentials
            flushFileSystem();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            mKeyStore.load(null);
            mKeyGenerator.init(new
                    KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean cipherInit() {
        //Get Cipher
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" +
                    KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        //Get key and pass it to cipher
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(DEFAULT_KEY_NAME,
                    null);
            defaultCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void authenticationSuccess(boolean withFingerprint,
                                      @Nullable FingerprintManagerCompat.CryptoObject cryptoObject) {

        storeCredentials();

        if (null != successCallback) {
            successCallback.invoke();
        }
    }

//    // Show confirmation, if fingerprint was used show crypto information.
//    public void showConfirmation(byte[] encrypted) {
//        if (encrypted != null) {
//            Toast.makeText(AppContext,
//                    "Confirmation " + Base64.encodeToString(encrypted, 0 /* flags */),
//                    Toast.LENGTH_LONG).show();
//        }
//    }

//    /**
//     * Tries to encrypt some data with the generated key in  which is
//     * only works if the user has just authenticated via fingerprint.
//     */
//    private void tryEncrypt(Cipher cipher) {
//        try {
//            byte[] encrypted = cipher.doFinal(SECRET_MESSAGE.getBytes());
//            showConfirmation(encrypted);
//        } catch (BadPaddingException | IllegalBlockSizeException e) {
//            Toast.makeText(AppContext, "Failed to encrypt the data with the generated key. "
//                    + "Retry the purchase", Toast.LENGTH_LONG).show();
//            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
//        }
//    }

    public void authenticationFailed(String errorMessage) {
        if (null != errorCallback)
            errorCallback.invoke(errorMessage);
    }

    private String decryptText(String encryptedText) {

        byte[] inputEncryptedJSONFromFile = Base64.decode(encryptedText, Base64.DEFAULT);
        String decryptedJSON = null;
        try {
            decryptedJSON = decryptor
                    .decrypt(SAMPLE_ALIAS, inputEncryptedJSONFromFile, AppContext/*, encryptor.getIv()*/);
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchPaddingException | NoSuchProviderException |
                IOException | InvalidKeyException e) {
            Log.e(TAG, "Error DecryptData : " + e.getMessage(), e);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, "Error DecryptData : " + e.getMessage(), e);
            e.printStackTrace();
        }
        return decryptedJSON;
    }

    private String encryptText(String inputJSON) {
        String encryptedJSON = null;
        try {
            final byte[] encryptedText = encryptor
                    .encrypt(SAMPLE_ALIAS, inputJSON, AppContext);
            encryptedJSON = Base64.encodeToString(encryptedText, Base64.DEFAULT);
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
            Log.e(TAG, "Error EncryptData : " + e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            Log.e(TAG, "Error EncryptData : " + e.getMessage(), e);
        }
        return encryptedJSON;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(AppContext.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String encryptedJSONFromFile = "{}";

        try {
            InputStream inputStream = AppContext.openFileInput("config.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                encryptedJSONFromFile = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
            e.printStackTrace();
        }

        Log.e(TAG, "readFileSystem : " + encryptedJSONFromFile);

        return encryptedJSONFromFile;
    }

    private void flushFileSystem() {
        AppContext.deleteFile(new File(AppContext.getFilesDir(), "config.txt").getName());
    }
}
