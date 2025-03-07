package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WindowResizer {
    private static final int BORDER_SIZE = 10;
    private boolean isResizing = false;

    public WindowResizer(JFrame frame) {
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isResizing = isInResizeArea(frame, e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isResizing = false;
            }
        });

        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setResizeCursor(frame, e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isResizing) {
                    resizeFrame(frame, e);
                }
            }
        });
    }

    private boolean isInResizeArea(JFrame frame, MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int w = frame.getWidth();
        int h = frame.getHeight();
        return (x >= w - BORDER_SIZE || y >= h - BORDER_SIZE);
    }

    private void setResizeCursor(JFrame frame, MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int w = frame.getWidth();
        int h = frame.getHeight();

        if (x >= w - BORDER_SIZE && y >= h - BORDER_SIZE) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        } else {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }

    private void resizeFrame(JFrame frame, MouseEvent e) {
        frame.setSize(e.getX(), e.getY());
        frame.revalidate();
    }
}
