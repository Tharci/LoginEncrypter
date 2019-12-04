package com.tharci.loginencrypter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class DataService {
    private static Handler popUpWindowHandler = new Handler();
    static Runnable popUpWindowRunnable = new Runnable() {
        @Override
        public void run() {}
    };

    static void runPopupRunnable() {
        popUpWindowHandler.post(popUpWindowRunnable);
        popUpWindowRunnable = null;
    }

    static String DATA_FILENAME = "data.dat";

    static String passwordHash;

    static boolean fileExists(Context context, String filename) {
        try
        {
            InputStream inputStream = context.openFileInput(filename);
            inputStream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static void deleteFile(Context context, String filename) {
        File dir = context.getFilesDir();
        File file = new File(dir, filename);
        file.delete();
    }

    static String hashPassword(String string) {
        String randomStringForHash = "rPhHegQyJthn3dcThN1gHIUzylWzZJgAjGGueO8ZtzcwLlwiMsEIwIlyAebmhTFDqK6LvZCc5aCelcbXWjtmuQ9SOHCZCLDhCOs1ULW2o53NAbDU3QALCjsnSawD3FqB";
        return hashSimple(hashSimple(string) + randomStringForHash);
    }

    static void saveData(Context context, String data) throws java.io.IOException {
        HashMap<String, byte[]> map = encryptData(data.getBytes(), passwordHash);
        saveMap(context, map, DATA_FILENAME);
    }

    static String loadData(Context context) throws java.io.IOException, java.lang.ClassNotFoundException {
        return decryptData(loadMap(context, DATA_FILENAME), passwordHash);
    }

    static boolean authenticate(Context context) {
        try {
            FileInputStream fis = context.openFileInput (DATA_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap<String, byte[]> map = (HashMap<String, byte[]>) ois.readObject();
            decryptData(map, passwordHash);
            ois.close();
            fis.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static String validatePassword(String pw) {
        if (pw.length() > 6) {
            return "";
        } else {
            return "Password length must be at least 6.";
        }
    }

    private static String hashSimple(String string)
    {
        String hash = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes());

            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b: digest)
            {
                sb.append(String.format("%02x", b & 0xff));
            }

            hash = sb.toString();
        } catch (Exception e) {}

        return hash;
    }

    static HashMap<String, byte[]> encryptData(byte[] plainTextBytes, String passwordString)
    {
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();

        try
        {
            //Random salt for next step
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[256];
            random.nextBytes(salt);

            //PBKDF2 - derive the key from the password, don't use passwords directly
            char[] passwordChar = passwordString.toCharArray(); //Turn password into char[] array
            PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256); //1324 iterations
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            //Create initialization vector for AES
            SecureRandom ivRandom = new SecureRandom(); //not caching previous seeded instance of SecureRandom
            byte[] iv = new byte[16];
            ivRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            //Encrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainTextBytes);

            map.put("salt", salt);
            map.put("iv", iv);
            map.put("encrypted", encrypted);
        }
        catch(Exception e)
        {
            Log.e("MYAPP", "encryption exception", e);
        }

        return map;
    }

    static String decryptData(HashMap<String, byte[]> map, String passwordString)
    {
        byte[] decrypted = null;
        try
        {
            byte[] salt = map.get("salt");
            byte[] iv = map.get("iv");
            byte[] encrypted = map.get("encrypted");

            //regenerate key from password
            char[] passwordChar = passwordString.toCharArray();
            PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            //Decrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            decrypted = cipher.doFinal(encrypted);
        }
        catch(Exception e)
        {
            Log.e("MYAPP", "decryption exception", e);
        }

        return new String(decrypted);
    }

    static void saveMap(Context context, HashMap<String, byte[]> map, String filename) throws java.io.IOException {
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(map);
        oos.close();
        fos.close();
    }

    static HashMap<String, byte[]> loadMap(Context context, String filename) throws java.io.IOException, java.lang.ClassNotFoundException {
        FileInputStream fis = context.openFileInput(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        HashMap<String, byte[]> map = (HashMap<String, byte[]>) ois.readObject();
        ois.close();
        fis.close();

        return map;
    }
}