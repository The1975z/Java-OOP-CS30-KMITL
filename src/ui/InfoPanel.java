package ui;

import game.GameController;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

public class InfoPanel extends JPanel {

    private JLabel whiteTimeLabel;
    private JLabel blackTimeLabel;
    private JLabel whiteRemainingLabel;
    private JLabel blackRemainingLabel;
    private JLabel whiteCapturedLabel;
    private JLabel blackCapturedLabel;
    private GameController gameController;
    private JButton switchSidesButton;

    private final Color BUTTON_COLOR = new Color(157, 105, 53);
    private final Color BUTTON_HOVER_COLOR = new Color(187, 135, 83);
    private final Color TEXT_COLOR = new Color(227, 198, 181);
    private final Color GOLD_ACCENT = new Color(212, 175, 55);

    public InfoPanel(GameController controller) {
        this.gameController = controller;
        if (controller != null) {
            controller.setInfoPanel(this);
        }
        setBackground(new Color(30, 30, 30));
        setPreferredSize(new Dimension(getWidth(), 100));

        setLayout(new BorderLayout(20, 0));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JPanel whiteTimerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        whiteTimerPanel.setOpaque(false);
        whiteTimeLabel = new JLabel("White: 10:00");
        whiteTimeLabel.setFont(new Font("Trajan Pro", Font.BOLD, 32));
        whiteTimeLabel.setForeground(Color.WHITE);
        whiteTimerPanel.add(whiteTimeLabel);

        JPanel whitePiecesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        whitePiecesPanel.setOpaque(false);

        whiteRemainingLabel = new JLabel("Remaining: 8");
        whiteRemainingLabel.setFont(new Font("Cinzel", Font.BOLD, 16));
        whiteRemainingLabel.setForeground(new Color(255, 220, 180));
        whiteRemainingLabel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(         new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 100), 1), BorderFactory.createEmptyBorder(3, 8, 3, 8)));

        whiteCapturedLabel = new JLabel("Captured: 0");
        whiteCapturedLabel.setFont(new Font("Cinzel", Font.BOLD, 16));
        whiteCapturedLabel.setForeground(new Color(255, 150, 150));
        whiteCapturedLabel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(new Color(255, 100, 100, 100), 1), BorderFactory.createEmptyBorder(3, 8, 3, 8)));

        whitePiecesPanel.add(whiteRemainingLabel);
        whitePiecesPanel.add(Box.createHorizontalStrut(20));
        whitePiecesPanel.add(whiteCapturedLabel);

        leftPanel.add(whiteTimerPanel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(whitePiecesPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JPanel blackTimerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        blackTimerPanel.setOpaque(false);
        blackTimeLabel = new JLabel("Black: 10:00");
        blackTimeLabel.setFont(new Font("Trajan Pro", Font.BOLD, 32));
        blackTimeLabel.setForeground(Color.WHITE);
        blackTimerPanel.add(blackTimeLabel);

        JPanel blackPiecesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        blackPiecesPanel.setOpaque(false);

        blackRemainingLabel = new JLabel("Remaining: 8");
        blackRemainingLabel.setFont(new Font("Cinzel", Font.BOLD, 16));
        blackRemainingLabel.setForeground(new Color(255, 220, 180));
        blackRemainingLabel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(         new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 100), 1), BorderFactory.createEmptyBorder(3, 8, 3, 8)));

        blackCapturedLabel = new JLabel("Captured: 0");
        blackCapturedLabel.setFont(new Font("Cinzel", Font.BOLD, 16));
        blackCapturedLabel.setForeground(new Color(255, 150, 150));
        blackCapturedLabel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(new Color(255, 100, 100, 100), 1), BorderFactory.createEmptyBorder(3, 8, 3, 8)));

        blackPiecesPanel.add(blackRemainingLabel);
        blackPiecesPanel.add(Box.createHorizontalStrut(20));
        blackPiecesPanel.add(blackCapturedLabel);

        rightPanel.add(blackTimerPanel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(blackPiecesPanel);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        switchSidesButton = createSwitchButton();
        centerPanel.add(switchSidesButton);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        updatePieceCount(8, 8, 0, 0);
    }

    private JButton createSwitchButton() {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }

                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));

                g2d.setStroke(new BasicStroke(2f));
                g2d.setColor(new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 120));
                g2d.draw(new RoundRectangle2D.Double(2, 2, getWidth() - 4, getHeight() - 4, 12, 12));

                int width = getWidth();
                int height = getHeight();
                int iconSize = Math.min(width, height) / 2;

                g2d.setColor(Color.WHITE);
                g2d.fillOval(width / 4 - iconSize / 2, height / 2 - iconSize / 2, iconSize, iconSize);

                g2d.setColor(Color.BLACK);
                g2d.fillOval(width * 3 / 4 - iconSize / 2, height / 2 - iconSize / 2, iconSize, iconSize);

                g2d.setColor(GOLD_ACCENT);
                g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int arrowY = height / 2;
                int arrowX1 = width / 2 - 10;
                int arrowX2 = width / 2 + 10;
                g2d.drawLine(arrowX1, arrowY, arrowX2, arrowY);
                g2d.drawLine(arrowX2 - 5, arrowY - 5, arrowX2, arrowY);
                g2d.drawLine(arrowX2 - 5, arrowY + 5, arrowX2, arrowY);

                int arrowY2 = height / 2 - 10;
                int arrowX3 = width / 2 + 10;
                int arrowX4 = width / 2 - 10;
                g2d.drawLine(arrowX3, arrowY2, arrowX4, arrowY2);
                g2d.drawLine(arrowX4 + 5, arrowY2 - 5, arrowX4, arrowY2);
                g2d.drawLine(arrowX4 + 5, arrowY2 + 5, arrowX4, arrowY2);

                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "FLIP BOARD";
                int textWidth = fm.stringWidth(text);
                g2d.setColor(TEXT_COLOR);
                g2d.drawString(text, (width - textWidth) / 2, height - 8);
            }
        };

        button.setPreferredSize(new Dimension(100, 50));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setToolTipText("สลับฝั่งขาว-ดำ");
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            if (gameController != null) {
                gameController.switchSides();
            }
        });

        return button;
    }

    public void updateTimer() {
        if (gameController != null) {
            whiteTimeLabel.setText("White: " + gameController.getWhiteTime());
            blackTimeLabel.setText("Black: " + gameController.getBlackTime());
        }
        repaint();
    }

    public void updatePieceCount(int whiteRemaining, int blackRemaining, int whiteCaptured, int blackCaptured) {
        System.out.println("InfoPanel: Updating piece count - White: " + whiteRemaining + "/" + blackCaptured + ", Black: " + blackRemaining + "/" + whiteCaptured);

        whiteRemainingLabel.setText("Remaining: " + whiteRemaining);
        blackRemainingLabel.setText("Remaining: " + blackRemaining);
        whiteCapturedLabel.setText("Captured: " + blackCaptured);
        blackCapturedLabel.setText("Captured: " + whiteCaptured);

        repaint();
    }
}