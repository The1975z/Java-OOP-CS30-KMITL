package ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class MoveHistoryPanel extends JPanel {

    // สีที่ใช้ในการตกแต่ง
    private final Color PANEL_BACKGROUND = new Color(30, 30, 40);
    private final Color HEADER_COLOR = new Color(40, 40, 60);
    private final Color MOVE_LIST_BG = new Color(25, 25, 35);
    private final Color WHITE_MOVE_COLOR = new Color(230, 230, 230);
    private final Color BLACK_MOVE_COLOR = new Color(70, 70, 70);
    private final Color TIME_BAR_WHITE = new Color(120, 180, 250);
    private final Color TIME_BAR_BLACK = new Color(250, 180, 120);
    private final Color BUTTON_COLOR = new Color(60, 60, 80);
    private final Color BUTTON_HOVER = new Color(80, 80, 100);

    // ข้อมูลประวัติการเดิน
    private List<MoveRecord> moveHistory = new ArrayList<>();
    private JPanel moveListPanel;
    private JScrollPane scrollPane;
    private JButton toggleButton;
    private boolean isExpanded = true;
    private int maxTimeInSeconds = 30; // เวลาสูงสุดที่แสดงในแถบเวลา (ปรับตามเกม)

    // คลาสเก็บข้อมูลการเดิน
    private class MoveRecord {

        int moveNumber;
        String whiteMove;
        String blackMove;
        int whiteTimeSeconds;
        int blackTimeSeconds;

        public MoveRecord(int moveNumber, String whiteMove, String blackMove,
                int whiteTimeSeconds, int blackTimeSeconds) {
            this.moveNumber = moveNumber;
            this.whiteMove = whiteMove;
            this.blackMove = blackMove;
            this.whiteTimeSeconds = whiteTimeSeconds;
            this.blackTimeSeconds = blackTimeSeconds;
        }
    }

    public MoveHistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(PANEL_BACKGROUND);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // สร้างส่วนหัว
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // สร้างตารางประวัติการเดิน
        moveListPanel = new JPanel();
        moveListPanel.setLayout(new BoxLayout(moveListPanel, BoxLayout.Y_AXIS));
        moveListPanel.setBackground(MOVE_LIST_BG);

        // สร้าง scrollPane
        scrollPane = new JScrollPane(moveListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(MOVE_LIST_BG);

        // เพิ่ม scrollPane
        add(scrollPane, BorderLayout.CENTER);

        // เตรียมข้อมูลตัวอย่าง
        addSampleMoves();
        updateMoveList();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        // สร้างปุ่มเปิด/ปิด
        toggleButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // วาดพื้นหลังปุ่ม
                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER);
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // วาดไอคอนลูกศร
                g2d.setColor(Color.WHITE);
                int arrowSize = 8;
                int x = getWidth() / 2;
                int y = getHeight() / 2;

                if (isExpanded) {
                    // ลูกศรลง (▼)
                    int[] xPoints = {x - arrowSize, x + arrowSize, x};
                    int[] yPoints = {y - arrowSize / 2, y - arrowSize / 2, y + arrowSize / 2};
                    g2d.fillPolygon(xPoints, yPoints, 3);
                } else {
                    // ลูกศรขึ้น (▲)
                    int[] xPoints = {x - arrowSize, x + arrowSize, x};
                    int[] yPoints = {y + arrowSize / 2, y + arrowSize / 2, y - arrowSize / 2};
                    g2d.fillPolygon(xPoints, yPoints, 3);
                }
            }
        };

        toggleButton.setPreferredSize(new Dimension(30, 25));
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        toggleButton.addActionListener(e -> toggleMoveList());

        // สร้างชื่อหัวข้อ
        JLabel titleLabel = new JLabel("ประวัติการเดิน");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(toggleButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void toggleMoveList() {
        isExpanded = !isExpanded;
        scrollPane.setVisible(isExpanded);
        toggleButton.repaint();

        // ปรับขนาดพาเนล
        revalidate();
    }

    public void addMove(String whiteMove, String blackMove, int whiteTimeSeconds, int blackTimeSeconds) {
        int moveNumber = moveHistory.size() + 1;
        moveHistory.add(new MoveRecord(moveNumber, whiteMove, blackMove, whiteTimeSeconds, blackTimeSeconds));
        updateMoveList();
    }

    private void updateMoveList() {
        moveListPanel.removeAll();

        // เพิ่มส่วนหัวของคอลัมน์
        JPanel headerRow = createColumnHeaders();
        moveListPanel.add(headerRow);

        // เพิ่มเส้นแบ่ง
        moveListPanel.add(createSeparator());

        // เพิ่มแต่ละการเดิน
        for (MoveRecord move : moveHistory) {
            JPanel moveRow = createMoveRow(move);
            moveListPanel.add(moveRow);
        }

        moveListPanel.revalidate();
        moveListPanel.repaint();

        // เลื่อนไปที่การเดินล่าสุด
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private JPanel createColumnHeaders() {
        JPanel headerRow = new JPanel(new GridLayout(1, 4, 5, 0));
        headerRow.setBackground(HEADER_COLOR);
        headerRow.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel numberLabel = new JLabel("#");
        JLabel whiteLabel = new JLabel("♔ ขาว");
        JLabel blackLabel = new JLabel("♚ ดำ");
        JLabel timeLabel = new JLabel("⏱ เวลา");

        Font headerFont = new Font("Arial", Font.BOLD, 14);
        numberLabel.setFont(headerFont);
        whiteLabel.setFont(headerFont);
        blackLabel.setFont(headerFont);
        timeLabel.setFont(headerFont);

        numberLabel.setForeground(Color.WHITE);
        whiteLabel.setForeground(Color.WHITE);
        blackLabel.setForeground(Color.WHITE);
        timeLabel.setForeground(Color.WHITE);

        headerRow.add(numberLabel);
        headerRow.add(whiteLabel);
        headerRow.add(blackLabel);
        headerRow.add(timeLabel);

        return headerRow;
    }

    private JPanel createMoveRow(MoveRecord move) {
        JPanel moveRow = new JPanel(new GridLayout(1, 4, 5, 0));
        moveRow.setBackground(move.moveNumber % 2 == 0 ? MOVE_LIST_BG.brighter() : MOVE_LIST_BG);
        moveRow.setBorder(new EmptyBorder(10, 10, 10, 10));

        // คอลัมน์ลำดับ
        JLabel numberLabel = new JLabel(String.valueOf(move.moveNumber));
        numberLabel.setForeground(Color.WHITE);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // คอลัมน์ขาว
        JPanel whitePanel = new JPanel(new BorderLayout());
        whitePanel.setBackground(moveRow.getBackground());
        JLabel whiteLabel = new JLabel(move.whiteMove);
        whiteLabel.setForeground(WHITE_MOVE_COLOR);
        whiteLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        whitePanel.add(whiteLabel, BorderLayout.CENTER);

        // คอลัมน์ดำ
        JPanel blackPanel = new JPanel(new BorderLayout());
        blackPanel.setBackground(moveRow.getBackground());
        JLabel blackLabel = new JLabel(move.blackMove != null ? move.blackMove : "");
        blackLabel.setForeground(WHITE_MOVE_COLOR); // ใช้สีขาวเพื่อให้อ่านง่าย
        blackLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        blackPanel.add(blackLabel, BorderLayout.CENTER);

        // คอลัมน์เวลา
        JPanel timePanel = new JPanel(new BorderLayout(0, 2));
        timePanel.setBackground(moveRow.getBackground());

        // แถบเวลาของฝ่ายขาว
        JPanel whiteTimeBar = createTimeBar(move.whiteTimeSeconds, TIME_BAR_WHITE);

        // แถบเวลาของฝ่ายดำ
        JPanel blackTimeBar = createTimeBar(move.blackTimeSeconds, TIME_BAR_BLACK);

        timePanel.add(whiteTimeBar, BorderLayout.NORTH);
        timePanel.add(blackTimeBar, BorderLayout.SOUTH);

        // เพิ่มทุกอย่างเข้าแถว
        moveRow.add(numberLabel);
        moveRow.add(whitePanel);
        moveRow.add(blackPanel);
        moveRow.add(timePanel);

        return moveRow;
    }

    private JPanel createTimeBar(int seconds, Color barColor) {
        JPanel barPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // คำนวณความยาวของแถบตามเวลา
                int width = getWidth();
                float percentage = Math.min(1.0f, (float) seconds / maxTimeInSeconds);
                int barWidth = (int) (width * percentage);

                // วาดแถบ
                g2d.setColor(barColor);
                g2d.fillRoundRect(0, 0, barWidth, getHeight(), 4, 4);

                // แสดงเวลา
                String timeText = String.format("%d:%02d", seconds / 60, seconds % 60);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(timeText);
                int textHeight = fm.getHeight();

                g2d.setColor(Color.WHITE);
                g2d.drawString(timeText,
                        (width - textWidth) / 2,
                        (getHeight() + textHeight) / 2 - fm.getDescent());
            }
        };

        barPanel.setPreferredSize(new Dimension(0, 20));
        barPanel.setOpaque(false);
        return barPanel;
    }

    private JComponent createSeparator() {
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(80, 80, 100));
        separator.setBackground(MOVE_LIST_BG);
        return separator;
    }

    // เพิ่มข้อมูลตัวอย่าง
    private void addSampleMoves() {
        addMove("e2-e4", "e7-e5", 5, 8);
        addMove("Nf3", "Nc6", 3, 5);
        addMove("Bb5", "a6", 7, 2);
        addMove("Ba4", "Nf6", 4, 6);
        addMove("O-O", "Be7", 2, 4);
        addMove("Re1", "b5", 6, 3);
        addMove("Bb3", "d6", 3, 5);
        addMove("c3", "O-O", 2, 7);
    }

    public void addGameMove(String notation, boolean isWhiteMove, int timeSpentSeconds) {
        if (moveHistory.isEmpty() || (moveHistory.get(moveHistory.size() - 1).blackMove != null && isWhiteMove)) {
            // เริ่มการเดินใหม่
            if (isWhiteMove) {
                addMove(notation, "", timeSpentSeconds, 0);
            } else {
                addMove("", notation, 0, timeSpentSeconds);
            }
        } else {
            // อัพเดทการเดินล่าสุด
            MoveRecord lastMove = moveHistory.get(moveHistory.size() - 1);
            if (isWhiteMove) {
                lastMove.whiteMove = notation;
                lastMove.whiteTimeSeconds = timeSpentSeconds;
            } else {
                lastMove.blackMove = notation;
                lastMove.blackTimeSeconds = timeSpentSeconds;
            }
            updateMoveList();
        }
    }

    private String formatTime(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }
}
