package ec_f2m;

public class Point {
    public static final Point O = new Zero();
    private long x;
    private long y;

    public Point(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;
        if (x != point.x) return false;
        if (y != point.y) return false;
        return true;
    }

    public int hashCode() {
        int result;
        result = (int) (x ^ (x >>> 32));
        result = 31 * result + (int) (y ^ (y >>> 32));
        return result;
    }
}
