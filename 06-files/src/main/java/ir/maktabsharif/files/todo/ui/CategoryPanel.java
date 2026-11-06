package ir.maktabsharif.files.todo.ui;

import ir.maktabsharif.files.todo.dto.CategoryView;
import ir.maktabsharif.files.todo.service.CategoryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class CategoryPanel extends JPanel {
    private final CategoryService service;
    private final Runnable onChanged;
    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField searchField = new JTextField();

    public CategoryPanel(CategoryService service, Runnable onChanged) {
        this.service = service;
        this.onChanged = onChanged;
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        model = new DefaultTableModel(new String[]{"ID", "Name"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Theme.ZEBRA);
                    c.setForeground(Theme.TEXT_DARK);
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
        table.setRowHeight(44);
        table.setShowHorizontalLines(true);
        table.setGridColor(Theme.BORDER_SOFT);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(Theme.FONT_TABLE);
        table.setBorder(BorderFactory.createEmptyBorder());
        table.getTableHeader().setBackground(Theme.ZEBRA);
        table.getTableHeader().setFont(Theme.FONT_TABLE_HEADER);
        table.getTableHeader().setForeground(Theme.TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setMaxWidth(70);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchField.putClientProperty("JTextField.placeholderText", "Filter categories...");
        searchField.putClientProperty("JTextField.showClearButton", true);
        searchField.putClientProperty("JComponent.roundRect", true);
        searchField.setPreferredSize(new Dimension(220, 32));
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setOpaque(false);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
        reload();
    }

    private void applyFilter() {
        String t = searchField.getText().trim();
        sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t, 1));
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 15));
        header.setOpaque(false);
        JLabel title = new JLabel("Categories");
        title.setFont(Theme.FONT_H1);
        title.setForeground(Theme.TEXT_DARK);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));

        RoundedPanel wrap = new RoundedPanel(14, Color.WHITE, false, Theme.BORDER_SOFT);
        wrap.setLayout(new BorderLayout(8, 0));
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        wrap.setPreferredSize(new Dimension(280, 40));
        wrap.setMaximumSize(new Dimension(280, 40));
        wrap.setMinimumSize(new Dimension(200, 40));
        JLabel icon = new JLabel("🔍");
        icon.setForeground(Theme.TEXT_MUTED);
        wrap.add(icon, BorderLayout.WEST);
        wrap.add(searchField, BorderLayout.CENTER);
        controls.add(wrap);
        controls.add(Box.createHorizontalGlue());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(ModernButton.primary("+New Category", this::onCreate));
        actions.add(ModernButton.secondary("Edit", this::onEdit));
        actions.add(ModernButton.danger("Delete", this::onDelete));
        controls.add(actions);

        header.add(title, BorderLayout.NORTH);
        header.add(controls, BorderLayout.CENTER);
        return header;
    }

    private JComponent buildTableCard() {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);
        RoundedPanel card = new RoundedPanel(18, Color.WHITE, false, Theme.BORDER_SOFT);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(6, 6, 6, 6));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    public void reload() {
        model.setRowCount(0);
        List<CategoryView> cats = service.findAll();
        for (CategoryView c : cats) {
            model.addRow(new Object[]{c.id(), c.name()});
        }
    }

    private CategoryView selected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            ModernDialogs.info(this, "No selection", "Select a category first.");
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (int) model.getValueAt(modelRow, 0);
        try {
            return service.findById(id);
        } catch (RuntimeException ex) {
            ModernDialogs.error(this, "Error", ex.getMessage());
            return null;
        }
    }

    private void onCreate() {
        CategoryDialog dlg = new CategoryDialog(SwingUtilities.getWindowAncestor(this), service, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            reload();
            onChanged.run();
        }
    }

    private void onEdit() {
        CategoryView c = selected();
        if (c == null) return;
        CategoryDialog dlg = new CategoryDialog(SwingUtilities.getWindowAncestor(this), service, c);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            reload();
            onChanged.run();
        }
    }

    private void onDelete() {
        CategoryView c = selected();
        if (c == null) return;
        boolean ok = ModernDialogs.confirm(this, "Delete category",
                "Delete category \"" + c.name() + "\"?");
        if (!ok) return;
        try {
            service.delete(c.id());
            reload();
            onChanged.run();
        } catch (RuntimeException ex) {
            ModernDialogs.error(this, "Error", ex.getMessage());
        }
    }
}