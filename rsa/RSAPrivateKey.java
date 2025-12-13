package rsa;

import java.math.BigInteger;

public class RSAPrivateKey {
    private final BigInteger d;
    private final BigInteger n;

    public RSAPrivateKey(BigInteger d, BigInteger n) {
        this.d = d;
        this.n = n;
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger getN() {
        return n;
    }

    @Override
    public String toString() {
        return "Private Key (d, n):\nExponent: " + d + "\nModulus: " + n;
    }
}
