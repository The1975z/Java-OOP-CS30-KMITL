package ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

public class VolumeIndicatorPanel extends JPanel {
    private final float volume;
    private final Color goldAccent;
    
    public VolumeIndicatorPanel(float volume, Color goldAccent) {
        this.volume = volume;
        this.goldAccent = goldAccent;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // วาดพื้นหลังโปร่งใส
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        
        // วาดขอบ
        g2d.setColor(goldAccent);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 18, 18);
        
        // แสดงไอคอนเสียง
        g2d.setFont(new Font("Dialog", Font.BOLD, 24));
        g2d.setColor(Color.WHITE);
        String icon = volume > 0.7f ? "🔊" : volume > 0.3f ? "🔉" : volume > 0.0f ? "🔈" : "🔇";
        g2d.drawString(icon, 15, 35);
        
        // แสดงแถบระดับเสียง
        int barWidth = getWidth() - 80;
        int barHeight = 20;
        int barX = 60;
        int barY = (getHeight() - barHeight) / 2;
        
        // แถบพื้นหลัง
        g2d.setColor(new Color(70, 70, 70));
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);
        
        // แถบเสียง
        int filledWidth = (int)(barWidth * volume);
        if (filledWidth > 0) {
            g2d.setColor(new Color(80, 200, 80));
            g2d.fillRoundRect(barX, barY, filledWidth, barHeight, 10, 10);
        }
        
        // เปอร์เซ็นต์เสียง
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        String volumeText = Math.round(volume * 100) + "%";
        g2d.drawString(volumeText, barX + barWidth + 10, barY + 15);
    }
}
