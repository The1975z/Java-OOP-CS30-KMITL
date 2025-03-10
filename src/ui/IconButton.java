package ui;

import java.awt.*;
import javax.swing.*;

public class IconButton extends JButton {
    private final String text;
    
    public IconButton(String text, int width, int height) {
        super(text);
        this.text = text;
        setPreferredSize(new Dimension(width, height));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // วาดพื้นหลังปุ่ม
        if (getModel().isPressed()) {
            g2d.setColor(new Color(180, 20, 20));
        } else if (getModel().isRollover()) {
            g2d.setColor(new Color(220, 60, 60));
        } else {
            g2d.setColor(new Color(180, 40, 40));
        }

        g2d.fillOval(0, 0, getWidth(), getHeight());

        // วาดขอบปุ่ม
        g2d.setColor(new Color(250, 250, 250, 100));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawOval(1, 1, getWidth() - 2, getHeight() - 2);

        // วาดตัวอักษร X
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);
    }
}
