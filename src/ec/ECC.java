package ec;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;

/**
 * @author Jose A. Manas
 * @version 22-abr-2009
 */
public class ECC {
//    private static final String TITLE = "Criptograf\u00EDa de Curvas El\u00EDpticas / 16.7.2015";
    private static final String TITLE = Text.get("ECC") + " / 23.9.2015";

    private static final String BODY;

    static {
        BODY = "<html>"
                + "<body>" +
                header(1, TITLE) +

                header(2, Text.get("Elliptic curves over Zp")) +
                "<ol>" +
                "<li>" + reference("ZpCurves", Text.get("curve analysis")) +
                "<li>" + reference("ZpAlgo", Text.get("cryptographic algorithms")) +
                "</ol>" +

                header(2, Text.get("Elliptic curves over F2m")) +
                "<ol>" +
                "<li>" + reference("F2mCurves", Text.get("curve analysis")) +
                "<li>" + reference("F2mAlgo", Text.get("cryptographic algorithms")) +
                "</ol>" +

                "</body>" +
                "</html>";
    }

    private static String header(int nivel, String titulo) {
        return String.format("<h%d>%s</h%d>", nivel, titulo, nivel);
    }

    private static String reference(String ref, String text) {
        return String.format("<a href=\"%s\">%s</a>", ref, text);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JEditorPane roadMapPane = new JEditorPane("text/html", BODY);
        roadMapPane.setEditable(false);
        roadMapPane.addHyperlinkListener(new MyHyperlinkListener());
        frame.getContentPane().add(new JScrollPane(roadMapPane), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private static void start(String what) {
        if (what.equals("ZpCurves"))
            ec_fp.CGUI.main(null);
        else if (what.equals("ZpAlgo"))
            ec_fp.AlgoGUI.main(null);
        else if (what.equals("F2mCurves"))
            ec_f2m.CGUI.main(null);
        else if (what.equals("F2mAlgo"))
            ec_f2m.AlgoGUI.main(null);
    }

    private static class MyHyperlinkListener
            implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    start(e.getDescription());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
