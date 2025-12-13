package rsa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RSAEncryptor {

    public String encrypt(String message, RSAPublicKey publicKey) {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        BigInteger m = new BigInteger(messageBytes);

        if (m.compareTo(publicKey.getN()) >= 0) {
            throw new IllegalArgumentException("Message is too long for the key size!");
        }

        BigInteger c = RSAMath.modPow(m, publicKey.getE(), publicKey.getN());

        return Base64.getEncoder().encodeToString(c.toByteArray());
    }
}
