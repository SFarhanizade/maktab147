package ir.maktabsharif.files.todo.ui;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private static final int SHADOW_SIZE = 6;

    private final int radius;
    private final Color bgColor;
    private final boolean hasShadow;
    private final Color borderColor;

    public RoundedPanel(int radius, Color bgColor, boolean hasShadow) {
        this(radius, bgColor, hasShadow, null);
    }

    public RoundedPanel(int radius, Color bgColor, boolean hasShadow, Color borderColor) {
        this.radius = radius;
        this.bgColor = bgColor;
        this.hasShadow = hasShadow;
        this.borderColor = borderColor;
        setOpaque(true);
        setBackground(Theme.OUTER_FILL);
    }

    @Override
    public Insets getInsets() {
        Insets base = super.getInsets();
        if (!hasShadow) return base;
        return new Insets(base.top + SHADOW_SIZE, base.left + SHADOW_SIZE,
                base.bottom + SHADOW_SIZE, base.right + SHADOW_SIZE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Theme.OUTER_FILL);
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int inset = hasShadow ? SHADOW_SIZE : 0;
        int w = getWidth() - inset * 2 - 1;
        int h = getHeight() - inset * 2 - 1;

        if (hasShadow) {
            for (int i = 1; i <= SHADOW_SIZE; i++) {
                int alpha = Math.max(0, (int) (12.0 * (1.0 - (double) i / (SHADOW_SIZE + 1))));
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.fillRoundRect(inset - i, inset - i + 2, w + i * 2, h + i * 2, radius, radius);
            }
        }
        g2.setColor(bgColor);
        g2.fillRoundRect(inset, inset, w, h, radius, radius);

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(inset, inset, w, h, radius, radius);
        }
        g2.dispose();
    }
}