package board;

import game.GameController;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import pieces.RokPiece;

public class Board extends JPanel {

    private final int cols = 8, rows = 8;
    private RokPiece[][] board = new RokPiece[cols][rows];
    String ROK_PATH = "Rok/Rok.png";
    private boolean isWhiteTurn = true;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean isInitialized = false;
    private boolean isBoardFlipped = false; // สลับมุมมองกระดาน
    private int whiteCaptured = 0;
    private int blackCaptured = 0;
    private int whiteRemaining = 8; // เริ่มต้นมีหมาก 8 ตัว เท่ากัน
    private int blackRemaining = 8; // เริ่มต้นมีหมาก 8 ตัว เท่ากัน
    private List<Point> legalMoves = new ArrayList<>();
    private GameController gameController;

    private int boardX, boardY, tileSize, boardSize;

    private int lastMoveFromRow = -1;
    private int lastMoveFromCol = -1;
    private int lastMoveToRow = -1;
    private int lastMoveToCol = -1;

    // Components
    private BoardRenderer renderer;
    private BoardLogic logic;
    private BoardInputHandler inputHandler;
    private BoardAnimator animator;

    public Board() {
        setOpaque(false);
        
        // Initialize components
        renderer = new BoardRenderer(this);
        logic = new BoardLogic(this);
        inputHandler = new BoardInputHandler(this);
        animator = new BoardAnimator(this);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                inputHandler.handleMousePress(e);
            }
        });

        animator.startAnimationTimer();
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
        System.out.println("GameController set, isWhiteTurn: " + isWhiteTurn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        boardSize = Math.min(getWidth(), getHeight()) - 100;
        tileSize = boardSize / cols;

        setPreferredSize(new Dimension(boardSize, boardSize));

        if (!isInitialized) {
            logic.initializePieces(tileSize);
            isInitialized = true;
        }

        boardX = (getWidth() - boardSize) / 2;
        boardY = (getHeight() - boardSize) / 2;

        renderer.render((Graphics2D)g);
    }

    public void switchPieces() {
        logic.switchPieces();
    }

    public void selectPiece(int row, int col) {
        logic.selectPiece(row, col);
    }

    public void movePiece(int toRow, int toCol) {
        logic.movePiece(toRow, toCol);
    }

    



    public void showCaptureEffect(int row, int col) {
        animator.showCaptureEffect(row, col);
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }


    public RokPiece getPiece(int row, int col) {
        if (isValidPosition(row, col)) {
            return board[row][col];
        }
        return null;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public void setWhiteTurn(boolean isWhiteTurn) {
        this.isWhiteTurn = isWhiteTurn;
        repaint();
    }

    public Object getGameController() {
        return this.gameController;
    }

    public void resetGame() {
        logic.resetGame();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        animator.handleVisibilityChange(visible);
    }

    public RokPiece[][] getBoard() {
        return this.board;
    }
    
    public int getRows() {
        return this.rows;
    }
    
    public int getCols() {
        return this.cols;
    }

    public int getBoardX() {
        return this.boardX;
    }
    
    public int getBoardY() {
        return this.boardY;
    }
    
    public int getTileSize() {
        return this.tileSize;
    }
    
    public int getBoardSize() {
        return this.boardSize;
    }
    
    public boolean isBoardFlipped() {
        return this.isBoardFlipped;
    }
    
    public int getSelectedRow() {
        return this.selectedRow;
    }
    
    public int getSelectedCol() {
        return this.selectedCol;
    }
    
    public void setSelectedRow(int row) {
        this.selectedRow = row;
    }
    
    public void setSelectedCol(int col) {
        this.selectedCol = col;
    }
    
    public GameController getGameControllerObj() {
        return this.gameController;
    }
    
    public List<Point> getLegalMoves() {
        return this.legalMoves;
    }
    
    public void playSound(String soundType) {
        if (gameController != null) {
            gameController.playSound(soundType);
        }
    }

    public BoardRenderer getRenderer() {
        return this.renderer;
    }

    public int getLastMoveFromRow() {
        return this.lastMoveFromRow;
    }
    
    public int getLastMoveFromCol() {
        return this.lastMoveFromCol;
    }
    
    public int getLastMoveToRow() {
        return this.lastMoveToRow;
    }
    
    public int getLastMoveToCol() {
        return this.lastMoveToCol;
    }
    
    public void setLastMoveFromRow(int row) {
        this.lastMoveFromRow = row;
    }
    
    public void setLastMoveFromCol(int col) {
        this.lastMoveFromCol = col;
    }
    
    public void setLastMoveToRow(int row) {
        this.lastMoveToRow = row;
    }
    
    public void setLastMoveToCol(int col) {
        this.lastMoveToCol = col;
    }
    
    public int getWhiteCaptured() {
        return this.whiteCaptured;
    }
    
    public int getBlackCaptured() {
        return this.blackCaptured;
    }
    
    public int getWhiteRemaining() {
        return this.whiteRemaining;
    }
    
    public int getBlackRemaining() {
        return this.blackRemaining;
    }
    
    public void setWhiteCaptured(int count) {
        this.whiteCaptured = count;
    }
    
    public void setBlackCaptured(int count) {
        this.blackCaptured = count;
    }
    
    public void setWhiteRemaining(int count) {
        this.whiteRemaining = count;
    }
    
    public void setBlackRemaining(int count) {
        this.blackRemaining = count;
    }
    
    public void setBoardFlipped(boolean flipped) {
        this.isBoardFlipped = flipped;
    }
    
    public String getRokPath() {
        return this.ROK_PATH;
    }
}
