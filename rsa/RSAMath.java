package rsa;

import java.math.BigInteger;

public class RSAMath {

    public static BigInteger modPow(BigInteger base, BigInteger exponent, BigInteger modulus) {
        if (modulus.compareTo(BigInteger.ZERO) <= 0) {
            throw new ArithmeticException("modulus must be positive");
        }

        BigInteger result = BigInteger.ONE;
        base = base.remainder(modulus);

        BigInteger e = exponent;
        BigInteger b = base;

        while (e.compareTo(BigInteger.ZERO) > 0) {
            if (e.testBit(0)) {
                result = result.multiply(b).remainder(modulus);
            }

            b = b.multiply(b).remainder(modulus);

            e = e.shiftRight(1);
        }

        return result;
    }

    public static BigInteger modInverse(BigInteger a, BigInteger m) {
        BigInteger[] result = extendedGCD(a, m);
        BigInteger gcd = result[0];
        BigInteger x = result[1];

        if (!gcd.equals(BigInteger.ONE)) {
            throw new ArithmeticException("Modular inverse does not exist (gcd != 1)");
        }

        return x.mod(m).add(m).mod(m);
    }

    public static BigInteger gcd(BigInteger a, BigInteger b) {
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger temp = b;
            b = a.remainder(b);
            a = temp;
        }
        return a.abs();
    }

    private static BigInteger[] extendedGCD(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return new BigInteger[] { a, BigInteger.ONE, BigInteger.ZERO };
        }

        BigInteger[] vals = extendedGCD(b, a.remainder(b));
        BigInteger d = vals[0];
        BigInteger x1 = vals[1];
        BigInteger y1 = vals[2];

        BigInteger x = y1;
        BigInteger y = x1.subtract(a.divide(b).multiply(y1));

        return new BigInteger[] { d, x, y };
    }
}
