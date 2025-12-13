package rsa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RSADecryptor {

    public String decrypt(String encryptedMessage, RSAPrivateKey privateKey) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
            BigInteger c = new BigInteger(encryptedBytes);

            BigInteger m = RSAMath.modPow(c, privateKey.getD(), privateKey.getN());

            return new String(m.toByteArray(), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return "Error: Invalid Base64 input or decryption failure.";
        }
    }
}
