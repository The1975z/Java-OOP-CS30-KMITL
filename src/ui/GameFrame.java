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
        historyPanel = new MoveHistoryPanel();
        add(historyPanel, BorderLayout.EAST);
        add(board, BorderLayout.CENTER);

        infoPanel = new InfoPanel(gameController);
        add(infoPanel, BorderLayout.SOUTH);

        new WindowResizer(this);
        setVisible(true);

        startTimerUpdates();
        board.setGameController(gameController);
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

    public void recordMove(int fromRow, int fromCol, int toRow, int toCol, boolean isWhiteMove, int timeSpent) {
        if (historyPanel != null) {
            int opponentFromRow = -1;
            int opponentFromCol = -1;
            int opponentToRow = -1;
            int opponentToCol = -1;
            int opponentTime = 0;

            if (!historyPanel.getMoveHistory().isEmpty()) {
                if (isWhiteMove && !historyPanel.getLastBlackMoveFrom().equals("-")) {
                    opponentFromRow = convertNotationToRow(historyPanel.getLastBlackMoveFrom());
                    opponentFromCol = convertNotationToCol(historyPanel.getLastBlackMoveFrom());
                    opponentToRow = convertNotationToRow(historyPanel.getLastBlackMoveTo());
                    opponentToCol = convertNotationToCol(historyPanel.getLastBlackMoveTo());
                    opponentTime = historyPanel.getLastBlackTimeSeconds();
                } else if (!isWhiteMove && !historyPanel.getLastWhiteMoveFrom().equals("-")) {
                    opponentFromRow = convertNotationToRow(historyPanel.getLastWhiteMoveFrom());
                    opponentFromCol = convertNotationToCol(historyPanel.getLastWhiteMoveFrom());
                    opponentToRow = convertNotationToRow(historyPanel.getLastWhiteMoveTo());
                    opponentToCol = convertNotationToCol(historyPanel.getLastWhiteMoveTo());
                    opponentTime = historyPanel.getLastWhiteTimeSeconds();
                }
            }

            historyPanel.addGameMove(
                isWhiteMove ? fromRow : opponentFromRow,
                isWhiteMove ? fromCol : opponentFromCol,
                isWhiteMove ? toRow : opponentToRow,
                isWhiteMove ? toCol : opponentToCol,
                isWhiteMove ? opponentFromRow : fromRow,
                isWhiteMove ? opponentFromCol : fromCol,
                isWhiteMove ? opponentToRow : toRow,
                isWhiteMove ? opponentToCol : toCol,
                isWhiteMove ? timeSpent : opponentTime,
                isWhiteMove ? opponentTime : timeSpent
            );
        }
    }

    private int convertNotationToRow(String notation) {
        if (notation.equals("-") || notation.length() < 2) return -1;
        int rank = Integer.parseInt(notation.substring(1)) - 1;
        return 7 - rank; 
    }

    private int convertNotationToCol(String notation) {
        if (notation.equals("-") || notation.length() < 2) return -1;
        char file = notation.charAt(0);
        return file - 'a'; 
    }

    public MoveHistoryPanel getMoveHistoryPanel() {
        return this.historyPanel;
    }

    public TimerManager getTimerManager() {
        return this.timerManager;
    }
    public GameController getGameController() {
        return this.gameController;
    }
    public void restartGame() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}