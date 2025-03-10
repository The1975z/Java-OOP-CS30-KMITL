package board;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class BoardAnimator {
    private Board board;
    private Timer animationTimer;
    private float animationPhase = 0f;
    
    public BoardAnimator(Board board) {
        this.board = board;
    }
    
    public void startAnimationTimer() {
        animationTimer = new Timer(50, e -> {
            animationPhase += 0.05f;
            if (animationPhase > 1.0f) {
                animationPhase = 0f;
            }
            
            if (board.getRenderer() != null) {
                board.getRenderer().setAnimationPhase(animationPhase);
            }
            
            board.repaint();
        });
        animationTimer.start();
    }
    
    public void handleVisibilityChange(boolean visible) {
        if (visible && animationTimer != null && !animationTimer.isRunning()) {
            animationTimer.start();
        } else if (!visible && animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }
    
    public void showCaptureEffect(int row, int col) {
        int x = board.getBoardX() + col * board.getTileSize();
        int y = board.getBoardY() + row * board.getTileSize();

        Timer captureTimer = new Timer(50, new ActionListener() {
            private int frame = 0;
            private final int MAX_FRAMES = 5;

            @Override
            public void actionPerformed(ActionEvent e) {
                frame++;
                if (frame >= MAX_FRAMES) {
                    ((Timer) e.getSource()).stop();
                }
                board.repaint();
            }
        });
        
        captureTimer.setRepeats(true);
        captureTimer.start();
    }
}
