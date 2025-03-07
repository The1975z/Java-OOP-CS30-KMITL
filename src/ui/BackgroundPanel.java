package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import main.GameLauncher;
import utils.ResourceManager;
import utils.SoundManager;

public class BackgroundPanel extends JPanel {

    private Image currentBackground;
    private Image nextBackground;
    private float alpha = 0f;
    private boolean transitioning = false;
    private Timer transitionTimer;
    private final StartScreen parent;
    private final List<String> backgroundPaths;
    private int currentBackgroundIndex;
    
    public BackgroundPanel(StartScreen parent, List<String> backgroundPaths, int currentBackgroundIndex) {
        this.parent = parent;
        this.backgroundPaths = backgroundPaths;
        this.currentBackgroundIndex = currentBackgroundIndex;
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

        Color BACKGROUND_COLOR = new Color(20, 20, 20);
        Color GOLD_ACCENT = new Color(212, 175, 55);
        Font COPYRIGHT_FONT = new Font("Arial", Font.ITALIC, 12);
        
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
        titleLabel.setFont(new Font("Trajan Pro", Font.BOLD, 52));
        titleLabel.setForeground(new Color(212, 175, 55));
        titleLabel.setBounds(centerPanelX, centerPanelY + 190, centerPanelWidth, 60);
        add(titleLabel);

        JLabel subtitleLabel = new JLabel("Custom Go Game with 8 Rok Pieces", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Lato", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(227, 198, 181));
        subtitleLabel.setBounds(centerPanelX, centerPanelY + 250, centerPanelWidth, 30);
        add(subtitleLabel);

        JButton playButton = parent.createStyledButton("PLAY GAME");
        playButton.setBounds(centerPanelX + (centerPanelWidth - 300) / 2, centerPanelY + 320, 300, 60);
        playButton.addActionListener(e -> {
            SoundManager.getInstance().stopSound("SongGame");
            SoundManager.getInstance().playSound("start");
            GameLauncher.startGame();
            parent.dispose();
        });
        add(playButton);

        JButton rulesButton = parent.createStyledButton("GAME RULES");
        rulesButton.setBounds(centerPanelX + (centerPanelWidth - 300) / 2, centerPanelY + 400, 300, 60);
        rulesButton.addActionListener(e -> parent.showRules());
        add(rulesButton);

        JButton exitButton = parent.createStyledButton("EXIT GAME");
        exitButton.setBounds(centerPanelX + (centerPanelWidth - 300) / 2, centerPanelY + 480, 300, 60);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        // Panel สำหรับปุ่มควบคุมเสียง
        JPanel soundControlPanel = new JPanel();
        soundControlPanel.setLayout(null);
        soundControlPanel.setOpaque(false);
        soundControlPanel.setBounds(centerPanelX + centerPanelWidth - 240, centerPanelY + 40, 220, 60);
        
        // ปุ่มเปิด/ปิดเสียง
        JButton soundToggleButton = parent.createSoundButton(parent.soundEnabled ? "SOUND ON" : "SOUND OFF", 120, 50);
        soundToggleButton.setBounds(0, 0, 120, 50);
        soundToggleButton.addActionListener(e -> {
            parent.soundEnabled = !parent.soundEnabled;
            SoundManager.getInstance().setSoundEnabled(parent.soundEnabled);
            
            // เปลี่ยนข้อความและสีของปุ่ม
            soundToggleButton.setText(parent.soundEnabled ? "SOUND ON" : "SOUND OFF");
            
            if (parent.soundEnabled) {
                SoundManager.getInstance().loopSound("SongGame");
                SoundManager.getInstance().setVolume("SongGame", parent.currentVolume);
            }
            
            // ทำเอฟเฟค blink เมื่อเปลี่ยนสถานะ
            parent.blinkButton(soundToggleButton);
        });
        
        // ปุ่มเพิ่มเสียง
        JButton volumeUpButton = parent.createSoundButton("+", 50, 25);
        volumeUpButton.setBounds(130, 0, 50, 25);
        volumeUpButton.addActionListener(e -> {
            if (parent.currentVolume < 1.0f) {
                parent.currentVolume = Math.min(1.0f, parent.currentVolume + 0.1f);
                SoundManager.getInstance().setVolume("SongGame", parent.currentVolume);
                SoundManager.getInstance().playSound("select");
                
                // แสดงระดับเสียงบนหน้าจอ
                parent.showVolumeIndicator(parent.currentVolume);
            }
        });
        
        // ปุ่มลดเสียง
        JButton volumeDownButton = parent.createSoundButton("-", 50, 25);
        volumeDownButton.setBounds(130, 30, 50, 25);
        volumeDownButton.addActionListener(e -> {
            if (parent.currentVolume > 0.0f) {
                parent.currentVolume = Math.max(0.0f, parent.currentVolume - 0.1f);
                SoundManager.getInstance().setVolume("SongGame", parent.currentVolume);
                SoundManager.getInstance().playSound("select");
                
                // แสดงระดับเสียงบนหน้าจอ
                parent.showVolumeIndicator(parent.currentVolume);
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
