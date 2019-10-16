package com.tharci.loginencrypter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SharedStuff {

    private static SharedStuff instance = new SharedStuff();
    public static SharedStuff getInstance()
    {
        return instance;
    }

    String randomShittyStringForHash = "valamihosszuszovegetkeneirjakmertazugybiztonsagosszovalirokvalamithogysohasenkinekabudoskurvaeletbennesikeruljonfeltornie";

    Integer pin;
    String pinHash;

    String[] abc = {"×", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
            "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű",
            "Á", "É", "Í", "Ó", "Ö", "Ő", "Ú", "Ü", "Ű",
            " ", "!", "\"", "'", "#", "$", "%", "&", "(", ")", "*", "+", ",", "-", ".", "/", "\\",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            ":", ";", "<", ">", "=", "?", "@", "[", "]", "{", "}", "^", "_", "`"};

    Integer abcLen = abc.length;

    Integer deletable;
    Boolean saved = false;


    String hashSimple(String string) throws NoSuchAlgorithmException
    {
        String hash;

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(string.getBytes());

        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b: digest)
        {
            sb.append(String.format("%02x", b & 0xff));
        }

        hash = sb.toString();
        //errorMessage.setText(hash);
        return hash;
    }

    String hash (String string) throws NoSuchAlgorithmException
    {
        return hashSimple(hashSimple(string) + randomShittyStringForHash);
    }

}
