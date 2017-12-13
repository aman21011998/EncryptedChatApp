package com.example.aman.chatapp;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Created by aman on 27/10/17.
 */

public class AESEncryption
{

    /*public static void main(String[] args) throws Exception {

       // String plainText = "Hello World";





       // String decryptedText = decryptText(cipherText, secKey);




       // System.out.println("AES Key (Hex Form):"+bytesToHex(secKey.getEncoded()));

       // System.out.println("Encrypted Text (Hex Form):"+bytesToHex(cipherText));

       // System.out.println("Descrypted Text:"+decryptedText);


    }*/




    public static SecretKey getSecretEncryptionKey() throws Exception{

        KeyGenerator generator = KeyGenerator.getInstance("AES");


        generator.init(128); // The AES key size in number of bits


        SecretKey secKey = generator.generateKey();

        return secKey;

    }


    public static String encryptText(String plainText, SecretKey secKey) throws Exception{

// AES defaults to AES/ECB/PKCS5Padding in Java 7

        Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS7Padding");

        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);


        // byte[] byteCipherText=Base64.encode(aesCipher.doFinal(plainText.getBytes()), Base64.DEFAULT);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes("UTF-8"));

        //String text=byteCipherText.toString();
        String text=Base64.encodeToString(byteCipherText, Base64.NO_WRAP);

        return text;

    }



  /*  public static byte[] encryptText(String plainText,SecretKey secKey) throws Exception{

// AES defaults to AES/ECB/PKCS5Padding in Java 7

        Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);


       // byte[] byteCipherText=Base64.encode(aesCipher.doFinal(plainText.getBytes()), Base64.DEFAULT);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());



        return byteCipherText;

    }*/





    public static String decryptText(String byteCipherText, SecretKey secKey) throws Exception {

// AES defaults to AES/ECB/PKCS5Padding in Java 7

        Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS7Padding");


        aesCipher.init(Cipher.DECRYPT_MODE, secKey);
        byte[]  bytePlainText=aesCipher.doFinal(Base64.decode(byteCipherText, Base64.NO_WRAP));
        //byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
        String plainText = new String(bytePlainText,"UTF8");
        return new String(plainText);

    }




   /* private static String  bytesToHex(byte[] hash) {


        return DatatypeConverter.printHexBinary(hash);

    }*/
}
