package com.senorpez.guildwars2;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncryptedProperty {
    public static void main(String[] args) {
        StandardPBEStringEncryptor strongEncryptor = new StandardPBEStringEncryptor();
        strongEncryptor.setAlgorithm("PBEWithMD5AndDES");
        strongEncryptor.setPassword("thePassword");

        String out = strongEncryptor.encrypt("theOtherPassword");
        System.out.println(out);
    }
}
