package ir.maktabsharif.files.todo.ui;

import ir.maktabsharif.files.todo.dto.TaskDetailView;
import ir.maktabsharif.files.todo.entity.TaskStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskDetailDialog extends JDialog {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Point initialClick;

    public TaskDetailDialog(Window owner, TaskDetailView task) {
        super(owner, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(Theme.OUTER_FILL);
        buildUi(task);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUi(TaskDetailView t) {
        RoundedPanel main = new RoundedPanel(24, Color.WHITE, true, Theme.BORDER_SOFT);
        main.setLayout(new BorderLayout());

        var titleBar = createTitleBar();

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addField(body, gbc, row++, "Title:", t.title());
        addField(body, gbc, row++, "Category:", t.categoryName());
        addField(body, gbc, row++, "Status:",
                t.status() == null ? "—" : t.status().name());
        addField(body, gbc, row++, "Due:",
                t.dueDate() == null ? "—" : t.dueDate().format(FMT));

        boolean overdue = t.status() == TaskStatus.ACTIVE
                && t.dueDate() != null
                && t.dueDate().isBefore(LocalDateTime.now());
        if (overdue) {
            gbc.gridx = 1;
            gbc.gridy = row - 1;
            JLabel od = new JLabel("⚠ Overdue");
            od.setForeground(Theme.DANGER);
            od.setFont(Theme.FONT_LABEL);
            body.add(od, gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel descLbl = new JLabel("Description:");
        descLbl.setFont(Theme.FONT_LABEL);
        descLbl.setForeground(Theme.TEXT_DARK);
        body.add(descLbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;

        JTextArea desc = new JTextArea(5, 30);
        desc.setText(t.description() == null || t.description().isBlank()
                ? "—"
                : t.description());
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setFont(Theme.FONT_TABLE);
        desc.setBackground(Theme.ZEBRA);
        desc.setForeground(Theme.TEXT_DARK);
        desc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SOFT),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        body.add(new JScrollPane(desc), gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        buttons.add(ModernButton.secondary("Close", this::dispose));
        getRootPane().setDefaultButton(null);

        main.add(titleBar, BorderLayout.NORTH);
        main.add(body, BorderLayout.CENTER);
        main.add(buttons, BorderLayout.SOUTH);
        setContentPane(main);
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 18));

        JLabel title = new JLabel("Task Details");
        title.setFont(Theme.FONT_H2);
        title.setForeground(Theme.TEXT_DARK);

        JButton close = new JButton("✕");
        close.setContentAreaFilled(false);
        close.setBorderPainted(false);
        close.setFocusPainted(false);
        close.setForeground(Theme.DANGER);
        close.setToolTipText("Close (Esc)");
        close.setFont(Theme.FONT_ICON_SM);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.addActionListener(e -> dispose());

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(close, BorderLayout.EAST);

        MouseAdapter drag = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { initialClick = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e) {
                if (initialClick == null) return;
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - initialClick.x, loc.y + e.getY() - initialClick.y);
            }
        };
        titleBar.addMouseListener(drag);
        titleBar.addMouseMotionListener(drag);
        return titleBar;
    }

    private void addField(JPanel p, GridBagConstraints gbc, int row,
                          String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.TEXT_DARK);
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel val = new JLabel(value == null || value.isBlank() ? "—" : value);
        val.setFont(false ? Theme.FONT_LABEL : Theme.FONT_TABLE);
        val.setForeground(Theme.TEXT_DARK);
        p.add(val, gbc);
    }
}