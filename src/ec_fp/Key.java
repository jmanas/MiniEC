package ec_fp;

/**
 * @author Jose A. Manas
 * @version 04-mar-2009
 */
public class Key {
    private final Curve curve;
    private final Point g;
    private final int ks;
    private final Point kp;

    public Key(Curve curve, Point g, int ks) {
        this.curve = curve;
        this.g = g;
        this.ks = ks;
        kp = curve.mul(ks, g);
    }

    public Curve getCurve() {
        return curve;
    }

    public Point getG() {
        return g;
    }

    public int getKs() {
        return ks;
    }

    public Point getKp() {
        return kp;
    }

    public String toString() {
        return String.format("kp: %s; ks: %d", kp, ks);
    }
}
