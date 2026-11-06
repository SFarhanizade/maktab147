package ir.maktabsharif.files.todo.ui;

import ir.maktabsharif.files.todo.dto.CategoryView;
import ir.maktabsharif.files.todo.dto.TaskDetailView;
import ir.maktabsharif.files.todo.entity.TaskStatus;
import ir.maktabsharif.files.todo.service.CategoryService;
import ir.maktabsharif.files.todo.service.TaskService;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TaskDialog extends JDialog {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final TaskDetailView existing;

    private final JTextField titleField = new JTextField(25);
    private final JTextArea descriptionArea = new JTextArea(4, 25);
    private final JTextField dueDateField = new JTextField(12);
    private final JTextField dueTimeField = new JTextField(6);
    private final JComboBox<CategoryView> categoryBox = new JComboBox<>();
    private final JComboBox<TaskStatus> statusBox = new JComboBox<>(TaskStatus.values());
    private final JCheckBox draftBox = new JCheckBox("Save as draft");

    @Getter
    private boolean saved = false;
    private Point initialClick;

    public TaskDialog(Window owner, TaskService taskService,
                      CategoryService categoryService, TaskDetailView existing) {
        super(SwingUtilities.getWindowAncestor(owner), ModalityType.APPLICATION_MODAL);
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.existing = existing;

        setUndecorated(true);
        setBackground(Theme.OUTER_FILL);

        buildUi();
        loadCategories();
        if (existing != null) populate(existing);
        else defaultsForCreate();

        installEscapeToClose();
        pack();
        setLocationRelativeTo(owner);
        SwingUtilities.invokeLater(titleField::requestFocusInWindow);
    }

    private void installEscapeToClose() {
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void buildUi() {
        RoundedPanel mainPanel = new RoundedPanel(24, Color.WHITE, true, Theme.BORDER_SOFT);
        mainPanel.setLayout(new BorderLayout());

        var titleBar = createTitleBar();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(form, gbc, row++, "Title:", titleField);

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SOFT));
        addRow(form, gbc, row++, "Description:", descScroll);

        dueDateField.putClientProperty("JTextField.placeholderText", "yyyy-MM-dd");
        dueTimeField.putClientProperty("JTextField.placeholderText", "HH:mm");
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        datePanel.setOpaque(false);
        datePanel.add(dueDateField);
        datePanel.add(new JLabel("at"));
        datePanel.add(dueTimeField);
        addRow(form, gbc, row++, "Due Date:", datePanel);

        addRow(form, gbc, row++, "Category:", categoryBox);

        if (existing != null) {
            addRow(form, gbc, row++, "Status:", statusBox);
        } else {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            draftBox.setOpaque(false);
            draftBox.setFont(Theme.FONT_BASE);
            draftBox.setForeground(Theme.TEXT_DARK);
            form.add(draftBox, gbc);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        buttons.add(ModernButton.secondary("Cancel", this::dispose));
        JButton saveBtn = ModernButton.primary("Save Task", this::onSave);
        buttons.add(saveBtn);
        getRootPane().setDefaultButton(saveBtn);

        mainPanel.add(titleBar, BorderLayout.NORTH);
        mainPanel.add(form, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 18));

        JLabel titleLabel = new JLabel(existing == null ? "New Task" : "Edit Task");
        titleLabel.setFont(Theme.FONT_H2);
        titleLabel.setForeground(Theme.TEXT_DARK);

        JButton closeBtn = new JButton("✕");
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setFont(Theme.FONT_ICON_SM);
        closeBtn.setForeground(Theme.DANGER);
        closeBtn.setToolTipText("Close (Esc)");
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(closeBtn, BorderLayout.EAST);

        MouseAdapter drag = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick == null) return;
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - initialClick.x, loc.y + e.getY() - initialClick.y);
            }
        };
        titleBar.addMouseListener(drag);
        titleBar.addMouseMotionListener(drag);
        return titleBar;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row,
                        String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.TEXT_DARK);
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        field.setFont(Theme.FONT_TABLE);
        p.add(field, gbc);
    }

    private void loadCategories() {
        categoryBox.removeAllItems();
        List<CategoryView> cats = categoryService.findAll();
        for (CategoryView c : cats) categoryBox.addItem(c);
        if (cats.isEmpty()) {
            SwingUtilities.invokeLater(() -> ModernDialogs.warning(
                    this, "No categories", "No categories exist yet. Create one first."));
        }
    }

    private void populate(TaskDetailView t) {
        titleField.setText(t.title());
        descriptionArea.setText(t.description() == null ? "" : t.description());
        LocalDateTime due = t.dueDate();
        if (due != null) {
            dueDateField.setText(due.toLocalDate().toString());
            dueTimeField.setText(due.toLocalTime().withSecond(0).withNano(0).format(TIME_FMT));
        }
        statusBox.setSelectedItem(t.status());
        for (int i = 0; i < categoryBox.getItemCount(); i++) {
            if (categoryBox.getItemAt(i).id() == t.categoryId()) {
                categoryBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void defaultsForCreate() {
        dueDateField.setText(LocalDate.now().plusDays(1).toString());
        dueTimeField.setText("12:00");
    }

    private void onSave() {
        try {
            String title = titleField.getText().trim();
            String desc = descriptionArea.getText().trim();
            LocalDateTime due = parseDueDate();
            CategoryView cat = (CategoryView) categoryBox.getSelectedItem();
            if (cat == null) throw new IllegalArgumentException("Please select a category.");

            if (existing == null) {
                taskService.create(title, desc, due, draftBox.isSelected(), cat.id());
            } else {
                taskService.update(existing.id(), title, desc, due, cat.id(),
                        (TaskStatus) statusBox.getSelectedItem());
            }
            saved = true;
            dispose();
        } catch (DateTimeParseException ex) {
            ModernDialogs.error(this, "Validation",
                    "Invalid date/time. Use yyyy-MM-dd and HH:mm.");
        } catch (RuntimeException ex) {
            ModernDialogs.error(this, "Error", ex.getMessage());
        }
    }

    private LocalDateTime parseDueDate() {
        LocalDate date = LocalDate.parse(dueDateField.getText().trim());
        LocalTime time = LocalTime.parse(dueTimeField.getText().trim(), TIME_FMT);
        return LocalDateTime.of(date, time);
    }
}