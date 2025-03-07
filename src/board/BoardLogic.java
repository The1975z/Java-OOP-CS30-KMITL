package board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import pieces.RokPiece;

public class BoardLogic {
    private Board board;
    
    public BoardLogic(Board board) {
        this.board = board;
    }
    
    public void initializePieces(int tileSize) {
        RokPiece[][] pieces = board.getBoard();
        int cols = board.getCols();
        
        for (int j = 0; j < cols; j++) {
            try {
                pieces[0][j] = new RokPiece(RokPiece.Color.BLACK, board.getRokPath(), tileSize);
                pieces[7][j] = new RokPiece(RokPiece.Color.WHITE, board.getRokPath(), tileSize);
            } catch (Exception e) {
                System.err.println("เกิดข้อผิดพลาดในการสร้างหมาก: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public void switchPieces() {
        RokPiece[][] pieces = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        
        boolean hasPieces = false;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (pieces[r][c] != null) {
                    hasPieces = true;
                    break;
                }
            }
            if (hasPieces) {
                break;
            }
        }

        if (!hasPieces) {
            initializePieces(board.getTileSize());
            board.repaint();
            return;
        }
        
        RokPiece[][] newBoard = new RokPiece[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (pieces[r][c] != null) {
                    int newRow = 7 - r;
                    newBoard[newRow][c] = pieces[r][c];
                }
            }
        }
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                pieces[r][c] = newBoard[r][c];
            }
        }
        
        board.setBoardFlipped(!board.isBoardFlipped());
        board.setSelectedRow(-1);
        board.setSelectedCol(-1);
        board.setLastMoveFromRow(-1);
        board.setLastMoveFromCol(-1);
        board.setLastMoveToRow(-1);
        board.setLastMoveToCol(-1);
        board.repaint();
    }
    
    public void selectPiece(int row, int col) {
        RokPiece[][] pieces = board.getBoard();
        
        if (pieces[row][col] != null) {
            boolean isWhitePiece = (pieces[row][col].getColor() == RokPiece.Color.WHITE);
            if (isWhitePiece == board.isWhiteTurn()) {
                board.setSelectedRow(row);
                board.setSelectedCol(col);
                pieces[row][col].setSelected(true);

                board.playSound("select");
                
                updateLegalMoves();
            }
        }
    }
    
    private void updateLegalMoves() {
        int selectedRow = board.getSelectedRow();
        int selectedCol = board.getSelectedCol();
        List<Point> legalMoves = board.getLegalMoves();
        
        if (selectedRow != -1 && selectedCol != -1) {
            legalMoves.clear();

            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] dir : directions) {
                int r = selectedRow;
                int c = selectedCol;

                while (true) {
                    r += dir[0];
                    c += dir[1];

                    if (!isValidPosition(r, c) || board.getBoard()[r][c] != null) {
                        break;
                    }

                    legalMoves.add(new Point(c, r));
                }
            }
        }
    }
    
    public void movePiece(int toRow, int toCol) {
        int selectedRow = board.getSelectedRow();
        int selectedCol = board.getSelectedCol();
        RokPiece[][] pieces = board.getBoard();
        
        if (isValidMove(selectedRow, selectedCol, toRow, toCol)) {
            board.setLastMoveFromRow(selectedRow);
            board.setLastMoveFromCol(selectedCol);
            board.setLastMoveToRow(toRow);
            board.setLastMoveToCol(toCol);

            pieces[toRow][toCol] = pieces[selectedRow][selectedCol];
            pieces[selectedRow][selectedCol] = null;

            boolean captured = checkCaptures(toRow, toCol);

            if (captured) {
                board.playSound("capture");
            } else {
                board.playSound("move");
            }

            boolean previousTurn = board.isWhiteTurn();
            board.setWhiteTurn(!previousTurn);
            
            if (board.getGameControllerObj() != null) {
                board.getGameControllerObj().switchTurn();
            }
            
            board.setWhiteTurn(!board.isWhiteTurn());

            pieces[toRow][toCol].setSelected(false);
            board.setSelectedRow(-1);
            board.setSelectedCol(-1);
            board.getLegalMoves().clear();

            checkAtari();
            
            if (isGameOver()) {
                String winner = getWinner();
                JOptionPane.showMessageDialog(board, winner.equals("DRAW") ? "Game ended in a draw!" : winner + " wins the game!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                if (board.getGameControllerObj() != null) {
                    board.getGameControllerObj().gameOver(winner);
                }
            }
        } else if (pieces[toRow][toCol] != null && 
                   ((pieces[toRow][toCol].getColor() == RokPiece.Color.WHITE && board.isWhiteTurn()) || 
                   (pieces[toRow][toCol].getColor() == RokPiece.Color.BLACK && !board.isWhiteTurn()))) {
            
            if (selectedRow != -1 && selectedCol != -1 && pieces[selectedRow][selectedCol] != null) {
                pieces[selectedRow][selectedCol].setSelected(false);
            }
            
            board.setSelectedRow(toRow);
            board.setSelectedCol(toCol);
            pieces[toRow][toCol].setSelected(true);
            board.playSound("select");
            
            updateLegalMoves();
        } else {
            if (selectedRow != -1 && selectedCol != -1 && pieces[selectedRow][selectedCol] != null) {
                pieces[selectedRow][selectedCol].setSelected(false);
            }
            
            board.setSelectedRow(-1);
            board.setSelectedCol(-1);
            board.getLegalMoves().clear();
            board.playSound("error");
        }
        
        board.repaint();
    }
    
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol)) {
            return false;
        }
        
        RokPiece[][] pieces = board.getBoard();
        
        if (pieces[toRow][toCol] != null) {
            return false;
        } else if (fromCol != toCol && fromRow != toRow) {
            return false;
        }

        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);

        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;

        while (currentRow != toRow || currentCol != toCol) {
            if (pieces[currentRow][currentCol] != null) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;
    }
    
    public boolean checkCaptures(int row, int col) {
        RokPiece[][] pieces = board.getBoard();
        RokPiece movingPiece = pieces[row][col];
        
        if (movingPiece == null) {
            System.out.println("DEBUG: หมากที่เดินไม่มีอยู่จริงที่ตำแหน่ง (" + row + "," + col + ")");
            return false;
        }

        RokPiece.Color currentColor = movingPiece.getColor();
        RokPiece.Color opponentColor = (currentColor == RokPiece.Color.WHITE) ? RokPiece.Color.BLACK : RokPiece.Color.WHITE;

        boolean captured = false;
        int[][] directions = {{0, -1, 0, 1}, {-1, 0, 1, 0}}; // [ซ้าย, บน, ขวา, ล่าง] แต่ละคู่

        for (int[] dir : directions) {
            int r1 = row + dir[0];
            int c1 = col + dir[1];
            int r2 = row + dir[2];
            int c2 = col + dir[3];

            // ตรวจสอบว่าทั้งสองตำแหน่งอยู่ในกระดานและมีหมากของฝ่ายตรงข้าม
            if (isValidPosition(r1, c1) && isValidPosition(r2, c2)) {
                boolean hasOpponentPiece1 = (pieces[r1][c1] != null && pieces[r1][c1].getColor() == opponentColor);
                boolean hasOpponentPiece2 = (pieces[r2][c2] != null && pieces[r2][c2].getColor() == opponentColor);

                // ถ้ามีหมากฝ่ายตรงข้ามทั้งสองด้าน จะกินทั้งสองตัว
                if (hasOpponentPiece1 && hasOpponentPiece2) {
                    // ลบหมากฝ่ายตรงข้ามทั้งสองตัว
                    pieces[r1][c1] = null;
                    pieces[r2][c2] = null;
                    if (opponentColor == RokPiece.Color.WHITE) {
                        board.setWhiteCaptured(board.getWhiteCaptured() + 2);
                        board.setWhiteRemaining(board.getWhiteRemaining() - 2);
                    } else {
                        board.setBlackCaptured(board.getBlackCaptured() + 2);
                        board.setBlackRemaining(board.getBlackRemaining() - 2);
                    }
                    captured = true;
                    board.showCaptureEffect(r1, c1);
                    board.showCaptureEffect(r2, c2);
                }
            }
            for (int i = 0; i < 2; i++) {
                int dr = dir[i * 2];
                int dc = dir[i * 2 + 1];

                if (dr == 0 && dc == 0) {
                    continue;
                }
                
                // ไว้เก็บ ตําแหน้่งของหมากในแนวเดียวกัน 
                List<Point> opponentPieces = new ArrayList<>();
                int r = row + dr;
                int c = col + dc;

                while (isValidPosition(r, c) && pieces[r][c] != null && pieces[r][c].getColor() == opponentColor) {
                    opponentPieces.add(new Point(r, c));
                    r += dr;
                    c += dc;
                }

                boolean hasSameColorAtEnd = (isValidPosition(r, c) && pieces[r][c] != null && pieces[r][c].getColor() == currentColor);

                if (hasSameColorAtEnd && !opponentPieces.isEmpty()) {
                    for (Point p : opponentPieces) {
                        pieces[p.x][p.y] = null;
                        board.showCaptureEffect(p.x, p.y);
                    }
                    if (currentColor == RokPiece.Color.WHITE) {
                        board.setWhiteCaptured(board.getWhiteCaptured() + opponentPieces.size());
                        board.setWhiteRemaining(board.getWhiteRemaining() - opponentPieces.size());
                    } else {
                        board.setBlackCaptured(board.getBlackCaptured() + opponentPieces.size());
                        board.setBlackRemaining(board.getBlackRemaining() - opponentPieces.size());
                    }
                    captured = true;
                }
            }
        }
        if (captured && board.getGameControllerObj() != null) {
            board.getGameControllerObj().updatePieceCount(
                board.getWhiteRemaining(), 
                board.getBlackRemaining(), 
                board.getWhiteCaptured(), 
                board.getBlackCaptured()
            );
        }
        
        return captured;
    }

    public void checkAtari() {
        RokPiece[][] pieces = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        
        if (board.getLastMoveFromRow() == -1 && board.getLastMoveToRow() == -1) {
            return;
        }

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // บน, ล่าง, ซ้าย, ขวา

        RokPiece.Color currentColor = null;
        if (board.getLastMoveToRow() != -1 && board.getLastMoveToCol() != -1 && 
            pieces[board.getLastMoveToRow()][board.getLastMoveToCol()] != null) {
            currentColor = pieces[board.getLastMoveToRow()][board.getLastMoveToCol()].getColor();
        } else {
            return;
        }

        RokPiece.Color opponentColor = (currentColor == RokPiece.Color.WHITE) ? RokPiece.Color.BLACK : RokPiece.Color.WHITE;

        List<Point> piecesToRemove = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (pieces[r][c] != null && pieces[r][c].getColor() == opponentColor) {
                    if ((r == 0 || r == 7) && board.getLastMoveToRow() == -1) {
                        continue;
                    }

                    int liberties = 0;
                    boolean hasSurroundingSpace = false;

                    for (int[] dir : directions) {
                        int nr = r + dir[0];
                        int nc = c + dir[1];

                        if (isValidPosition(nr, nc)) {
                            if (pieces[nr][nc] == null) {
                                liberties++;
                                hasSurroundingSpace = true;
                            } else if (pieces[nr][nc].getColor() == opponentColor) {
                                int neighborLiberties = countLibertiesForGroup(nr, nc, opponentColor, new boolean[rows][cols]);
                                if (neighborLiberties > 0) {
                                    hasSurroundingSpace = true;
                                }
                            }
                        }
                    }

                    if (!hasSurroundingSpace) {
                        piecesToRemove.add(new Point(r, c));
                    }
                }
            }
        }
        
        int capturedCount = 0;
        // ลบหมากที่ถูก Atari (ก็คือที่ถูกกิน)
        for (Point p : piecesToRemove) {
            if (pieces[p.x][p.y] != null) {
                RokPiece.Color capturedColor = pieces[p.x][p.y].getColor();
                pieces[p.x][p.y] = null;
                board.showCaptureEffect(p.x, p.y);
                board.playSound("capture");
                if (capturedColor == RokPiece.Color.WHITE) {
                    board.setWhiteCaptured(board.getWhiteCaptured() + 1);
                    board.setWhiteRemaining(board.getWhiteRemaining() - 1);
                } else {
                    board.setBlackCaptured(board.getBlackCaptured() + 1);
                    board.setBlackRemaining(board.getBlackRemaining() - 1);
                }
                capturedCount++;
            }
        }
        
        if (capturedCount > 0 && board.getGameControllerObj() != null) {
            board.getGameControllerObj().updatePieceCount(
                board.getWhiteRemaining(), 
                board.getBlackRemaining(), 
                board.getWhiteCaptured(), 
                board.getBlackCaptured()
            );
        }
    }

    private int countLibertiesForGroup(int row, int col, RokPiece.Color color, boolean[][] visited) {
        if (!isValidPosition(row, col) || visited[row][col]) {
            return 0;
        } else if (board.getBoard()[row][col] == null) {
            return 1;
        } else if (board.getBoard()[row][col].getColor() != color) {
            return 0;
        }
    
        visited[row][col] = true;
    
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int totalLiberties = 0;
    
        for (int[] dir : directions) {
            int nr = row + dir[0];
            int nc = col + dir[1];
            totalLiberties += countLibertiesForGroup(nr, nc, color, visited);
        }
    
        return totalLiberties;
    }

    public boolean isGameOver() {
        int whiteCount = 0, blackCount = 0;
        RokPiece[][] pieces = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (pieces[r][c] != null) {
                    if (pieces[r][c].getColor() == RokPiece.Color.WHITE) {
                        whiteCount++;
                    } else {
                        blackCount++;
                    }
                }
            }
        }
        return whiteCount < 2 || blackCount < 2;
    }
    
    public String getWinner() {
        int whiteCount = 0, blackCount = 0;
        RokPiece[][] pieces = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (pieces[r][c] != null) {
                    if (pieces[r][c].getColor() == RokPiece.Color.WHITE) {
                        whiteCount++;
                    } else {
                        blackCount++;
                    }
                }
            }
        }
        
        if (whiteCount < 2 && blackCount < 2) {
            return "DRAW";
        } else if (whiteCount < 2) {
            return "BLACK";
        } else if (blackCount < 2) {
            return "WHITE";
        }
        return "ONGOING";
    }
    
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < board.getRows() && col >= 0 && col < board.getCols();
    }
    
    public void resetGame() {
        RokPiece[][] pieces = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                pieces[r][c] = null;
            }
        }
        
        initializePieces(board.getTileSize());
        board.setWhiteTurn(true);
        board.setSelectedRow(-1);
        board.setSelectedCol(-1);
        board.setLastMoveFromRow(-1);
        board.setLastMoveFromCol(-1);
        board.setLastMoveToRow(-1);
        board.setLastMoveToCol(-1);
        board.getLegalMoves().clear();
        board.repaint();
    }
}
