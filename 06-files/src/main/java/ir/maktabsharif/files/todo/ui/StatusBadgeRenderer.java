package ir.maktabsharif.files.todo.ui;

import ir.maktabsharif.files.todo.entity.TaskStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (value instanceof TaskStatus status) {
            PillLabel pill = new PillLabel();
            Color cellBg = isSelected ? Theme.PRIMARY : (row % 2 == 0 ? Color.WHITE : Theme.ZEBRA);
            pill.configure(status.name(), colorFor(status), cellBg);
            return pill;
        }
        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value == null ? "" : value.toString(), isSelected, hasFocus, row, column);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setForeground(isSelected ? Color.WHITE : Theme.TEXT_DARK);
        return label;
    }

    private Color colorFor(TaskStatus status) {
        return switch (status) {
            case DRAFT -> Theme.DRAFT_CLR;
            case ACTIVE -> Theme.WARNING;
            case DONE -> Theme.SUCCESS;
        };
    }

    private static final class PillLabel extends JLabel {
        private Color bg = Theme.DRAFT_CLR;
        private Color cellBg = Color.WHITE;

        PillLabel() {
            setForeground(Color.WHITE);
            setFont(Theme.FONT_BADGE);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            setOpaque(false);
        }

        void configure(String text, Color bg, Color cellBg) {
            setText(text);
            this.bg = bg;
            this.cellBg = cellBg;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cellBg);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}