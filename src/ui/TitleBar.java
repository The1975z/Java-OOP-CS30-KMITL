package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import utils.ResourceManager;

public class TitleBar extends JPanel {

    private int mouseX, mouseY;

    public TitleBar(JFrame frame) {
        setBackground(new Color(30, 30, 30));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(frame.getWidth(), 50));

        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(ResourceManager.loadIcon("icon/ICONKMITL.png", 40, 40));
        add(iconLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);

        buttonPanel.add(createButton("icon/minimizeButton.png", e -> frame.setState(Frame.ICONIFIED)));
        buttonPanel.add(createButton("icon/resizeButton.png", e -> frame.setExtendedState(frame.getExtendedState() == JFrame.MAXIMIZED_BOTH ? JFrame.NORMAL : JFrame.MAXIMIZED_BOTH)));
        buttonPanel.add(createButton("icon/closeButton.png", e -> System.exit(0)));

        add(buttonPanel, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                frame.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
            }
        });
    }

    private JButton createButton(String path, ActionListener action) {
        JButton button = new JButton(ResourceManager.loadIcon(path, 24, 24));
        button.setPreferredSize(new Dimension(35, 35));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        return button;
    }
}
