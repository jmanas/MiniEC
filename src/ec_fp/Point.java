package ec_fp;

public class Point {
    public static final Point O = new Zero();
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    public int hashCode() {
        int result;
        result = x;
        result = 31 * result + y;
        return result;
    }
}
