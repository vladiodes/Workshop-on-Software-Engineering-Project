package main.Security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Security implements ISecurity {

    // using the SHA 512 bytes algorithm for the crypto-hash function
    private String get_SHA_512_SecurePassword(String passwordToHash
                                              ) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    @Override
    public String hashPassword(String password) {
        return get_SHA_512_SecurePassword(password);
    }
}
