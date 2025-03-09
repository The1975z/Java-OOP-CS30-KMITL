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

    private class MoveRecord {
        int moveNumber;
        String whiteMoveFrom;
        String whiteMoveTo;
        String blackMoveFrom;
        String blackMoveTo;
        int whiteTimeSeconds;
        int blackTimeSeconds;

        public MoveRecord(int moveNumber, String whiteMoveFrom, String whiteMoveTo, String blackMoveFrom, String blackMoveTo, int whiteTimeSeconds, int blackTimeSeconds) {
            this.moveNumber = moveNumber;
            this.whiteMoveFrom = whiteMoveFrom;
            this.whiteMoveTo = whiteMoveTo;
            this.blackMoveFrom = blackMoveFrom;
            this.blackMoveTo = blackMoveTo;
            this.whiteTimeSeconds = whiteTimeSeconds;
            this.blackTimeSeconds = blackTimeSeconds;
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
        moveHistory.add(new MoveRecord(moveNumber, whiteMoveFrom, whiteMoveTo, blackMoveFrom, blackMoveTo,
                                      whiteTimeSeconds, blackTimeSeconds));
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

        String[] headers = {"#", "White", "Black", "White Time", "Black Time"};
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

        String timeStr = String.format("%02d:%02d", move.whiteTimeSeconds / 60, move.whiteTimeSeconds % 60);
        JLabel whiteTimeLabel = new JLabel(timeStr);
        whiteTimeLabel.setForeground(Color.WHITE);
        whiteTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        String timeStrBlack = String.format("%02d:%02d", move.blackTimeSeconds / 60, move.blackTimeSeconds % 60);
        JLabel blackTimeLabel = new JLabel(timeStrBlack);
        blackTimeLabel.setForeground(Color.WHITE);
        blackTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        moveRow.add(numberLabel);
        moveRow.add(whiteMoveLabel);
        moveRow.add(blackMoveLabel);
        moveRow.add(whiteTimeLabel);
        moveRow.add(blackTimeLabel);

        return moveRow;
    }

    public void addGameMove(int whiteFromRow, int whiteFromCol, int whiteToRow, int whiteToCol, int blackFromRow, int blackFromCol, int blackToRow, int blackToCol, int whiteTimeSeconds, int blackTimeSeconds) {
        addMove(whiteFromRow, whiteFromCol, whiteToRow, whiteToCol, blackFromRow, blackFromCol, blackToRow, blackToCol, whiteTimeSeconds, blackTimeSeconds);
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
}