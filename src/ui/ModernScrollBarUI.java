package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ModernScrollBarUI extends BasicScrollBarUI {
    private final Color goldAccent;
    
    public ModernScrollBarUI(Color goldAccent) {
        this.goldAccent = goldAccent;
    }
    
    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = new Color(goldAccent.getRed(), goldAccent.getGreen(), goldAccent.getBlue(), 100);
        this.thumbDarkShadowColor = null;
        this.thumbHighlightColor = null;
        this.thumbLightShadowColor = null;
        this.trackColor = new Color(30, 30, 40);
        this.trackHighlightColor = null;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!thumbBounds.isEmpty() && this.scrollbar.isEnabled()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.translate(thumbBounds.x, thumbBounds.y);
            g2d.setColor(thumbColor);
            g2d.fillRoundRect(0, 0, thumbBounds.width, thumbBounds.height, 10, 10);
            g2d.translate(-thumbBounds.x, -thumbBounds.y);
        }
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(trackColor);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
}
