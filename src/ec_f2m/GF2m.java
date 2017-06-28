package ec_f2m;

/**
 * @author Jose A. Manas
 * @version 01-mar-2009
 */
public class GF2m {
    private static final int MAX_M = 31;

    private static final long[] mask;
    private static final long[] primePolynomials;

    private final int m;
    private final long mx;

    static {
        mask = new long[2 * MAX_M];
        mask[0] = 1;
        for (int i = 1; i < mask.length; i++)
            mask[i] = mask[i - 1] * 2;

        // Menezes page 161
        primePolynomials = new long[32];
        primePolynomials[2] = primitive(2, 1);
        primePolynomials[3] = primitive(3, 1);
        primePolynomials[4] = primitive(4, 1);
        primePolynomials[5] = primitive(5, 2);
        primePolynomials[6] = primitive(6, 1);
        primePolynomials[7] = primitive(7, 1);
        primePolynomials[8] = primitive(8, 6, 5, 1);
        primePolynomials[9] = primitive(9, 4);
        primePolynomials[10] = primitive(10, 3);
        primePolynomials[11] = primitive(11, 2);
        primePolynomials[12] = primitive(12, 7, 4, 3);
        primePolynomials[13] = primitive(13, 4, 3, 1);
        primePolynomials[14] = primitive(14, 12, 11, 1);
        primePolynomials[15] = primitive(15, 1);
        primePolynomials[16] = primitive(16, 5, 3, 2);
        primePolynomials[17] = primitive(17, 3);
        primePolynomials[18] = primitive(18, 7);
        primePolynomials[19] = primitive(19, 6, 5, 1);
        primePolynomials[20] = primitive(20, 3);
        primePolynomials[21] = primitive(21, 2);
        primePolynomials[22] = primitive(22, 1);
        primePolynomials[23] = primitive(23, 5);
        primePolynomials[24] = primitive(24, 4, 3, 1);
        primePolynomials[25] = primitive(25, 3);
        primePolynomials[26] = primitive(26, 8, 7, 1);
        primePolynomials[27] = primitive(27, 8, 7, 1);
        primePolynomials[28] = primitive(28, 3);
        primePolynomials[29] = primitive(29, 2);
        primePolynomials[30] = primitive(30, 16, 15, 1);
        primePolynomials[31] = primitive(31, 3);
    }

    public static long mkPolynomial(String coeficients) {
        long poly = 0;
        for (int i = 0; i < coeficients.length(); i++)
            if (coeficients.charAt(i) == '1')
                poly ^= mask[coeficients.length() - 1 - i];
        return poly;
    }

    private static boolean bit(long n, int i) {
        return (n & mask[i]) != 0;
    }

    private static long primitive(int m, int k) {
        return mask[m] ^ mask[k] ^ mask[0];
    }

    private static long primitive(int m, int k1, int k2, int k3) {
        return mask[m] ^ mask[k1] ^ mask[k2] ^ mask[k3] ^ mask[0];
    }

    public GF2m(int m) {
        if (m < 2 || m > MAX_M)
            throw new IllegalArgumentException(String.format("m in [2, %d]", MAX_M));
        this.m = m;
        mx = primePolynomials[m];
    }

    public GF2m(int m, int k) {
        if (m < 2 || m > MAX_M)
            throw new IllegalArgumentException(String.format("m in [2, %d]", MAX_M));
        this.m = m;
        mx = primitive(m, k);
    }

    public GF2m(int m, int k1, int k2, int k3) {
        if (m < 2 || m > MAX_M)
            throw new IllegalArgumentException(String.format("m in [2, %d]", MAX_M));
        this.m = m;
        mx = primitive(m, k1, k2, k3);
    }

    public GF2m(int m, long ipol) {
        if (m < 2 || m > MAX_M)
            throw new IllegalArgumentException(String.format("m in [2, %d]", MAX_M));
        this.m = m;
        this.mx = ipol;
    }

    public int getM() {
        return m;
    }

    public long getPrimePolinomial() {
        return mx;
    }

    public long add(long a, long b) {
        return a ^ b;
    }

    public long sub(long a, long b) {
        return a ^ b;
    }

    public long mul(long a, long b) {
        return mod(raw_mul(a, b), mx);
    }

    private long raw_mul(long a, long b) {
        long result = 0;
        while (b > 0) {
            if (b % 2 == 1)
                result ^= a;
            a *= 2;
            b /= 2;
        }
        return result;
    }

    public long div(long a, long b) {
        return mul(a, inv(b));
    }

    public long mod(long a, long b) {
        long quotient = 0;
        int gn = grado(a);
        int dn = grado(b);
        while (a > 0 && gn >= dn) {
            quotient ^= mask[gn - dn];
            a ^= raw_mul(quotient, b);
            gn = grado(a);
        }
        return a;
    }

    public long gcd(long a, long b) {
        if (a < b) {
            long t = a;
            a = b;
            b = t;
        }
        while (b != 0) {
            long r = mod(a, b);
            a = b;
            b = r;
        }
        return a;
    }

    public long inv(long number) {
        if (number == 0)
            throw new IllegalArgumentException("inv(0)");
        long a3 = mx;
        long b3 = number;
        long a1 = 0;
        long a2 = 1;
        while (b3 > 1) {
            long a = a3;
            long q = 0;
            {
                int gn = grado(a);
                int dn = grado(b3);
                while (gn >= dn) {
                    q ^= mask[gn - dn];
                    a ^= raw_mul(q, b3);
                    gn = grado(a);
                }
            }
            long t3 = a;
            long ta = sub(a1, mul(q, a2));
            a3 = b3;
            b3 = t3;
            a1 = a2;
            a2 = ta;
        }
        return a2;
    }

    public int grado(long n) {
        for (int i = mask.length - 1; i > 0; i--)
            if (bit(n, i))
                return i;
        return 0;
    }

    public String printBinary(long number) {
        int gn = grado(number);
        char[] binary = new char[gn + 1];
        for (int i = 0; i < binary.length; i++)
            if (bit(number, i))
                binary[binary.length - 1 - i] = '1';
            else
                binary[binary.length - 1 - i] = '0';
        return new String(binary);
    }

    public String printZx(long number) {
        StringBuilder buffer = new StringBuilder();
        for (int i = grado(number); i > 0; i--) {
            if (bit(number, i)) {
                if (buffer.length() > 0)
                    buffer.append(" + ");
                buffer.append("x");
                if (i > 1)
                    buffer.append(i);
            }
        }
        if (bit(number, 0)) {
            if (buffer.length() > 0)
                buffer.append(" + ");
            buffer.append("1");
        }
        return buffer.toString();
    }
}
