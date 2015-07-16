package ec_f2m;

import ec.Text;
import labelleditem.LabelledItemPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * @author Jose A. Manas
 * @version 2-mar-2009
 */
public class CGUI
        extends JApplet
        implements ActionListener {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    public Curve curve;
    public Set<Point> points;

    private JComponent graphPanel;
    private JComponent numberPanel;

    private JTextField mTextField;
    private JTextField aTextField;
    private JTextField bTextField;
    private JButton goButton;
    private JLabel curvaLabel;

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
        menu.add(mkExample(5));
        menu.add(mkExample(4));
        return menuBar;
    }

    private JMenuItem mkExample(int m) {
        JMenuItem item = new JMenuItem(String.format("m = %d", m));
        item.addActionListener(this);
        return item;
    }

    private void setup(Container container) {
        graphPanel = graphPanel();
        numberPanel = numberPanel();
        container.add(pamametersPanel(), BorderLayout.NORTH);
        container.add(graphPanel, BorderLayout.CENTER);
        container.add(numberPanel, BorderLayout.EAST);
    }

    private JComponent pamametersPanel() {
        LabelledItemPanel panel = new LabelledItemPanel();
        mTextField = new JTextField(7);
        aTextField = new JTextField(7);
        bTextField = new JTextField(7);
        goButton = new JButton(Text.get("go"));
        curvaLabel = new JLabel("");
        goButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int m = Integer.parseInt(mTextField.getText());
                    int a = Integer.parseInt(aTextField.getText());
                    int b = Integer.parseInt(bTextField.getText());
                    curve = new Curve(a, b, m);
                    points = curve.points();
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("<html>y<sup>2</sup> + xy = x<sup>3</sup>");
                    if (a == 1)
                        buffer.append(" + x<sup>2</sup>");
                    if (a > 1)
                        buffer.append(String.format(" + %dx<sup>2</sup>", a));
                    buffer.append(String.format(" + %d", b));
                    buffer.append(String.format(" (F<sub>2</sub>%d)", m));

                    GF2m field = curve.getField();
                    long primePolinomial = field.getPrimePolinomial();
                    buffer.append(String.format("  { m(x) = %s }", field.printZx(primePolinomial)));

                    buffer.append(String.format("  [%d %s + O]", points.size(), Text.get("points")));

                    curvaLabel.setText(buffer.toString());
                    clearNumberPanel();
                    graphPanel.repaint();
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(null, x);
                }
            }
        });
        panel.addItem("m", mTextField);
        panel.addItem("a", aTextField);
        panel.addItem("b", bTextField);
        panel.addItem("", goButton);
        panel.addItem("", curvaLabel);
        return panel;
    }

    private JComponent numberPanel() {
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

    private JComponent graphPanel() {
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
        double max = 1 << curve.getM();
        // l�neas horizontales
        for (int y = 0; y < max; y++) {
            int py = pixelY(y);
            g.setColor(Color.GRAY);
            g.drawLine(30, py, width - 10, py);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(y), 10, py + 6);
        }
        // l�neas verticales
        for (int x = 0; x < max; x++) {
            int px = pixelX(x);
            g.setColor(Color.GRAY);
            g.drawLine(px, height - 30, px, 10);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(x), px - 6, height - 10);
        }
        // points
        for (Point point : points) {
            int px = pixelX((int) point.getX());
            int py = pixelY((int) point.getY());
            int radio = 3;
            g.drawOval(px - radio, py - radio, 2 * radio, 2 * radio);
        }
    }

    private int pixelX(int x) {
        if (curve == null)
            return 0;
        int width = graphPanel.getWidth();
        double max = 1 << curve.getM();
        double deltaX = (width - 40) / (max - 1);
        return (int) Math.round(30 + x * deltaX);
    }

    private int pixelY(int y) {
        if (curve == null)
            return 0;
        int height = graphPanel.getHeight();
        double max = 1 << curve.getM();
        double deltaY = (height - 40) / (max - 1);
        return (int) Math.round(10 + (max - 1 - y) * deltaY);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(Text.get("Elliptic curves over F2m"));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        CGUI gui = new CGUI();
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
            goButton.doClick();
        } else if (which.equals("m = 4")) {
            mTextField.setText("4");
            aTextField.setText("1");
            bTextField.setText("1");
            goButton.doClick();
        }
    }
}
