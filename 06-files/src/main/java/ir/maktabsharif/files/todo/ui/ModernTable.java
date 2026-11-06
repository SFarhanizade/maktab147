package ir.maktabsharif.files.todo.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public final class ModernTable {
    private ModernTable() {
    }

    public static JTable create(TaskTableModel model) {
        var table = createTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(Theme.ZEBRA);
        table.getTableHeader().setFont(Theme.FONT_TABLE_HEADER);
        table.getTableHeader().setForeground(Theme.TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);

        int statusIdx = findColumnIndex(table, "Status");
        if (statusIdx >= 0) {
            table.getColumnModel().getColumn(statusIdx).setCellRenderer(new StatusBadgeRenderer());
            table.getColumnModel().getColumn(statusIdx).setMaxWidth(130);
        }

        int catIdx = findColumnIndex(table, "Category");
        if (catIdx >= 0) {
            table.getColumnModel().getColumn(catIdx).setCellRenderer(new CategoryTagRenderer());
            table.getColumnModel().getColumn(catIdx).setPreferredWidth(140);
            table.getColumnModel().getColumn(catIdx).setMaxWidth(200);
        }

        int dueIdx = findColumnIndex(table, "Due");
        if (dueIdx >= 0) {
            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(SwingConstants.CENTER);
            table.getColumnModel().getColumn(dueIdx).setCellRenderer(center);
        }

        int idIdx = findColumnIndex(table, "ID");
        if (idIdx >= 0) {
            table.removeColumn(table.getColumnModel().getColumn(idIdx));
        }
        return table;
    }

    private static JTable createTable(TaskTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Theme.ZEBRA);
                    int modelRow = convertRowIndexToModel(row);
                    c.setForeground(model.isOverdue(modelRow) ? Theme.DANGER : Theme.TEXT_DARK);
                } else {
                    c.setBackground(Theme.PRIMARY);
                    c.setForeground(Color.WHITE);
                }
                if (c instanceof JComponent jc) {
                    jc.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                }
                return c;
            }
        };
        table.setFillsViewportHeight(true);
        table.setRowHeight(46);
        table.setShowHorizontalLines(true);
        table.setGridColor(Theme.BORDER_SOFT);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFont(Theme.FONT_TABLE);
        table.setBorder(BorderFactory.createEmptyBorder());
        return table;
    }

    private static int findColumnIndex(JTable table, String name) {
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            if (name.equals(table.getColumnModel().getColumn(i).getHeaderValue())) {
                return i;
            }
        }
        return -1;
    }
}