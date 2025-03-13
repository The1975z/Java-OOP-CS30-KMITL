package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    
    private final Color BACKGROUND_COLOR = new Color(20, 20, 20);
    private final Color GOLD_ACCENT = new Color(212, 175, 55);
    private final Color TEXT_COLOR = new Color(227, 198, 181);
    private final Color BUTTON_COLOR = new Color(139, 69, 19); 
    private final Color BUTTON_HOVER = new Color(160, 82, 45); 
    private final Font COPYRIGHT_FONT = new Font("Arial", Font.ITALIC, 12);
    private final Font BUTTON_FONT = new Font("Lato", Font.BOLD, 16); 

    private JButton timeButton;
    private Timer timeUpdateTimer;
    
    private JLabel logoLabel;
    private JLabel leftRokLabel;
    private JLabel rightRokLabel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JButton playButton;
    private JButton rulesButton;
    private JButton teamButton;
    private JButton exitButton;
    private JPanel soundControlPanel;
    private JLabel versionLabel;
    private JButton soundToggleButton;
    private JButton volumeUpButton;
    private JButton volumeDownButton;

    public BackgroundPanel(StartScreen parent, List<String> backgroundPaths, int currentBackgroundIndex) {
        this.parent = parent;
        this.backgroundPaths = backgroundPaths;
        this.currentBackgroundIndex = currentBackgroundIndex;
        setLayout(null);
        loadCurrentBackground();
        createContent();
        setupTimeDisplay(); 
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                updateComponentPositions();
            }
        });
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

        int panelWidth = Math.min(getWidth() / 2, 800);
        int panelHeight = Math.min(getHeight() * 3 / 4, 900); 
        int panelX = (getWidth() - panelWidth) / 2;
        int panelY = (getHeight() - panelHeight) / 2;

        g2d.setColor(new Color(10, 10, 10, 220));
        g2d.fill(new RoundRectangle2D.Double(panelX, panelY, panelWidth, panelHeight, 40, 40));

        g2d.setStroke(new BasicStroke(3f));
        g2d.setColor(new Color(GOLD_ACCENT.getRed(), GOLD_ACCENT.getGreen(), GOLD_ACCENT.getBlue(), 120));
        g2d.draw(new RoundRectangle2D.Double(panelX + 5, panelY + 5, panelWidth - 10, panelHeight - 10, 30, 30));

        float fontScale = Math.min(getWidth() / 1920f, getHeight() / 1080f);
        Font scaledCopyrightFont = new Font(COPYRIGHT_FONT.getFamily(), COPYRIGHT_FONT.getStyle(), 
                                           Math.max(10, (int)(COPYRIGHT_FONT.getSize() * fontScale)));
        
        g2d.setFont(scaledCopyrightFont);
        g2d.setColor(new Color(200, 200, 200, 180));
        String copyright = "© 2025 Clamp War Game. All Rights Reserved. KMITL Computer Science.";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(copyright);
        g2d.drawString(copyright, (getWidth() - textWidth) / 2, getHeight() - 20);
    }
    
    private void createContent() {
        ImageIcon logoIcon = ResourceManager.loadIcon("icon/ICONKMITL.png", 140, 140);
        ImageIcon rokIcon = ResourceManager.loadIcon("Rok/Rok.png", 80, 80);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        int centerPanelWidth = Math.min(panelWidth / 2, 800);
        int centerPanelHeight = Math.min(panelHeight * 3 / 4, 900);
        int centerPanelX = (panelWidth - centerPanelWidth) / 2;
        int centerPanelY = (panelHeight - centerPanelHeight) / 2 + 25;

        if (logoIcon != null) {
            logoLabel = new JLabel(logoIcon);
            logoLabel.setBounds(centerPanelX + (centerPanelWidth - 140) / 2, centerPanelY + 40, 140, 140);
            add(logoLabel);
        }

        if (rokIcon != null) {
            leftRokLabel = new JLabel(rokIcon);
            leftRokLabel.setBounds(centerPanelX + 60, centerPanelY + 170, 80, 80);
            add(leftRokLabel);

            rightRokLabel = new JLabel(rokIcon);
            rightRokLabel.setBounds(centerPanelX + centerPanelWidth - 140, centerPanelY + 170, 80, 80);
            add(rightRokLabel);
        }

        titleLabel = new JLabel("Clamp War", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Trajan Pro", Font.BOLD, 52));
        titleLabel.setForeground(GOLD_ACCENT);
        titleLabel.setBounds(centerPanelX, centerPanelY + 190, centerPanelWidth, 60);
        add(titleLabel);

        subtitleLabel = new JLabel("Custom Go Game with 8 Rok Pieces", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Lato", Font.PLAIN, 18));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setBounds(centerPanelX, centerPanelY + 250, centerPanelWidth, 30);
        add(subtitleLabel);

        int buttonWidth = Math.min(300, centerPanelWidth - 100);
        
        playButton = parent.createStyledButton("PLAY GAME");
        playButton.setBounds(centerPanelX + (centerPanelWidth - buttonWidth) / 2, centerPanelY + 320, buttonWidth, 60);
        playButton.addActionListener(e -> {
            SoundManager.getInstance().stopSound("SongGame");
            SoundManager.getInstance().playSound("start");
            GameLauncher.startGame();
            parent.dispose();
        });
        add(playButton);

        rulesButton = parent.createStyledButton("GAME RULES");
        rulesButton.setBounds(centerPanelX + (centerPanelWidth - buttonWidth) / 2, centerPanelY + 400, buttonWidth, 60);
        rulesButton.addActionListener(e -> parent.showRules());
        add(rulesButton);

        teamButton = parent.createStyledButton("TEAM MEMBERS");
        teamButton.setBounds(centerPanelX + (centerPanelWidth - buttonWidth) / 2, centerPanelY + 480, buttonWidth, 60);
        teamButton.addActionListener(e -> TeamMembersPanel.showTeamMembersDialog(this, BACKGROUND_COLOR, GOLD_ACCENT, TEXT_COLOR));
        add(teamButton);
        
        exitButton = parent.createStyledButton("EXIT GAME");
        exitButton.setBounds(centerPanelX + (centerPanelWidth - buttonWidth) / 2, centerPanelY + 560, buttonWidth, 60);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        soundControlPanel = new JPanel();
        soundControlPanel.setLayout(null);
        soundControlPanel.setOpaque(false);
        soundControlPanel.setBounds(centerPanelX + centerPanelWidth - 240, centerPanelY + 40, 220, 60);
        
        soundToggleButton = parent.createSoundButton(parent.soundEnabled ? "SOUND ON" : "SOUND OFF", 120, 50);
        soundToggleButton.setBounds(0, 0, 120, 50);
        soundToggleButton.addActionListener(e -> {
            parent.soundEnabled = !parent.soundEnabled;
            SoundManager.getInstance().setSoundEnabled(parent.soundEnabled);
            
            soundToggleButton.setText(parent.soundEnabled ? "SOUND ON" : "SOUND OFF");
            
            if (parent.soundEnabled) {
                SoundManager.getInstance().loopSound("SongGame");
                SoundManager.getInstance().setVolume("SongGame", parent.currentVolume);
            }
            
            parent.blinkButton(soundToggleButton);
        });
        
        volumeUpButton = parent.createSoundButton("+", 50, 25);
        volumeUpButton.setBounds(130, 0, 50, 25);
        volumeUpButton.addActionListener(e -> {
            if (parent.currentVolume < 1.0f) {
                parent.currentVolume = Math.min(1.0f, parent.currentVolume + 0.1f);
                SoundManager.getInstance().setVolume("SongGame", parent.currentVolume);
                SoundManager.getInstance().playSound("select");
                
                parent.showVolumeIndicator(parent.currentVolume);
            }
        });
        
        volumeDownButton = parent.createSoundButton("-", 50, 25);
        volumeDownButton.setBounds(130, 30, 50, 25);
        volumeDownButton.addActionListener(e -> {
            if (parent.currentVolume > 0.0f) {
                parent.currentVolume = Math.max(0.0f, parent.currentVolume - 0.1f);
                SoundManager.getInstance().setVolume("SongGame", parent.currentVolume);
                SoundManager.getInstance().playSound("select");
                parent.showVolumeIndicator(parent.currentVolume);
            }
        });
        
        soundControlPanel.add(soundToggleButton);
        soundControlPanel.add(volumeUpButton);
        soundControlPanel.add(volumeDownButton);
        add(soundControlPanel);

        versionLabel = new JLabel("Version 1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Lato", Font.PLAIN, 26));
        versionLabel.setForeground(new Color(150, 150, 150));
        versionLabel.setBounds(centerPanelX, centerPanelY + centerPanelHeight - 30, centerPanelWidth, 70); 
        add(versionLabel);
    }

    private void setupTimeDisplay() {
        timeButton = new JButton("Current Time: --:--");
        timeButton.setFont(BUTTON_FONT);
        timeButton.setForeground(Color.WHITE);
        timeButton.setBackground(BUTTON_COLOR);
        timeButton.setFocusPainted(false);
        timeButton.setBorderPainted(false);
        timeButton.setOpaque(true);
        timeButton.setPreferredSize(new Dimension(200, 40));
    
        timeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                timeButton.setBackground(BUTTON_HOVER);
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                timeButton.setBackground(BUTTON_COLOR);
            }
        });
    
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        int centerPanelWidth = Math.min(panelWidth / 2, 800);
        int centerPanelHeight = Math.min(panelHeight * 3 / 4, 900);
        int centerPanelX = (panelWidth - centerPanelWidth) / 2;
        int centerPanelY = (panelHeight - centerPanelHeight) / 2 + 25;
    
        timeButton.setBounds(centerPanelX + centerPanelWidth - 220, centerPanelY + centerPanelHeight - 80, 200, 40);
        add(timeButton);
    
        timeUpdateTimer = new Timer(1000, e -> updateTime());
        timeUpdateTimer.start();
    }
    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
        String currentTime = sdf.format(new Date());
        timeButton.setText("Current Time: " + currentTime);
    }
    
    private void updateComponentPositions() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        int centerPanelWidth = Math.min(panelWidth / 2, 800);
        int centerPanelHeight = Math.min(panelHeight * 3 / 4, 900);
        int centerPanelX = (panelWidth - centerPanelWidth) / 2;
        int centerPanelY = (panelHeight - centerPanelHeight) / 2 + 25;
        
        float scaleX = (float) panelWidth / 1920;
        float scaleY = (float) panelHeight / 1080;
        float scale = Math.min(scaleX, scaleY);
        

        if (logoLabel != null) {
            int logoSize = (int) (140 * scale);
            logoSize = Math.max(60, logoSize); 
            logoLabel.setBounds(centerPanelX + (centerPanelWidth - logoSize) / 2,  centerPanelY + (int)(40 * scale),  logoSize, logoSize);
        }
        
        if (leftRokLabel != null && rightRokLabel != null) {
            int rokSize = (int) (80 * scale);
            rokSize = Math.max(40, rokSize);
            
            leftRokLabel.setBounds(centerPanelX + (int)(60 * scale),  centerPanelY + (int)(170 * scale),  rokSize, rokSize);
            
            rightRokLabel.setBounds(centerPanelX + centerPanelWidth - rokSize - (int)(60 * scale),  centerPanelY + (int)(170 * scale),  rokSize, rokSize);
        }
        
        if (titleLabel != null) {
            int titleFontSize = (int) (52 * scale);
            titleFontSize = Math.max(24, titleFontSize); 
            titleLabel.setFont(new Font("Trajan Pro", Font.BOLD, titleFontSize));
            titleLabel.setBounds(centerPanelX,  centerPanelY + (int)(190 * scale),  centerPanelWidth, (int)(60 * scale));
        }
        if (timeButton != null) {
            int timeButtonWidth = (int) (200 * scale);
            int timeButtonHeight = (int) (40 * scale);
            timeButtonHeight = Math.max(30, timeButtonHeight);
            
            timeButton.setBounds(centerPanelX + centerPanelWidth - timeButtonWidth - (int)(20 * scale), 
                                centerPanelY + centerPanelHeight - timeButtonHeight - (int)(40 * scale), 
                                timeButtonWidth, timeButtonHeight);
            
            int timeFontSize = (int) (BUTTON_FONT.getSize() * scale);
            timeFontSize = Math.max(12, timeFontSize);
            timeButton.setFont(new Font(BUTTON_FONT.getFamily(), BUTTON_FONT.getStyle(), timeFontSize));
        }
        if (subtitleLabel != null) {
            int subtitleFontSize = (int) (18 * scale);
            subtitleFontSize = Math.max(12, subtitleFontSize); 
            subtitleLabel.setFont(new Font("Lato", Font.PLAIN, subtitleFontSize));
            subtitleLabel.setBounds(centerPanelX,  centerPanelY + (int)(250 * scale),  centerPanelWidth, (int)(30 * scale));
        }
        
        int buttonWidth = Math.min(300, centerPanelWidth - 100);
        int buttonHeight = (int) (60 * scale);
        buttonHeight = Math.max(40, buttonHeight); 
        
        if (playButton != null) {
            playButton.setBounds(centerPanelX + (centerPanelWidth - buttonWidth) / 2,  centerPanelY + (int)(320 * scale),  buttonWidth, buttonHeight);
        }
        
        if (rulesButton != null) {
            rulesButton.setBounds(centerPanelX + (centerPanelWidth - buttonWidth) / 2,  centerPanelY + (int)(400 * scale),  buttonWidth, buttonHeight);
        }
        
        if (teamButton != null) {
            teamButton.setBounds(centerPanelX + (centerPanelWidth - buttonWidth) / 2,  centerPanelY + (int)(480 * scale),  buttonWidth, buttonHeight);
        }
        
        if (exitButton != null) {
            exitButton.setBounds(centerPanelX + (centerPanelWidth - buttonWidth) / 2,  centerPanelY + (int)(560 * scale),  buttonWidth, buttonHeight);
        }
        
        if (soundControlPanel != null) {
            int controlPanelWidth = (int) (220 * scale);
            int controlPanelHeight = (int) (60 * scale);
            
            soundControlPanel.setBounds(centerPanelX + centerPanelWidth - controlPanelWidth - (int)(20 * scale),  centerPanelY + (int)(40 * scale),  controlPanelWidth, controlPanelHeight);
            
            if (soundToggleButton != null) {
                int soundButtonWidth = (int) (120 * scale);
                int soundButtonHeight = (int) (50 * scale);
                soundToggleButton.setBounds(0, 0, soundButtonWidth, soundButtonHeight);
            }
            
            if (volumeUpButton != null && volumeDownButton != null) {
                int volumeButtonWidth = (int) (50 * scale);
                int volumeButtonHeight = (int) (25 * scale);
                
                volumeUpButton.setBounds((int)(130 * scale), 0, volumeButtonWidth, volumeButtonHeight);
                volumeDownButton.setBounds((int)(130 * scale), (int)(30 * scale), volumeButtonWidth, volumeButtonHeight);
            }
        }
        
        if (versionLabel != null) {
            int versionFontSize = (int) (26 * scale);
            versionFontSize = Math.max(14, versionFontSize);
            versionLabel.setFont(new Font("Lato", Font.PLAIN, versionFontSize));
            versionLabel.setBounds(centerPanelX, centerPanelY + centerPanelHeight - (int)(70 * scale), centerPanelWidth, (int)(70 * scale));
        }
        
        revalidate();
        repaint();
    }
}
