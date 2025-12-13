package rsa;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAKeyPairGenerator {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public void generateKeyPair(int bitLength) {
        SecureRandom random = new SecureRandom();

        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);

        BigInteger n = p.multiply(q);

        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        BigInteger e = BigInteger.valueOf(65537);

        while (RSAMath.gcd(phi, e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e = e.add(BigInteger.valueOf(2));
        }

        BigInteger d = RSAMath.modInverse(e, phi);

        this.publicKey = new RSAPublicKey(e, n);
        this.privateKey = new RSAPrivateKey(d, n);
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }
}
