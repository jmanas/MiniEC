package ec;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jose on 15-Jul-15.
 */
public class Text {
    private static Map<String, String> dictionary= new HashMap<>();

    static {
        dictionary.put("A's key", "A's key");
        dictionary.put("B's key", "B's key");
        dictionary.put("agreed key", "agreed key");
        dictionary.put("cryptographic algorithms", "cryptographic algorithms");
        dictionary.put("curve analysis", "curve analysis");
        dictionary.put("decrypt", "decrypt");
        dictionary.put("ECC", "Elliptic Curve Cryptography");
        dictionary.put("Elliptic curves over F2m", "Elliptic curves over F2m");
        dictionary.put("Elliptic curves over Zp", "Elliptic curves over Zp");
        dictionary.put("encrypt", "encrypt");
        dictionary.put("examples", "examples");
        dictionary.put("does not belong to the curve", " does not belong to the curve");
        dictionary.put("go", "go");
        dictionary.put("input", "input");
        dictionary.put("is prime", " is prime");
        dictionary.put("is not prime", " is not prime");
        dictionary.put("message", "message");
        dictionary.put("no solution", "no solution after %d tries");
        dictionary.put("not prime", "not prime");
        dictionary.put("order", "order");
        dictionary.put("output", "output");
        dictionary.put("p is not prime", "p is not prime");
        dictionary.put("p is prime", "p is prime");
        dictionary.put("points", "points");
        dictionary.put("prime", "prime");
        dictionary.put("public", "public");
        dictionary.put("secret", "secret");
        dictionary.put("signature", "signature");
        dictionary.put("verification", "verification");
    }

    public static String get(String key) {
        String s = dictionary.get(key);
        if (s == null)
            return key;
        else
            return s;
    }
}
