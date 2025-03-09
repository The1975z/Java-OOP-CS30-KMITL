package ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyEvent;

public class RulesDialog extends JDialog {
    
    private final Color goldAccent;
    private final Color textColor;
    private final Font copyrightFont;
    private final Color lightSquareColor;
    private final Color darkSquareColor;
    private JPanel mainPanel;
    private StartScreen parent;
    
    public RulesDialog(StartScreen parent, Color goldAccent, Color textColor, Font copyrightFont, 
                        Color lightSquareColor, Color darkSquareColor) {
        super(parent, "Game Rules", true);
        this.parent = parent;
        this.goldAccent = goldAccent;
        this.textColor = textColor;
        this.copyrightFont = copyrightFont;
        this.lightSquareColor = lightSquareColor;
        this.darkSquareColor = darkSquareColor;
        
        setSize(900, 680);
        setLocationRelativeTo(getOwner());

        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 40), getWidth(), getHeight(), new Color(20, 20, 30));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(40, 40, 50));
                g2d.fillRoundRect(15, 15, getWidth() - 30, getHeight() - 30, 25, 25);

                g2d.setStroke(new BasicStroke(2.5f));
                g2d.setColor(goldAccent);
                g2d.drawRoundRect(15, 15, getWidth() - 30, getHeight() - 30, 25, 25);

                Font logoFont = new Font("Arial", Font.BOLD, 180);
                g2d.setFont(logoFont);
                g2d.setColor(new Color(35, 35, 45));
                String logo = "MN";
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(logo, (getWidth() - fm.stringWidth(logo)) / 2, getHeight() / 2 + 60);

                g2d.setFont(copyrightFont);
                g2d.setColor(new Color(150, 150, 150));
                String copyright = "© 2023-2025 Mak Neeb Game. All Rights Reserved. KMITL Computer Science.";
                fm = g2d.getFontMetrics();
                g2d.drawString(copyright, (getWidth() - fm.stringWidth(copyright)) / 2, getHeight() - 25);
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 15, 30));

        JLabel titleLabel = new JLabel("MAK NEEB GAME RULES", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Cinzel", Font.BOLD, 36));
        titleLabel.setForeground(goldAccent);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel closeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closeButtonPanel.setOpaque(false);
        JButton closeXButton = parent.createIconButton("X", 30, 30);
        closeXButton.addActionListener(e -> dispose());
        closeButtonPanel.add(closeXButton);
        headerPanel.add(closeButtonPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        JPanel textWithImagesPanel = new JPanel();
        textWithImagesPanel.setLayout(new BoxLayout(textWithImagesPanel, BoxLayout.Y_AXIS));
        textWithImagesPanel.setOpaque(false);

        String rulesText = createDetailedRules();

        String[] sections = rulesText.split("\n\n");

        for (String section : sections) {
            addRuleSection(textWithImagesPanel, section);
        }

        JScrollPane scrollPane = new JScrollPane(textWithImagesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUI(new ModernScrollBarUI(goldAccent));
        verticalBar.setPreferredSize(new Dimension(12, 0));

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JButton teamButton = parent.createStyledButton("TEAM MEMBERS");
        teamButton.setPreferredSize(new Dimension(200, 50));
        teamButton.addActionListener(e -> TeamMembersPanel.showTeamMembersDialog(this, new Color(30, 30, 40), goldAccent, textColor));

        JButton closeButton = parent.createStyledButton("CLOSE");
        closeButton.setPreferredSize(new Dimension(200, 50));
        closeButton.addActionListener(e -> dispose());

        bottomPanel.add(teamButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setUndecorated(true);
    }
    
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    private void addRuleSection(JPanel panel, String sectionText) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BorderLayout(15, 10));
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setOpaque(false);

        String htmlText = "<html><body style='font-family:Lato,Arial,sans-serif; font-size:16px; color:#E3C6B5; width:560px;'>";

        String[] parts = sectionText.split("\n", 2);
        String title = parts[0];
        String content = parts.length > 1 ? parts[1] : "";

        htmlText += "<h2 style='color:#D4AF37; margin-top:5px; margin-bottom:10px;'>" + title + "</h2>";

        if (content.contains("1.")) {
            htmlText += "<ul style='margin-left:20px; margin-top:5px;'>";
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty())
                    continue;

                if (line.matches("^\\d+\\..*")) {
                    htmlText += "<li style='margin-bottom:6px;'>" + line.substring(line.indexOf('.') + 1).trim() + "</li>";
                } else if (line.startsWith("-")) {
                    htmlText += "<li style='margin-bottom:6px;'>" + line.substring(1).trim() + "</li>";
                } else {
                    htmlText += "<p style='margin:5px 0px;'>" + line.trim() + "</p>";
                }
            }
            htmlText += "</ul>";
        } else {
            htmlText += "<p style='margin:5px 0px;'>" + content.replace("\n", "<br>") + "</p>";
        }

        htmlText += "</body></html>";
        textPane.setText(htmlText);

        if (title.contains("CAPTURING") || title.contains("ATARI")) {
            JPanel imagePanel = new GameRuleDiagramPanel("capture_animation", lightSquareColor, darkSquareColor);
            sectionPanel.add(imagePanel, BorderLayout.EAST);
        } else if (title.contains("BASIC RULES")) {
            JPanel imagePanel = new GameRuleDiagramPanel("board", lightSquareColor, darkSquareColor);
            sectionPanel.add(imagePanel, BorderLayout.EAST);
        } else if (title.contains("MOVEMENT")) {
            JPanel imagePanel = new GameRuleDiagramPanel("movement", lightSquareColor, darkSquareColor);
            sectionPanel.add(imagePanel, BorderLayout.EAST);
        } else if (title.contains("EXAMPLE CAPTURES")) {
            JPanel verticalPanel = new GameRuleDiagramPanel("vertical_capture", lightSquareColor, darkSquareColor);
            JPanel surroundPanel = new GameRuleDiagramPanel("surrounded_capture", lightSquareColor, darkSquareColor);
            JPanel chainPanel = new GameRuleDiagramPanel("chain_capture", lightSquareColor, darkSquareColor);
            JPanel cornerPanel = new GameRuleDiagramPanel("corner_capture", lightSquareColor, darkSquareColor);

            JPanel capturesPanel = new JPanel();
            capturesPanel.setLayout(new GridLayout(2, 2, 10, 10));
            capturesPanel.setOpaque(false);
            capturesPanel.add(verticalPanel);
            capturesPanel.add(surroundPanel);
            capturesPanel.add(chainPanel);
            capturesPanel.add(cornerPanel);
            sectionPanel.add(capturesPanel, BorderLayout.EAST);
        } else if (title.contains("TIME RULES")) {
            JPanel imagePanel = new GameRuleDiagramPanel("time_rule", lightSquareColor, darkSquareColor);
            sectionPanel.add(imagePanel, BorderLayout.EAST);
        }

        sectionPanel.add(textPane, BorderLayout.CENTER);
        panel.add(sectionPanel);

        if (!title.contains("STRATEGY TIPS")) {
            JPanel separator = new JPanel();
            separator.setPreferredSize(new Dimension(panel.getWidth(), 1));
            separator.setBackground(new Color(goldAccent.getRed(), goldAccent.getGreen(), goldAccent.getBlue(), 50));
            panel.add(separator);
        }
    }
    
    private String createDetailedRules() {
        StringBuilder sb = new StringBuilder();

        sb.append("INTRODUCTION TO MAK NEEB GAME\n");
        sb.append("Mak Neeb is a strategic board game combining elements of Go and Checkers. Players must think several moves ahead while trying to capture opponent's pieces and protect their own. The game offers a perfect balance of strategy, tactics, and planning.\n\n");

        sb.append("BASIC RULES\n");
        sb.append("1. Board: 8×8 square grid similar to a chess/checkers board\n");
        sb.append("2. Players: Two players - Black and White\n");
        sb.append("3. Pieces: 8 Rok pieces per player\n");
        sb.append("4. Setup: Black pieces start on top row (row 0), White pieces on bottom row (row 7)\n");
        sb.append("5. Objective: Capture opponent's pieces until they have fewer than 2 pieces remaining\n\n");

        sb.append("MOVEMENT RULES\n");
        sb.append("1. Direction: Pieces can move only in straight lines (horizontally or vertically)\n");
        sb.append("2. Distance: Pieces can move any number of squares in one direction\n");
        sb.append("3. Restrictions:\n");
        sb.append("   - Cannot move diagonally\n");
        sb.append("   - Cannot jump over other pieces\n");
        sb.append("   - Can only move to empty spaces\n");
        sb.append("   - Must move at least one square (no passing turns)\n\n");

        sb.append("CAPTURING STONES\n");
        sb.append("1. Flanking Capture: If you place your piece so that enemy pieces are between your pieces on both sides (horizontally or vertically), those enemy pieces are captured and removed from the board\n");
        sb.append("2. Chain Capture: If multiple enemy pieces are lined up, all can be captured at once if flanked by your pieces\n");
        sb.append("3. Atari: When an opponent's piece has no liberties (no adjacent empty spaces) it is captured\n\n");

        sb.append("EXAMPLE CAPTURES\n");
        sb.append("The diagrams on the right illustrate different capture scenarios:\n");
        sb.append("- Vertical Capture: Capturing pieces in a vertical line\n");
        sb.append("- Surrounded Capture: Capturing by surrounding (Atari)\n");
        sb.append("- Chain Capture: Capturing multiple pieces in a row\n");
        sb.append("- Corner Capture: Capturing in a corner position\n\n");

        sb.append("TIME RULES\n");
        sb.append("1. Each player has 10 minutes total game time\n");
        sb.append("2. Clock only runs during a player's turn\n");
        sb.append("3. If a player's time runs out, they lose the game immediately\n");
        sb.append("4. No added time or overtime is allowed\n");
        sb.append("5. Time spent per move is recorded in the move history\n\n");

        sb.append("GAME END CONDITIONS\n");
        sb.append("1. Victory: Reduce opponent to fewer than 2 pieces\n");
        sb.append("2. Time Victory: Opponent runs out of time\n");
        sb.append("3. Draw Conditions:\n");
        sb.append("   - Both players have only 1 piece each\n");
        sb.append("   - A player has no valid moves on their turn\n");
        sb.append("   - No successful capture in 3 consecutive turns by both players\n");
        sb.append("   - Both players run out of time simultaneously\n\n");

        sb.append("STRATEGY TIPS\n");
        sb.append("1. Control the center of the board whenever possible\n");
        sb.append("2. Keep your pieces connected to support each other\n");
        sb.append("3. Create traps for opponent's pieces by setting up multiple capture opportunities\n");
        sb.append("4. Balance offense and defense - sometimes sacrificing a piece can lead to a bigger advantage\n");
        sb.append("5. Be mindful of your time, especially in complex situations\n");

        return sb.toString();
    }
}