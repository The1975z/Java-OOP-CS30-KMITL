package board;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import pieces.RokPiece;

public class BoardRenderer {
    private Board board;
    private final Color SELECTED_BORDER_COLOR = new Color(255, 215, 0);
    private final Color VALID_MOVE_COLOR = new Color(0, 200, 0, 120);
    private final Color VALID_MOVE_BORDER = new Color(0, 255, 0, 180);
    private final Color LIGHT_SQUARE_COLOR = new Color(227, 198, 181);
    private final Color DARK_SQUARE_COLOR = new Color(157, 105, 53);
    private final Color BOARD_BORDER_COLOR = new Color(101, 67, 33);
    private final Color COORDINATE_LIGHT = new Color(245, 245, 245);
    private final Color COORDINATE_DARK = new Color(50, 50, 50);
    
    private BufferedImage marbleTexture;
    private GradientPaint boardGradient;
    private float animationPhase;

    public BoardRenderer(Board board) {
        this.board = board;
    }
    
    public void setAnimationPhase(float phase) {
        this.animationPhase = phase;
    }

    public void render(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (boardGradient == null || boardGradient.getPoint1().getX() != 0 || boardGradient.getPoint2().getX() != board.getWidth()) {
            boardGradient = new GradientPaint(0, 0, new Color(30, 30, 40), board.getWidth(), board.getHeight(), new Color(15, 15, 20));
        }

        g2d.setPaint(boardGradient);
        g2d.fillRect(0, 0, board.getWidth(), board.getHeight());

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
        int boardX = board.getBoardX();
        int boardY = board.getBoardY();
        int boardSize = board.getBoardSize();

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
        int boardX = board.getBoardX();
        int boardY = board.getBoardY();
        int boardSize = board.getBoardSize();

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
        int boardX = board.getBoardX();
        int boardY = board.getBoardY();
        int boardSize = board.getBoardSize();
        int tileSize = board.getTileSize();
        int rows = board.getRows();
        int cols = board.getCols();
        int selectedRow = board.getSelectedRow();
        int selectedCol = board.getSelectedCol();
        RokPiece[][] pieces = board.getBoard();

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
                    g2d.fillOval(boardX + c * tileSize + tileSize / 2 - dotSize / 2, boardY + r * tileSize + tileSize / 2 - dotSize / 2, dotSize, dotSize);
                }

                if (pieces[r][c] != null) {
                    Image pieceImage = pieces[r][c].getImage();
                    if (pieceImage != null) {
                        int offset = (tileSize - pieceImage.getWidth(null)) / 2;

                        g2d.setColor(new Color(0, 0, 0, 40));
                        g2d.fillOval(boardX + c * tileSize + offset + 3, boardY + r * tileSize + offset + 3, tileSize - 10, tileSize - 10);

                        if (r == selectedRow && c == selectedCol) {
                            drawSelectedPieceHighlight(g2d, c, r);
                        }

                        g2d.drawImage(pieceImage, boardX + c * tileSize + offset, boardY + r * tileSize + offset, tileSize - 10, tileSize - 10, board);
                    }
                }
            }
        }
    }

    private void drawSelectedPieceHighlight(Graphics2D g2d, int col, int row) {
        float pulse = (float) Math.sin(animationPhase * Math.PI * 2) * 0.2f + 0.8f;
        int alpha = (int) (pulse * 255);
        int boardX = board.getBoardX();
        int boardY = board.getBoardY();
        int tileSize = board.getTileSize();

        for (int i = 0; i < 3; i++) {
            int glowAlpha = alpha - (i * 60);
            if (glowAlpha > 0) {
                g2d.setColor(new Color(SELECTED_BORDER_COLOR.getRed(), SELECTED_BORDER_COLOR.getGreen(), SELECTED_BORDER_COLOR.getBlue(), glowAlpha));

                int inset = 2 - i;
                int size = tileSize + i * 2 - inset * 2;
                g2d.setStroke(new BasicStroke(3f - i * 0.5f));
                g2d.draw(new RoundRectangle2D.Double(boardX + col * tileSize + inset - i, boardY + row * tileSize + inset - i, size, size, 15, 15));
            }
        }

        g2d.setColor(new Color(255, 255, 200, 90));
        g2d.fill(new RoundRectangle2D.Double( boardX + col * tileSize + 4, boardY + row * tileSize + 4, tileSize - 8, tileSize - 8, 10, 10));
    }

    private void drawLastMove(Graphics2D g2d) {
        int lastMoveFromRow = board.getLastMoveFromRow();
        int lastMoveFromCol = board.getLastMoveFromCol();
        int lastMoveToRow = board.getLastMoveToRow();
        int lastMoveToCol = board.getLastMoveToCol();
        int boardX = board.getBoardX();
        int boardY = board.getBoardY();
        int tileSize = board.getTileSize();

        if (lastMoveFromRow != -1 && lastMoveToRow != -1) {
            g2d.setColor(new Color(255, 200, 100, 80));
            g2d.fillRect(boardX + lastMoveFromCol * tileSize, boardY + lastMoveFromRow * tileSize, tileSize, tileSize);

            g2d.setColor(new Color(255, 200, 100, 120));
            g2d.fillRect(boardX + lastMoveToCol * tileSize, boardY + lastMoveToRow * tileSize, tileSize, tileSize);

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
        int selectedRow = board.getSelectedRow();
        int selectedCol = board.getSelectedCol();
        List<Point> legalMoves = board.getLegalMoves();
        int boardX = board.getBoardX();
        int boardY = board.getBoardY();
        int tileSize = board.getTileSize();

        if (selectedRow != -1 && selectedCol != -1) {
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
        boolean isWhiteTurn = board.isWhiteTurn();
        String turnText = isWhiteTurn ? "WHITE'S TURN" : "BLACK'S TURN";
        Font turnFont = new Font("Arial", Font.BOLD, 20);
        g2d.setFont(turnFont);
    
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(turnText);
        int textHeight = fm.getHeight();
    
        float pulse = (float) Math.sin(animationPhase * Math.PI * 2) * 0.1f + 0.9f;
    
        int bgWidth = textWidth + 60;
        int bgHeight = textHeight + 20;
        int bgX = board.getBoardX() + (board.getBoardSize() - bgWidth) / 2;
        int bgY = board.getBoardY() - bgHeight - 13;
    
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fill(new RoundRectangle2D.Double(bgX + 2, bgY + 2, bgWidth, bgHeight, 14, 14));
    
        g2d.setColor(isWhiteTurn ? new Color(255, 255, 255, 220) : new Color(0, 0, 0, 220));
        g2d.fill(new RoundRectangle2D.Double(bgX, bgY, bgWidth, bgHeight, 14, 14));
    
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.setColor(new Color(255, 223, 128, (int) (150 * pulse)));
        g2d.draw(new RoundRectangle2D.Double(bgX, bgY, bgWidth, bgHeight, 14, 14));
    
        int iconSize = bgHeight - 10;
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
    }
    
    private void drawBoardCoordinates(Graphics2D g2d) {
        Font coordFont = new Font("Arial", Font.BOLD, 18);
        g2d.setFont(coordFont);
        int boardX = board.getBoardX();
        int boardY = board.getBoardY();
        int boardSize = board.getBoardSize();
        int tileSize = board.getTileSize();
        int rows = board.getRows();
        int cols = board.getCols();
        boolean isBoardFlipped = board.isBoardFlipped();
    
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
}
