package game;

import board.Board;
import javax.swing.*;
import utils.SoundManager;
import ui.InfoPanel;
public class GameController {

    private Board gameBoard;
    private TimerManager timerManager;
    private SoundManager soundManager;
    private InfoPanel infoPanel; 

    public GameController(Board board) {
        this.gameBoard = board;
        board.setGameController(this);
        this.soundManager = SoundManager.getInstance();
        this.timerManager = new TimerManager(board, true);
        this.infoPanel = null;
    }

    public void gameOver(String winner) {
        System.out.println("Game over! Winner: " + winner);

        // Make sure the timer is stopped
        if (timerManager != null) {
            timerManager.stopGameTimer();
        }

        if (winner.equals("DRAW")) {
            playSound("draw");
        } else {
            playSound("victory");
        }

        String message;
        if (winner.equals("WHITE")) {
            message = "White wins!";
        } else if (winner.equals("BLACK")) {
            message = "Black wins!";
        } else {
            message = "It's a draw! Both players have fewer than 2 pieces.";
        }

        int option = JOptionPane.showConfirmDialog(
            gameBoard, // Use the gameBoard as the parent component
            message + "\n\nWould you like to play again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> restartGame());
        } else {
            // Return to the start menu when "No" is selected
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gameBoard);
            if (frame != null) {
                frame.dispose();
                
                SwingUtilities.invokeLater(() -> {
                    new ui.StartScreen();
                });
            }
        }
    }

    public void updatePieceCount(int whiteRemaining, int blackRemaining, int whiteCaptured, int blackCaptured) {
        if (infoPanel != null) {
            infoPanel.updatePieceCount(whiteRemaining, blackRemaining, whiteCaptured, blackCaptured);
        }
    }
    public void setInfoPanel(InfoPanel panel) {
        this.infoPanel = panel;
        System.out.println("GameController: InfoPanel set");
    }
    public void playSound(String soundType) {
        if (soundManager != null) {
            soundManager.playSound(soundType);
        }
    }

    public void switchSides() {
        playSound("select");

        gameBoard.switchPieces();

        // System.out.println("สลับมุมมองกระดานเรียบร้อย");
    }

    public void switchTurn() {
        timerManager.switchTurn();
        gameBoard.setWhiteTurn(!gameBoard.isWhiteTurn());
    }

    private void restartGame() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gameBoard);
        if (frame != null) {
            frame.dispose();

            SwingUtilities.invokeLater(() -> {
                new ui.GameFrame();
            });
        }
    }

    public String getWhiteTime() {
        return timerManager.getWhiteTime();
    }

    public String getBlackTime() {
        return timerManager.getBlackTime();
    }
}
