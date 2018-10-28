package ec_f2m;

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

    public boolean equals(Object x) {
        return this == x;
    }

    public int hashCode() {
        return 0;
    }
}
