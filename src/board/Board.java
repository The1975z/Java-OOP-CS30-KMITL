package board;

import game.GameController;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
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
    private final Color SELECTED_BORDER_COLOR = new Color(255, 215, 0);
    private final Color VALID_MOVE_COLOR = new Color(0, 200, 0, 120);
    private final Color VALID_MOVE_BORDER = new Color(0, 255, 0, 180);
    private final Color LIGHT_SQUARE_COLOR = new Color(227, 198, 181);
    private final Color DARK_SQUARE_COLOR = new Color(157, 105, 53);
    private final Color BOARD_BORDER_COLOR = new Color(101, 67, 33);
    private final Color COORDINATE_LIGHT = new Color(245, 245, 245);
    private final Color COORDINATE_DARK = new Color(50, 50, 50);

    private float animationPhase = 0f;
    private Timer animationTimer;
    private List<Point> legalMoves = new ArrayList<>();

    private BufferedImage marbleTexture;
    private GradientPaint boardGradient;

    private GameController gameController;

    private int boardX, boardY, tileSize, boardSize;

    private int lastMoveFromRow = -1;
    private int lastMoveFromCol = -1;
    private int lastMoveToRow = -1;
    private int lastMoveToCol = -1;

    public Board() {
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePress(e);
            }
        });

        startAnimationTimer();
    }

    private void startAnimationTimer() {
        animationTimer = new Timer(50, e -> {
            animationPhase += 0.05f;
            if (animationPhase > 1.0f) {
                animationPhase = 0f;
            }
            repaint();
        });
        animationTimer.start();
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
        System.out.println("GameController set, isWhiteTurn: " + isWhiteTurn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        boardSize = Math.min(getWidth(), getHeight()) - 100;
        tileSize = boardSize / cols;

        setPreferredSize(new Dimension(boardSize, boardSize));

        if (!isInitialized) {
            initializePieces(tileSize);
            isInitialized = true;
        }

        boardX = (getWidth() - boardSize) / 2;
        boardY = (getHeight() - boardSize) / 2;

        if (boardGradient == null || boardGradient.getPoint1().getX() != 0 || boardGradient.getPoint2().getX() != getWidth()) {
            boardGradient = new GradientPaint(0, 0, new Color(30, 30, 40),getWidth(), getHeight(), new Color(15, 15, 20));
        }

        g2d.setPaint(boardGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        drawBoardFrame(g2d);

        drawCoordinateBackground(g2d);

        drawBoard(g2d);

        drawLastMove(g2d);

        drawLegalMoves(g2d);

        drawTurnIndicator(g2d);

        drawBoardCoordinates(g2d);
    }

    private void drawBoardFrame(Graphics2D g2d) {
        int frameSize = 30;

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fill(new RoundRectangle2D.Double(boardX - frameSize + 5, boardY - frameSize + 5, boardSize + frameSize * 2, boardSize + frameSize * 2, 20, 20));
        g2d.setColor(BOARD_BORDER_COLOR);
        g2d.fill(new RoundRectangle2D.Double(boardX - frameSize, boardY - frameSize, boardSize + frameSize * 2, boardSize + frameSize * 2, 20, 20));

        g2d.setColor(new Color(0, 0, 0, 30));
        for (int i = 0; i < 100; i++) {
            int x = boardX - frameSize + (int) (Math.random() * (boardSize + frameSize * 2));
            int y = boardY - frameSize + (int) (Math.random() * (boardSize + frameSize * 2));
            int width = 1 + (int) (Math.random() * 3);
            int height = 5 + (int) (Math.random() * 15);
            g2d.fillRect(x, y, width, height);
        }

        g2d.setColor(new Color(101, 67, 33).brighter());
        g2d.fill(new RoundRectangle2D.Double(boardX - frameSize / 2, boardY - frameSize / 2, boardSize + frameSize, boardSize + frameSize, 15, 15));

        g2d.setColor(new Color(212, 175, 55, 150));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.draw(new RoundRectangle2D.Double(boardX - 5, boardY - 5, boardSize + 10, boardSize + 10, 10, 10));
    }

    private void drawCoordinateBackground(Graphics2D g2d) {
        int coordSize = 20;

        g2d.setColor(new Color(50, 50, 50, 180));
        g2d.fillRect(boardX, boardY - coordSize, boardSize, coordSize);
        g2d.fillRect(boardX, boardY + boardSize, boardSize, coordSize);

        g2d.fillRect(boardX - coordSize, boardY, coordSize, boardSize);
        g2d.fillRect(boardX + boardSize, boardY, coordSize, boardSize);

        g2d.fillRect(boardX - coordSize, boardY - coordSize, coordSize, coordSize);
        g2d.fillRect(boardX + boardSize, boardY - coordSize, coordSize, coordSize);
        g2d.fillRect(boardX - coordSize, boardY + boardSize, coordSize, coordSize);
        g2d.fillRect(boardX + boardSize, boardY + boardSize, coordSize, coordSize);
    }

    private void drawBoard(Graphics2D g2d) {
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillRect(boardX, boardY, boardSize, boardSize);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean isLightSquare = (r + c) % 2 == 0;

                if (marbleTexture != null && isLightSquare) {
                    TexturePaint marblePaint = new TexturePaint(marbleTexture, new Rectangle(c * 50, r * 50, marbleTexture.getWidth(), marbleTexture.getHeight()));
                    g2d.setPaint(marblePaint);
                    g2d.fillRect(boardX + c * tileSize, boardY + r * tileSize, tileSize, tileSize);
                } else {
                    g2d.setColor(isLightSquare ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR);
                    g2d.fillRect(boardX + c * tileSize, boardY + r * tileSize, tileSize, tileSize);
                }

                g2d.setColor(isLightSquare ? new Color(0, 0, 0, 20) : new Color(0, 0, 0, 40));
                g2d.drawRect(boardX + c * tileSize, boardY + r * tileSize, tileSize, tileSize);

                if ((r == 2 || r == 5) && (c == 2 || c == 5)) {
                    int dotSize = 6;
                    g2d.setColor(isLightSquare ? new Color(60, 40, 20) : new Color(200, 180, 150));
                    g2d.fillOval(boardX + c * tileSize + tileSize / 2 - dotSize / 2,boardY + r * tileSize + tileSize / 2 - dotSize / 2,dotSize, dotSize);
                }

                if (board[r][c] != null) {
                    Image pieceImage = board[r][c].getImage();
                    if (pieceImage != null) {
                        int offset = (tileSize - pieceImage.getWidth(null)) / 2;

                        g2d.setColor(new Color(0, 0, 0, 40));
                        g2d.fillOval(boardX + c * tileSize + offset + 3,boardY + r * tileSize + offset + 3,tileSize - 10, tileSize - 10);

                        if (r == selectedRow && c == selectedCol) {
                            drawSelectedPieceHighlight(g2d, c, r);
                        }

                        g2d.drawImage(pieceImage,boardX + c * tileSize + offset,boardY + r * tileSize + offset,tileSize - 10, tileSize - 10, this);
                    }
                }
            }
        }
    }

    private void drawSelectedPieceHighlight(Graphics2D g2d, int col, int row) {
        float pulse = (float) Math.sin(animationPhase * Math.PI * 2) * 0.2f + 0.8f;
        int alpha = (int) (pulse * 255);

        for (int i = 0; i < 3; i++) {
            int glowAlpha = alpha - (i * 60);
            if (glowAlpha > 0) {
                g2d.setColor(new Color(SELECTED_BORDER_COLOR.getRed(), SELECTED_BORDER_COLOR.getGreen(), SELECTED_BORDER_COLOR.getBlue(), glowAlpha));

                int inset = 2 - i;
                int size = tileSize + i * 2 - inset * 2;
                g2d.setStroke(new BasicStroke(3f - i * 0.5f));
                g2d.draw(new RoundRectangle2D.Double(boardX + col * tileSize + inset - i,boardY + row * tileSize + inset - i,size, size,15, 15));
            }
        }

        g2d.setColor(new Color(255, 255, 200, 90));
        g2d.fill(new RoundRectangle2D.Double(
                boardX + col * tileSize + 4,
                boardY + row * tileSize + 4,
                tileSize - 8, tileSize - 8,
                10, 10));
    }

    private void drawLastMove(Graphics2D g2d) {
        if (lastMoveFromRow != -1 && lastMoveToRow != -1) {
            g2d.setColor(new Color(255, 200, 100, 80));
            g2d.fillRect(boardX + lastMoveFromCol * tileSize,boardY + lastMoveFromRow * tileSize,tileSize, tileSize);

            g2d.setColor(new Color(255, 200, 100, 120));
            g2d.fillRect(boardX + lastMoveToCol * tileSize,boardY + lastMoveToRow * tileSize,tileSize, tileSize);

            g2d.setColor(new Color(255, 200, 100, 180));
            g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int x1 = boardX + lastMoveFromCol * tileSize + tileSize / 2;
            int y1 = boardY + lastMoveFromRow * tileSize + tileSize / 2;
            int x2 = boardX + lastMoveToCol * tileSize + tileSize / 2;
            int y2 = boardY + lastMoveToRow * tileSize + tileSize / 2;

            drawArrow(g2d, x1, y1, x2, y2, 10, 4);
        }
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, int arrowSize, int arrowWidth) {
        g2d.drawLine(x1, y1, x2, y2);

        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        double xDirection = dx / length;
        double yDirection = dy / length;

        double xPerpendicular = -yDirection;
        double yPerpendicular = xDirection;

        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        xPoints[0] = x2;
        yPoints[0] = y2;

        xPoints[1] = (int) (x2 - arrowSize * xDirection + arrowWidth * xPerpendicular);
        yPoints[1] = (int) (y2 - arrowSize * yDirection + arrowWidth * yPerpendicular);

        xPoints[2] = (int) (x2 - arrowSize * xDirection - arrowWidth * xPerpendicular);
        yPoints[2] = (int) (y2 - arrowSize * yDirection - arrowWidth * yPerpendicular);

        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawLegalMoves(Graphics2D g2d) {
        if (selectedRow != -1 && selectedCol != -1) {
            legalMoves.clear();

            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] dir : directions) {
                int r = selectedRow;
                int c = selectedCol;

                while (true) {
                    r += dir[0];
                    c += dir[1];

                    if (!isValidPosition(r, c) || board[r][c] != null) {
                        break;
                    }

                    legalMoves.add(new Point(c, r));
                }
            }

            float pulse = (float) Math.sin(animationPhase * Math.PI * 2) * 0.3f + 0.7f;

            for (Point move : legalMoves) {
                int x = boardX + move.x * tileSize;
                int y = boardY + move.y * tileSize;

                int circleSize = (int) (tileSize * 0.4 * pulse);
                int circleX = x + (tileSize - circleSize) / 2;
                int circleY = y + (tileSize - circleSize) / 2;

                for (int i = 0; i < 3; i++) {
                    int size = circleSize + i * 5;
                    int alpha = 120 - i * 40;
                    if (alpha > 0) {
                        g2d.setColor(new Color(0, 200, 0, alpha));
                        g2d.fill(new Ellipse2D.Double(x + (tileSize - size) / 2, y + (tileSize - size) / 2, size, size));
                    }
                }

                g2d.setColor(VALID_MOVE_COLOR);
                g2d.fill(new Ellipse2D.Double(circleX, circleY, circleSize, circleSize));

                g2d.setColor(VALID_MOVE_BORDER);
                g2d.setStroke(new BasicStroke(2f));
                g2d.draw(new Ellipse2D.Double(circleX, circleY, circleSize, circleSize));

                if (selectedRow != -1 && selectedCol != -1) {
                    g2d.setColor(new Color(0, 200, 0, 100));
                    g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    int x1 = boardX + selectedCol * tileSize + tileSize / 2;
                    int y1 = boardY + selectedRow * tileSize + tileSize / 2;
                    int x2 = x + tileSize / 2;
                    int y2 = y + tileSize / 2;

                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }

    private void drawTurnIndicator(Graphics2D g2d) {
        String turnText = isWhiteTurn ? "WHITE'S TURN" : "BLACK'S TURN";
        Font turnFont = new Font("Arial", Font.BOLD, 20); //  ปรับขนาดตัวอักษรให้ใหญ่ขึ้น
        g2d.setFont(turnFont);
    
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(turnText);
        int textHeight = fm.getHeight();
    
        float pulse = (float) Math.sin(animationPhase * Math.PI * 2) * 0.1f + 0.9f;
    
        int bgWidth = textWidth + 60; //  เพิ่มความกว้างของกรอบ
        int bgHeight = textHeight + 20; //  เพิ่มความสูงของกรอบ
        int bgX = boardX + (boardSize - bgWidth) / 2;
        int bgY = boardY - bgHeight - 13; // 
    
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fill(new RoundRectangle2D.Double(bgX + 2, bgY + 2, bgWidth, bgHeight, 14, 14));
    
        g2d.setColor(isWhiteTurn ? new Color(255, 255, 255, 220) : new Color(0, 0, 0, 220));
        g2d.fill(new RoundRectangle2D.Double(bgX, bgY, bgWidth, bgHeight, 14, 14));
    
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.setColor(new Color(255, 223, 128, (int) (150 * pulse)));
        g2d.draw(new RoundRectangle2D.Double(bgX, bgY, bgWidth, bgHeight, 14, 14));
    
        int iconSize = bgHeight - 10; //  เพิ่มขนาดไอคอน
        int iconX = bgX + 12;
        int textX = iconX + iconSize + 12;
    
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(iconX, bgY + 6, iconSize, iconSize);
    
        g2d.setColor(isWhiteTurn ? Color.WHITE : Color.BLACK);
        g2d.fillOval(iconX, bgY + 4, iconSize, iconSize);
    
        g2d.setColor(isWhiteTurn ? new Color(0, 0, 0, 50) : new Color(255, 255, 255, 50));
        g2d.drawOval(iconX, bgY + 4, iconSize, iconSize);
    
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.drawString(turnText, textX + 1, bgY + (bgHeight + textHeight) / 2 - 3);
    
        g2d.setColor(isWhiteTurn ? Color.BLACK : Color.WHITE);
        g2d.drawString(turnText, textX, bgY + (bgHeight + textHeight) / 2 - 4);
    
        // System.out.println("Current turn: " + turnText + ", isWhiteTurn: " + isWhiteTurn);
    }
    
    
    private void drawBoardCoordinates(Graphics2D g2d) {
        Font coordFont = new Font("Arial", Font.BOLD, 18);
        g2d.setFont(coordFont);
    
        for (int c = 0; c < cols; c++) {
            String colLabel;
            if (isBoardFlipped) {
                colLabel = String.valueOf((char) ('H' - c));
            } else {
                colLabel = String.valueOf((char) ('A' + c));
            }
    
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(colLabel);
    
            boolean isLight = c % 2 == 0;
            g2d.setColor(isLight ? COORDINATE_LIGHT : COORDINATE_DARK);
            g2d.drawString(colLabel, boardX + c * tileSize + (tileSize - labelWidth) / 2, boardY - 7);
    
            isLight = c % 2 == 1;
            g2d.setColor(isLight ? COORDINATE_LIGHT : COORDINATE_DARK);
            g2d.drawString(colLabel, boardX + c * tileSize + (tileSize - labelWidth) / 2, boardY + boardSize + 15);
        }
    
        for (int r = 0; r < rows; r++) {
            String rowLabel;
            if (isBoardFlipped) {
                rowLabel = String.valueOf(r + 1);
            } else {
                rowLabel = String.valueOf(8 - r);
            }
    
            FontMetrics fm = g2d.getFontMetrics();
            int labelHeight = fm.getHeight();
    
            boolean isLight = r % 2 == 1;
            g2d.setColor(isLight ? COORDINATE_LIGHT : COORDINATE_DARK);
            g2d.drawString(rowLabel, boardX - 15, boardY + r * tileSize + (tileSize + labelHeight) / 2 - 3);
    
            isLight = r % 2 == 0;
            g2d.setColor(isLight ? COORDINATE_LIGHT : COORDINATE_DARK);
            g2d.drawString(rowLabel, boardX + boardSize + 7, boardY + r * tileSize + (tileSize + labelHeight) / 2 - 3);
        }
    }

    private void initializePieces(int tileSize) {
        for (int j = 0; j < cols; j++) {
            try {
                board[0][j] = new RokPiece(RokPiece.Color.BLACK, ROK_PATH, tileSize);
                board[7][j] = new RokPiece(RokPiece.Color.WHITE, ROK_PATH, tileSize);
            } catch (Exception e) {
                System.err.println("เกิดข้อผิดพลาดในการสร้างหมาก: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void switchPieces() {
        boolean hasPieces = false;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] != null) {
                    hasPieces = true;
                    break;
                }
            }
            if (hasPieces) {
                break;
            }
        }

        if (!hasPieces) {
            initializePieces(tileSize);
            repaint();
            return;
        }
        RokPiece[][] newBoard = new RokPiece[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] != null) {
                    int newRow = 7 - r;
                    newBoard[newRow][c] = board[r][c];
                }
            }
        }
        board = newBoard;
        isBoardFlipped = !isBoardFlipped;
        selectedRow = -1;
        selectedCol = -1;
        lastMoveFromRow = -1;
        lastMoveFromCol = -1;
        lastMoveToRow = -1;
        lastMoveToCol = -1;
        repaint();

        // System.out.println("สลับมุมมองกระดานเรียบร้อย");
    }

    private void handleMousePress(MouseEvent e) {
        if (boardSize == 0) {
            return;
        }
        int mouseX = e.getX() - boardX;
        int mouseY = e.getY() - boardY;

        if (mouseX < 0 || mouseX >= boardSize || mouseY < 0 || mouseY >= boardSize) {
            return;
        }

        int col = mouseX / tileSize;
        int row = mouseY / tileSize;

        if (selectedRow == -1 && selectedCol == -1) {
            selectPiece(row, col);
        } else {
            movePiece(row, col);
        }
        repaint();

        if (isGameOver()) {
            String winner = getWinner();
            JOptionPane.showMessageDialog(this, winner.equals("DRAW") ? "Game ended in a draw!" : winner + " wins the game!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            if (gameController != null) {
                gameController.gameOver(winner);
            }
        }
    }

    private void selectPiece(int row, int col) {
        if (board[row][col] != null) {
            boolean isWhitePiece = (board[row][col].getColor() == RokPiece.Color.WHITE);
            if (isWhitePiece == isWhiteTurn) {
                selectedRow = row;
                selectedCol = col;
                board[row][col].setSelected(true);

                playSound("select");

                // System.out.println("Selecting piece at (" + row + "," + col + "), isWhiteTurn: " + isWhiteTurn);
            } else {
                // System.out.println("Cannot select piece at (" + row + "," + col + "): Wrong turn, isWhiteTurn: " + isWhiteTurn);
            }
        }
    }

    private void movePiece(int toRow, int toCol) {
        if (isValidMove(selectedRow, selectedCol, toRow, toCol)) {
            lastMoveFromRow = selectedRow;
            lastMoveFromCol = selectedCol;
            lastMoveToRow = toRow;
            lastMoveToCol = toCol;

            board[toRow][toCol] = board[selectedRow][selectedCol];
            board[selectedRow][selectedCol] = null;

            boolean captured = checkCaptures(toRow, toCol);

            if (captured) {
                playSound("capture");
            } else {
                playSound("move");
            }

            boolean previousTurn = isWhiteTurn;
            isWhiteTurn = !isWhiteTurn;
            // System.out.println("After move from (" + selectedRow + "," + selectedCol + ") to (" + toRow + "," + toCol + "), isWhiteTurn changed from " + previousTurn + " to: " + isWhiteTurn);
            if (gameController != null) {
                gameController.switchTurn();
                // System.out.println("Called gameController.switchTurn(), isWhiteTurn in Board: " + isWhiteTurn);
            } else {
                // System.out.println("gameController is null!");
            }
            isWhiteTurn = !isWhiteTurn;

            board[toRow][toCol].setSelected(false);
            selectedRow = -1;
            selectedCol = -1;
            legalMoves.clear();

            checkAtari();
        } else if (board[toRow][toCol] != null && ((board[toRow][toCol].getColor() == RokPiece.Color.WHITE && isWhiteTurn) || (board[toRow][toCol].getColor() == RokPiece.Color.BLACK && !isWhiteTurn))) {
            if (selectedRow != -1 && selectedCol != -1 && board[selectedRow][selectedCol] != null) {
                board[selectedRow][selectedCol].setSelected(false);
            }
            selectedRow = toRow;
            selectedCol = toCol;
            board[toRow][toCol].setSelected(true);
            playSound("select");
            System.out.println("Selected new piece at (" + toRow + "," + toCol + "), isWhiteTurn: " + isWhiteTurn);
        } else {
            if (selectedRow != -1 && selectedCol != -1 && board[selectedRow][selectedCol] != null) {
                board[selectedRow][selectedCol].setSelected(false);
            }
            selectedRow = -1;
            selectedCol = -1;
            legalMoves.clear();
            playSound("error");
            // System.out.println("Invalid move attempted from (" + selectedRow + "," + selectedCol + ") to (" + toRow + "," + toCol + "), isWhiteTurn: " + isWhiteTurn);
        }
    }
    private void playSound(String soundType) {
        if (gameController != null) {
            gameController.playSound(soundType);
        }
    }
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (board[toRow][toCol] != null) {
            return false;
        } else if (fromCol != toCol && fromRow != toRow) {
            return false;
        }

        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);

        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;

        while (currentRow != toRow || currentCol != toCol) {
            if (board[currentRow][currentCol] != null) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;
    }
    private boolean checkCaptures(int row, int col) {
        RokPiece movingPiece = board[row][col];
        if (movingPiece == null) {
            System.out.println("DEBUG: หมากที่เดินไม่มีอยู่จริงที่ตำแหน่ง (" + row + "," + col + ")");
            return false;
        }

        RokPiece.Color currentColor = movingPiece.getColor();
        RokPiece.Color opponentColor = (currentColor == RokPiece.Color.WHITE) ? RokPiece.Color.BLACK : RokPiece.Color.WHITE;

        // System.out.println("DEBUG: ตรวจสอบการกินโดยหมาก" + (currentColor == RokPiece.Color.WHITE ? "ขาว" : "ดำ")+ " ที่ตำแหน่ง (" + row + "," + col + ")");
        boolean captured = false;
        int[][] directions = {{0, -1, 0, 1}, {-1, 0, 1, 0}}; // [ซ้าย, บน, ขวา, ล่าง] แต่ละคู่

        for (int[] dir : directions) {
            int r1 = row + dir[0];
            int c1 = col + dir[1];
            int r2 = row + dir[2];
            int c2 = col + dir[3];

            // System.out.println("DEBUG: ตรวจสอบการหนีบที่ตำแหน่ง (" + r1 + "," + c1 + ") และ (" + r2 + "," + c2 + ")");

            // ตรวจสอบว่าทั้งสองตำแหน่งอยู่ในกระดานและมีหมากของฝ่ายตรงข้าม
            if (isValidPosition(r1, c1) && isValidPosition(r2, c2)) {
                boolean hasOpponentPiece1 = (board[r1][c1] != null && board[r1][c1].getColor() == opponentColor);
                boolean hasOpponentPiece2 = (board[r2][c2] != null && board[r2][c2].getColor() == opponentColor);

                // System.out.println("DEBUG: มีหมากฝ่ายตรงข้ามที่ตำแหน่ง (" + r1 + "," + c1 + "): " + hasOpponentPiece1);
                // System.out.println("DEBUG: มีหมากฝ่ายตรงข้ามที่ตำแหน่ง (" + r2 + "," + c2 + "): " + hasOpponentPiece2);
                // ถ้ามีหมากฝ่ายตรงข้ามทั้งสองด้าน จะกินทั้งสองตัว
                if (hasOpponentPiece1 && hasOpponentPiece2) {
                    // ลบหมากฝ่ายตรงข้ามทั้งสองตัว
                    board[r1][c1] = null;
                    board[r2][c2] = null;
                    if (opponentColor == RokPiece.Color.WHITE) {
                        whiteCaptured += 2;
                        whiteRemaining -= 2;
                    } else {
                        blackCaptured += 2;
                        blackRemaining -= 2;
                    }
                    captured = true;
                    showCaptureEffect(r1, c1);
                    showCaptureEffect(r2, c2);
                    // System.out.println("กินหมากแบบหนีบที่ตำแหน่ง (" + r1 + "," + c1 + ") และ (" + r2 + "," + c2 + ")");
                }
            }
            for (int i = 0; i < 2; i++) {
                int dr = dir[i * 2];
                int dc = dir[i * 2 + 1];

                if (dr == 0 && dc == 0) {
                    continue;
                }
                // System.out.println("DEBUG: ตรวจสอบการกินแบบโอเทลโล่ในทิศทาง (" + dr + "," + dc + ")");
                // ไว้เก็บ ตําแหน้่งของหมากในแนวเดียวกัน 
                List<Point> opponentPieces = new ArrayList<>();
                int r = row + dr;
                int c = col + dc;

                while (isValidPosition(r, c) && board[r][c] != null && board[r][c].getColor() == opponentColor) {
                    opponentPieces.add(new Point(r, c));
                    r += dr;
                    c += dc;
                }
                // System.out.println("DEBUG: พบหมากฝ่ายตรงข้าม " + opponentPieces.size() + " ตัวในแนวเดียวกัน");

                boolean hasSameColorAtEnd = (isValidPosition(r, c) && board[r][c] != null && board[r][c].getColor() == currentColor);
                // System.out.println("DEBUG: มีหมากสีเดียวกันที่ปลายแถว: " + hasSameColorAtEnd);

                if (hasSameColorAtEnd && !opponentPieces.isEmpty()) {
                    for (Point p : opponentPieces) {
                        // System.out.println("DEBUG: กินหมากที่ตำแหน่ง (" + p.x + "," + p.y + ")");
                        board[p.x][p.y] = null;

                        showCaptureEffect(p.x, p.y);
                    }
                    if (currentColor == RokPiece.Color.WHITE) {
                        whiteCaptured += opponentPieces.size();
                        whiteRemaining -= opponentPieces.size();
                    } else {
                        blackCaptured += opponentPieces.size();
                        blackRemaining -= opponentPieces.size();
                    }
                    captured = true;
                    // System.out.println("กินหมากแบบโอเทลโล่ในแนว " + (dr == 0 ? "แนวนอน" : "แนวตั้ง"));
                }
            }
        }
        if (captured && gameController != null) {
            gameController.updatePieceCount(whiteRemaining, blackRemaining, whiteCaptured, blackCaptured);
        }
        
        return captured;
    }

    private void checkAtari() {

        if (lastMoveFromRow == -1 && lastMoveToRow == -1) {
            // System.out.println("DEBUG: ข้ามการตรวจสอบ Atari เนื่องจากยังไม่มีการเดินหมาก");
            return;
        }

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // บน, ล่าง, ซ้าย, ขวา

        RokPiece.Color currentColor = null;
        if (lastMoveToRow != -1 && lastMoveToCol != -1 && board[lastMoveToRow][lastMoveToCol] != null) {
            currentColor = board[lastMoveToRow][lastMoveToCol].getColor();
            // System.out.println("DEBUG: หมาก" + (currentColor == RokPiece.Color.WHITE ? "ขาว" : "ดำ")+ " เพิ่งเดินไปที่ตำแหน่ง (" + lastMoveToRow + "," + lastMoveToCol + ")");
        } else {
            // System.out.println("DEBUG: ไม่พบข้อมูลของหมากที่เพิ่งเดิน");
            return;
        }

        RokPiece.Color opponentColor = (currentColor == RokPiece.Color.WHITE) ? RokPiece.Color.BLACK : RokPiece.Color.WHITE;
        System.out.println("DEBUG: ตรวจสอบ Atari สำหรับหมาก" + (opponentColor == RokPiece.Color.WHITE ? "ขาว" : "ดำ"));

        List<Point> piecesToRemove = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] != null && board[r][c].getColor() == opponentColor) {
                    if ((r == 0 || r == 7) && lastMoveToRow == -1) {
                        continue;
                    }

                    int liberties = 0;
                    boolean hasSurroundingSpace = false;
                    System.out.println("DEBUG: ตรวจสอบหมาก" + (board[r][c].getColor() == RokPiece.Color.WHITE ? "ขาว" : "ดำ") + " ที่ตำแหน่ง (" + r + "," + c + ")");

                    for (int[] dir : directions) {
                        int nr = r + dir[0];
                        int nc = c + dir[1];

                        if (isValidPosition(nr, nc)) {
                            if (board[nr][nc] == null) {
                                liberties++;
                                hasSurroundingSpace = true;
                                // System.out.println("DEBUG: พบช่องว่างที่ตำแหน่ง (" + nr + "," + nc + ")");
                            } else if (board[nr][nc].getColor() == opponentColor) {
                                int neighborLiberties = countLibertiesForGroup(nr, nc, opponentColor, new boolean[rows][cols]);
                                if (neighborLiberties > 0) {
                                    hasSurroundingSpace = true;
                                }
                                // System.out.println("DEBUG: พบหมาก" + (board[nr][nc].getColor() == RokPiece.Color.WHITE ? "ขาว" : "ดำ") + " ที่ตำแหน่ง (" + nr + "," + nc + ") มี liberties = " + neighborLiberties);
                            } else {
                                // System.out.println("DEBUG: พบหมาก" + (board[nr][nc].getColor() == RokPiece.Color.WHITE ? "ขาว" : "ดำ") + " ที่ตำแหน่ง (" + nr + "," + nc + ")");
                            }
                        }
                    }

                    // System.out.println("DEBUG: หมากที่ตำแหน่ง (" + r + "," + c + ") มี liberties ทั้งหมด = " + liberties);
                    // System.out.println("DEBUG: หมากที่ตำแหน่ง (" + r + "," + c + ") มีพื้นที่ว่างรอบๆ: " + hasSurroundingSpace);
                    if (!hasSurroundingSpace) {
                        piecesToRemove.add(new Point(r, c));
                        // System.out.println("พบหมากที่ถูก Atari ที่ตำแหน่ง (" + r + "," + c + ")");
                    }
                }
            }
        }
        int capturedCount = 0;
        // ลบหมากที่ถูก Atari (ก็คือที่ถูกกิน)
        for (Point p : piecesToRemove) {
            if (board[p.x][p.y] != null) {
                RokPiece.Color capturedColor = board[p.x][p.y].getColor();
                System.out.println("DEBUG: กำลังลบหมาก" + (capturedColor == RokPiece.Color.WHITE ? "ขาว" : "ดำ") + " ที่ตำแหน่ง (" + p.x + "," + p.y + ")");
                board[p.x][p.y] = null;
                showCaptureEffect(p.x, p.y);
                playSound("capture");
                if (capturedColor == RokPiece.Color.WHITE) {
                    whiteCaptured++;
                    whiteRemaining--;
                } else {
                    blackCaptured++;
                    blackRemaining--;
                }
                // System.out.println("หมาก" + (capturedColor == RokPiece.Color.WHITE ? "ขาว" : "ดำ") + " ถูกกินที่ตำแหน่ง (" + p.x + "," + p.y + ") เนื่องจากถูก Atari");
            }
        }
        if (capturedCount > 0 && gameController != null) {
            gameController.updatePieceCount(whiteRemaining, blackRemaining, whiteCaptured, blackCaptured);
        }
    }

    private int countLibertiesForGroup(int row, int col, RokPiece.Color color, boolean[][] visited) {
        if (!isValidPosition(row, col) || visited[row][col]) {
            return 0;
        } else if (board[row][col] == null) {
            return 1;
        } else if (board[row][col].getColor() != color) {
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
    private void showCaptureEffect(int row, int col) {
        int x = boardX + col * tileSize;
        int y = boardY + row * tileSize;

        Timer captureTimer = new Timer(50, new ActionListener() {
            private int frame = 0;
            private final int MAX_FRAMES = 5;

            @Override
            public void actionPerformed(ActionEvent e) {
                frame++;
                if (frame >= MAX_FRAMES) {
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            }
        });
        captureTimer.setRepeats(true);
        captureTimer.start();
    }
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    private boolean isGameOver() {
        int whiteCount = 0, blackCount = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] != null) {
                    if (board[r][c].getColor() == RokPiece.Color.WHITE) {
                        whiteCount++;
                    } else {
                        blackCount++;
                    }
                }
            }
        }
        return whiteCount < 2 || blackCount < 2;
    }
    private String getWinner() {
        int whiteCount = 0, blackCount = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] != null) {
                    if (board[r][c].getColor() == RokPiece.Color.WHITE) {
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
        // System.out.println("setWhiteTurn called, isWhiteTurn: " + this.isWhiteTurn + ", selectedRow: " + selectedRow + ", selectedCol: " + selectedCol);
        repaint();
    }

    public Object getGameController() {
        return this.gameController;
    }

    public void resetGame() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = null;
            }
        }
        initializePieces(tileSize);
        isWhiteTurn = true;
        selectedRow = -1;
        selectedCol = -1;
        lastMoveFromRow = -1;
        lastMoveFromCol = -1;
        lastMoveToRow = -1;
        lastMoveToCol = -1;
        legalMoves.clear();
        repaint();
    }
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && animationTimer != null && !animationTimer.isRunning()) {
            animationTimer.start();
        } else if (!visible && animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }
}
