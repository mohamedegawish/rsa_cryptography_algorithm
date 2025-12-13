package rsa;

import java.math.BigInteger;

public class TestManualRSA {
    public static void main(String[] args) {
        System.out.println("--- Starting Manual RSA Verification ---");

        BigInteger base = BigInteger.valueOf(2);
        BigInteger exp = BigInteger.valueOf(10);
        BigInteger mod = BigInteger.valueOf(1000);
        BigInteger res = RSAMath.modPow(base, exp, mod);
        check(res.intValue() == 24, "modPow(2, 10, 1000) should be 24. Got: " + res);

        BigInteger a = BigInteger.valueOf(12);
        BigInteger b = BigInteger.valueOf(18);
        BigInteger gcd = RSAMath.gcd(a, b);
        check(gcd.intValue() == 6, "gcd(12, 18) should be 6. Got: " + gcd);

        BigInteger val = BigInteger.valueOf(3);
        BigInteger m = BigInteger.valueOf(11);
        BigInteger inv = RSAMath.modInverse(val, m);
        check(inv.intValue() == 4, "modInverse(3, 11) should be 4. Got: " + inv);

        try {
            System.out.println("Generating keys...");
            RSAKeyPairGenerator gen = new RSAKeyPairGenerator();
            gen.generateKeyPair(512);
            RSAPublicKey pub = gen.getPublicKey();
            RSAPrivateKey priv = gen.getPrivateKey();

            System.out.println("Public Key: " + pub);
            System.out.println("Private Key: " + priv);

            String original = "Hello RSA From Scratch";
            System.out.println("Original: " + original);

            RSAEncryptor encryptor = new RSAEncryptor();
            String encrypted = encryptor.encrypt(original, pub);
            System.out.println("Encrypted: " + encrypted);

            RSADecryptor decryptor = new RSADecryptor();
            String decrypted = decryptor.decrypt(encrypted, priv);
            System.out.println("Decrypted: " + decrypted);

            check(original.equals(decrypted), "Decrypted text matches original");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Integration test failed: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("--- Verification Passed ---");
    }

    private static void check(boolean condition, String msg) {
        if (condition) {
            System.out.println("[PASS] " + msg);
        } else {
            System.err.println("[FAIL] " + msg);
            System.exit(1);
        }
    }
}
