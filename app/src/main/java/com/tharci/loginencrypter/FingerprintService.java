package com.tharci.loginencrypter;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import java.security.KeyStore;
import java.util.HashMap;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class FingerprintService {
    static String FINGERPRINT_FILENAME = "fingerprint.dat";

    static boolean isFingerprintAuthSetup(Context context) {
        return DataService.fileExists(context, FINGERPRINT_FILENAME);
    }

    static void deleteFingerprintAuth(Context context) {
        DataService.deleteFile(context, FINGERPRINT_FILENAME);
    }

    static void createFingerprintAuth(Context context) throws Exception {
        generateSecretKey(new KeyGenParameterSpec.Builder(
                "KEY",
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(false)
                .build());

        HashMap<String, byte[]> map = DataService.encryptData(DataService.passwordHash.getBytes(), ((Integer)getSecretKey().hashCode()).toString());
        DataService.saveMap(context, map, FINGERPRINT_FILENAME);
    }

    private static void generateSecretKey(KeyGenParameterSpec keyGenParameterSpec) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyGenerator.init(keyGenParameterSpec);
        keyGenerator.generateKey();
    }

    private static SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return ((SecretKey)keyStore.getKey("KEY", null));
    }

    static void loadPwHash_Fingerprint(Context context) throws Exception {
        HashMap<String, byte[]> map = DataService.loadMap(context, FINGERPRINT_FILENAME);
        DataService.passwordHash = DataService.decryptData(map, ((Integer)getSecretKey().hashCode()).toString());
    }
}
