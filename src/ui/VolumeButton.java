package ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

public class VolumeButton extends JButton {
    private final Color goldAccent;
    
    public VolumeButton(String text, int width, int height, Color goldAccent) {
        super(text);
        this.goldAccent = goldAccent;
        setPreferredSize(new Dimension(width, height));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // เพิ่ม tooltip สำหรับปุ่ม
        if (text.equals("+")) {
            setToolTipText("เพิ่มระดับเสียง");
        } else if (text.equals("-")) {
            setToolTipText("ลดระดับเสียง");
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // สีสำหรับปุ่มปรับระดับเสียง - สีทอง
        Color bgColor;
        if (getModel().isPressed()) {
            bgColor = new Color(180, 140, 60).darker();
        } else if (getModel().isRollover()) {
            bgColor = new Color(212, 175, 55);
        } else {
            bgColor = new Color(180, 140, 60);
        }
        
        // วาดพื้นหลังปุ่ม
        g2d.setColor(bgColor);
        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
        
        // วาดขอบปุ่ม
        g2d.setColor(new Color(goldAccent.getRed(), goldAccent.getGreen(), goldAccent.getBlue(), 150));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 10, 10));
        
        // วาดตัวอักษร + หรือ -
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getHeight();
        g2d.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2 - 2);
    }
}
