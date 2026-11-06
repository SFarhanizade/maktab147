package ir.maktabsharif.files.todo.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ModernButton {
    private ModernButton() {
    }

    public static JButton primary(String text, Runnable action) {
        return build(text, Theme.PRIMARY, Theme.PRIMARY_HOVER, Color.WHITE, action, false);
    }

    public static JButton secondary(String text, Runnable action) {
        return build(text, Color.WHITE, Theme.SURFACE_HOVER, Theme.TEXT_DARK, action, true);
    }

    public static JButton danger(String text, Runnable action) {
        return build(text, Color.WHITE, Theme.DANGER_SOFT, Theme.DANGER, action, true);
    }

    private static JButton build(String text, Color bg, Color hover, Color fg,
                                 Runnable action, boolean outlined) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color fill = getBackground();
                if (!isEnabled()) {
                    fill = blend(fill);
                }
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                if (outlined) {
                    g2.setColor(Theme.BORDER_SOFT);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                }
                g2.dispose();
                super.paintComponent(g);

                if (isFocusOwner()) {
                    Graphics2D f = (Graphics2D) g.create();
                    f.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    f.setColor(Theme.PRIMARY);
                    f.setStroke(new BasicStroke(2f));
                    f.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);
                    f.dispose();
                }
            }
        };
        b.setFont(Theme.FONT_BUTTON);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) {
                    b.setBackground(hover);
                    b.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
                b.repaint();
            }
        });
        b.addPropertyChangeListener("enabled", evt -> {
            if (!b.isEnabled()) b.setBackground(bg);
            b.repaint();
        });
        if (action != null) b.addActionListener(e -> action.run());
        return b;
    }

    private static Color blend(Color a) {
        return new Color(
                (int) (a.getRed() * (1 - (float) 0.55) + Color.WHITE.getRed() * (float) 0.55),
                (int) (a.getGreen() * (1 - (float) 0.55) + Color.WHITE.getGreen() * (float) 0.55),
                (int) (a.getBlue() * (1 - (float) 0.55) + Color.WHITE.getBlue() * (float) 0.55)
        );
    }
}