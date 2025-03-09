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
    public boolean soundEnabled = true;
    public float currentVolume = 1.0f;
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
        
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, screenWidth, screenHeight);
        setContentPane(layeredPane);
        
        TitleBar titleBar = new TitleBar(this);
        titleBar.setBounds(0, 0, screenWidth, 30);
        layeredPane.add(titleBar, JLayeredPane.DEFAULT_LAYER);

        backgroundPanel = new BackgroundPanel(this, backgroundPaths, currentBackgroundIndex);
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
    
    public void blinkButton(JButton button) {
        Timer blinkTimer = new Timer(100, null);
        final int[] count = {0};
        
        blinkTimer.addActionListener(e -> {
            button.setVisible(!button.isVisible());
            count[0]++;
            if (count[0] >= 6) { 
                button.setVisible(true);
                blinkTimer.stop();
            }
        });
        
        blinkTimer.start();
    }
    
    public void showVolumeIndicator(float volume) {
        Component[] components = layeredPane.getComponentsInLayer(JLayeredPane.POPUP_LAYER);
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals("volumeIndicator")) {
                layeredPane.remove(comp);
            }
        }
        
        JPanel volumeIndicator = new VolumeIndicatorPanel(volume, GOLD_ACCENT);
        volumeIndicator.setName("volumeIndicator");
        
        Dimension screenSize = getSize();
        int width = 350;
        int height = 60;
        volumeIndicator.setBounds((screenSize.width - width) / 2, screenSize.height - 120, width, height);
        
        layeredPane.add(volumeIndicator, JLayeredPane.POPUP_LAYER);
        layeredPane.setLayer(volumeIndicator, JLayeredPane.POPUP_LAYER);
        volumeIndicator.setVisible(true);
        layeredPane.repaint();
        
        Timer fadeTimer = new Timer(2000, e -> {
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

    public JButton createStyledButton(String text) {
        JButton button = new StyledButton(text, BUTTON_COLOR, BUTTON_HOVER_COLOR, GOLD_ACCENT, TEXT_COLOR, BUTTON_FONT);
        return button;
    }
    
    public JButton createSoundButton(String text, int width, int height) {
        JButton button;
        if (text.equals("SOUND ON") || text.equals("SOUND OFF")) {
            button = new SoundToggleButton(text, width, height, GOLD_ACCENT);
        } else {
            button = new VolumeButton(text, width, height, GOLD_ACCENT);
        }
        return button;
    }
    
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
    
    public JButton createIconButton(String text, int width, int height) {
        JButton button = new IconButton(text, width, height);
        return button;
    }

    public void showRules() {
        RulesDialog rulesDialog = new RulesDialog(this, GOLD_ACCENT, TEXT_COLOR, COPYRIGHT_FONT, 
                                                LIGHT_SQUARE_COLOR, DARK_SQUARE_COLOR);
        fadeInDialog(rulesDialog, rulesDialog.getMainPanel());
    }

    public JPanel createRuleDiagram(String type) {
        JPanel diagramPanel = new GameRuleDiagramPanel(type, LIGHT_SQUARE_COLOR, DARK_SQUARE_COLOR);
        return diagramPanel;
    }

    private String createDetailedRules() {
        StringBuilder sb = new StringBuilder();

        sb.append("INTRODUCTION TO MAK NEEB GAME\n");
        sb.append("Mak Neeb is a strategic board game combining elements of Go and Checkers. Players must think several moves ahead while trying to capture opponent's pieces and protect their own. The game offers a perfect balance of strategy, tactics, and planning.\n\n");
        // ยังไม่ได้แก้ ของโรมัน
        // ...existing code...

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
