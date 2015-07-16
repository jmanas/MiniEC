package ec_f2m;

import ec.Text;
import ec_fp.Zp;
import labelleditem.LabelledItemPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Set;

/**
 * @author Jose A. Manas
 * @version 18-feb-2009
 */
public class AlgoGUI
        extends JApplet
        implements ActionListener {
    private static final int AREA_WIDTH = 30;
    private final Random random = new Random(System.currentTimeMillis());

    private Curve curve;
    private Set<Point> points;
    private Point G;
    private int n;
    private Zp zn;

    private Key keyA;
    private Key keyB;

    private JTextField mTextField;
    private JTextField aTextField;
    private JTextField bTextField;
    private JTextField gTextField;
    private JButton goButton;

    private JComponent reportPanel;
    private JLabel groupLabel;
    private JLabel nBitsLabel;
    private JLabel equationLabel;
    private JLabel mxLabel;
    private JLabel nPointsLabel;
    private JLabel afpLabel;
    private JLabel gOrderLabel;

    private JTextField aksField;
    private JLabel akpField;
    private JTextField bksField;
    private JLabel bkpField;

    private JTextArea ecdhA;
    private JTextArea ecdhB;

    private JTextField messageToEncryptField_dh;
    private JTextArea ec_dh_A_1;
    private JTextArea ec_dh_B_1;

    private JTextField messageToSignField;
    private JTextArea ecdsaA;
    private JTextArea ecdsaB;

    public void init() {
        setJMenuBar(getMenuBar());
        getContentPane().add(new JScrollPane(setup()));
    }

    public void init(JFrame frame) {
        frame.setJMenuBar(getMenuBar());
        frame.getContentPane().add(new JScrollPane(setup()));
    }

    private JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu(Text.get("examples"));
        menuBar.add(menu);
        menu.add(mkExample(5));
        return menuBar;
    }

    private JMenuItem mkExample(int m) {
        JMenuItem item = new JMenuItem(String.format("m = %d", m));
        item.addActionListener(this);
        return item;
    }

    private JPanel setup() {
        JPanel container = new JPanel(new BorderLayout());

        JComponent topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(mkParametersPanel());
        reportPanel = mkReportPanel();
        topPanel.add(reportPanel);
        container.add(topPanel, BorderLayout.NORTH);

        JComponent panel = Box.createVerticalBox();
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        {
            JComponent grid = new JPanel(new GridLayout(1, 2));
            grid.setAlignmentX(Component.LEFT_ALIGNMENT);
            grid.add(mkKeyAPanel());
            grid.add(mkKeyBPanel());
            panel.add(grid);
        }

        JLabel ecdhLabel = new JLabel("ECDH - " + Text.get("agreed key"));
        ecdhA = new JTextArea(2, AREA_WIDTH);
        ecdhB = new JTextArea(2, AREA_WIDTH);
        ecdhLabel.addMouseListener(new ECDHMouseListener());
        mkAlg(panel, ecdhLabel, ecdhA, ecdhB);

        messageToEncryptField_dh = new JTextField(10);
        ec_dh_A_1 = new JTextArea(4, AREA_WIDTH);
        ec_dh_B_1 = new JTextArea(4, AREA_WIDTH);
        messageToEncryptField_dh.addActionListener(new EncryptDHActionListener());
        JPanel ec_dhPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ec_dhPanel.add(new JLabel("ECDH - " + Text.get("message") + ": "));
        ec_dhPanel.add(messageToEncryptField_dh);
        mkAlg(panel, ec_dhPanel, ec_dh_A_1, ec_dh_B_1);

        messageToSignField = new JTextField(10);
        ecdsaA = new JTextArea(4, AREA_WIDTH);
        ecdsaB = new JTextArea(4, AREA_WIDTH);
        messageToSignField.addActionListener(new ECDSAActionListener());
        JPanel ecdsaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ecdsaPanel.add(new JLabel("ECDSA - " + Text.get("message") + ": "));
        ecdsaPanel.add(messageToSignField);
        mkAlg(panel, ecdsaPanel, ecdsaA, ecdsaB);

        container.add(panel, BorderLayout.CENTER);

        return container;
    }

    private void mkAlg(JComponent panel, JComponent label, JTextArea left, JTextArea right) {
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        JPanel grid = new JPanel(new GridLayout(1, 2, 5, 5));
        grid.add(left);
        grid.add(right);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(grid);
    }

    private JComponent mkParametersPanel() {
        LabelledItemPanel panel = new LabelledItemPanel();
        mTextField = new JTextField(7);
        aTextField = new JTextField(7);
        bTextField = new JTextField(7);
        gTextField = new JTextField(7);
        goButton = new JButton(Text.get("go"));
        goButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    aksField.setText("");
                    akpField.setText("");
                    keyA = null;
                    bksField.setText("");
                    bkpField.setText("");
                    keyB = null;
                    clearAreas();

                    int m = Integer.parseInt(mTextField.getText());

                    int a = Integer.parseInt(aTextField.getText());
                    int b = Integer.parseInt(bTextField.getText());
                    curve = new Curve(a, b, m);
                    String det = "<html>b";
                    if (curve.isGroup()) {
                        groupLabel.setText(det + " &ne; 0</html>");
                        groupLabel.setBackground(Color.GREEN);
                    } else {
                        groupLabel.setText(det + " = 0</html>");
                        groupLabel.setBackground(Color.RED);
                    }

                    int nBits = 1 << m;
                    nBitsLabel.setText(nBits + " bits");

                    {
                        StringBuilder buffer = new StringBuilder();
                        buffer.append("<html>y<sup>2</sup> + xy = x<sup>3</sup>");
                        if (a == 1)
                            buffer.append(" + x<sup>2</sup>");
                        if (a > 1)
                            buffer.append(String.format(" + %dx<sup>2</sup>", a));
                        if (b > 0)
                            buffer.append(String.format(" + %d", b));
                        buffer.append(String.format(" (F<sub>2</sub>%d)", m));
                        equationLabel.setText(buffer.toString());
                    }

                    {
                        StringBuilder buffer = new StringBuilder();
                        buffer.append("<html>m(x) = ");
                        buffer.append(printZx(curve.getField().getPrimePolinomial()));
                        buffer.append("</html>");
                        mxLabel.setText(buffer.toString());
                    }

                    points = curve.points();
                    nPointsLabel.setText(String.format("%d " + Text.get("points") + " + O", points.size()));
                    StringBuilder afp = new StringBuilder();
                    int np = 0;
                    for (Point point : points) {
                        if (afp.length() > 0)
                            afp.append(", ");
                        afp.append(point.toString());
                        if (np++ > Math.min(points.size(), 9))
                            break;
                    }
                    if (points.size() > 9)
                        afp.append(", ...");
                    afpLabel.setText(afp.toString());

                    G = mkPunto(gTextField.getText());
                    if (!curve.belongs(G))
                        throw new IllegalArgumentException("G" + Text.get("does not belong to the curve"));
                    n = curve.order(G);
                    if (isPrime(n)) {
                        gOrderLabel.setText(String.format("%s(G)= %s (%s)", Text.get("order"), n, Text.get("prime")));
                        gOrderLabel.setBackground(Color.GREEN);
                    } else {
                        gOrderLabel.setText(String.format("%s(G)= %s (%s)", Text.get("order"), n, Text.get("not prime")));
                        gOrderLabel.setBackground(Color.RED);
                    }
                    zn = new Zp(n);

                    reportPanel.repaint();
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(null, x.getMessage());
                }
            }
        });
        panel.addItem("m", mTextField);
        panel.addItem("a", aTextField);
        panel.addItem("b", bTextField);
        panel.addItem("G", gTextField);
        panel.addItem("", goButton);
        return panel;
    }

    private String printZx(long number) {
        StringBuilder buffer = new StringBuilder();
        for (int i = curve.getField().grado(number); i > 0; i--) {
            if ((number & 0x1 << i) != 0) {
                if (buffer.length() > 0)
                    buffer.append(" + ");
                buffer.append("x");
                if (i > 1)
                    buffer.append("<sup>").append(i).append("</sup>");
            }
        }
        if ((number & 0x01) != 0) {
            if (buffer.length() > 0)
                buffer.append(" + ");
            buffer.append("1");
        }
        return buffer.toString();
    }

    private JComponent mkReportPanel() {
        String whiteSpace = "                 ";

        JPanel panel = new JPanel(new GridLayout(0, 1));

        nBitsLabel = new JLabel(whiteSpace);
        panel.add(nBitsLabel);

        equationLabel = new JLabel(whiteSpace);
        panel.add(equationLabel);

        groupLabel = new JLabel(whiteSpace);
        groupLabel.setOpaque(true);
        panel.add(groupLabel);

        mxLabel = new JLabel(whiteSpace);
        panel.add(mxLabel);

        nPointsLabel = new JLabel(whiteSpace);
        panel.add(nPointsLabel);

        afpLabel = new JLabel(whiteSpace);
        panel.add(afpLabel);

        gOrderLabel = new JLabel(whiteSpace);
        gOrderLabel.setOpaque(true);
        panel.add(gOrderLabel);

        return panel;
    }

    private JPanel mkKeyAPanel() {
        LabelledItemPanel panel = new LabelledItemPanel();
        aksField = new JTextField(10);
        akpField = new JLabel();
        aksField.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                akpField.setText("");
                keyA = null;
                clearAreas();
                try {
                    String s = aksField.getText().trim();
                    if (s.length() == 0) return;
                    int n = Integer.parseInt(s);
                    keyA = new Key(curve, G, n);
                    akpField.setText(keyA.getKp().toString());
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(null, x.getMessage());
                }
            }
        });
        panel.addItem(Text.get("secret") + ", A.ks", aksField);
        panel.addItem(Text.get("public") + ", A.kp", akpField);
        panel.setBorder(new TitledBorder(Text.get("A's key")));
        return panel;
    }

    private JPanel mkKeyBPanel() {
        LabelledItemPanel panel = new LabelledItemPanel();
        bksField = new JTextField(10);
        bkpField = new JLabel();
        bksField.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                bkpField.setText("");
                keyB = null;
                clearAreas();
                try {
                    String s = bksField.getText().trim();
                    if (s.length() == 0) return;
                    int n = Integer.parseInt(s);
                    keyB = new Key(curve, G, n);
                    bkpField.setText(keyB.getKp().toString());
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(null, x.getMessage());
                }
            }
        });
        panel.addItem(Text.get("secret") + ", B.ks", bksField);
        panel.addItem(Text.get("public") + ", B.kp", bkpField);
        panel.setBorder(new TitledBorder(Text.get("B's key")));
        return panel;
    }

    private void clearAreas() {
        ecdhA.setText("");
        ecdhB.setText("");
        if (ec_dh_A_1 != null)
            ec_dh_A_1.setText("");
        if (ec_dh_B_1 != null)
            ec_dh_B_1.setText("");
        if (ecdsaA != null)
            ecdsaA.setText("");
        if (ecdsaB != null)
            ecdsaB.setText("");
    }

    private Point mkPunto(String text) {
        if (text == null || text.length() == 0)
            return null;
        int sep = text.indexOf(',');
        if (sep < 0)
            sep = text.indexOf(' ');
        if (sep < 0)
            return null;
        int x = Integer.parseInt(text.substring(0, sep).trim());
        int y = Integer.parseInt(text.substring(sep + 1).trim());
        return new Point(x, y);
    }

    private boolean isPrime(int n) {
        if (n < 2)
            return false;
        if (n == 2)
            return true;
        if (n % 2 == 0)
            return false;
        for (int i = 3; i * i <= n; i += 2)
            if (n % i == 0)
                return false;
        return true;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(Text.get("Elliptic curves over F2m"));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        AlgoGUI gui = new AlgoGUI();
        gui.init(frame);
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String which = e.getActionCommand();
        if (which.equals("m = 5")) {
            mTextField.setText("5");
            aTextField.setText("1");
            bTextField.setText("5");
            gTextField.setText("30, 26");
            goButton.doClick();
        }
    }

    private class ECDHMouseListener
            extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            try {
                ecdhA.setText("");
                ecdhB.setText("");
                if (keyA == null) return;
                if (keyB == null) return;
                String msgA = String.format(
                        "A.ks * B.kp= %s",
                        curve.mul(keyA.getKs(), keyB.getKp())
                );
                String msgB = String.format(
                        "B.ks * A.kp= %s",
                        curve.mul(keyB.getKs(), keyA.getKp())
                );
                ecdhA.setText(msgA);
                ecdhB.setText(msgB);
            } catch (Exception x) {
                JOptionPane.showMessageDialog(null, x.getMessage());
            }
        }
    }

    private class EncryptDHActionListener
            extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            try {
                if (keyB == null) return;
                String redMsg = messageToEncryptField_dh.getText().trim();
                if (redMsg.length() == 0) return;

                Point pK;
                {
                    int k = 1 + random.nextInt(curve.getM() - 1);
                    pK = curve.mul(k, G);
                    Point shared = curve.mul(k, keyB.getKp());

                    String msgA = "";
                    ec_dh_A_1.setText(msgA);
                    msgA += String.format("%s: %s%n", Text.get("input"), redMsg);
                    msgA += String.format("k= %d; pK= %s%n", k, pK);
                    msgA += String.format("S= k * B.kp= %d * %s= %s%n", k, keyB.getKp(), shared);
                    msgA += String.format("%s= [%s, %s(%s, %s)]%n", Text.get("output"), pK, Text.get("encrypt"), "S", redMsg);
                    ec_dh_A_1.setText(msgA);
                }

                {
                    Point shared = curve.mul(keyB.getKs(), pK);

                    String msgB = "";
                    ec_dh_B_1.setText(msgB);
                    msgB += String.format("%s= [%s, %s]%n", Text.get("input"), pK, "***");
                    msgB += String.format("S= B.ks * S= %d * %s= %s%n", keyB.getKs(), pK, shared);
                    msgB += String.format("%s= %s(S, %s)= %s%n", Text.get("output"), Text.get("decrypt"), "***", redMsg);
                    ec_dh_B_1.setText(msgB);
                }
            } catch (Exception x) {
                JOptionPane.showMessageDialog(null, x.getMessage());
            }
        }
    }

    private class ECDSAActionListener
            extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            try {
                String msgA = "";
                String msgB = "";
                ecdsaA.setText(msgA);
                ecdsaB.setText(msgB);
                if (keyA == null) return;
                String text = messageToSignField.getText().trim();
                if (text.length() == 0) return;
                int msg = Integer.parseInt(text);
                int k;
                int r, s;
                int ntries = 0;
                while (true) {
                    if (ntries++ > 100) {
                        JOptionPane.showMessageDialog(null,
                                String.format(Text.get("no solution"), ntries));
                        return;
                    }
                    k = random.nextInt(zn.getP());
                    if (k == 0) continue;
                    Point R = curve.mul(k, G);
                    if (R.equals(Point.O)) continue;
                    r = zn.mod((int) R.getX());
                    if (r == 0) continue;
                    int k_1 = zn.inv(k);
                    s = zn.mul(k_1, zn.add(msg, zn.mul(r, keyA.getKs())));
                    if (s == 0) continue;
                    break;
                }
                msgA += String.format("%s: %d%n", Text.get("input"), msg);
                msgA += String.format("k= %d%n", k);
                msgA += String.format("%s: [r= %d; s= %d]%n", Text.get("signature"), r, s);

                int w = zn.inv(s);
                int u1 = zn.mul(msg, w);
                int u2 = zn.mul(r, w);
                Point X = curve.add(curve.mul(u1, G), curve.mul(u2, keyA.getKp()));
                int v = zn.mod((int) X.getX());
                msgB += String.format("%s: [r= %d, s= %d]%n", Text.get("input"), r, s);
                msgB += String.format("X= %s%n", X);
                msgB += String.format("%s: %d = %d :: %b%n", Text.get("verification"), r, v, r == v);

                ecdsaA.setText(msgA);
                ecdsaB.setText(msgB);
            } catch (Exception x) {
                JOptionPane.showMessageDialog(null, x.getMessage());
            }
        }
    }
}
