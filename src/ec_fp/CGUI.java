package ec_fp;

import ec.Text;
import labelleditem.LabelledItemPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Set;

/**
 * @author Jose A. Manas
 * @version 18-feb-2009
 */
public class CGUI
        extends JApplet
        implements ActionListener {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    public Curve curve;
    public Set<Point> points;

    private JComponent reportPanel;
    private JLabel pPrimeLabel;
    private JLabel groupLabel;
    private JLabel nBitsLabel;
    private JLabel equationLabel;
    private JLabel nPointsLabel;

    private JComponent graphPanel;
    private JComponent numberPanel;

    private JTextField pTextField;
    private JTextField aTextField;
    private JTextField bTextField;
    private JButton goButton;

    private JTextField pField;
    private JTextField qField;
    private JTextField kField;
    private JTextField pqField;
    private JTextField p2Field;
    private JTextField kpField;

    public void init() {
        setJMenuBar(getMenuBar());
        setup(getContentPane());
    }

    public void init(JDialog dialog) {
        dialog.setJMenuBar(getMenuBar());
        setup(dialog.getContentPane());
    }

    public void init(JFrame frame) {
        frame.setJMenuBar(getMenuBar());
        setup(frame.getContentPane());
    }

    private JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu(Text.get("examples"));
        menuBar.add(menu);
        menu.add(mkExample(23));
        menu.add(mkExample(19));
        menu.add(mkExample(17));
        menu.add(mkExample(13));
        menu.add(mkExample(11));
        return menuBar;
    }

    private JMenuItem mkExample(int p) {
        JMenuItem item = new JMenuItem(String.format("p = %d", p));
        item.addActionListener(this);
        return item;
    }

    private void setup(Container container) {
        JComponent topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(parametersPanel());
        reportPanel = mkReportPanel();
        topPanel.add(reportPanel);
        container.add(topPanel, BorderLayout.NORTH);
        graphPanel = mkGraphPanel();
        numberPanel = mkNumberPanel();
        container.add(graphPanel, BorderLayout.CENTER);
        container.add(numberPanel, BorderLayout.EAST);
    }

    private JComponent parametersPanel() {
        LabelledItemPanel panel = new LabelledItemPanel();
        pTextField = new JTextField(7);
        aTextField = new JTextField(7);
        bTextField = new JTextField(7);
        goButton = new JButton("go");
        goButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int p = Integer.parseInt(pTextField.getText());
                    if (isPrime(p)) {
                        pPrimeLabel.setText("p" + Text.get("is prime"));
                        pPrimeLabel.setBackground(Color.GREEN);
                    } else {
                        pPrimeLabel.setText("p" + Text.get("is not prime"));
                        pPrimeLabel.setBackground(Color.RED);
                    }

                    int a = Integer.parseInt(aTextField.getText());
                    int b = Integer.parseInt(bTextField.getText());
                    curve = new Curve(a, b, p);
                    String det = "<html>4a<sup>3</sup> + 27b<sup>2</sup>";
                    if (curve.isGroup()) {
                        groupLabel.setText(det + " &ne; 0</html>");
                        groupLabel.setBackground(Color.GREEN);
                    } else {
                        groupLabel.setText(det + " = 0</html>");
                        groupLabel.setBackground(Color.RED);
                    }

                    int nBits = BigInteger.valueOf(p).bitLength();
                    nBitsLabel.setText(nBits + " bits");

                    StringBuilder buffer = new StringBuilder();
                    buffer.append("<html>y<sup>2</sup> = x<sup>3</sup>");
                    if (a == 1)
                        buffer.append(" + x");
                    if (a > 1)
                        buffer.append(String.format(" + %dx", a));
                    if (b > 0)
                        buffer.append(String.format(" + %d", b));
                    buffer.append(String.format(" (F<sub>%d</sub>)", p));
//                    buffer.append(String.format(" [%d puntos + O]", points.size()));
//                    curvaLabel.setText(buffer.toString());
                    equationLabel.setText(buffer.toString());

                    points = curve.points();
                    nPointsLabel.setText(String.format("%d %s + O", points.size(), Text.get("points")));
                    reportPanel.repaint();

                    clearNumberPanel();
                    graphPanel.repaint();
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(null, x);
                }
            }
        });
        panel.addItem("p", pTextField);
        panel.addItem("a", aTextField);
        panel.addItem("b", bTextField);
        panel.addItem("", goButton);
        return panel;
    }

    public static boolean isPrime(int n) {
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

    private JComponent mkReportPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        pPrimeLabel = new JLabel("                 ");
        pPrimeLabel.setOpaque(true);
        panel.add(pPrimeLabel);

        nBitsLabel = new JLabel("                 ");
        panel.add(nBitsLabel);

        equationLabel = new JLabel("                 ");
        panel.add(equationLabel);

        groupLabel = new JLabel("                 ");
        groupLabel.setOpaque(true);
        panel.add(groupLabel);

        nPointsLabel = new JLabel("                 ");
        panel.add(nPointsLabel);

        return panel;
    }

    private JComponent mkNumberPanel() {
        LabelledItemPanel panel = new LabelledItemPanel();

        pField = new JTextField(7);
        qField = new JTextField(7);
        kField = new JTextField(7);
        pqField = new JTextField(7);
        pqField.setEditable(false);
        p2Field = new JTextField(7);
        p2Field.setEditable(false);
        kpField = new JTextField(7);
        kpField.setEditable(false);

        panel.addItem("P", pField);
        panel.addItem("Q", qField);
        panel.addItem("k", kField);
        panel.addItem("P+Q", pqField);
        panel.addItem("2P", p2Field);
        panel.addItem("kP", kpField);

        AbstractAction action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                calcNumbers();
                numberPanel.repaint();
            }
        };
        pField.addActionListener(action);
        qField.addActionListener(action);
        kField.addActionListener(action);
        return panel;
    }

    private void clearNumberPanel() {
        pField.setText("");
        qField.setText("");
        kField.setText("");
        p2Field.setText("");
        pqField.setText("");
        kpField.setText("");
        numberPanel.repaint();
    }

    private void calcNumbers() {
        try {
            p2Field.setText("");
            pqField.setText("");
            kpField.setText("");
            if (curve == null)
                return;
            Point p = mkPunto(pField.getText());
            if (p == null)
                return;
            if (!curve.belongs(p))
                throw new Exception(p + Text.get("does not belong to the curve"));
            Point q = mkPunto(qField.getText());
            if (q != null && !curve.belongs(q))
                throw new Exception(q + Text.get("does not belong to the curve"));
            String kText = kField.getText();
            int k;
            if (kText == null || kText.length() == 0)
                k = -1;
            else
                k = Integer.parseInt(kText);
            if (q != null)
                pqField.setText(curve.add(p, q).toString());
            p2Field.setText(curve.add(p, p).toString());
            if (k >= 0)
                kpField.setText(curve.mul(k, p).toString());
        } catch (NumberFormatException x) {
            JOptionPane.showMessageDialog(null, x);
        } catch (Exception x) {
            JOptionPane.showMessageDialog(null, x.getMessage());
        }
    }

    private Point mkPunto(String text) {
        if (text == null)
            return null;
        text = text.trim();
        if (text.length() == 0)
            return null;
        if (text.equals("0"))
            return Point.O;
        int sep = text.indexOf(',');
        if (sep < 0)
            sep = text.indexOf(' ');
        if (sep < 0)
            return null;
        int x = Integer.parseInt(text.substring(0, sep).trim());
        int y = Integer.parseInt(text.substring(sep + 1).trim());
        return new Point(x, y);
    }

    private JComponent mkGraphPanel() {
        JPanel panel = new JPanel() {
            public void paint(Graphics g) {
                drawGraph(g);
            }
        };
        Dimension dimension = new Dimension(WIDTH, HEIGHT);
        panel.setMinimumSize(dimension);
        panel.setMaximumSize(dimension);
        panel.setPreferredSize(dimension);
        return panel;
    }

    private void drawGraph(Graphics g) {
        if (curve == null)
            return;
        int width = graphPanel.getWidth();
        int height = graphPanel.getHeight();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        double p = curve.getP();
        // lineas horizontales
        for (int y = 0; y < p; y++) {
            int py = pixelY(y);
            g.setColor(Color.GRAY);
            g.drawLine(30, py, width - 10, py);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(y), 10, py + 6);
        }
        // lineas verticales
        for (int x = 0; x < p; x++) {
            int px = pixelX(x);
            g.setColor(Color.GRAY);
            g.drawLine(px, height - 30, px, 10);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(x), px - 6, height - 10);
        }
        // points
        for (Point point : points) {
            int px = pixelX(point.getX());
            int py = pixelY(point.getY());
            int radio = 3;
            g.drawOval(px - radio, py - radio, 2 * radio, 2 * radio);
        }
    }

    private int pixelX(int x) {
        if (curve == null)
            return 0;
        int width = graphPanel.getWidth();
        double p = curve.getP();
        double deltaX = (width - 40) / (p - 1);
        return (int) Math.round(30 + x * deltaX);
    }

    private int pixelY(int y) {
        if (curve == null)
            return 0;
        int height = graphPanel.getHeight();
        double p = curve.getP();
        double deltaY = (height - 40) / (p - 1);
        return (int) Math.round(10 + (p - 1 - y) * deltaY);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(Text.get("Elliptic curves over Zp"));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        CGUI gui = new CGUI();
        gui.init(frame);
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String which = e.getActionCommand();
        if (which.equals("p = 23")) {
            pTextField.setText("23");
            aTextField.setText("1");
            bTextField.setText("4");
            goButton.doClick();
        } else if (which.equals("p = 19")) {
            pTextField.setText("19");
            aTextField.setText("2");
            bTextField.setText("3");
            goButton.doClick();
        } else if (which.equals("p = 17")) {
            pTextField.setText("19");
            aTextField.setText("1");
            bTextField.setText("2");
            goButton.doClick();
        } else if (which.equals("p = 13")) {
            pTextField.setText("13");
            aTextField.setText("1");
            bTextField.setText("1");
            goButton.doClick();
        } else if (which.equals("p = 11")) {
            pTextField.setText("11");
            aTextField.setText("1");
            bTextField.setText("0");
            goButton.doClick();
        }
    }
}
