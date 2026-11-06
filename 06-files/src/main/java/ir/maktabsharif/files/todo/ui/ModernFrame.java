package ir.maktabsharif.files.todo.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class ModernFrame extends JFrame {
    private Point initialClick;
    private static final int RESIZE_MARGIN = 6;
    private boolean maximized = false;
    private Rectangle restoreBounds;

    protected ModernFrame(String title) {
        super(title);
        setUndecorated(true);
        setBackground(Theme.OUTER_FILL);
        setResizable(true);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        installResizeSupport();
        installMaximizeShortcut();
    }

    protected JComponent roundedShell(JComponent content) {
        int cornerRadius = 24;
        return new ShadowRoundedPanel(cornerRadius, Theme.OUTER_FILL, content);
    }

    protected JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 18, 10, 18));

        JPanel lights = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        lights.setOpaque(false);
        lights.add(dot(new Color(0xFF5F57), true, true));

        JLabel titleLabel = new JLabel("Maktab Todo");
        titleLabel.setFont(Theme.FONT_LABEL);
        titleLabel.setForeground(Theme.TEXT_DARK);

        JButton maxBtn = new JButton("⛶");
        maxBtn.setContentAreaFilled(false);
        maxBtn.setBorderPainted(false);
        maxBtn.setFocusPainted(false);
        maxBtn.setFont(Theme.FONT_ICON_SM);
        maxBtn.setForeground(Theme.TEXT_MUTED);
        maxBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        maxBtn.setToolTipText("Maximize / Restore (F11)");
        maxBtn.addActionListener(e -> toggleMaximize());
        lights.add(maxBtn);

        bar.add(lights, BorderLayout.WEST);
        bar.add(titleLabel, BorderLayout.CENTER);

        MouseAdapter drag = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick == null || maximized) return;
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - initialClick.x, loc.y + e.getY() - initialClick.y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                initialClick = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) toggleMaximize();
            }
        };
        bar.addMouseListener(drag);
        bar.addMouseMotionListener(drag);
        return bar;
    }

    private void installMaximizeShortcut() {
        getRootPane().registerKeyboardAction(
                e -> toggleMaximize(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    protected void toggleMaximize() {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        Rectangle screen = gc.getBounds();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);

        if (!maximized) {
            restoreBounds = getBounds();
            setBounds(screen.x + screenInsets.left,
                    screen.y + screenInsets.top,
                    screen.width - screenInsets.left - screenInsets.right,
                    screen.height - screenInsets.top - screenInsets.bottom);
            maximized = true;
        } else {
            if (restoreBounds != null) setBounds(restoreBounds);
            maximized = false;
        }
        revalidate();
        repaint();
    }

    private void installResizeSupport() {
        MouseAdapter resizeHandler = new MouseAdapter() {
            private int lastX, lastY;
            private int edge = 0;

            @Override
            public void mouseMoved(MouseEvent e) {
                edge = edgeAt(e.getPoint());
                setCursor(edge == 0 ? Cursor.getDefaultCursor() : cursorFor(edge));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                edge = edgeAt(e.getPoint());
                lastX = e.getXOnScreen();
                lastY = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (edge == 0) return;
                int dx = e.getXOnScreen() - lastX;
                int dy = e.getYOnScreen() - lastY;
                lastX = e.getXOnScreen();
                lastY = e.getYOnScreen();
                Rectangle b = getBounds();
                int x = b.x, y = b.y, w = b.width, h = b.height;
                if ((edge & 4) != 0) {
                    x += dx;
                    w -= dx;
                }
                if ((edge & 8) != 0) {
                    w += dx;
                }
                if ((edge & 1) != 0) {
                    y += dy;
                    h -= dy;
                }
                if ((edge & 2) != 0) {
                    h += dy;
                }
                if (w < getMinimumSize().width) w = getMinimumSize().width;
                if (h < getMinimumSize().height) h = getMinimumSize().height;
                setBounds(x, y, w, h);
                if (maximized) maximized = false;
            }
        };
        getRootPane().addMouseListener(resizeHandler);
        getRootPane().addMouseMotionListener(resizeHandler);
    }

    private int edgeAt(Point p) {
        int w = getWidth(), h = getHeight();
        int edge = 0;
        if (p.y <= RESIZE_MARGIN) edge |= 1;
        if (p.y >= h - RESIZE_MARGIN) edge |= 2;
        if (p.x <= RESIZE_MARGIN) edge |= 4;
        if (p.x >= w - RESIZE_MARGIN) edge |= 8;
        return edge;
    }

    private Cursor cursorFor(int edge) {
        return switch (edge) {
            case 1, 2 -> Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
            case 4, 8 -> Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
            case 1 | 4, 2 | 8 -> Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
            case 1 | 8, 2 | 4 -> Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
            default -> Cursor.getDefaultCursor();
        };
    }

    private JButton dot(Color c, boolean close, boolean isCloseDot) {
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(13, 13));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBackground(c);
        b.setToolTipText(isCloseDot ? "Close" : null);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (close) {
            b.addActionListener(e -> {
                Window w = SwingUtilities.getWindowAncestor(ModernFrame.this);
                if (w != null) w.dispose();
                System.exit(0);
            });
        }
        return b;
    }
}