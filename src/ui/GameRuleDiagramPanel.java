package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import javax.swing.*;

public class GameRuleDiagramPanel extends JPanel {
    private final String type;
    private final Color lightSquareColor;
    private final Color darkSquareColor;
    private Timer animationTimer;
    private int animationStep = 0;
    private static final int MAX_STEPS = 60;
    private float movePhase = 0;

    public GameRuleDiagramPanel(String type, Color lightSquareColor, Color darkSquareColor) {
        this.type = type;
        this.lightSquareColor = lightSquareColor;
        this.darkSquareColor = darkSquareColor;
        setPreferredSize(new Dimension(250, 200));
        setOpaque(false);

        if (type.equals("capture_animation") || type.equals("vertical_capture") ||
                type.equals("surrounded_capture") || type.equals("movement")) {
            animationTimer = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    animationStep = (animationStep + 1) % MAX_STEPS;
                    movePhase = (float) animationStep / MAX_STEPS;
                    repaint();
                }
            });
            animationTimer.start();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 20;
        int cellSize = size / 8;
        int startX = (getWidth() - size) / 2;
        int startY = (getHeight() - size) / 2;

        switch (type) {
            case "capture":
                drawCaptureExample(g2d, startX, startY, cellSize);
                break;
            case "vertical_capture":
                drawVerticalCaptureExample(g2d, startX, startY, cellSize);
                break;
            case "surrounded_capture":
                drawSurroundedCaptureExample(g2d, startX, startY, cellSize);
                break;
            case "board":
                drawBoardExample(g2d, startX, startY, cellSize);
                break;
            case "movement":
                drawMovementExample(g2d, startX, startY, cellSize);
                break;
            case "time_rule":
                drawTimeRuleExample(g2d, startX, startY, cellSize);
                break;
            case "capture_animation":
                drawCaptureAnimationExample(g2d, startX, startY, cellSize);
                break;
            case "chain_capture":
                drawChainCaptureExample(g2d, startX, startY, cellSize);
                break;
            case "corner_capture":
                drawCornerCaptureExample(g2d, startX, startY, cellSize);
                break;
        }
    }

    private void drawCaptureExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        g2d.setColor(Color.BLACK);
        drawRook(g2d, startX + cellSize + cellSize / 4, startY + cellSize + cellSize / 4, cellSize / 2);
        g2d.setColor(Color.WHITE);
        drawRook(g2d, startX + cellSize / 4, startY + cellSize + cellSize / 4, cellSize / 2);
        drawRook(g2d, startX + 2 * cellSize + cellSize / 4, startY + cellSize + cellSize / 4, cellSize / 2);

        g2d.setColor(new Color(255, 0, 0, 150));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(startX + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2);
        g2d.drawLine(startX + 2 * cellSize + cellSize / 2, startY + cellSize + cellSize / 2, startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Horizontal Capture", startX, startY - 5);
    }

    private void drawVerticalCaptureExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        g2d.setColor(Color.BLACK);
        drawRook(g2d, startX + cellSize + cellSize / 4, startY + cellSize + cellSize / 4, cellSize / 2);
        g2d.setColor(Color.WHITE);
        drawRook(g2d, startX + cellSize + cellSize / 4, startY + cellSize / 4, cellSize / 2);
        drawRook(g2d, startX + cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);

        g2d.setColor(new Color(255, 0, 0, 150));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(startX + cellSize + cellSize / 2, startY + cellSize / 2, startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2);
        g2d.drawLine(startX + cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2, startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Vertical Capture", startX, startY - 5);
    }

    private void drawSurroundedCaptureExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        int middleRow = 1;
        int middleCol = 1;
        g2d.setColor(Color.BLACK);
        drawRook(g2d, startX + middleCol * cellSize + cellSize / 4, startY + middleRow * cellSize + cellSize / 4, cellSize / 2);
        g2d.setColor(Color.WHITE);
        drawRook(g2d, startX + (middleCol - 1) * cellSize + cellSize / 4, startY + middleRow * cellSize + cellSize / 4, cellSize / 2);
        drawRook(g2d, startX + (middleCol + 1) * cellSize + cellSize / 4, startY + middleRow * cellSize + cellSize / 4, cellSize / 2);
        drawRook(g2d, startX + middleCol * cellSize + cellSize / 4, startY + (middleRow - 1) * cellSize + cellSize / 4, cellSize / 2);
        drawRook(g2d, startX + middleCol * cellSize + cellSize / 4, startY + (middleRow + 1) * cellSize + cellSize / 4, cellSize / 2);

        g2d.setColor(new Color(255, 0, 0, 120));
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawOval(startX + middleCol * cellSize + cellSize / 8, startY + middleRow * cellSize + cellSize / 8, cellSize * 3 / 4, cellSize * 3 / 4);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Surrounded Capture (Atari)", startX, startY - 5);
    }

    private void drawBoardExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        for (int c = 0; c < 4; c++) {
            g2d.setColor(Color.BLACK);
            drawRook(g2d, startX + c * cellSize + cellSize / 4, startY + cellSize / 4, cellSize / 2);
            g2d.setColor(Color.WHITE);
            drawRook(g2d, startX + c * cellSize + cellSize / 4, startY + 3 * cellSize + cellSize / 4, cellSize / 2);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Initial Setup", startX, startY - 5);
    }

    private void drawMovementExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        g2d.setColor(Color.WHITE);
        drawRook(g2d, startX + cellSize + cellSize / 4, startY + cellSize + cellSize / 4, cellSize / 2);

        g2d.setColor(new Color(0, 200, 0, 180));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + cellSize / 2, startY + cellSize + cellSize / 2);
        g2d.drawLine(startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + 2 * cellSize + cellSize / 2, startY + cellSize + cellSize / 2);
        g2d.drawLine(startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + cellSize + cellSize / 2, startY + cellSize / 2);
        g2d.drawLine(startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Movement Directions", startX, startY - 5);
    }

    private void drawTimeRuleExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        g2d.setColor(new Color(40, 40, 40, 200));
        g2d.fillRoundRect(startX, startY, cellSize * 5, cellSize * 3, 20, 20);

        g2d.setColor(new Color(70, 70, 70, 200));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawRoundRect(startX + 5, startY + 5, cellSize * 5 - 10, cellSize * 3 - 10, 15, 15);

        g2d.setColor(Color.WHITE);
        int clockX = startX + cellSize / 2;
        int clockY = startY + cellSize / 2;
        int clockSize = cellSize;

        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawOval(clockX, clockY, clockSize, clockSize);

        double angle = movePhase * 2 * Math.PI;
        int handLength = clockSize / 2 - 5;
        int centerX = clockX + clockSize / 2;
        int centerY = clockY + clockSize / 2;
        int handEndX = centerX + (int) (Math.sin(angle) * handLength);
        int handEndY = centerY - (int) (Math.cos(angle) * handLength);

        g2d.setStroke(new BasicStroke(1.5f));
        g2d.setColor(new Color(255, 50, 50));
        g2d.drawLine(centerX, centerY, handEndX, handEndY);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(centerX - 3, centerY - 3, 6, 6);

        int timeDisplay = 10 - (int) (movePhase * 10);
        String timeText = timeDisplay + ":00";

        Font timeFont = new Font("Digital-7", Font.BOLD, 24);
        Font fallbackFont = new Font("Monospaced", Font.BOLD, 24);
        g2d.setFont(g2d.getFont().canDisplayUpTo(timeText) == -1 ? timeFont : fallbackFont);

        FontMetrics fm = g2d.getFontMetrics();
        int timeWidth = fm.stringWidth(timeText);

        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRect(clockX + clockSize + 20, clockY + 15, cellSize * 2, 40);
        g2d.setColor(new Color(100, 100, 100));
        g2d.drawRect(clockX + clockSize + 20, clockY + 15, cellSize * 2, 40);

        if (timeDisplay <= 1) g2d.setColor(new Color(255, 50, 50));
        else if (timeDisplay <= 3) g2d.setColor(new Color(255, 150, 0));
        else g2d.setColor(new Color(50, 255, 50));
        g2d.drawString(timeText, clockX + clockSize + 20 + (cellSize * 2 - timeWidth) / 2, clockY + 15 + 30);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Time Limit: 10 Minutes", startX, startY - 5);
    }

    private void drawCaptureAnimationExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        int phase = animationStep % 60;

        if (phase < 20) {
            g2d.setColor(new Color(0, 255, 0, 100));
            g2d.fillRect(startX + 2 * cellSize, startY + 1 * cellSize, cellSize, cellSize);

            g2d.setColor(Color.WHITE);
            drawRook(g2d, startX + 2 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
            g2d.setColor(Color.BLACK);
            drawRook(g2d, startX + 1 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
            drawRook(g2d, startX + 3 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);

            drawPath(g2d, startX + 2 * cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2,
                    startX + 2 * cellSize + cellSize / 2, startY + 1 * cellSize + cellSize / 2, phase / 20.0f);
        } else if (phase < 40) {
            float moveProgress = (phase - 20) / 20.0f;
            int yPos = (int) (startY + (2 * cellSize + (1 * cellSize - 2 * cellSize) * moveProgress) + cellSize / 4);

            g2d.setColor(Color.BLACK);
            drawRook(g2d, startX + 1 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
            drawRook(g2d, startX + 3 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);

            g2d.setColor(Color.WHITE);
            drawRook(g2d, startX + 2 * cellSize + cellSize / 4, yPos, cellSize / 2);

            g2d.setColor(new Color(255, 0, 0, 100));
            g2d.fillRect(startX + 1 * cellSize, startY + 2 * cellSize, cellSize, cellSize);
            g2d.fillRect(startX + 3 * cellSize, startY + 2 * cellSize, cellSize, cellSize);
        } else {
            g2d.setColor(Color.WHITE);
            drawRook(g2d, startX + 2 * cellSize + cellSize / 4, startY + 1 * cellSize + cellSize / 4, cellSize / 2);

            float captureProgress = (phase - 40) / 20.0f;
            int alpha = (int) (255 * (1 - captureProgress));
            if (alpha > 0) {
                g2d.setColor(new Color(0, 0, 0, alpha));
                drawRook(g2d, startX + 1 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
                drawRook(g2d, startX + 3 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);

                g2d.setColor(new Color(255, 0, 0, alpha));
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawLine(startX + 1 * cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2, startX + 2 * cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2);
                g2d.drawLine(startX + 3 * cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2, startX + 2 * cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2);
            }
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Horizontal Capture Animation", startX, startY - 5);
    }

    private void drawChainCaptureExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        int phase = animationStep % 60;

        if (phase < 20) {
            g2d.setColor(new Color(0, 255, 0, 100));
            g2d.fillRect(startX + 2 * cellSize, startY + 1 * cellSize, cellSize, cellSize);

            g2d.setColor(Color.WHITE);
            drawRook(g2d, startX + 2 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
            g2d.setColor(Color.BLACK);
            drawRook(g2d, startX + 1 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
            drawRook(g2d, startX + 2 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
            drawRook(g2d, startX + 3 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);

            drawPath(g2d, startX + 2 * cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2, startX + 2 * cellSize + cellSize / 2, startY + 1 * cellSize + cellSize / 2, phase / 20.0f);
        } else if (phase < 40) {
            float moveProgress = (phase - 20) / 20.0f;
            int yPos = (int) (startY + (2 * cellSize + (1 * cellSize - 2 * cellSize) * moveProgress) + cellSize / 4);

            g2d.setColor(Color.BLACK);
            drawRook(g2d, startX + 1 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
            drawRook(g2d, startX + 2 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
            drawRook(g2d, startX + 3 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);

            g2d.setColor(Color.WHITE);
            drawRook(g2d, startX + 2 * cellSize + cellSize / 4, yPos, cellSize / 2);

            g2d.setColor(new Color(255, 0, 0, 100));
            g2d.fillRect(startX + 1 * cellSize, startY + 2 * cellSize, cellSize, cellSize);
            g2d.fillRect(startX + 2 * cellSize, startY + 2 * cellSize, cellSize, cellSize);
            g2d.fillRect(startX + 3 * cellSize, startY + 2 * cellSize, cellSize, cellSize);
        } else {
            g2d.setColor(Color.WHITE);
            drawRook(g2d, startX + 2 * cellSize + cellSize / 4, startY + 1 * cellSize + cellSize / 4, cellSize / 2);

            float captureProgress = (phase - 40) / 20.0f;
            int alpha = (int) (255 * (1 - captureProgress));
            if (alpha > 0) {
                g2d.setColor(new Color(0, 0, 0, alpha));
                drawRook(g2d, startX + 1 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
                drawRook(g2d, startX + 2 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);
                drawRook(g2d, startX + 3 * cellSize + cellSize / 4, startY + 2 * cellSize + cellSize / 4, cellSize / 2);

                g2d.setColor(new Color(255, 0, 0, alpha));
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawLine(startX + 1 * cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2, startX + 2 * cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2);
                g2d.drawLine(startX + 3 * cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2, startX + 2 * cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2);
            }
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Chain Capture Animation", startX, startY - 5);
    }

    private void drawCornerCaptureExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        int phase = animationStep % 60;

        if (phase < 20) {
            g2d.setColor(new Color(0, 255, 0, 100));
            g2d.fillRect(startX + 1 * cellSize, startY + 1 * cellSize, cellSize, cellSize);

            g2d.setColor(Color.BLACK);
            drawRook(g2d, startX + 0 * cellSize + cellSize / 4, startY + 0 * cellSize + cellSize / 4, cellSize / 2);
            g2d.setColor(Color.WHITE);
            drawRook(g2d, startX + 0 * cellSize + cellSize / 4, startY + 1 * cellSize + cellSize / 4, cellSize / 2);
            drawRook(g2d, startX + 1 * cellSize + cellSize / 4, startY + 0 * cellSize + cellSize / 4, cellSize / 2);

            drawPath(g2d, startX + 0 * cellSize + cellSize / 2, startY + 0 * cellSize + cellSize / 2, startX + 1 * cellSize + cellSize / 2, startY + 0 * cellSize + cellSize / 2, phase / 20.0f);
        } else if (phase < 40) {
            float moveProgress = (phase - 20) / 20.0f;
            int xPos = (int) (startX + (0 * cellSize + (1 * cellSize - 0 * cellSize) * moveProgress) + cellSize / 4);

            g2d.setColor(Color.BLACK);
            drawRook(g2d, startX + 0 * cellSize + cellSize / 4, startY + 0 * cellSize + cellSize / 4, cellSize / 2);

            g2d.setColor(Color.WHITE);
            drawRook(g2d, xPos, startY + 0 * cellSize + cellSize / 4, cellSize / 2);
            drawRook(g2d, startX + 1 * cellSize + cellSize / 4, startY + 1 * cellSize + cellSize / 4, cellSize / 2);

            g2d.setColor(new Color(255, 0, 0, 100));
            g2d.fillRect(startX + 0 * cellSize, startY + 0 * cellSize, cellSize, cellSize);
        } else {
            g2d.setColor(Color.WHITE);
            drawRook(g2d, startX + 1 * cellSize + cellSize / 4, startY + 0 * cellSize + cellSize / 4, cellSize / 2);
            drawRook(g2d, startX + 0 * cellSize + cellSize / 4, startY + 1 * cellSize + cellSize / 4, cellSize / 2);

            float captureProgress = (phase - 40) / 20.0f;
            int alpha = (int) (255 * (1 - captureProgress));
            if (alpha > 0) {
                g2d.setColor(new Color(0, 0, 0, alpha));
                drawRook(g2d, startX + 0 * cellSize + cellSize / 4, startY + 0 * cellSize + cellSize / 4, cellSize / 2);

                g2d.setColor(new Color(255, 0, 0, alpha));
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawLine(startX + 0 * cellSize + cellSize / 2, startY + 0 * cellSize + cellSize / 2, startX + 1 * cellSize + cellSize / 2, startY + 0 * cellSize + cellSize / 2);
                g2d.drawLine(startX + 0 * cellSize + cellSize / 2, startY + 0 * cellSize + cellSize / 2, startX + 0 * cellSize + cellSize / 2, startY + 1 * cellSize + cellSize / 2);
            }
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Corner Capture Animation", startX, startY - 5);
    }

    private void drawRook(Graphics2D g2d, int x, int y, int size) {
        int baseWidth = size / 2;
        int topWidth = size / 3;
        int height = size;
        int battlementHeight = size / 6;

        Polygon rook = new Polygon();
        rook.addPoint(x + (size - baseWidth) / 2, y + height);
        rook.addPoint(x + (size - baseWidth) / 2, y + battlementHeight);
        rook.addPoint(x + (size - topWidth) / 2, y + battlementHeight);
        rook.addPoint(x + (size - topWidth) / 2, y + battlementHeight / 2);
        rook.addPoint(x + (size - topWidth) / 2 + topWidth / 3, y + battlementHeight / 2);
        rook.addPoint(x + (size - topWidth) / 2 + topWidth / 3, y);
        rook.addPoint(x + (size + topWidth) / 2 - topWidth / 3, y);
        rook.addPoint(x + (size + topWidth) / 2 - topWidth / 3, y + battlementHeight / 2);
        rook.addPoint(x + (size + topWidth) / 2, y + battlementHeight / 2);
        rook.addPoint(x + (size + topWidth) / 2, y + battlementHeight);
        rook.addPoint(x + (size + baseWidth) / 2, y + battlementHeight);
        rook.addPoint(x + (size + baseWidth) / 2, y + height);
        g2d.fillPolygon(rook);

        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawPolygon(rook);
    }

    private void drawPath(Graphics2D g2d, int startX, int startY, int endX, int endY, float progress) {
        g2d.setColor(new Color(0, 200, 0, 200));
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int x = (int) (startX + (endX - startX) * progress);
        int y = (int) (startY + (endY - startY) * progress);
        g2d.drawLine(startX, startY, x, y);

        if (progress > 0.8) {
            g2d.drawLine(x, y, x - 5, y - 7);
            g2d.drawLine(x, y, x + 5, y - 7);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (animationTimer != null && !animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }
}