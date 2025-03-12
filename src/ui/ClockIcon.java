package ui;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;

public class ClockIcon extends JComponent {

    private boolean isWhiteTurn = true;
    private final int size;
    private final Color WHITE_COLOR = new Color(240, 240, 240);
    private final Color BLACK_COLOR = new Color(50, 50, 50);
    private final Color BORDER_COLOR = new Color(180, 140, 80);
    private final Color NEEDLE_COLOR = new Color(180, 40, 40);

    public ClockIcon(int size) {
        this.size = size;
        setPreferredSize(new Dimension(size, size));
    }

    public void setTurn(boolean isWhiteTurn) {
        this.isWhiteTurn = isWhiteTurn;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fill(new Ellipse2D.Double(3, 3, size - 2, size - 2));

        Color clockColor = isWhiteTurn ? WHITE_COLOR : BLACK_COLOR;
        g2d.setColor(clockColor);
        g2d.fill(new Ellipse2D.Double(1, 1, size - 2, size - 2));

        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(new Ellipse2D.Double(1, 1, size - 2, size - 2));

        g2d.setStroke(new BasicStroke(1.0f));
        for (int i = 0; i < 12; i++) {
            double angle = Math.PI * 2 * i / 12;
            double innerRadius = size / 2 - 5;
            double outerRadius = size / 2 - 2;

            int x1 = (int) (size / 2 + innerRadius * Math.sin(angle));
            int y1 = (int) (size / 2 - innerRadius * Math.cos(angle));
            int x2 = (int) (size / 2 + outerRadius * Math.sin(angle));
            int y2 = (int) (size / 2 - outerRadius * Math.cos(angle));

            g2d.setColor(isWhiteTurn ? Color.BLACK : Color.WHITE);
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.setColor(NEEDLE_COLOR);
        g2d.fillOval(size / 2 - 2, size / 2 - 2, 4, 4);

        g2d.setStroke(new BasicStroke(1.5f));

        double hourAngle = Math.PI * 2 * (3) / 12;
        drawNeedle(g2d, hourAngle, size / 2 * 0.5);

        double minuteAngle = Math.PI * 2 * (15) / 60;
        drawNeedle(g2d, minuteAngle, size / 2 * 0.7);
    }

    private void drawNeedle(Graphics2D g2d, double angle, double length) {
        g2d.setColor(NEEDLE_COLOR);
        int x = (int) (size / 2 + length * Math.sin(angle));
        int y = (int) (size / 2 - length * Math.cos(angle));
        g2d.drawLine(size / 2, size / 2, x, y);
    }
}
