package utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class SoundManager {

    private static SoundManager instance;
    private Map<String, Clip> soundCache = new HashMap<>();
    private boolean soundEnabled = true;

    private final String SOUND_PATH = "sound/";
    
    private SoundManager() {
        preloadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void preloadSounds() {
        loadSound("select", "select.wav");
        loadSound("move", "move.wav");
        loadSound("capture", "capture.wav");
        loadSound("start", "start.wav");
        loadSound("error", "error.wav");
        loadSound("victory", "victory.wav");
        loadSound("SongGame", "SongGame.wav");
    }

    private void loadSound(String name, String filename) {
        try {
            // ใช้ ResourceManager เพื่อหาไฟล์เสียง
            File soundFile = ResourceManager.loadFile(SOUND_PATH + filename);
            
            if (soundFile == null || !soundFile.exists()) {
                System.err.println("ไม่พบไฟล์เสียง: " + SOUND_PATH + filename);
                return;
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            soundCache.put(name, clip);
            System.out.println("โหลดไฟล์เสียงสำเร็จ: " + filename);
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("เกิดข้อผิดพลาดในการโหลดเสียง: " + filename);
            e.printStackTrace();
        }
    }

    public void playSound(String name) {
        if (!soundEnabled) {
            return;
        }

        Clip clip = soundCache.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
            System.out.println("เล่นเสียง: " + name);
        } else {
            System.err.println("ไม่พบเสียง: " + name + " ในแคช");
            
            // ลองโหลดเสียงอีกครั้ง
            if (name.equals("move")) loadSound(name, "move.wav");
            else if (name.equals("capture")) loadSound(name, "capture.wav");
            else if (name.equals("start")) loadSound(name, "start.wav");
            else if (name.equals("error")) loadSound(name, "error.wav");
            else if (name.equals("victory")) loadSound(name, "victory.wav");
            else if (name.equals("select")) loadSound(name, "select.wav");
            else if (name.equals("SongGame")) loadSound(name, "SongGame.wav");
            
            // ลองเล่นอีกครั้งหลังจากโหลดใหม่
            clip = soundCache.get(name);
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
                System.out.println("เล่นเสียงหลังจากโหลดใหม่: " + name);
            }
        }
    }
    
    public void loopSound(String name) {
        if (!soundEnabled) {
            return;
        }

        Clip clip = soundCache.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            System.out.println("เล่นเสียงแบบวนซ้ำ: " + name);
        } else {
            System.err.println("ไม่พบเสียง: " + name + " ในแคช");
            
            // ลองโหลดเสียงอีกครั้ง (เฉพาะ SongGame ที่มักจะใช้ loop)
            if (name.equals("SongGame")) {
                loadSound(name, "SongGame.wav");
                clip = soundCache.get(name);
                if (clip != null) {
                    clip.setFramePosition(0);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    System.out.println("เล่นเสียงแบบวนซ้ำหลังจากโหลดใหม่: " + name);
                }
            }
        }
    }
    
    // Add a new method to handle game over sound management
    public void handleGameOver() {
        // Stop any background music that might be playing
        stopSound("SongGame");
        // Play victory sound
        playSound("victory");
        System.out.println("Game over sound effects applied");
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        System.out.println("เสียง: " + (enabled ? "เปิด" : "ปิด"));
        if (!enabled) {
            stopAll();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void stopAll() {
        for (Clip clip : soundCache.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
        System.out.println("หยุดเสียงทั้งหมด");
    }
    
    public void stopSound(String name) {
        Clip clip = soundCache.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
            System.out.println("หยุดเสียง: " + name);
        }
    }
    
    public void pauseSound(String name) {
        Clip clip = soundCache.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
            System.out.println("หยุดเสียงชั่วคราว: " + name);
        }
    }
    
    public void resumeSound(String name) {
        if (!soundEnabled) {
            return;
        }
        
        Clip clip = soundCache.get(name);
        if (clip != null && !clip.isRunning()) {
            clip.start();
            System.out.println("เล่นเสียงต่อ: " + name);
        }
    }
    
    public void setVolume(String name, float volume) {
        Clip clip = soundCache.get(name);
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (volume <= 0.0f) ? -80.0f : (float) (20.0 * Math.log10(volume));
            gainControl.setValue(dB);
            System.out.println("ตั้งความดังเสียง " + name + ": " + volume);
        }
    }
    
    public boolean isPlaying(String name) {
        Clip clip = soundCache.get(name);
        return clip != null && clip.isRunning();
    }
}