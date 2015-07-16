package ec_fp;

import java.util.HashSet;
import java.util.Set;

// y2 = x3 + ax + b mod p
public class Curve {
    private final int a;
    private final int b;
    private final Zp field;

    public Curve(int a, int b, int p) {
        field = new Zp(p);
        this.a = a;
        this.b = b;
    }

    public boolean isGroup() {
        return add(mul(4, a, a, a), mul(27, b, b)) != 0;
    }

    public boolean belongs(Point p) {
        return p == Point.O || belongs(p.getX(), p.getY());
    }

    public boolean belongs(int x, int y) {
        int left = mul(y, y);
        int right = add(mul(x, x, x), mul(a, x), b);
        return left == right;
    }

    public Point add(Point p, Point q) {
        if (p.equals(Point.O))
            return q;
        if (q.equals(Point.O))
            return p;
        if (p.equals(q))
            return twice(p);

        int xp = p.getX();
        int yp = p.getY();
        int xq = q.getX();
        int yq = q.getY();
        if (xp == xq)  // P - P = 0
            return Point.O;

//        s = (yp - yq) / (xp - xq);
        int s = div(sub(yp, yq), sub(xp, xq));
//        xr = s * s - xp - xq;
        int xr = sub(mul(s, s), add(xp, xq));
//        yr = -yp + s * (xp - xr);
        int yr = sub(mul(s, sub(xp, xr)), yp);
        return new Point(xr, yr);
    }

    public Point twice(Point p) {
        int xp = p.getX();
        int yp = p.getY();
        if (yp == 0)
            return Point.O;
//        s = (3 * xp * xp + a) / (2 * yp);
        int s = div(add(mul(3, xp, xp), a), mul(2, yp));
//        xr = s * s - 2 * xp;
        int xr = sub(field.mul(s, s), field.mul(2, xp));
//        yr = -yp + s * (xp - xr);
        int yr = sub(field.mul(s, sub(xp, xr)), yp);
        return new Point(xr, yr);
    }

    public Point neg(Point z) {
        return new Point(z.getX(), -z.getY());
    }

    private int add(int a, int... b) {
        int total = a;
        for (int n : b)
            total = field.add(total, n);
        return total;
    }

    private int sub(int a, int b) {
        return field.sub(a, b);
    }

    private int mul(int a, int... b) {
        int total = a;
        for (int n : b)
            total = field.mul(total, n);
        return total;
    }


    private int div(int a, int b) {
        return field.mul(a, field.inv(b));
    }

    public int getP() {
        return field.getP();
    }

    public String toString() {
        return String.format("y2 = x3 + %d x + %d (mod %d)", a, b, field.getP());
    }

    public Point mul(int k, Point p) {
        Point kp = Point.O;
        for (int i = 0; i < k; i++)
            kp = add(kp, p);
        return kp;
    }

    public Set<Point> points() {
        Set<Point> points = new HashSet<Point>();
        int p = field.getP();
        for (int x = 0; x < p; x++)
            for (int y = 0; y <= p / 2; y++)
                if (belongs(x, y)) {
                    points.add(new Point(x, y));
                    if (y > 0)
                        points.add(new Point(x, p - y));
                }
        return points;
    }

    public Zp getField() {
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
