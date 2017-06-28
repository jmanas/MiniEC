package ec_fp;

/**
 * @author Jose A. Manas
 * @version 01-mar-2009
 */
public class Zp {
    private int p;

    public Zp(int p) {
        this.p = p;
    }

    public int getP() {
        return p;
    }

    public int add(int a, int b) {
        int r = (a + b) % p;
        if (r < 0)
            r += p;
        return r;
    }

    public int sub(int a, int b) {
        int r = (a - b) % p;
        if (r < 0)
            r += p;
        return r;
    }

    public int mul(int a, int b) {
        int r = (a * b) % p;
        if (r < 0)
            r += p;
        return r;
    }

    public int div(int a, int b) {
        return mul(a, inv(b));
    }

    public int mod(int n) {
        int r = n % p;
        if (r < 0)
            r += p;
        return r;
    }

    public int inv(int number) {
        number = mod(number);
        if (number == 0)
            throw new IllegalArgumentException("inv(0)");
        int a1 = 1;
        int a2 = 0;
        int a3 = p;
        int b1 = 0;
        int b2 = 1;
        int b3 = number;
        while (b3 != 1) {
            int q = a3 / b3;
            int t1 = a1 - q * b1;
            int t2 = a2 - q * b2;
            int t3 = a3 - q * b3;
            a1 = b1;
            a2 = b2;
            a3 = b3;
            b1 = t1;
            b2 = t2;
            b3 = t3;
        }
        if (b2 < 0)
            b2 += p;
        return b2;
    }

    public static void main(String[] args) {
        test(2);
        test(3);
        test(5);
        test(7);
        test(11);
        test(13);
        test(17);
        test(19);
        test(23);
    }

    private static void test(int p) {
        Zp zp = new Zp(p);
        for (int n = 1; n < zp.getP(); n++) {
            int inv = zp.inv(n);
            if (zp.mul(n, inv) != 1)
                System.out.printf("fail: %d * inv(%d) != 1%n", n, n);
        }
    }
}
