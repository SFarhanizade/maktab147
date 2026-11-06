package ir.maktabsharif.files.todo.ui;

import ir.maktabsharif.files.todo.dto.CategoryView;
import ir.maktabsharif.files.todo.dto.TaskDetailView;
import ir.maktabsharif.files.todo.dto.TaskView;
import ir.maktabsharif.files.todo.entity.TaskStatus;
import ir.maktabsharif.files.todo.service.CategoryService;
import ir.maktabsharif.files.todo.service.TaskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TaskPanel extends JPanel {

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final TaskTableModel model;
    private final JTable table;

    private final JTextField searchField = new JTextField();
    private final JComboBox<String> statusFilterBox;
    private final JComboBox<Object> categoryFilterBox;

    private JPanel emptyCard;
    private JLabel statusLine;
    private JButton viewBtn, editBtn, doneBtn, deleteBtn;

    public TaskPanel(TaskService taskService, CategoryService categoryService) {
        this.taskService = taskService;
        this.categoryService = categoryService;

        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        model = new TaskTableModel();
        table = ModernTable.create(model);

        statusFilterBox = new JComboBox<>(new String[]{"All", "Draft", "Active", "Done"});
        statusFilterBox.setBackground(Color.WHITE);
        statusFilterBox.setPreferredSize(new Dimension(120, 40));
        statusFilterBox.putClientProperty("JComponent.roundRect", true);
        statusFilterBox.addActionListener(e -> reload());

        categoryFilterBox = new JComboBox<>();
        categoryFilterBox.setBackground(Color.WHITE);
        categoryFilterBox.setPreferredSize(new Dimension(170, 40));
        categoryFilterBox.putClientProperty("JComponent.roundRect", true);
        categoryFilterBox.addActionListener(e -> {
            Object sel = categoryFilterBox.getSelectedItem();
            List<TaskView> tasks = (sel instanceof CategoryView cv)
                    ? taskService.findByCategory(cv.id())
                    : taskService.findAll();
            model.setData(tasks);
            updateEmptyState();
        });

        searchField.putClientProperty("JTextField.placeholderText", "Search tasks...");
        searchField.putClientProperty("JTextField.showClearButton", true);
        searchField.putClientProperty("JComponent.roundRect", true);
        searchField.setPreferredSize(new Dimension(220, 32));
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setOpaque(false);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                onSearch();
            }

            public void removeUpdate(DocumentEvent e) {
                onSearch();
            }

            public void changedUpdate(DocumentEvent e) {
                onSearch();
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> updateActionStates());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int viewRow = table.rowAtPoint(e.getPoint());
                    if (viewRow >= 0) {
                        table.setRowSelectionInterval(viewRow, viewRow);
                        onView();
                    }
                }
            }
        });

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        installShortcuts();
        reload();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel title = new JLabel("Your Tasks");
        title.setFont(Theme.FONT_H1);
        title.setForeground(Theme.TEXT_DARK);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton newBtn = ModernButton.primary("+ New Task", this::onCreate);
        newBtn.setMnemonic(KeyEvent.VK_N);
        newBtn.setToolTipText("Create a new task (Ctrl+N)");

        viewBtn = ModernButton.secondary("View", this::onView);
        viewBtn.setToolTipText("View task details (Ctrl+O / double-click)");

        editBtn = ModernButton.secondary("Edit", this::onEdit);
        editBtn.setToolTipText("Edit selected task (Enter)");

        doneBtn = ModernButton.secondary("Done", this::onMarkDone);

        deleteBtn = ModernButton.danger("Delete", this::onDelete);
        deleteBtn.setToolTipText("Delete selected task (Del)");

        actions.add(newBtn);
        actions.add(viewBtn);
        actions.add(editBtn);
        actions.add(doneBtn);
        actions.add(deleteBtn);

        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(actions, BorderLayout.EAST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setOpaque(false);
        filters.add(buildSearchWrap());
        filters.add(statusFilterBox);
        filters.add(categoryFilterBox);

        header.add(titleRow, BorderLayout.NORTH);
        header.add(filters, BorderLayout.CENTER);
        return header;
    }

    private JComponent buildSearchWrap() {
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
        return wrap;
    }

    private JComponent buildTableArea() {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);

        RoundedPanel tableCard = new RoundedPanel(18, Color.WHITE, false, Theme.BORDER_SOFT);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        tableCard.add(scroll, BorderLayout.CENTER);

        emptyCard = new JPanel(new CardLayout());
        emptyCard.setOpaque(false);
        emptyCard.add(tableCard, "table");
        emptyCard.add(buildEmptyState(), "empty");
        return emptyCard;
    }

    private JComponent buildEmptyState() {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE, false, Theme.BORDER_SOFT);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel icon = new JLabel("📋");
        icon.setFont(Theme.FONT_ICON_LG);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("No tasks yet");
        heading.setFont(Theme.FONT_H2);
        heading.setForeground(Theme.TEXT_DARK);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Click '+ New Task' to get started.");
        sub.setFont(Theme.FONT_BASE);
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(heading);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);
        return wrapper;
    }

    private JComponent buildStatusBar() {
        statusLine = new JLabel("");
        statusLine.setFont(Theme.FONT_SMALL);
        statusLine.setForeground(Theme.TEXT_MUTED);
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(4, 4, 0, 4));
        bar.add(statusLine, BorderLayout.WEST);
        return bar;
    }

    private void updateEmptyState() {
        if (emptyCard == null) return;
        CardLayout cl = (CardLayout) emptyCard.getLayout();
        cl.show(emptyCard, model.getRowCount() == 0 ? "empty" : "table");
        int total = model.getRowCount();
        statusLine.setText(total + (total == 1 ? " task" : " tasks"));
    }

    private void updateActionStates() {
        TaskView t = peekSelected();
        boolean has = t != null;
        TaskStatus st = has ? t.status() : null;
        viewBtn.setEnabled(has);
        editBtn.setEnabled(has);
        deleteBtn.setEnabled(has);
        doneBtn.setEnabled(has && (st == TaskStatus.ACTIVE || st == TaskStatus.DRAFT));
    }

    private void installShortcuts() {
        InputMap im = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = table.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteTask");
        am.put("deleteTask", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                onDelete();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editTask");
        am.put("editTask", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                onEdit();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "newTask");
        getActionMap().put("newTask", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                onCreate();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "viewTask");
        getActionMap().put("viewTask", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                onView();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "focusSearch");
        getActionMap().put("focusSearch", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                searchField.requestFocusInWindow();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearSearch");
        getActionMap().put("clearSearch", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!searchField.getText().isEmpty()) searchField.setText("");
            }
        });
    }

    public void reload() {
        TaskStatus status = switch ((String) statusFilterBox.getSelectedItem()) {
            case "Draft" -> TaskStatus.DRAFT;
            case "Active" -> TaskStatus.ACTIVE;
            case "Done" -> TaskStatus.DONE;
            default -> null;
        };

        List<TaskView> tasks = (status == null)
                ? taskService.findAll()
                : taskService.findByStatus(status);

        model.setData(tasks);

        reloadCategoryFilter();
        updateEmptyState();
        updateActionStates();
    }

    private void reloadCategoryFilter() {
        Integer previous = null;
        Object sel = categoryFilterBox.getSelectedItem();
        if (sel instanceof CategoryView cv) previous = cv.id();

        categoryFilterBox.removeAllItems();
        categoryFilterBox.addItem("All Categories");
        List<CategoryView> cats = categoryService.findAll();
        for (CategoryView c : cats) categoryFilterBox.addItem(c);

        if (previous != null) {
            for (int i = 0; i < categoryFilterBox.getItemCount(); i++) {
                Object it = categoryFilterBox.getItemAt(i);
                if (it instanceof CategoryView cv && cv.id() == previous) {
                    categoryFilterBox.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    private void onSearch() {
        model.setFilter(searchField.getText());
        updateEmptyState();
    }

    private TaskView peekSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        return model.taskAt(modelRow);
    }

    private TaskView selected() {
        TaskView t = peekSelected();
        if (t == null) {
            ModernDialogs.info(this, "No selection", "Select a task first.");
            return null;
        }
        return t;
    }

    private void onCreate() {
        TaskDialog dlg = new TaskDialog(
                SwingUtilities.getWindowAncestor(this), taskService, categoryService, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) reload();
    }

    private void onView() {
        TaskView t = selected();
        if (t == null) return;
        TaskDetailView detail = taskService.findById(t.id());
        new TaskDetailDialog(SwingUtilities.getWindowAncestor(this), detail).setVisible(true);
    }

    private void onEdit() {
        TaskView t = selected();
        if (t == null) return;
        TaskDetailView detail = taskService.findById(t.id());
        TaskDialog dlg = new TaskDialog(
                SwingUtilities.getWindowAncestor(this), taskService, categoryService, detail);
        dlg.setVisible(true);
        if (dlg.isSaved()) reload();
    }

    private void onMarkDone() {
        TaskView t = selected();
        if (t == null) return;
        run(() -> taskService.markDone(t.id()));
    }

    private void onDelete() {
        TaskView t = selected();
        if (t == null) return;
        boolean ok = ModernDialogs.confirm(this, "Delete task",
                "Delete task \"" + t.title() + "\"? This action cannot be undone.");
        if (ok) run(() -> taskService.delete(t.id()));
    }

    private void run(Runnable op) {
        try {
            op.run();
            reload();
        } catch (RuntimeException ex) {
            ModernDialogs.error(this, "Error", ex.getMessage());
        }
    }
}