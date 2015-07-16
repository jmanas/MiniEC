package ec_fp;

/**
 * @author Jose A. Manas
 * @version 09-feb-2009
 */
public class Zero
        extends Point {

    public Zero() {
        super(-1, -1);
    }

    public String toString() {
        return "O";
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object x) {
        return this == x;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
