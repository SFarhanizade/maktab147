package ir.maktabsharif.files.todo.ui;

import javax.swing.*;
import java.awt.*;

public class ShadowRoundedPanel extends JPanel {
    public static final Color OUTER_FILL = Theme.GLOW;
    private static final int SHADOW_SIZE = 10;
    private final int radius;
    private final Color bg;

    public ShadowRoundedPanel(int radius, Color bg, JComponent content) {
        this.radius = radius;
        this.bg = bg;
        setOpaque(true);
        setBackground(OUTER_FILL);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE));
        add(content, BorderLayout.CENTER);
    }

    @Override protected void paintComponent(Graphics g) {
        g.setColor(OUTER_FILL);
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = SHADOW_SIZE;
        int y = SHADOW_SIZE;
        int w = getWidth() - SHADOW_SIZE * 2 - 1;
        int h = getHeight() - SHADOW_SIZE * 2 - 1;


        for (int i = 1; i <= SHADOW_SIZE; i++) {
            int alpha = Math.max(0, (int) (10.0 * (1.0 - (double) i / (SHADOW_SIZE + 1))));
            g2.setColor(new Color(0, 0, 0, alpha));
            g2.fillRoundRect(x - i, y - i + 3, w + i * 2, h + i * 2, radius, radius);
        }

        g2.setColor(bg);
        g2.fillRoundRect(x, y, w, h, radius, radius);
        g2.dispose();
    }
}