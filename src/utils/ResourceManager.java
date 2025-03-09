package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public class ResourceManager {
    
    public static Image loadImage(String relativePath) {
        try {
            Image image = null;
            
            InputStream is = ResourceManager.class.getClassLoader().getResourceAsStream("assets/" + relativePath);
            if (is != null) {
                System.out.println("โหลดไฟล์จาก classpath: assets/" + relativePath);
                image = ImageIO.read(is);
                if (image != null) return image;
            }
            
            String[] possiblePaths = {
                "assets/" + relativePath,
                "../assets/" + relativePath,
                "../../assets/" + relativePath,
                "../../../assets/" + relativePath,
                "C:/Users/PC/Desktop/Programing/Project Java/MakNeebGame/assets/" + relativePath,
                "./" + relativePath,
                relativePath
            };
            
            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    System.out.println("โหลดไฟล์จาก: " + file.getAbsolutePath());
                    image = ImageIO.read(file);
                    if (image != null) return image;
                }
            }
            
            System.err.println("ไม่พบไฟล์: " + relativePath);
            
            if (relativePath.contains("background")) {
                BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillRect(0, 0, 800, 600);
                g2d.dispose();
                return img;
            } else if (relativePath.contains("Rok")) {
                BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(Color.BLACK);
                g2d.fillOval(5, 5, 40, 40);
                g2d.dispose();
                return img;
            } else if (relativePath.contains("Button")) {
                BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(0, 0, 32, 32);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(0, 0, 31, 31);
                g2d.dispose();
                return img;
            } else if (relativePath.contains("ICONKMITL")) {
                BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(Color.ORANGE);
                g2d.fillOval(4, 4, 56, 56);
                g2d.dispose();
                return img;
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการโหลดรูปภาพ: " + relativePath);
            e.printStackTrace();
            return null;
        }
    }

    public static Image loadImage(String relativePath, int width, int height) {
        Image image = loadImage(relativePath);
        if (image != null) {
            return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }
        return null;
    }

    public static ImageIcon loadIcon(String relativePath, int width, int height) {
        Image image = loadImage(relativePath, width, height);
        if (image != null) {
            return new ImageIcon(image);
        }
        return null;
    }

    public static File loadFile(String relativePath) {
        String[] possiblePaths = {
            "assets/" + relativePath,
            "../assets/" + relativePath,
            "../../assets/" + relativePath,
            "../../../assets/" + relativePath,
            "../../../../assets/" + relativePath,
            "C:/Users/PC/Desktop/Programing/Project Java/MakNeebGame/assets/" + relativePath, 
            "D:/Games/MakNeebGame/assets/" + relativePath, 
            "E:/Projects/MakNeebGame/assets/" + relativePath, 
            System.getenv("APPDATA") + "/MakNeebGame/assets/" + relativePath, 
            System.getenv("LOCALAPPDATA") + "/MakNeebGame/assets/" + relativePath, 
            System.getProperty("user.dir") + "/assets/" + relativePath, 
            System.getProperty("user.home") + "/MakNeebGame/assets/" + relativePath, 
            System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH") + "/MakNeebGame/assets/" + relativePath, 
            "./" + relativePath,
            "/" + relativePath, 
            relativePath 
        };
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                System.out.println("พบไฟล์ที่: " + file.getAbsolutePath());
                return file;
            }
        }
        System.err.println("ไม่พบไฟล์: " + relativePath);
        return null;
    }
}