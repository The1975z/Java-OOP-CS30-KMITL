package ui;

import java.awt.*;
import javax.swing.*;

public class GameRuleDiagramPanel extends JPanel {
    private final String type;
    private final Color lightSquareColor;
    private final Color darkSquareColor;
    
    public GameRuleDiagramPanel(String type, Color lightSquareColor, Color darkSquareColor) {
        this.type = type;
        this.lightSquareColor = lightSquareColor;
        this.darkSquareColor = darkSquareColor;
        setPreferredSize(new Dimension(200, 200));
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 20;
        int cellSize = size / 8;
        int startX = (getWidth() - size) / 2;
        int startY = (getHeight() - size) / 2;

        if (type.equals("capture")) {
            drawCaptureExample(g2d, startX, startY, cellSize);
        } else if (type.equals("board")) {
            drawBoardExample(g2d, startX, startY, cellSize);
        } else if (type.equals("movement")) {
            drawMovementExample(g2d, startX, startY, cellSize);
        }
    }
    
    // วาดตัวอย่างการกินหมาก
    private void drawCaptureExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        // วาดพื้นหลังตาราง 3x3
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);

                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        // วาดหมากดำตรงกลาง (ตัวที่จะถูกกิน)
        g2d.setColor(Color.BLACK);
        g2d.fillOval(startX + cellSize + cellSize / 4, startY + cellSize + cellSize / 4, cellSize / 2, cellSize / 2);

        // วาดหมากขาวซ้าย-ขวา (ที่จับหนีบ)
        g2d.setColor(Color.WHITE);
        g2d.fillOval(startX + cellSize / 4, startY + cellSize + cellSize / 4, cellSize / 2, cellSize / 2);
        g2d.fillOval(startX + 2 * cellSize + cellSize / 4, startY + cellSize + cellSize / 4, cellSize / 2, cellSize / 2);

        // วาดลูกศรแสดงการกิน
        g2d.setColor(new Color(255, 0, 0, 150));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(startX + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2);
        g2d.drawLine(startX + 2 * cellSize + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2);

        // ข้อความอธิบาย
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Capture Example", startX, startY - 5);
    }

    // วาดตัวอย่างกระดานเริ่มต้น
    private void drawBoardExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        // วาดพื้นหลังกระดานขนาดเล็ก
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);

                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        // วาดหมากเริ่มต้น (ดำด้านบน ขาวด้านล่าง)
        for (int c = 0; c < 4; c++) {
            // หมากดำแถวบน
            g2d.setColor(Color.BLACK);
            g2d.fillOval(startX + c * cellSize + cellSize / 4, startY + cellSize / 4, cellSize / 2, cellSize / 2);

            // หมากขาวแถวล่าง
            g2d.setColor(Color.WHITE);
            g2d.fillOval(startX + c * cellSize + cellSize / 4, startY + 3 * cellSize + cellSize / 4, cellSize / 2, cellSize / 2);
        }

        // ข้อความอธิบาย
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Initial Setup", startX, startY - 5);
    }

    // วาดตัวอย่างการเคลื่อนที่
    private void drawMovementExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        // วาดพื้นหลังตาราง 3x3
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? lightSquareColor : darkSquareColor);
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);

                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        // วาดหมากขาวตรงกลาง
        g2d.setColor(Color.WHITE);
        g2d.fillOval(startX + cellSize + cellSize / 4, startY + cellSize + cellSize / 4, cellSize / 2, cellSize / 2);

        // วาดลูกศรแสดงทิศทางการเคลื่อนที่
        g2d.setColor(new Color(0, 200, 0, 180));
        g2d.setStroke(new BasicStroke(2.0f));

        // ลูกศรซ้าย
        g2d.drawLine(startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + cellSize / 2, startY + cellSize + cellSize / 2);

        // ลูกศรขวา
        g2d.drawLine(startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + 2 * cellSize + cellSize / 2, startY + cellSize + cellSize / 2);

        // ลูกศรบน
        g2d.drawLine(startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + cellSize + cellSize / 2, startY + cellSize / 2);

        // ลูกศรล่าง
        g2d.drawLine(startX + cellSize + cellSize / 2, startY + cellSize + cellSize / 2,
                startX + cellSize + cellSize / 2, startY + 2 * cellSize + cellSize / 2);

        // ข้อความอธิบาย
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Movement Directions", startX, startY - 5);
    }
}
