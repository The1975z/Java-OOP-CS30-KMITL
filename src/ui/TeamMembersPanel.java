package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TeamMembersPanel extends JPanel {
    private final Color BACKGROUND_COLOR;
    private final Color GOLD_ACCENT;
    private final Color TEXT_COLOR;
    private final Color HOVER_COLOR;
    private final Color CARD_BACKGROUND;
    private final Color SHADOW_COLOR;
    Timer hoverTimer = new Timer(50, null);

    private final String[] members = {
        "Wiwat (PotterWW) - https://github.com/PotterWW",
        "Roman Bichedi (defnotRM) - https://github.com/defnotRM",
        "Akeaphap Waree (The1975z) - https://github.com/The1975z",
        "Chatchanok Tancharoen (412) - https://github.com/pokogummybear",
        "Anapat Danjiramontri (AnapatD) - https://github.com/AnapatD"
    };

    public TeamMembersPanel(Color backgroundColor, Color goldAccent, Color textColor) {
        this.BACKGROUND_COLOR = backgroundColor;
        this.GOLD_ACCENT = goldAccent;
        this.TEXT_COLOR = textColor;
        this.HOVER_COLOR = new Color(50, 50, 70, 220);
        this.CARD_BACKGROUND = new Color(35, 35, 45, 180);
        this.SHADOW_COLOR = new Color(0, 0, 0, 80);

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                float angle = (float) ((System.currentTimeMillis() % 10000) / 10000.0) * 2 * (float) Math.PI;
                GradientPaint gradient = new GradientPaint(
                    0, 0, BACKGROUND_COLOR.darker(),
                    (float) (width * Math.cos(angle)), (float) (height * Math.sin(angle)),
                    BACKGROUND_COLOR.brighter());
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Double(0, 0, width, height, 40, 40));

                g2d.setStroke(new BasicStroke(3f));
                g2d.setColor(new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 150));
                g2d.draw(new RoundRectangle2D.Double(8, 8, width - 16, height - 16, 35, 35));

                for (int i = 0; i < 6; i++) {
                    g2d.setColor(new Color(SHADOW_COLOR.getRed(), SHADOW_COLOR.getGreen(), SHADOW_COLOR.getBlue(), 20 * (6 - i)));
                    g2d.drawRoundRect(8 + i, 8 + i, width - 16 - 2 * i, height - 16 - 2 * i, 35, 35);
                }
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("DEVELOPMENT TEAM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Trajan Pro", Font.BOLD, 32));
        titleLabel.setForeground(GOLD_ACCENT);
        titleLabel.setBorder(new EmptyBorder(25, 15, 25, 15));

        JPanel membersPanel = new JPanel();
        membersPanel.setLayout(new BoxLayout(membersPanel, BoxLayout.Y_AXIS));
        membersPanel.setOpaque(false);
        membersPanel.setBorder(new EmptyBorder(15, 40, 40, 40));

        for (String member : members) {
            JPanel cardPanel = createMemberCard(member);
            membersPanel.add(cardPanel);
            membersPanel.add(Box.createVerticalStrut(20));
        }

        JLabel courseLabel = new JLabel("OOP PROGRAMMING - COMPUTER SCIENCE KMITL", SwingConstants.CENTER);
        courseLabel.setFont(new Font("Lato", Font.ITALIC, 18));
        courseLabel.setForeground(new Color(180, 180, 190));
        courseLabel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton closeButton = new JButton("CLOSE");
        closeButton.setFont(new Font("Cinzel", Font.BOLD, 18));
        closeButton.setForeground(TEXT_COLOR);
        closeButton.setBackground(new Color(157, 105, 53));
        closeButton.setBorder(BorderFactory.createLineBorder(GOLD_ACCENT, 2));
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(150, 40));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(new Color(187, 135, 83));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(new Color(157, 105, 53));
            }
        });
        closeButton.addActionListener(e -> {
            Window dialog = SwingUtilities.getWindowAncestor(TeamMembersPanel.this);
            if (dialog != null) {
                dialog.dispose();
            }
        });

        bottomPanel.add(closeButton);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(courseLabel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(membersPanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMemberCard(String memberData) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setStroke(new BasicStroke(2f));
                g2d.setColor(new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 100));
                g2d.drawRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 18, 18);

                for (int i = 0; i < 4; i++) {
                    g2d.setColor(new Color(SHADOW_COLOR.getRed(), SHADOW_COLOR.getGreen(), SHADOW_COLOR.getBlue(), 15 * (4 - i)));
                    g2d.drawRoundRect(4 + i, 4 + i, getWidth() - 8 - 2 * i, getHeight() - 8 - 2 * i, 18, 18);
                }
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(450, 80));
        card.setMaximumSize(new Dimension(450, 80));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        String[] parts = memberData.split(" - ");
        String name = parts[0];
        String githubUrl = parts[1];

        JLabel memberLabel = new JLabel(name);
        memberLabel.setFont(new Font("Lato", Font.BOLD, 20));
        memberLabel.setForeground(TEXT_COLOR);
        memberLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        hoverTimer.setRepeats(false);
        memberLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hoverTimer = new Timer(50, null);
                hoverTimer.addActionListener(ae -> {
                    if (memberLabel.getForeground().equals(TEXT_COLOR)) {
                        memberLabel.setForeground(GOLD_ACCENT);
                        card.setBackground(HOVER_COLOR);
                    }
                });
                hoverTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverTimer.stop();
                memberLabel.setForeground(TEXT_COLOR);
                card.setBackground(null);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI(githubUrl));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JLabel githubIcon = new JLabel("GITHUB");
        githubIcon.setFont(new Font("Arial", Font.PLAIN, 18));
        githubIcon.setForeground(GOLD_ACCENT);
        githubIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                githubIcon.setForeground(new Color(255, 215, 0));
                githubIcon.setFont(new Font("Arial", Font.PLAIN, 20));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                githubIcon.setForeground(GOLD_ACCENT);
                githubIcon.setFont(new Font("Arial", Font.PLAIN, 18));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI(githubUrl));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        card.add(memberLabel, BorderLayout.WEST);
        card.add(githubIcon, BorderLayout.EAST);
        return card;
    }

    public static void showTeamMembersDialog(Component parent, Color backgroundColor, Color goldAccent, Color textColor) {
        TeamMembersPanel panel = new TeamMembersPanel(backgroundColor, goldAccent, textColor);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Team Members", true);
        dialog.setContentPane(panel);
        dialog.setSize(550, 700);
        dialog.setLocationRelativeTo(parent);
        dialog.setUndecorated(true);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(goldAccent, 3));
        dialog.setVisible(true);
    }
}