package ec_fp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jose on 23-Sep-15.
 */
public class Example {
    private static final Map<Integer, Example> exs;

    static {
        exs = new HashMap<>();
        exs.put(29, new Example(29, 4, 9, "10, 11"));
//        exs.put(23, new Example(23, 1, 4, "18, 14"));
        exs.put(23, new Example(23, 5, 1, "4, 4"));
//        exs.put(19, new Example(19, 2, 3, null));
        exs.put(19, new Example(19, 2, 9, "8, 9"));
//        exs.put(17, new Example(17, 1, 2, null));
//        exs.put(17, new Example(17, 2, 2, "5, 1"));
        exs.put(17, new Example(17, 3, 5, "9, 9"));
        exs.put(13, new Example(13, 1, 1, null));
        exs.put(11, new Example(11, 1, 0, null));
    }

    static Example get(int p) {
        return exs.get(p);
    }

    private final int p;
    private final int a;
    private final int b;
    private final String g;

    private Example(int p, int a, int b, String g) {
        this.p = p;
        this.a = a;
        this.b = b;
        this.g = g;
    }

    public int getP() {
        return p;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public String getG() {
        return g;
    }
}
