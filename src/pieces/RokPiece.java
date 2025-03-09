package pieces;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import utils.ResourceManager;

public class RokPiece {

    public enum Color {
        WHITE, BLACK
    }

    private Color color;
    private Image image;
    private int size;
    private static final float GLOW_INTENSITY = 0.8f;

    private boolean isSelected = false;
    private Image glowEffect;

    public RokPiece(Color color, String imagePath, int size) {
        this.color = color;
        this.size = size;
        loadAndProcessImage(imagePath, size);
        createGlowEffect();
    }

    public Image getImage() {
        return image;
    }

    public Image getGlowEffect() {
        return glowEffect;
    }

    public Color getColor() {
        return color;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isWhite() {
        return color == Color.WHITE;
    }

    public boolean isBlack() {
        return color == Color.BLACK;
    }

    private void loadAndProcessImage(String path, int size) {
        try {
            Image loadedImage = ResourceManager.loadImage(path);
            BufferedImage originalImg = null;

            if (loadedImage != null) {
                originalImg = new BufferedImage(loadedImage.getWidth(null),loadedImage.getHeight(null),BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = originalImg.createGraphics();
                g.drawImage(loadedImage, 0, 0, null);
                g.dispose();
            } else {
                originalImg = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = originalImg.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(color == Color.BLACK ? java.awt.Color.BLACK : java.awt.Color.WHITE);
                g.fillOval(5, 5, 40, 40);
                g.dispose();
            }

            BufferedImage processedImg = new BufferedImage(originalImg.getWidth(),originalImg.getHeight(),BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = processedImg.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.drawImage(originalImg, 0, 0, null);

            if (color == Color.WHITE) {
                for (int y = 0; y < processedImg.getHeight(); y++) {
                    for (int x = 0; x < processedImg.getWidth(); x++) {
                        int rgba = processedImg.getRGB(x, y);
                        int alpha = (rgba >> 24) & 0xff;

                        if (alpha > 0) {
                            int white = (alpha << 24) | 0xffffff;
                            processedImg.setRGB(x, y, white);
                        }
                    }
                }
            }

            g2d.dispose();

            BufferedImage shadowImg = new BufferedImage( originalImg.getWidth(), originalImg.getHeight(), BufferedImage.TYPE_INT_ARGB);

            g2d = shadowImg.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2d.setColor(new java.awt.Color(0, 0, 0, 50));
            g2d.fillOval(3, 3, originalImg.getWidth() - 6, originalImg.getHeight() - 6);

            g2d.drawImage(processedImg, 0, 0, null);
            g2d.dispose();

            this.image = shadowImg.getScaledInstance(size, size, Image.SCALE_SMOOTH);

        } catch (Exception e) {
            System.err.println("ไม่สามารถโหลดรูปภาพได้: " + path);
            e.printStackTrace();
            this.image = null;
        }
    }

    private void createGlowEffect() {
        BufferedImage glowImg = new BufferedImage(size * 2, size * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = glowImg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        java.awt.Color glowColor = color == Color.WHITE ? new java.awt.Color(255, 255, 200) : new java.awt.Color(255, 200, 100);

        for (int i = 0; i < 5; i++) {
            float alpha = GLOW_INTENSITY * (1.0f - (i * 0.2f));
            g2d.setColor(new java.awt.Color( glowColor.getRed() / 255f, glowColor.getGreen() / 255f, glowColor.getBlue() / 255f, alpha));

            int offset = i * 2;
            g2d.fill(new Ellipse2D.Double(offset, offset,glowImg.getWidth() - (offset * 2),glowImg.getHeight() - (offset * 2)));
        }

        g2d.dispose();
        this.glowEffect = glowImg;
    }
}
