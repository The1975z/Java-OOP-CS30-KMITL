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

    // เพิ่มตัวแปรสำหรับเก็บค่าเวลาในการเดินแต่ละตา
    private int previousWhiteUsedTime = 0; 
    private int previousBlackUsedTime = 0;

    public GameFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        setTitle("Clamp War");
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
            System.out.println("DEBUG GameFrame.recordMove: Recording move for " + 
                             (isWhiteMove ? "WHITE" : "BLACK") + " with timeSpent=" + timeSpent);
            
            int opponentFromRow = -1;
            int opponentFromCol = -1;
            int opponentToRow = -1;
            int opponentToCol = -1;
            
            // ดึงข้อมูลการเคลื่อนที่ครั้งก่อนหน้าของฝ่ายตรงข้าม
            if (!historyPanel.getMoveHistory().isEmpty()) {
                if (isWhiteMove && !historyPanel.getLastBlackMoveFrom().equals("-")) {
                    opponentFromRow = convertNotationToRow(historyPanel.getLastBlackMoveFrom());
                    opponentFromCol = convertNotationToCol(historyPanel.getLastBlackMoveFrom());
                    opponentToRow = convertNotationToRow(historyPanel.getLastBlackMoveTo());
                    opponentToCol = convertNotationToCol(historyPanel.getLastBlackMoveTo());
                    System.out.println("  Using last black move data");
                } else if (!isWhiteMove && !historyPanel.getLastWhiteMoveFrom().equals("-")) {
                    opponentFromRow = convertNotationToRow(historyPanel.getLastWhiteMoveFrom());
                    opponentFromCol = convertNotationToCol(historyPanel.getLastWhiteMoveFrom());
                    opponentToRow = convertNotationToRow(historyPanel.getLastWhiteMoveTo());
                    opponentToCol = convertNotationToCol(historyPanel.getLastWhiteMoveTo());
                    System.out.println("  Using last white move data");
                }
            }
            
            // ดึงเวลาที่เหลือจาก TimerManager
            int whiteRemainingTime = timerManager.getWhiteTimeSeconds(); // เวลาที่เหลือของขาว (วินาที)
            int blackRemainingTime = timerManager.getBlackTimeSeconds(); // เวลาที่เหลือของดำ (วินาที)
            
            // คำนวณเวลาที่ใช้ไปจากเวลาเริ่มต้น (600 วินาที)
            int totalInitialTime = 600; // 10 นาที
            int whiteUsedTime = totalInitialTime - whiteRemainingTime; // เวลาที่ผู้เล่นขาวใช้ไปทั้งหมด
            int blackUsedTime = totalInitialTime - blackRemainingTime; // เวลาที่ผู้เล่นดำใช้ไปทั้งหมด
            
            System.out.println("  Timer state: White time " + whiteRemainingTime + "s remaining, Black time " + blackRemainingTime + "s remaining");
            System.out.println("  Total used times - White: " + whiteUsedTime + "s, Black: " + blackUsedTime + "s");
            
            // For the first move, explicitly set both timers
            if (historyPanel.isFirstMove()) {
                // Initialize both timers to current values
                historyPanel.initializeTimers(0, 0);  // Start from 0 for accurate first move time calculation
            }
            
            // ตรวจสอบว่าการเคลื่อนไหวครั้งนี้เป็นของฝ่ายใด และส่งข้อมูลในรูปแบบที่ถูกต้อง
            if (isWhiteMove) {
                // การเคลื่อนไหวนี้เป็นของฝ่ายขาว
                historyPanel.addGameMove(
                    fromRow,             // ตำแหน่งเริ่มต้นของขาว (ขาวเพิ่งเดิน)
                    fromCol,
                    toRow,               // ตำแหน่งสุดท้ายของขาว (ขาวเพิ่งเดิน)
                    toCol,
                    opponentFromRow,     // ตำแหน่งเริ่มต้นของดำ (จากตาที่แล้ว ถ้ามี)
                    opponentFromCol,
                    opponentToRow,       // ตำแหน่งสุดท้ายของดำ (จากตาที่แล้ว ถ้ามี)
                    opponentToCol,
                    whiteUsedTime,       // เวลารวมที่ขาวใช้ไป
                    blackUsedTime        // เวลารวมที่ดำใช้ไป
                );
                System.out.println("MOVE DATA: WHITE moved from (" + fromRow + "," + fromCol + ") to (" + toRow + "," + toCol + ")");
            } else {
                // การเคลื่อนไหวนี้เป็นของฝ่ายดำ
                historyPanel.addGameMove(
                    opponentFromRow,     // ตำแหน่งเริ่มต้นของขาว (จากตาที่แล้ว ถ้ามี)
                    opponentFromCol,
                    opponentToRow,       // ตำแหน่งสุดท้ายของขาว (จากตาที่แล้ว ถ้ามี)
                    opponentToCol,
                    fromRow,             // ตำแหน่งเริ่มต้นของดำ (ดำเพิ่งเดิน)
                    fromCol,
                    toRow,               // ตำแหน่งสุดท้ายของดำ (ดำเพิ่งเดิน)
                    toCol,
                    whiteUsedTime,       // เวลารวมที่ขาวใช้ไป
                    blackUsedTime        // เวลารวมที่ดำใช้ไป
                );
                System.out.println("MOVE DATA: BLACK moved from (" + fromRow + "," + fromCol + ") to (" + toRow + "," + toCol + ")");
            }
            
            // แสดงประวัติการเดินทั้งหมดเพื่อการตรวจสอบ
            historyPanel.dumpMoveHistory();
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
        // รีเซ็ตตัวแปรเวลา
        previousWhiteUsedTime = 0;
        previousBlackUsedTime = 0;
        
        dispose();
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }

    
}