package main;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ui.GameFrame;
import ui.StartScreen;
import utils.ResourceManager;

public class GameLauncher {
    
    private static StartScreen startScreen;
    private static GameFrame gameFrame;
    private static boolean consoleVisible = false; 
    
    public static void launch() {
        if (!consoleVisible) {
            redirectOutputToFile();
        }
        
        SwingUtilities.invokeLater(() -> {
            startScreen = new StartScreen();
            setAppIcon(startScreen);
        });
    }
    
    private static void redirectOutputToFile() {
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            File logFile = new File("logs/app_" + timestamp + ".log");
            
            PrintStream fileOut = new PrintStream(new FileOutputStream(logFile, true));
            System.setOut(fileOut);
            System.setErr(fileOut);
            
            System.out.println("=== Application Started at " + new java.util.Date() + " ===");
        } catch (Exception e) {
            System.err.println("ไม่สามารถเบี่ยงเบนการแสดงผลได้: " + e.getMessage());
        }
    }
    

    public static void toggleConsole() {
        consoleVisible = !consoleVisible;
        
        if (consoleVisible) {
            System.setOut(System.out);
            System.setErr(System.err);
            System.out.println("Console output restored");
        } else {
            redirectOutputToFile();
        }
    }
    
    public static boolean isConsoleVisible() {
        return consoleVisible;
    }
    
    private static void setAppIcon(JFrame frame) {
        try {
            Image appIcon = ResourceManager.loadImage("icon/AppIcon.png");
            
            if (appIcon != null) {
                List<Image> icons = new ArrayList<>();
                
                icons.add(appIcon.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
                icons.add(appIcon.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
                icons.add(appIcon.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
                icons.add(appIcon.getScaledInstance(128, 128, Image.SCALE_SMOOTH));
                
                frame.setIconImages(icons);
            } else {
                Image fallbackIcon = ResourceManager.loadImage("icon/ICONKMITL.png");
                if (fallbackIcon != null) {
                    frame.setIconImage(fallbackIcon);
                }
            }
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการตั้งค่าไอคอนแอพพลิเคชั่น: " + e.getMessage());
        }
    }
    
    public static void startGame() {
        if (startScreen != null) {
            startScreen.dispose();
        }
        
        gameFrame = new GameFrame();
        setAppIcon(gameFrame);
    }
}