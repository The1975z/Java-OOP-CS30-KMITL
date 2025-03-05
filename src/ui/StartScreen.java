package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.event.KeyEvent;
import main.GameLauncher;
import utils.ResourceManager;
import utils.SoundManager;
import utils.WindowResizer;

public class StartScreen extends JFrame {

    private final Color BACKGROUND_COLOR = new Color(20, 20, 20);
    private final Color BUTTON_COLOR = new Color(157, 105, 53);
    private final Color BUTTON_HOVER_COLOR = new Color(187, 135, 83);
    private final Color TEXT_COLOR = new Color(227, 198, 181);
    private final Color GOLD_ACCENT = new Color(212, 175, 55);
    private final Font TITLE_FONT = new Font("Trajan Pro", Font.BOLD, 52);
    private final Font BUTTON_FONT = new Font("Cinzel", Font.BOLD, 24);
    private final Font TEXT_FONT = new Font("Lato", Font.PLAIN, 18);
    private final Font COPYRIGHT_FONT = new Font("Arial", Font.ITALIC, 12);
    private final Color LIGHT_SQUARE_COLOR = new Color(240, 217, 181);
    private final Color DARK_SQUARE_COLOR = new Color(181, 136, 99);
    private final List<String> backgroundPaths = new ArrayList<>();
    private int currentBackgroundIndex = 0;
    private Timer backgroundTimer;
    private final int BACKGROUND_CHANGE_INTERVAL = 5000;
    private boolean soundEnabled = true;
    private float currentVolume = 1.0f;
    private JButton soundToggleButton;
    private JButton volumeUpButton;
    private JButton volumeDownButton;
    private JLayeredPane layeredPane;
    
    private BackgroundPanel backgroundPanel;

    public StartScreen() {
        backgroundPaths.add("background1.jpg");
        backgroundPaths.add("background2.jpg");
        backgroundPaths.add("background.jpg");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        setTitle("Mak Neeb Game");
        setSize(screenWidth, screenHeight);
        setUndecorated(true);
        setLocationRelativeTo(null);
        
        // เปลี่ยนเป็นใช้ JLayeredPane แทน BorderLayout
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, screenWidth, screenHeight);
        setContentPane(layeredPane);
        
        TitleBar titleBar = new TitleBar(this);
        titleBar.setBounds(0, 0, screenWidth, 30);
        layeredPane.add(titleBar, JLayeredPane.DEFAULT_LAYER);

        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        startBackgroundAnimation();
        SoundManager.getInstance().loopSound("SongGame");

        new WindowResizer(this);
        setVisible(true);
    }

    private void startBackgroundAnimation() {
        backgroundTimer = new Timer(BACKGROUND_CHANGE_INTERVAL, e -> {
            currentBackgroundIndex = (currentBackgroundIndex + 1) % backgroundPaths.size();
            backgroundPanel.loadNextBackground();
            backgroundPanel.repaint();
        });
        backgroundTimer.start();
    }

    private class BackgroundPanel extends JPanel {

        private Image currentBackground;
        private Image nextBackground;
        private float alpha = 0f;
        private boolean transitioning = false;
        private Timer transitionTimer;
        
        public BackgroundPanel() {
            setLayout(null);
            loadCurrentBackground();
            createContent();
        }

        private void loadCurrentBackground() {
            currentBackground = ResourceManager.loadImage(backgroundPaths.get(currentBackgroundIndex));
            if (currentBackground == null) {
                System.err.println("ไม่พบรูปภาพพื้นหลัง: " + backgroundPaths.get(currentBackgroundIndex));
            }
        }

        public void loadNextBackground() {
            if (transitioning) {
                return;
            }

            int nextIndex = (currentBackgroundIndex + 1) % backgroundPaths.size();
            nextBackground = ResourceManager.loadImage(backgroundPaths.get(nextIndex));

            if (nextBackground != null) {
                startTransition();
            } else {
                System.err.println("ไม่พบรูปภาพพื้นหลังถัดไป: " + backgroundPaths.get(nextIndex));
            }
        }

        private void startTransition() {
            transitioning = true;
            alpha = 0f;

            transitionTimer = new Timer(20, e -> {
                alpha += 0.02f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    transitioning = false;
                    currentBackground = nextBackground;
                    nextBackground = null;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            transitionTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (currentBackground != null) {
                g.drawImage(currentBackground, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(BACKGROUND_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            if (transitioning && nextBackground != null) {
                Composite originalComposite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g.drawImage(nextBackground, 0, 0, getWidth(), getHeight(), this);
                g2d.setComposite(originalComposite);
            }

            int panelWidth = getWidth() / 2;
            int panelHeight = getHeight() * 3 / 4;
            int panelX = (getWidth() - panelWidth) / 2;
            int panelY = (getHeight() - panelHeight) / 2;

            g2d.setColor(new Color(10, 10, 10, 220));
            g2d.fill(new RoundRectangle2D.Double(panelX, panelY, panelWidth, panelHeight, 40, 40));

            g2d.setStroke(new BasicStroke(3f));
            g2d.setColor(new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 120));
            g2d.draw(new RoundRectangle2D.Double(panelX + 5, panelY + 5, panelWidth - 10, panelHeight - 10, 30, 30));

            g2d.setFont(COPYRIGHT_FONT);
            g2d.setColor(new Color(200, 200, 200, 180));
            String copyright = "© 2025 Mak Neeb Game. All Rights Reserved. KMITL Computer Science.";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(copyright);
            g2d.drawString(copyright, (getWidth() - textWidth) / 2, getHeight() - 20);
        }
        
        private void createContent() {
            ImageIcon logoIcon = ResourceManager.loadIcon("icon/ICONKMITL.png", 140, 140);
            ImageIcon rokIcon = ResourceManager.loadIcon("Rok/Rok.png", 80, 80);
 
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int screenWidth = screenSize.width;
            int screenHeight = screenSize.height;
 
            int centerPanelWidth = screenWidth / 2;
            int centerPanelHeight = screenHeight * 3 / 4;
            int centerPanelX = (screenWidth - centerPanelWidth) / 2;
            int centerPanelY = (screenHeight - centerPanelHeight) / 2 + 25;
 
            if (logoIcon != null) {
                JLabel logoLabel = new JLabel(logoIcon);
                logoLabel.setBounds(centerPanelX + (centerPanelWidth - 140) / 2, centerPanelY + 40, 140, 140);
                add(logoLabel);
            }
 
            if (rokIcon != null) {
                JLabel leftRokLabel = new JLabel(rokIcon);
                leftRokLabel.setBounds(centerPanelX + 60, centerPanelY + 170, 80, 80);
                add(leftRokLabel);
 
                JLabel rightRokLabel = new JLabel(rokIcon);
                rightRokLabel.setBounds(centerPanelX + centerPanelWidth - 140, centerPanelY + 170, 80, 80);
                add(rightRokLabel);
            }
 
            JLabel titleLabel = new JLabel("MAK NEEB GAME", SwingConstants.CENTER);
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(GOLD_ACCENT);
            titleLabel.setBounds(centerPanelX, centerPanelY + 190, centerPanelWidth, 60);
            add(titleLabel);
 
            JLabel subtitleLabel = new JLabel("Custom Go Game with 8 Rok Pieces", SwingConstants.CENTER);
            subtitleLabel.setFont(TEXT_FONT);
            subtitleLabel.setForeground(TEXT_COLOR);
            subtitleLabel.setBounds(centerPanelX, centerPanelY + 250, centerPanelWidth, 30);
            add(subtitleLabel);
 
            JButton playButton = createStyledButton("PLAY GAME");
            playButton.setBounds(centerPanelX + (centerPanelWidth - 300) / 2, centerPanelY + 320, 300, 60);
            playButton.addActionListener(e -> {
                SoundManager.getInstance().stopSound("SongGame");
                SoundManager.getInstance().playSound("start");
                GameLauncher.startGame();
                dispose();
            });
            add(playButton);
 
            JButton rulesButton = createStyledButton("GAME RULES");
            rulesButton.setBounds(centerPanelX + (centerPanelWidth - 300) / 2, centerPanelY + 400, 300, 60);
            rulesButton.addActionListener(e -> showRules());
            add(rulesButton);
 
            JButton exitButton = createStyledButton("EXIT GAME");
            exitButton.setBounds(centerPanelX + (centerPanelWidth - 300) / 2, centerPanelY + 480, 300, 60);
            exitButton.addActionListener(e -> System.exit(0));
            add(exitButton);
 
            // Panel สำหรับปุ่มควบคุมเสียง
            JPanel soundControlPanel = new JPanel();
            soundControlPanel.setLayout(null);
            soundControlPanel.setOpaque(false);
            soundControlPanel.setBounds(centerPanelX + centerPanelWidth - 240, centerPanelY + 40, 220, 60);
            
            // ปุ่มเปิด/ปิดเสียง
            soundToggleButton = createSoundButton(soundEnabled ? "SOUND ON" : "SOUND OFF", 120, 50);
            soundToggleButton.setBounds(0, 0, 120, 50);
            soundToggleButton.addActionListener(e -> {
                soundEnabled = !soundEnabled;
                SoundManager.getInstance().setSoundEnabled(soundEnabled);
                
                // เปลี่ยนข้อความและสีของปุ่ม
                soundToggleButton.setText(soundEnabled ? "SOUND ON" : "SOUND OFF");
                
                if (soundEnabled) {
                    SoundManager.getInstance().loopSound("SongGame");
                    SoundManager.getInstance().setVolume("SongGame", currentVolume);
                }
                
                // ทำเอฟเฟค blink เมื่อเปลี่ยนสถานะ
                blinkButton(soundToggleButton);
            });
            
            // ปุ่มเพิ่มเสียง
            volumeUpButton = createSoundButton("+", 50, 25);
            volumeUpButton.setBounds(130, 0, 50, 25);
            volumeUpButton.addActionListener(e -> {
                if (currentVolume < 1.0f) {
                    currentVolume = Math.min(1.0f, currentVolume + 0.1f);
                    SoundManager.getInstance().setVolume("SongGame", currentVolume);
                    SoundManager.getInstance().playSound("select");
                    
                    // แสดงระดับเสียงบนหน้าจอ
                    showVolumeIndicator(currentVolume);
                }
            });
            
            // ปุ่มลดเสียง
            volumeDownButton = createSoundButton("-", 50, 25);
            volumeDownButton.setBounds(130, 30, 50, 25);
            volumeDownButton.addActionListener(e -> {
                if (currentVolume > 0.0f) {
                    currentVolume = Math.max(0.0f, currentVolume - 0.1f);
                    SoundManager.getInstance().setVolume("SongGame", currentVolume);
                    SoundManager.getInstance().playSound("select");
                    
                    // แสดงระดับเสียงบนหน้าจอ
                    showVolumeIndicator(currentVolume);
                }
            });
            
            soundControlPanel.add(soundToggleButton);
            soundControlPanel.add(volumeUpButton);
            soundControlPanel.add(volumeDownButton);
            add(soundControlPanel);
 
            JLabel versionLabel = new JLabel("Version 1.0.0", SwingConstants.CENTER);
            versionLabel.setFont(new Font("Lato", Font.PLAIN, 26));
            versionLabel.setForeground(new Color(150, 150, 150));
            versionLabel.setBounds(centerPanelX, centerPanelY + centerPanelHeight - 30, centerPanelWidth, 70); 
            add(versionLabel);
            
        }
    }
    
    // ทำให้ปุ่มกระพริบเวลากด
    private void blinkButton(JButton button) {
        Timer blinkTimer = new Timer(100, null);
        final int[] count = {0};
        
        blinkTimer.addActionListener(e -> {
            button.setVisible(!button.isVisible());
            count[0]++;
            if (count[0] >= 6) { // กระพริบ 3 ครั้ง
                button.setVisible(true);
                blinkTimer.stop();
            }
        });
        
        blinkTimer.start();
    }
    
    // แสดงระดับเสียงบนหน้าจอ
    private void showVolumeIndicator(float volume) {
        // ลบป้ายบอกเสียงเก่าออกก่อน (ถ้ามี)
        Component[] components = layeredPane.getComponentsInLayer(JLayeredPane.POPUP_LAYER);
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals("volumeIndicator")) {
                layeredPane.remove(comp);
            }
        }
        
        // สร้างแผงแสดงระดับเสียง
        JPanel volumeIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // วาดพื้นหลังโปร่งใส
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // วาดขอบ
                g2d.setColor(GOLD_ACCENT);
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 18, 18);
                
                // แสดงไอคอนเสียง
                g2d.setFont(new Font("Dialog", Font.BOLD, 24));
                g2d.setColor(Color.WHITE);
                String icon = volume > 0.7f ? "🔊" : volume > 0.3f ? "🔉" : volume > 0.0f ? "🔈" : "🔇";
                g2d.drawString(icon, 15, 35);
                
                // แสดงแถบระดับเสียง
                int barWidth = getWidth() - 80;
                int barHeight = 20;
                int barX = 60;
                int barY = (getHeight() - barHeight) / 2;
                
                // แถบพื้นหลัง
                g2d.setColor(new Color(70, 70, 70));
                g2d.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);
                
                // แถบเสียง
                int filledWidth = (int)(barWidth * volume);
                if (filledWidth > 0) {
                    g2d.setColor(new Color(80, 200, 80));
                    g2d.fillRoundRect(barX, barY, filledWidth, barHeight, 10, 10);
                }
                
                // เปอร์เซ็นต์เสียง
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(Color.WHITE);
                String volumeText = Math.round(volume * 100) + "%";
                g2d.drawString(volumeText, barX + barWidth + 10, barY + 15);
            }
        };
        
        volumeIndicator.setOpaque(false);
        volumeIndicator.setName("volumeIndicator");
        
        // กำหนดขนาดและตำแหน่ง
        Dimension screenSize = getSize();
        int width = 350;
        int height = 60;
        volumeIndicator.setBounds((screenSize.width - width) / 2, screenSize.height - 120, width, height);
        
        // เพิ่มเข้าไปใน layeredPane
        layeredPane.add(volumeIndicator, JLayeredPane.POPUP_LAYER);
        layeredPane.setLayer(volumeIndicator, JLayeredPane.POPUP_LAYER);
        volumeIndicator.setVisible(true);
        layeredPane.repaint();
        
        // ตั้งเวลาให้หายไป
        Timer fadeTimer = new Timer(2000, e -> {
            // ทำ fade out
            Timer fadeOutTimer = new Timer(50, null);
            final float[] opacity = {1.0f};
            
            fadeOutTimer.addActionListener(fade -> {
                opacity[0] -= 0.1f;
                if (opacity[0] <= 0) {
                    layeredPane.remove(volumeIndicator);
                    layeredPane.repaint();
                    fadeOutTimer.stop();
                } else {
                    volumeIndicator.setBackground(new Color(0, 0, 0, (int)(opacity[0] * 200)));
                    volumeIndicator.repaint();
                }
            });
            
            fadeOutTimer.start();
        });
        fadeTimer.setRepeats(false);
        fadeTimer.start();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
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

                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

                g2d.setStroke(new BasicStroke(2f));
                g2d.setColor(new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 100));
                g2d.draw(new RoundRectangle2D.Double(3, 3, getWidth() - 6, getHeight() - 6, 16, 16));

                g2d.setColor(TEXT_COLOR);
                g2d.setFont(BUTTON_FONT);

                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();

                g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - fm.getDescent());
            }
        };

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
                SoundManager.getInstance().playSound("select");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        return button;
    }
    
    private JButton createSoundButton(String text, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // กำหนดสีพื้นหลังปุ่มตามสถานะ
                Color bgColor;
                if (text.equals("SOUND OFF")) {
                    // สีเมื่อปิดเสียง - สีแดงเข้ม
                    if (getModel().isPressed()) {
                        bgColor = new Color(130, 40, 40);
                    } else if (getModel().isRollover()) {
                        bgColor = new Color(170, 60, 60);
                    } else {
                        bgColor = new Color(150, 50, 50);
                    }
                } else if (text.equals("+") || text.equals("-")) {
                    // สีสำหรับปุ่มปรับระดับเสียง - สีทอง
                    if (getModel().isPressed()) {
                        bgColor = new Color(180, 140, 60).darker();
                    } else if (getModel().isRollover()) {
                        bgColor = new Color(212, 175, 55);
                    } else {
                        bgColor = new Color(180, 140, 60);
                    }
                } else {
                    // สีเมื่อเปิดเสียง - สีเขียวเข้ม
                    if (getModel().isPressed()) {
                        bgColor = new Color(40, 120, 40);
                    } else if (getModel().isRollover()) {
                        bgColor = new Color(60, 160, 60);
                    } else {
                        bgColor = new Color(50, 140, 50);
                    }
                }
                
                // วาดพื้นหลังปุ่ม
                g2d.setColor(bgColor);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                
                // วาดขอบปุ่ม
                g2d.setColor(new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 150));
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 10, 10));
                
                // เพิ่มเอฟเฟกต์แสงเงา
                Paint oldPaint = g2d.getPaint();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 80),
                    0, getHeight(), new Color(255, 255, 255, 5)
                );
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Double(2, 2, getWidth() - 4, getHeight() / 2 - 2, 10, 10));
                g2d.setPaint(oldPaint);
                
                // วาดตัวอักษรหรือไอคอน
                g2d.setColor(Color.WHITE);
                
                if (text.equals("SOUND ON") || text.equals("SOUND OFF")) {
                    // วาดไอคอนเสียง
                    String soundIcon = text.equals("SOUND ON") ? "🔊" : "🔇";
                    g2d.setFont(new Font("Dialog", Font.BOLD, 18));
                    FontMetrics iconFm = g2d.getFontMetrics();
                    int iconWidth = iconFm.stringWidth(soundIcon);
                    g2d.drawString(soundIcon, 15, getHeight()/2 + iconFm.getAscent()/2 - 2);
                    
                    // วาดข้อความ
                    String displayText = text.equals("SOUND ON") ? "ON" : "OFF";
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(displayText);
                    g2d.drawString(displayText, getWidth() - textWidth - 15, getHeight()/2 + fm.getAscent()/2 - 2);
                } else {
                    // สำหรับปุ่ม + และ -
                    g2d.setFont(new Font("Arial", Font.BOLD, 22));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getHeight();
                    g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2 - 2);
                }
            }
        };
        
        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // เพิ่ม tooltip สำหรับปุ่ม
        if (text.equals("SOUND ON") || text.equals("SOUND OFF")) {
            button.setToolTipText("คลิกเพื่อเปิด/ปิดเสียง");
        } else if (text.equals("+")) {
            button.setToolTipText("เพิ่มระดับเสียง");
        } else if (text.equals("-")) {
            button.setToolTipText("ลดระดับเสียง");
        }
        
        return button;
    }
    
    // เพิ่ม Fade-in effect ให้กับหน้าต่าง
    private void fadeInDialog(JDialog dialog, JPanel panel) {
        dialog.setOpacity(0.0f);
        Timer fadeTimer = new Timer(20, null);
        final float[] opacity = { 0.0f };

        fadeTimer.addActionListener(e -> {
            opacity[0] += 0.05f;
            if (opacity[0] >= 1.0f) {
                opacity[0] = 1.0f;
                dialog.setOpacity(1.0f);
                fadeTimer.stop();
            } else {
                dialog.setOpacity(opacity[0]);
            }
        });

        fadeTimer.start();
    }
    // สร้างปุ่มไอคอน
    private JButton createIconButton(String text, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // วาดพื้นหลังปุ่ม
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(180, 20, 20));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(220, 60, 60));
                } else {
                    g2d.setColor(new Color(180, 40, 40));
                }

                g2d.fillOval(0, 0, getWidth(), getHeight());

                // วาดขอบปุ่ม
                g2d.setColor(new Color(250, 250, 250, 100));
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawOval(1, 1, getWidth() - 2, getHeight() - 2);

                // วาดตัวอักษร X
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);
            }
        };

        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void showRules() {
        // สร้างหน้าต่างกฎเกม
        JDialog rulesDialog = new JDialog(this, "Game Rules", true);
        rulesDialog.setSize(900, 680);
        rulesDialog.setLocationRelativeTo(this);

        // เพิ่ม ESC key listener เพื่อปิดหน้าต่าง
        rulesDialog.getRootPane().registerKeyboardAction( e -> rulesDialog.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // สร้างพาเนลหลักพร้อมกับพื้นหลังแบบกำหนดเอง
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // พื้นหลังหลัก
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(30, 30, 40),
                        getWidth(), getHeight(), new Color(20, 20, 30));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // กรอบด้านในแบบขอบมน
                g2d.setColor(new Color(40, 40, 50));
                g2d.fillRoundRect(15, 15, getWidth() - 30, getHeight() - 30, 25, 25);

                // ขอบสีทอง
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.setColor(GOLD_ACCENT);
                g2d.drawRoundRect(15, 15, getWidth() - 30, getHeight() - 30, 25, 25);

                // ลายน้ำโลโก้กลาง
                Font logoFont = new Font("Arial", Font.BOLD, 180);
                g2d.setFont(logoFont);
                g2d.setColor(new Color(35, 35, 45));
                String logo = "MN";
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(logo, (getWidth() - fm.stringWidth(logo)) / 2, getHeight() / 2 + 60);

                // ลิขสิทธิ์
                g2d.setFont(COPYRIGHT_FONT);
                g2d.setColor(new Color(150, 150, 150));
                String copyright = "© 2023-2025 Mak Neeb Game. All Rights Reserved. KMITL Computer Science.";
                fm = g2d.getFontMetrics();
                g2d.drawString(copyright, (getWidth() - fm.stringWidth(copyright)) / 2, getHeight() - 25);
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 0));

        // ส่วนหัวด้านบน
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 15, 30));

        // หัวข้อหลัก
        JLabel titleLabel = new JLabel("MAK NEEB GAME RULES", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Cinzel", Font.BOLD, 36));
        titleLabel.setForeground(GOLD_ACCENT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // ปุ่มปิดที่มุมขวาบน
        JPanel closeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closeButtonPanel.setOpaque(false);
        JButton closeXButton = createIconButton("X", 30, 30);
        closeXButton.addActionListener(e -> rulesDialog.dispose());
        closeButtonPanel.add(closeXButton);
        headerPanel.add(closeButtonPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ส่วนเนื้อหากฎเกม
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        // สร้างแผงข้อความพร้อมกับรูปภาพประกอบ
        JPanel textWithImagesPanel = new JPanel();
        textWithImagesPanel.setLayout(new BoxLayout(textWithImagesPanel, BoxLayout.Y_AXIS));
        textWithImagesPanel.setOpaque(false);

        // ข้อความกฎเกม
        String rulesText = createDetailedRules();

        // แยกกฎเป็นส่วนๆ เพื่อเพิ่มรูปภาพประกอบ
        String[] sections = rulesText.split("\n\n");

        for (String section : sections) {
            addRuleSection(textWithImagesPanel, section);
        }

        // สร้าง ScrollPane ที่สวยงาม
        JScrollPane scrollPane = new JScrollPane(textWithImagesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ปรับแต่ง scrollbar
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUI(new ModernScrollBarUI());
        verticalBar.setPreferredSize(new Dimension(12, 0));

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // ส่วนด้านล่าง - มีปุ่มปิด
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JButton closeButton = createStyledButton("CLOSE");
        closeButton.setPreferredSize(new Dimension(200, 50));
        closeButton.addActionListener(e -> rulesDialog.dispose());

        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ตั้งค่า Dialog
        rulesDialog.setContentPane(mainPanel);
        rulesDialog.setUndecorated(true);

        // เพิ่ม Fade-in effect
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        fadeInDialog(rulesDialog, mainPanel);

        rulesDialog.setVisible(true);
    }

  
    // เพิ่มส่วนกฎแต่ละส่วนเข้าไปในพาเนล
    private void addRuleSection(JPanel panel, String sectionText) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BorderLayout(15, 10));
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setOpaque(false);

        // แปลงข้อความเป็น HTML เพื่อการแสดงผลที่สวยงาม
        String htmlText = "<html><body style='font-family:Lato,Arial,sans-serif; font-size:16px; color:#E3C6B5; width:560px;'>";

        // แยกหัวข้อและเนื้อหา
        String[] parts = sectionText.split("\n", 2);
        String title = parts[0];
        String content = parts.length > 1 ? parts[1] : "";

        htmlText += "<h2 style='color:#D4AF37; margin-top:5px; margin-bottom:10px;'>" + title + "</h2>";

        // แปลงเนื้อหาเป็น HTML list ถ้ามีหมายเลข
        if (content.contains("1.")) {
            htmlText += "<ul style='margin-left:20px; margin-top:5px;'>";
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty())
                    continue;

                // ตรวจสอบรูปแบบหมายเลข หรือ bullet
                if (line.matches("^\\d+\\..*")) {
                    // หมายเลข
                    htmlText += "<li style='margin-bottom:6px;'>" + line.substring(line.indexOf('.') + 1).trim()
                            + "</li>";
                } else if (line.startsWith("-")) {
                    // bullet
                    htmlText += "<li style='margin-bottom:6px;'>" + line.substring(1).trim() + "</li>";
                } else {
                    // ข้อความปกติ
                    htmlText += "<p style='margin:5px 0px;'>" + line.trim() + "</p>";
                }
            }
            htmlText += "</ul>";
        } else {
            // ข้อความปกติไม่มีรายการ
            htmlText += "<p style='margin:5px 0px;'>" + content.replace("\n", "<br>") + "</p>";
        }

        htmlText += "</body></html>";
        textPane.setText(htmlText);

        // เพิ่มภาพประกอบตามหัวข้อ (ถ้ามี)
        if (title.contains("CAPTURING") || title.contains("ATARI")) {
            // เพิ่มภาพประกอบการกินหมาก
            JPanel imagePanel = createRuleDiagram("capture");
            sectionPanel.add(imagePanel, BorderLayout.EAST);
        } else if (title.contains("BASIC RULES")) {
            // เพิ่มภาพประกอบกระดานเริ่มต้น
            JPanel imagePanel = createRuleDiagram("board");
            sectionPanel.add(imagePanel, BorderLayout.EAST);
        } else if (title.contains("MOVEMENT")) {
            // เพิ่มภาพประกอบการเคลื่อนที่
            JPanel imagePanel = createRuleDiagram("movement");
            sectionPanel.add(imagePanel, BorderLayout.EAST);
        }

        sectionPanel.add(textPane, BorderLayout.CENTER);
        panel.add(sectionPanel);

        // เพิ่มเส้นคั่น
        if (!title.contains("TIME RULES")) { // ไม่ต้องใส่เส้นคั่นหลังส่วนสุดท้าย
            JPanel separator = new JPanel();
            separator.setPreferredSize(new Dimension(panel.getWidth(), 1));
            separator.setBackground(new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 50));
            panel.add(separator);
        }
    }

    // สร้างภาพแผนผังประกอบกฎเกม
    private JPanel createRuleDiagram(String type) {
        JPanel diagramPanel = new JPanel() {
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
        };

        diagramPanel.setPreferredSize(new Dimension(200, 200));
        diagramPanel.setOpaque(false);
        return diagramPanel;
    }

    // วาดตัวอย่างการกินหมาก
    private void drawCaptureExample(Graphics2D g2d, int startX, int startY, int cellSize) {
        // วาดพื้นหลังตาราง 3x3
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                boolean isLight = (r + c) % 2 == 0;
                g2d.setColor(isLight ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR);
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
        g2d.fillOval(startX + 2 * cellSize + cellSize / 4, startY + cellSize + cellSize / 4, cellSize / 2,
                cellSize / 2);

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
                g2d.setColor(isLight ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR);
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
            g2d.fillOval(startX + c * cellSize + cellSize / 4, startY + 3 * cellSize + cellSize / 4, cellSize / 2,
                    cellSize / 2);
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
                g2d.setColor(isLight ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR);
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

    // คลาสสำหรับปรับแต่ง scrollbar ให้ดูทันสมัย
    private class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 100);
            this.thumbDarkShadowColor = null;
            this.thumbHighlightColor = null;
            this.thumbLightShadowColor = null;
            this.trackColor = new Color(30, 30, 40);
            this.trackHighlightColor = null;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (!thumbBounds.isEmpty() && this.scrollbar.isEnabled()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.translate(thumbBounds.x, thumbBounds.y);
                g2d.setColor(thumbColor);
                g2d.fillRoundRect(0, 0, thumbBounds.width, thumbBounds.height, 10, 10);
                g2d.translate(-thumbBounds.x, -thumbBounds.y);
            }
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }

    // สร้างกฎเกมที่ละเอียดยิ่งขึ้น
    private String createDetailedRules() {
        StringBuilder sb = new StringBuilder();

        sb.append("INTRODUCTION TO MAK NEEB GAME\n");
        sb.append(
                "Mak Neeb is a strategic board game combining elements of Go and Checkers. Players must think several moves ahead while trying to capture opponent's pieces and protect their own. The game offers a perfect balance of strategy, tactics, and planning.\n\n");

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
        sb.append(
                "1. Flanking Capture: If you place your piece so that enemy pieces are between your pieces on both sides (horizontally or vertically), those enemy pieces are captured and removed from the board\n");
        sb.append(
                "2. Chain Capture: If multiple enemy pieces are lined up, all can be captured at once if flanked by your pieces\n");
        sb.append("3. Atari: When an opponent's piece has no liberties (no adjacent empty spaces) it is captured\n\n");

        sb.append("EXAMPLE CAPTURES\n");
        sb.append("- Direct Capture: ⚫ ⚪ ⚫ → The white piece is captured\n");
        sb.append("- Line Capture: ⚫ ⚪ ⚪ ⚪ ⚫ → All white pieces are captured\n");
        sb.append("- Atari Capture: When a piece is completely surrounded with no escape\n\n");

        sb.append("TIME RULES\n");
        sb.append("1. Each player has 10 minutes total game time\n");
        sb.append("2. Clock only runs during a player's turn\n");
        sb.append("3. If a player's time runs out, they lose the game immediately\n");
        sb.append("4. No added time or overtime is allowed\n\n");

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

    @Override
    public void dispose() {
        if (backgroundTimer != null) {
            backgroundTimer.stop();
        }
        super.dispose();
    }
}
