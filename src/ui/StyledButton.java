package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import utils.SoundManager;

public class StyledButton extends JButton {
    private final Color buttonColor;
    private final Color hoverColor;
    private final Color accentColor;
    private final Color textColor;
    private final Font buttonFont;
    private final String text;
    
    public StyledButton(String text, Color buttonColor, Color hoverColor, Color accentColor, Color textColor, Font buttonFont) {
        super(text);
        this.text = text;
        this.buttonColor = buttonColor;
        this.hoverColor = hoverColor;
        this.accentColor = accentColor;
        this.textColor = textColor;
        this.buttonFont = buttonFont;
        
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(buttonFont);
        setForeground(textColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
                SoundManager.getInstance().playSound("select");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2d.setColor(buttonColor.darker());
        } else if (getModel().isRollover()) {
            g2d.setColor(hoverColor);
        } else {
            g2d.setColor(buttonColor);
        }

        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 100));
        g2d.draw(new RoundRectangle2D.Double(3, 3, getWidth() - 6, getHeight() - 6, 16, 16));

        g2d.setColor(textColor);
        g2d.setFont(buttonFont);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - fm.getDescent());
    }
}
