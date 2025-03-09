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
    
    // เพิ่มตัวแปรเก็บเวลาเริ่มต้นของเกม
    private int gameStartWhiteTimeSeconds = -1;
    private int gameStartBlackTimeSeconds = -1;

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

    /**
     * เริ่มต้นการเก็บข้อมูลเวลาสำหรับเกมใหม่
     */
    public void initializeTimers(int initialWhiteUsedTime, int initialBlackUsedTime) {
        previousWhiteTimeSeconds = initialWhiteUsedTime;
        previousBlackTimeSeconds = initialBlackUsedTime;
        System.out.println("DEBUG: Initializing timers with White=" + initialWhiteUsedTime + 
                         "s, Black=" + initialBlackUsedTime + "s");
    }

    public void addMove(int whiteFromRow, int whiteFromCol, int whiteToRow, int whiteToCol,
                       int blackFromRow, int blackFromCol, int blackToRow, int blackToCol,
                       int whiteTimeSeconds, int blackTimeSeconds) {
        int moveNumber = moveHistory.size() + 1;
        String whiteMoveFrom = convertToNotation(whiteFromRow, whiteFromCol);
        String whiteMoveTo = convertToNotation(whiteToRow, whiteToCol);
        String blackMoveFrom = convertToNotation(blackFromRow, blackFromCol);
        String blackMoveTo = convertToNotation(blackToRow, blackToCol);
        
        System.out.println("DEBUG: Adding move #" + moveNumber + " - White time: " + whiteTimeSeconds + 
                         "s, Black time: " + blackTimeSeconds + "s");
        
        // Initialize time values when needed
        boolean isFirstMove = moveHistory.isEmpty();
        
        // For first move, set previous times to 0 to ensure we calculate correctly
        if (isFirstMove) {
            previousWhiteTimeSeconds = 0;
            previousBlackTimeSeconds = 0;
            System.out.println("First move - initializing previous times to 0");
        }
        
        // Calculate time taken for this move
        int whiteTimeTaken = 0;
        int blackTimeTaken = 0;
        
        boolean isWhiteMove = !whiteMoveFrom.equals("-") && !whiteMoveTo.equals("-");
        boolean isBlackMove = !blackMoveFrom.equals("-") && !blackMoveTo.equals("-");

        // Always calculate time taken, even for the first move
        if (isWhiteMove) {
            whiteTimeTaken = whiteTimeSeconds - previousWhiteTimeSeconds;
            previousWhiteTimeSeconds = whiteTimeSeconds;
            System.out.println("White moved and took: " + whiteTimeTaken + "s");
        }
        if(isBlackMove) {
            blackTimeTaken = blackTimeSeconds - previousBlackTimeSeconds;
            previousBlackTimeSeconds = blackTimeSeconds;
            System.out.println("Black moved and took: " + blackTimeTaken + "s");
        }
        
        // Ensure time taken is never negative
        whiteTimeTaken = Math.max(0, whiteTimeTaken);
        blackTimeTaken = Math.max(0, blackTimeTaken);
        
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

        // 1. คอลัมน์แรก: หมายเลขตา
        JLabel numberLabel = new JLabel(String.valueOf(move.moveNumber));
        numberLabel.setForeground(Color.WHITE);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 14));
        moveRow.add(numberLabel);

        // 2. คอลัมน์ที่สอง: การเคลื่อนที่ของหมากขาว
        String whiteMove = move.whiteMoveFrom.equals("-") ? "-" : move.whiteMoveFrom + " → " + move.whiteMoveTo;
        JLabel whiteMoveLabel = new JLabel(whiteMove);
        whiteMoveLabel.setForeground(WHITE_MOVE_COLOR);
        whiteMoveLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        moveRow.add(whiteMoveLabel);

        // 3. คอลัมน์ที่สาม: การเคลื่อนที่ของหมากดำ
        String blackMove = move.blackMoveFrom.equals("-") ? "-" : move.blackMoveFrom + " → " + move.blackMoveTo;
        JLabel blackMoveLabel = new JLabel(blackMove);
        blackMoveLabel.setForeground(BLACK_MOVE_COLOR);
        blackMoveLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        moveRow.add(blackMoveLabel);

        // 4. คอลัมน์ที่สี่: เวลาที่ใช้โดยฝ่ายขาว (W Time Used)
        String whiteTimeDisplay = formatTimeDisplay(move.whiteTimeTaken);
        JLabel whiteTimeLabel = new JLabel(whiteTimeDisplay);
        whiteTimeLabel.setForeground(new Color(255, 220, 150));
        whiteTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        whiteTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        moveRow.add(whiteTimeLabel);

        // 5. คอลัมน์ที่ห้า: เวลาที่ใช้โดยฝ่ายดำ (B Time Used)
        String blackTimeDisplay = formatTimeDisplay(move.blackTimeTaken);
        JLabel blackTimeLabel = new JLabel(blackTimeDisplay);
        blackTimeLabel.setForeground(new Color(255, 220, 150));
        blackTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        blackTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        moveRow.add(blackTimeLabel);

        return moveRow;
    }
    /**
     * จัดรูปแบบเวลาให้แสดงผลอย่างเหมาะสม
     */
    private String formatTimeDisplay(int seconds) {
        if (seconds <= 0) {
            return "-";
        } else if (seconds < 60) {
            return seconds + "s";
        } else {
            return String.format("%d:%02d", seconds / 60, seconds % 60);
        }
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
    
    // เพิ่มเมธอดสำหรับรีเซ็ตเวลาเมื่อเริ่มเกมใหม่
    public void resetGameTime(int initialWhiteTimeSeconds, int initialBlackTimeSeconds) {
        gameStartWhiteTimeSeconds = initialWhiteTimeSeconds;
        gameStartBlackTimeSeconds = initialBlackTimeSeconds;
        previousWhiteTimeSeconds = -1;
        previousBlackTimeSeconds = -1;
        moveHistory.clear(); // Clear move history for new game
        updateMoveList(); // Update the display
        System.out.println("Game time reset: White=" + initialWhiteTimeSeconds + ", Black=" + initialBlackTimeSeconds);
    }
    
    // เพิ่มเมธอดสำหรับบอกว่าเป็นตาแรกของเกมหรือไม่
    public boolean isFirstMove() {
        return moveHistory.isEmpty();
    }
    
    // Add this method for external debugging
    public void dumpMoveHistory() {
        System.out.println("==== MOVE HISTORY DUMP ====");
        for (MoveRecord move : moveHistory) {
            System.out.println("Move " + move.moveNumber + 
                             ": White " + move.whiteMoveFrom + "->" + move.whiteMoveTo + 
                             " (took " + move.whiteTimeTaken + "s), " +
                             "Black " + move.blackMoveFrom + "->" + move.blackMoveTo +
                             " (took " + move.blackTimeTaken + "s)");
        }
        System.out.println("=========================");
    }
}