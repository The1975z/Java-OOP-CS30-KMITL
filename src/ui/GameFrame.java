package ui;

import board.Board;
import game.GameController;
import game.TimerManager;
import java.awt.*;
import javax.swing.*;
import utils.WindowResizer;

public class GameFrame extends JFrame {

    private Board board;
    private GameController gameController;
    private InfoPanel infoPanel;
    private TimerManager timerManager;
    private MoveHistoryPanel historyPanel;

    public GameFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        setTitle("Mak Neeb Game");
        setSize(screenWidth, screenHeight);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        board = new Board();
        gameController = new GameController(board);
        timerManager = new TimerManager(board, true);
        // historyPanel = new MoveHistoryPanel(); // UI เดิมไม่เอาละกุ ขี้เกียจเขียน By The1975 
        // add(historyPanel, BorderLayout.EAST);
        add(board, BorderLayout.CENTER);

        infoPanel = new InfoPanel(gameController);
        add(infoPanel, BorderLayout.SOUTH);

        new WindowResizer(this);
        setVisible(true);

        startTimerUpdates();
    }

    private void startTimerUpdates() {
        Timer timer = new Timer(1000, e -> {
            infoPanel.updateTimer();

            if (timerManager != null) {
                timerManager.updateTimerDisplay();
            }
        });
        timer.start();
    }

    public void recordMove(String notation, boolean isWhiteMove, int timeSpent) {
        if (historyPanel != null) {
            historyPanel.addGameMove(notation, isWhiteMove, timeSpent);
        }
    }

    public MoveHistoryPanel getMoveHistoryPanel() {
        return historyPanel;
    }

    public void restartGame() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}
