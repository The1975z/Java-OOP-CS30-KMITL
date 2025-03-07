package ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

public class SoundToggleButton extends JButton {
    private final Color goldAccent;
    
    public SoundToggleButton(String text, int width, int height, Color goldAccent) {
        super(text);
        this.goldAccent = goldAccent;
        setPreferredSize(new Dimension(width, height));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setToolTipText("คลิกเพื่อเปิด/ปิดเสียง");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // กำหนดสีพื้นหลังปุ่มตามสถานะ
        Color bgColor;
        if (getText().equals("SOUND OFF")) {
            // สีเมื่อปิดเสียง - สีแดงเข้ม
            if (getModel().isPressed()) {
                bgColor = new Color(130, 40, 40);
            } else if (getModel().isRollover()) {
                bgColor = new Color(170, 60, 60);
            } else {
                bgColor = new Color(150, 50, 50);
            }
        } else {
            // สีเมื่อเปิดเสียง - สีเขียวเข้ม
            if (getModel().isPressed()) {
                bgColor = new Color(40, 120, 40);
            } else if (getModel().isRollover()) {
                bgColor = new Color(60, 160, 60);
            } else {
                bgColor = new Color(50, 140, 50);
            }
        }
        
        // วาดพื้นหลังปุ่ม
        g2d.setColor(bgColor);
        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
        
        // วาดขอบปุ่ม
        g2d.setColor(new Color(goldAccent.getRed(), goldAccent.getGreen(), goldAccent.getBlue(), 150));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 10, 10));
        
        // เพิ่มเอฟเฟกต์แสงเงา
        Paint oldPaint = g2d.getPaint();
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(255, 255, 255, 80),
            0, getHeight(), new Color(255, 255, 255, 5)
        );
        g2d.setPaint(gp);
        g2d.fill(new RoundRectangle2D.Double(2, 2, getWidth() - 4, getHeight() / 2 - 2, 10, 10));
        g2d.setPaint(oldPaint);
        
        // วาดตัวอักษรหรือไอคอน
        g2d.setColor(Color.WHITE);
        
        // วาดไอคอนเสียง
        String soundIcon = getText().equals("SOUND ON") ? "🔊" : "🔇";
        g2d.setFont(new Font("Dialog", Font.BOLD, 18));
        FontMetrics iconFm = g2d.getFontMetrics();
        g2d.drawString(soundIcon, 15, getHeight()/2 + iconFm.getAscent()/2 - 2);
        
        // วาดข้อความ
        String displayText = getText().equals("SOUND ON") ? "ON" : "OFF";
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(displayText);
        g2d.drawString(displayText, getWidth() - textWidth - 15, getHeight()/2 + fm.getAscent()/2 - 2);
    }
}
