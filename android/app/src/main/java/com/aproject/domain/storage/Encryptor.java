/*
 *
 * Copyright (C)  2017 HiteshSahu.com- All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * Written by Hitesh Sahu <hiteshkrsahu@Gmail.com>, 2017.
 *
 */

package com.aproject.domain.storage;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.aproject.utils.PreferenceHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * ______        _____                  _
 * |  ____|      / ____|                | |
 * | |__   _ __ | |     _ __ _   _ _ __ | |_ ___  _ __
 * |  __| | '_ \| |    | '__| | | | '_ \| __/ _ \| '__|
 * | |____| | | | |____| |  | |_| | |_) | || (_) | |
 * |______|_| |_|\_____|_|   \__, | .__/ \__\___/|_|
 * __/ | |
 * |___/|_|
 */
public final class Encryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    //private byte[] encryption;
//    private byte[] iv;

    public Encryptor() {
    }

    public byte[] encrypt(final String alias, final String textToEncrypt, Context appContext)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            InvalidAlgorithmParameterException, SignatureException, BadPaddingException,
            IllegalBlockSizeException {

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));

        String IV = Base64.encodeToString(cipher.getIV(), Base64.DEFAULT);
        Log.e("MILK", "IV : " + IV + " IV size " + IV.length());

        PreferenceHelper.getPrefernceHelperInstace().setString(appContext, "IV", IV);
//
//        iv = cipher.getIV();

        return (/*encryption = */cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
    }

    @NonNull
    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {

        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build());

        return keyGenerator.generateKey();
    }

//    public byte[] getEncryption() {
//        return encryption;
//    }

//    public byte[] getIv() {
//        return iv;
//    }
} 