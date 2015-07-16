package ec_f2m;

import java.util.HashSet;
import java.util.Set;

// y2 + xy = x3 + ax2 + b
public class Curve {
    private final int a;
    private final int b;
    private final GF2m field;

    public Curve(int a, int b, int m) {
        if (b == 0) {
//            String msg = String.format("<html>is not a group: 4a<sup>3</sup> + 27 b<sup>2</sup> = %d</html>", n);
            throw new IllegalArgumentException(("b = 0"));
        }
        field = new GF2m(m);
        this.a = a;
        this.b = b;
    }

    public boolean isGroup() {
        return b != 0;
    }

    public boolean belongs(Point p) {
        return p == Point.O || belongs(p.getX(), p.getY());
    }

    public boolean belongs(long x, long y) {
        long left = add(mul(y, y), mul(x, y));
        long right = add(mul(x, x, x), mul(a, x, x), b);
        return left == right;
    }

    public Point add(Point p, Point q) {
        if (p.equals(Point.O))
            return q;
        if (q.equals(Point.O))
            return p;
        if (p.equals(q))
            return twice(p);

        long xp = p.getX();
        long yp = p.getY();
        long xq = q.getX();
        long yq = q.getY();
        if (xp == xq && add(xp, yp) == yq)    // P - P = 0
            return Point.O;

//        s = (yp - yq) / (xp + xq);
        long s = div(sub(yp, yq), add(xp, xq));
//        xr = s * s + s + xp + xq + a;
        long xr = add(mul(s, s), s, xp, xq, a);
//        yr = s * (xp + xr) + xr + yp;
        long yr = add(mul(s, add(xp, xr)), xr, yp);
        return new Point(xr, yr);
    }

    public Point twice(Point p) {
        long xp = p.getX();
        long yp = p.getY();
        if (xp == 0)
            return Point.O;
//        s = xp + yp / xp;
        long s = add(xp, div(yp, xp));
//        xr = s * s + s + a;
        long xr = add(mul(s, s), s, a);
//        yr = xp * xp + (s + 1) * xr;
//        long yr = add(mul(xp, xp), mul(add(s, 1), xr));
        long yr = add(mul(xp, xp), mul(s, xr), xr);
        return new Point(xr, yr);
    }

    public Point neg(Point z) {
        return new Point(z.getX(), add(z.getX(), z.getY()));
    }

    private long add(long a, long... b) {
        long total = a;
        for (long n : b)
            total = field.add(total, n);
        return total;
    }

    private long sub(long a, long b) {
        return field.sub(a, b);
    }

    private long mul(long a, long... b) {
        long total = a;
        for (long n : b)
            total = field.mul(total, n);
        return total;
    }

    private long div(long a, long b) {
        return field.mul(a, field.inv(b));
    }

    public int getM() {
        return field.getM();
    }

    public String toString() {
        return String.format("y2 + xy = x3 + %d x2 + %d (F2%d)", a, b, field.getM());
    }

    public Point mul(int k, Point p) {
        Point kp = Point.O;
        for (int i = 0; i < k; i++)
            kp = add(kp, p);
        return kp;
    }

    public Set<Point> points() {
        Set<Point> points = new HashSet<Point>();
        long max = 1 << getM();
        for (long x = 0; x < max; x++)
            for (long y = 0; y < max; y++)
                if (belongs(x, y))
                    points.add(new Point(x, y));
        return points;
    }

    public GF2m getField() {
        return field;
    }

    public int order(Point g) {
        Point R = g;
        for (int n = 2; ; n++) {
            R = add(R, g);
            if (R.equals(Point.O))
                return n;
        }
    }
}
