package board;

import java.awt.event.MouseEvent;

public class BoardInputHandler {
    private Board board;
    
    public BoardInputHandler(Board board) {
        this.board = board;
    }
    
    public void handleMousePress(MouseEvent e) {
        if (board.getBoardSize() == 0) {
            return;
        }
        
        int mouseX = e.getX() - board.getBoardX();
        int mouseY = e.getY() - board.getBoardY();

        if (mouseX < 0 || mouseX >= board.getBoardSize() || mouseY < 0 || mouseY >= board.getBoardSize()) {
            return;
        }

        int col = mouseX / board.getTileSize();
        int row = mouseY / board.getTileSize();

        if (board.getSelectedRow() == -1 && board.getSelectedCol() == -1) {
            board.selectPiece(row, col);
        } else {
            board.movePiece(row, col);
        }
        
        board.repaint();
    }
}
