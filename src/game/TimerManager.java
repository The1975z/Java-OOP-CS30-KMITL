package game;

import board.Board;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import ui.ClockIcon;
import utils.SoundManager;

public class TimerManager implements ActionListener {

    private final Board gameBoard;
    private final SoundManager soundManager;
    private int whiteTimeSeconds = 10 * 60;
    private int blackTimeSeconds = 10 * 60;
    private Timer timer;
    private boolean isWhiteTurn;

    private JLabel timerLabel;
    private JPanel timerPanel;
    private ClockIcon clockIcon;

    public TimerManager(Board board, boolean initialTurnIsWhite) {
        this.gameBoard = board;
        this.soundManager = SoundManager.getInstance();
        this.isWhiteTurn = initialTurnIsWhite;

        createTimerUI();
        startTimer();
    }

    private void createTimerUI() {
        timerPanel = new JPanel();
        timerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        timerPanel.setBackground(Color.BLACK);

        clockIcon = new ClockIcon(24);
        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(Color.WHITE);

        timerPanel.add(clockIcon);
        timerPanel.add(timerLabel);
    }

    public JPanel getTimerPanel() {
        return timerPanel;
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer(1000, this);
            timer.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isWhiteTurn) {
            whiteTimeSeconds--;
            if (whiteTimeSeconds <= 0) {
                endGameDueToTime("BLACK");
            }
        } else {
            blackTimeSeconds--;
            if (blackTimeSeconds <= 0) {
                endGameDueToTime("WHITE");
            }
        }
        updateTimerDisplay();
    }

    public void updateTimerDisplay() {
        String whiteTime = formatTime(whiteTimeSeconds);
        String blackTime = formatTime(blackTimeSeconds);

        String displayTime = isWhiteTurn ? "White: " + whiteTime : "Black: " + blackTime;
        timerLabel.setText(displayTime);
        clockIcon.setTurn(isWhiteTurn);
        timerPanel.repaint();

        System.out.println("White Time: " + whiteTime + " | Black Time: " + blackTime);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public void switchTurn() {
        isWhiteTurn = !isWhiteTurn;
        gameBoard.setWhiteTurn(isWhiteTurn);
        updateTimerDisplay();
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    private void endGameDueToTime(String winner) {
        stopGameTimer(); // Replace direct timer.stop() with our new method
        soundManager.playSound("victory");

        Color bgColor = winner.equals("WHITE") ? Color.WHITE : Color.BLACK;
        Color textColor = winner.equals("WHITE") ? Color.BLACK : Color.WHITE;
        String iconText = winner.equals("WHITE") ? "🏆" : "❌";

        JPanel panel = new JPanel();
        panel.setBackground(bgColor);
        panel.setLayout(new GridBagLayout());

        JLabel messageLabel = new JLabel(iconText + " " + winner + " WINS!");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 26));
        messageLabel.setForeground(textColor);

        JLabel detailLabel = new JLabel("The opponent ran out of time.");
        detailLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        detailLabel.setForeground(textColor);

        panel.add(messageLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(detailLabel);

        JOptionPane.showMessageDialog(null, panel, "Game Over", JOptionPane.PLAIN_MESSAGE);

        GameController controller = (GameController) gameBoard.getGameController();
        if (controller != null) {
            controller.gameOver(winner);
        }
    }

    // private int formatIntTime(int seconds) {
    //     int minutes = seconds / 60;
    //     int remainingSeconds = seconds % 60;
    //     return (minutes * 100) + remainingSeconds;
    // }

    public String getWhiteTime() {
        return formatTime(whiteTimeSeconds);
    }

    public String getBlackTime() {
        return formatTime(blackTimeSeconds);
    }

    public int getWhiteTimeSeconds() {
        return whiteTimeSeconds; 
    }

    public int getBlackTimeSeconds() {
        return blackTimeSeconds; 
    }
    

    public void switchSides() {
        int tempTime = whiteTimeSeconds;
        whiteTimeSeconds = blackTimeSeconds;
        blackTimeSeconds = tempTime;

        updateTimerDisplay();

        // System.out.println("สลับเวลาผู้เล่น: ขาว = " + formatTime(whiteTimeSeconds) + ", ดำ = " + formatTime(blackTimeSeconds));
    }

    public void resetTimer() {
        whiteTimeSeconds = 10 * 60;
        blackTimeSeconds = 10 * 60;
        isWhiteTurn = true;
        gameBoard.setWhiteTurn(isWhiteTurn);
        updateTimerDisplay();
    }

    // Add a method to get the elapsed seconds for white player
    public int getWhiteElapsedSeconds() {
        // Calculate elapsed seconds based on total time allocation minus remaining time
        // For example, if using a countdown timer from 10 minutes (600 seconds):
        return Math.max(0, getTotalInitialTimeSeconds() - whiteTimeSeconds);
    }

    // Add a method to get the elapsed seconds for black player
    public int getBlackElapsedSeconds() {
        // Calculate elapsed seconds based on total time allocation minus remaining time
        return Math.max(0, getTotalInitialTimeSeconds() - blackTimeSeconds);
    }

    // Get the initial time allocation in seconds
    private int getTotalInitialTimeSeconds() {
        // Return the initial time allocation - adjust based on your actual implementation
        return 600; // Default 10 minutes (should match your actual initial time value)
    }

    public void ensureTimerRunning() {
        if (timer != null && !timer.isRunning()) {
            System.out.println("Timer was stopped - restarting it");
            timer.start();
        }
    }

    // Add this method to check the timer status
    public Timer getTimer() {
        return timer;
    }

    // Add a public method to stop the timer that can be called from anywhere
    public void stopGameTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            System.out.println("Game timer stopped");
        }
    }
}
