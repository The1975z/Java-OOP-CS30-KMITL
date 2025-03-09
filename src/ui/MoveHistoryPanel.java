package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MoveHistoryPanel extends JPanel {

    private final Color PANEL_BACKGROUND = new Color(30, 30, 40);
    private final Color HEADER_COLOR = new Color(40, 40, 60);
    private final Color MOVE_LIST_BG = new Color(25, 25, 35);
    private final Color WHITE_MOVE_COLOR = new Color(230, 230, 230);
    private final Color BLACK_MOVE_COLOR = new Color(70, 70, 70);
    private final Color BUTTON_COLOR = new Color(60, 60, 80);
    private final Color BUTTON_HOVER = new Color(80, 80, 100);
    private final Color HOVER_CARD = new Color(50, 50, 70, 180);

    private List<MoveRecord> moveHistory = new ArrayList<>();
    private JPanel moveListPanel;
    private JScrollPane scrollPane;
    private JButton toggleButton;
    private boolean isExpanded = true;
    
    // เพิ่มตัวแปรเก็บเวลาของตาก่อนหน้า กำหนดเป็น -1 เพื่อบ่งชี้ว่ายังไม่มีการเดิน
    private int previousWhiteTimeSeconds = -1;
    private int previousBlackTimeSeconds = -1;

    private class MoveRecord {
        int moveNumber;
        String whiteMoveFrom;
        String whiteMoveTo;
        String blackMoveFrom;
        String blackMoveTo;
        int whiteTimeSeconds;
        int blackTimeSeconds;
        int whiteTimeTaken; // เพิ่มตัวแปรเก็บเวลาที่ใช้
        int blackTimeTaken; // เพิ่มตัวแปรเก็บเวลาที่ใช้

        public MoveRecord(int moveNumber, String whiteMoveFrom, String whiteMoveTo, String blackMoveFrom, String blackMoveTo, 
                         int whiteTimeSeconds, int blackTimeSeconds, int whiteTimeTaken, int blackTimeTaken) {
            this.moveNumber = moveNumber;
            this.whiteMoveFrom = whiteMoveFrom;
            this.whiteMoveTo = whiteMoveTo;
            this.blackMoveFrom = blackMoveFrom;
            this.blackMoveTo = blackMoveTo;
            this.whiteTimeSeconds = whiteTimeSeconds;
            this.blackTimeSeconds = blackTimeSeconds;
            this.whiteTimeTaken = whiteTimeTaken;
            this.blackTimeTaken = blackTimeTaken;
        }
    }

    public MoveHistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(PANEL_BACKGROUND);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        moveListPanel = new JPanel();
        moveListPanel.setLayout(new BoxLayout(moveListPanel, BoxLayout.Y_AXIS));
        moveListPanel.setBackground(MOVE_LIST_BG);

        scrollPane = new JScrollPane(moveListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(MOVE_LIST_BG);
        add(scrollPane, BorderLayout.CENTER);

        updateMoveList();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        toggleButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER);
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(Color.WHITE);
                int arrowSize = 8;
                int x = getWidth() / 2;
                int y = getHeight() / 2;
                int[] xPoints = {x - arrowSize / 2, x + arrowSize / 2, x};
                int[] yPoints = isExpanded ? new int[]{y + arrowSize / 2, y + arrowSize / 2, y - arrowSize / 2}
                                          : new int[]{y - arrowSize / 2, y - arrowSize / 2, y + arrowSize / 2};
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
        };

        toggleButton.setPreferredSize(new Dimension(30, 25));
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.addActionListener(e -> toggleMoveList());

        JLabel titleLabel = new JLabel("Move History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(toggleButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void toggleMoveList() {
        isExpanded = !isExpanded;
        scrollPane.setVisible(isExpanded);
        if (!isExpanded) {
            scrollPane.setPreferredSize(new Dimension(0, 0));
        } else {
            scrollPane.setPreferredSize(null);
        }
        revalidate();
        repaint();
        toggleButton.repaint();
    }

    public void addMove(int whiteFromRow, int whiteFromCol, int whiteToRow, int whiteToCol,
                       int blackFromRow, int blackFromCol, int blackToRow, int blackToCol,
                       int whiteTimeSeconds, int blackTimeSeconds) {
        int moveNumber = moveHistory.size() + 1;
        String whiteMoveFrom = convertToNotation(whiteFromRow, whiteFromCol);
        String whiteMoveTo = convertToNotation(whiteToRow, whiteToCol);
        String blackMoveFrom = convertToNotation(blackFromRow, blackFromCol);
        String blackMoveTo = convertToNotation(blackToRow, blackToCol);
        
        // คำนวณเวลาที่ใช้ในตานี้
        int whiteTimeTaken, blackTimeTaken;
        
        if (previousWhiteTimeSeconds == -1) {
            // ตาแรก, ใช้เวลาทั้งหมด
            whiteTimeTaken = whiteTimeSeconds;
            blackTimeTaken = blackTimeSeconds;
        } else {
            // ตาถัดไป คำนวณความแตกต่าง
            whiteTimeTaken = Math.max(0, whiteTimeSeconds - previousWhiteTimeSeconds);
            blackTimeTaken = Math.max(0, blackTimeSeconds - previousBlackTimeSeconds);
        }
        
        // ถ้าตานี้ไม่มีการเดินของฝ่ายหนึ่ง ให้เวลาที่ใช้เป็น 0
        if (whiteMoveFrom.equals("-") || whiteMoveTo.equals("-")) {
            whiteTimeTaken = 0;
        }
        if (blackMoveFrom.equals("-") || blackMoveTo.equals("-")) {
            blackTimeTaken = 0;
        }
        
        // พิมพ์ค่าที่คำนวณได้เพื่อดีบัก
        System.out.println("Move " + moveNumber + ": White time taken = " + whiteTimeTaken + 
                         ", Black time taken = " + blackTimeTaken);
        
        // บันทึกเวลาปัจจุบันเพื่อใช้ในครั้งถัดไป
        previousWhiteTimeSeconds = whiteTimeSeconds;
        previousBlackTimeSeconds = blackTimeSeconds;
        
        moveHistory.add(new MoveRecord(moveNumber, whiteMoveFrom, whiteMoveTo, blackMoveFrom, blackMoveTo,
                                      whiteTimeSeconds, blackTimeSeconds, whiteTimeTaken, blackTimeTaken));
        updateMoveList();
    }

    private String convertToNotation(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) return "-";
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return file + Integer.toString(rank);
    }

    private void updateMoveList() {
        moveListPanel.removeAll();

        JPanel headerRow = createColumnHeaders();
        moveListPanel.add(headerRow);

        for (MoveRecord move : moveHistory) {
            JPanel moveRow = createMoveRow(move);
            moveListPanel.add(moveRow);
        }

        moveListPanel.revalidate();
        moveListPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private JPanel createColumnHeaders() {
        JPanel headerRow = new JPanel(new GridLayout(1, 5, 10, 0));
        headerRow.setBackground(HEADER_COLOR);
        headerRow.setBorder(new EmptyBorder(10, 15, 10, 15));

        // เปลี่ยนหัวตารางให้แสดงเวลาที่ใช้แทน
        String[] headers = {"#", "White", "Black", "W Time Used", "B Time Used"};
        Font headerFont = new Font("Arial", Font.BOLD, 14);

        for (String header : headers) {
            JLabel label = new JLabel(header);
            label.setFont(headerFont);
            label.setForeground(Color.WHITE);
            headerRow.add(label);
        }

        return headerRow;
    }

    private JPanel createMoveRow(MoveRecord move) {
        JPanel moveRow = new JPanel(new GridLayout(1, 5, 10, 0));
        moveRow.setBackground(move.moveNumber % 2 == 0 ? MOVE_LIST_BG.brighter() : MOVE_LIST_BG);
        moveRow.setBorder(new EmptyBorder(5, 10, 5, 10));
        moveRow.setCursor(new Cursor(Cursor.HAND_CURSOR));

        moveRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                moveRow.setBackground(HOVER_CARD);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                moveRow.setBackground(move.moveNumber % 2 == 0 ? MOVE_LIST_BG.brighter() : MOVE_LIST_BG);
            }
        });

        JLabel numberLabel = new JLabel(String.valueOf(move.moveNumber));
        numberLabel.setForeground(Color.WHITE);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel whiteMoveLabel = new JLabel(move.whiteMoveFrom + " → " + move.whiteMoveTo);
        whiteMoveLabel.setForeground(WHITE_MOVE_COLOR);
        whiteMoveLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel blackMoveLabel = new JLabel(move.blackMoveFrom + " → " + move.blackMoveTo);
        blackMoveLabel.setForeground(BLACK_MOVE_COLOR);
        blackMoveLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // แสดงเวลาที่ใช้ในตานี้แทนเวลารวม
        String whiteTimeUsed;
        if (move.whiteTimeTaken < 60) {
            // ถ้าน้อยกว่า 1 นาที แสดงเป็นวินาทีเท่านั้น
            whiteTimeUsed = move.whiteTimeTaken + "s";
        } else {
            whiteTimeUsed = String.format("%d:%02d", move.whiteTimeTaken / 60, move.whiteTimeTaken % 60);
        }
        
        JLabel whiteTimeLabel = new JLabel(whiteTimeUsed);
        whiteTimeLabel.setForeground(Color.WHITE);
        whiteTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        String blackTimeUsed;
        if (move.blackTimeTaken < 60) {
            // ถ้าน้อยกว่า 1 นาที แสดงเป็นวินาทีเท่านั้น
            blackTimeUsed = move.blackTimeTaken + "s";
        } else {
            blackTimeUsed = String.format("%d:%02d", move.blackTimeTaken / 60, move.blackTimeTaken % 60);
        }
        
        JLabel blackTimeLabel = new JLabel(blackTimeUsed);
        blackTimeLabel.setForeground(Color.WHITE);
        blackTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        moveRow.add(numberLabel);
        moveRow.add(whiteMoveLabel);
        moveRow.add(blackMoveLabel);
        moveRow.add(whiteTimeLabel);
        moveRow.add(blackTimeLabel);

        return moveRow;
    }

    public void addGameMove(int whiteFromRow, int whiteFromCol, int whiteToRow, int whiteToCol, 
                           int blackFromRow, int blackFromCol, int blackToRow, int blackToCol, 
                           int whiteTimeSeconds, int blackTimeSeconds) {
        addMove(whiteFromRow, whiteFromCol, whiteToRow, whiteToCol, blackFromRow, blackFromCol, 
               blackToRow, blackToCol, whiteTimeSeconds, blackTimeSeconds);
    }

    public List<MoveRecord> getMoveHistory() {
        return this.moveHistory;
    }
    
    public String getLastWhiteMoveFrom() {
        if (moveHistory.isEmpty()) return "-";
        return this.moveHistory.get(moveHistory.size() - 1).whiteMoveFrom;
    }

    public String getLastWhiteMoveTo() {
        if (moveHistory.isEmpty()) return "-";
        return this.moveHistory.get(moveHistory.size() - 1).whiteMoveTo;
    }

    public String getLastBlackMoveFrom() {
        if (moveHistory.isEmpty()) return "-";
        return this.moveHistory.get(moveHistory.size() - 1).blackMoveFrom;
    }

    public String getLastBlackMoveTo() {
        if (moveHistory.isEmpty()) return "-";
        return this.moveHistory.get(moveHistory.size() - 1).blackMoveTo;
    }

    public int getLastWhiteTimeSeconds() {
        if (moveHistory.isEmpty()) return 0;
        return this.moveHistory.get(moveHistory.size() - 1).whiteTimeSeconds;
    }

    public int getLastBlackTimeSeconds() {
        if (moveHistory.isEmpty()) return 0;
        return this.moveHistory.get(moveHistory.size() - 1).blackTimeSeconds;
    }
    
    // เพิ่มเมธอดใหม่เพื่อเข้าถึงเวลาที่ใช้ในตาล่าสุด
    public int getLastWhiteTimeTaken() {
        if (moveHistory.isEmpty()) return 0;
        return this.moveHistory.get(moveHistory.size() - 1).whiteTimeTaken;
    }

    public int getLastBlackTimeTaken() {
        if (moveHistory.isEmpty()) return 0;
        return this.moveHistory.get(moveHistory.size() - 1).blackTimeTaken;
    }
}