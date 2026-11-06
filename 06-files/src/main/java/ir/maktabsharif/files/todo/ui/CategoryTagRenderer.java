package ir.maktabsharif.files.todo.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CategoryTagRenderer extends DefaultTableCellRenderer {
    private static final Color[] PALETTE = {
            new Color(0xE3F2FD), new Color(0xFCE4EC), new Color(0xE8F5E9),
            new Color(0xFFF3E0), new Color(0xF3E5F5), new Color(0xE0F7FA),
            new Color(0xFFFDE7), new Color(0xEDE7F6)
    };
    private static final Color[] TEXT_PALETTE = {
            new Color(0x1565C0), new Color(0xC2185B), new Color(0x2E7D32),
            new Color(0xE65100), new Color(0x6A1B9A), new Color(0x00838F),
            new Color(0xF9A825), new Color(0x4527A0)
    };

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        String name = value == null ? "" : value.toString();
        if (name.isBlank() || "—".equals(name)) {
            JLabel empty = (JLabel) super.getTableCellRendererComponent(
                    table, "", isSelected, hasFocus, row, column);
            empty.setOpaque(true);
            empty.setBackground(isSelected ? Theme.PRIMARY : (row % 2 == 0 ? Color.WHITE : Theme.ZEBRA));
            return empty;
        }

        int idx = Math.floorMod(name.hashCode(), PALETTE.length);
        Color bg = PALETTE[idx];
        Color fg = TEXT_PALETTE[idx];
        Color cellBg = isSelected ? Theme.PRIMARY : (row % 2 == 0 ? Color.WHITE : Theme.ZEBRA);

        TagLabel tag = new TagLabel();
        tag.configure(name, bg, fg, cellBg);
        return tag;
    }

    private static final class TagLabel extends JLabel {
        private Color pillBg = Color.LIGHT_GRAY;
        private Color pillFg = Color.DARK_GRAY;
        private Color cellBg = Color.WHITE;

        TagLabel() {
            setFont(Theme.FONT_BADGE);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            setOpaque(false);
        }

        void configure(String text, Color bg, Color fg, Color cellBg) {
            setText(text);
            this.pillBg = bg;
            this.pillFg = fg;
            this.cellBg = cellBg;
            setForeground(fg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cellBg);
            g2.fillRect(0, 0, getWidth(), getHeight());

            FontMetrics fm = g2.getFontMetrics(getFont());
            int textW = fm.stringWidth(getText());
            int pillW = Math.min(textW + 16, getWidth() - 4);
            int pillH = Math.min(getHeight() - 8, 22);
            int x = (getWidth() - pillW) / 2;
            int y = (getHeight() - pillH) / 2;

            g2.setColor(pillBg);
            g2.fillRoundRect(x, y, pillW, pillH, pillH, pillH);
            g2.dispose();

            setForeground(pillFg);
            super.paintComponent(g);
        }
    }
}