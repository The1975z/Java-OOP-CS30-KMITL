// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.*;

// class Piece {
//     enum Color { WHITE, BLACK }
//     Color color;

//     public Piece(Color color) {
//         this.color = color;
//     }

//     public boolean isWhite() {
//         return color == Color.WHITE;
//     }

//     public boolean isBlack() {
//         return color == Color.BLACK;
//     }
// }

// class Board {
//     private Piece[][] board = new Piece[8][8];

//     public Board() {
//         initializePieces();
//     }

//     private void initializePieces() {
//         for (int j = 0; j < 8; j++) board[0][j] = new Piece(Piece.Color.BLACK);
//         for (int j = 0; j < 8; j++) board[7][j] = new Piece(Piece.Color.WHITE);
//     }

//     public Piece getPiece(int row, int col) {
//         return board[row][col];
//     }

//     public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
//         board[toRow][toCol] = board[fromRow][fromCol];
//         board[fromRow][fromCol] = null;
//         checkCapture(toRow, toCol);
//     }

//     private void checkCapture(int row, int col) {
//         Piece movedPiece = board[row][col];
//         if (movedPiece == null) return;
    
//         Piece.Color opponentColor = movedPiece.isWhite() ? Piece.Color.BLACK : Piece.Color.WHITE;
    
//         int[][] directions = { {0, -1}, {0, 1}, {-1, 0}, {1, 0} };
    
//         // ตรวจสอบการกินเบี้ยในทิศทางตรงกันข้าม (เช่น ซ้าย-ขวา, บน-ล่าง)
//         for (int i = 0; i < directions.length; i += 2) {
//             int[] dir1 = directions[i];
//             int[] dir2 = directions[i + 1];
    
//             int r1 = row + dir1[0], c1 = col + dir1[1];
//             int r2 = row + dir2[0], c2 = col + dir2[1];
    
//             if (isValidPos(r1, c1) && isValidPos(r2, c2)) {
//                 Piece p1 = board[r1][c1];
//                 Piece p2 = board[r2][c2];
    
//                 if (p1 != null && p1.color == opponentColor && p2 != null && p2.color == opponentColor) {
//                     board[r1][c1] = null;
//                     board[r2][c2] = null;
//                 }
//             }
//         }
    
//         // ตรวจสอบการกินเบี้ยในแนวตรง (เช่น Othello)
//         for (int[] d : directions) {
//             int r = row + d[0], c = col + d[1];
//             int startR = r, startC = c;
//             int count = 0;
    
//             while (isValidPos(r, c)) {
//                 Piece piece = board[r][c];
//                 if (piece == null || piece.color != opponentColor) break;
//                 r += d[0];
//                 c += d[1];
//                 count++;
//             }
    
//             if (isValidPos(r, c) && board[r][c] != null && board[r][c].color == movedPiece.color) {
//                 for (int i = 0; i < count; i++) {
//                     int captureR = startR + i * d[0];
//                     int captureC = startC + i * d[1];
//                     board[captureR][captureC] = null;
//                 }
//             }
//         }
//     }

//     public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, boolean isWhiteTurn) {
//         if (!isValidPos(toRow, toCol) || board[toRow][toCol] != null) return false;

//         Piece piece = board[fromRow][fromCol];
//         if (piece == null || (isWhiteTurn && piece.isBlack()) || (!isWhiteTurn && piece.isWhite())) return false;

//         if (fromRow != toRow && fromCol != toCol) return false;

//         int rowStep = Integer.compare(toRow, fromRow);
//         int colStep = Integer.compare(toCol, fromCol);
//         int steps = Math.max(Math.abs(toRow - fromRow), Math.abs(toCol - fromCol));

//         for (int i = 1; i < steps; i++) {
//             int currentRow = fromRow + rowStep * i;
//             int currentCol = fromCol + colStep * i;
//             if (board[currentRow][currentCol] != null) {
//                 return false;
//             }
//         }

//         return true;
//     }

//     public boolean isGameOver() {
//         int whiteCount = 0, blackCount = 0;
//         for (Piece[] row : board) {
//             for (Piece p : row) {
//                 if (p != null) {
//                     if (p.isWhite()) whiteCount++;
//                     else blackCount++;
//                 }
//             }
//         }
//         return whiteCount < 2 || blackCount < 2;
//     }

//     private boolean isValidPos(int row, int col) {
//         return row >= 0 && row < 8 && col >= 0 && col < 8;
//     }
// }

// class BoardPanel extends JPanel implements MouseListener {
//     private Board board;
//     private int selectedRow = -1, selectedCol = -1;
//     private boolean whiteTurn = true;

//     public BoardPanel(Board board) {
//         this.board = board;
//         setPreferredSize(new Dimension(400, 400));
//         addMouseListener(this);
//     }

//     protected void paintComponent(Graphics g) {
//         super.paintComponent(g);
//         int tileSize = getWidth() / 8;

//         for (int row = 0; row < 8; row++) {
//             for (int col = 0; col < 8; col++) {
//                 g.setColor((row + col) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
//                 g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);

//                 Piece piece = board.getPiece(row, col);
//                 if (piece != null) {
//                     g.setColor(piece.isWhite() ? Color.WHITE : Color.BLACK);
//                     g.fillOval(col * tileSize + 10, row * tileSize + 10, tileSize - 20, tileSize - 20);
//                 }

//                 if (row == selectedRow && col == selectedCol) {
//                     g.setColor(Color.RED);
//                     g.drawRect(col * tileSize, row * tileSize, tileSize, tileSize);
//                 }
//             }
//         }
//     }

//     public void mousePressed(MouseEvent e) {
//         int tileSize = getWidth() / 8;
//         int row = e.getY() / tileSize;
//         int col = e.getX() / tileSize;

//         if (selectedRow == -1) {
//             if (board.getPiece(row, col) != null) {
//                 selectedRow = row;
//                 selectedCol = col;
//             }
//         } else {
//             if (board.isValidMove(selectedRow, selectedCol, row, col, whiteTurn)) {
//                 board.movePiece(selectedRow, selectedCol, row, col);
//                 whiteTurn = !whiteTurn;

//                 if (board.isGameOver()) {
//                     JOptionPane.showMessageDialog(this, "เกมจบแล้ว!");
//                     System.exit(0);
//                 }
//             }
//             selectedRow = -1;
//             selectedCol = -1;
//         }
//         repaint();
//     }

//     public void mouseReleased(MouseEvent e) {}
//     public void mouseClicked(MouseEvent e) {}
//     public void mouseEntered(MouseEvent e) {}
//     public void mouseExited(MouseEvent e) {}
// }

// public class makNeep {
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             JFrame frame = new JFrame("หมากหนีบ");
//             Board board = new Board();
//             BoardPanel boardPanel = new BoardPanel(board);

//             frame.add(boardPanel);
//             frame.pack();
//             frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             frame.setVisible(true);
//         });
//     }
// }