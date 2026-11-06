package ir.maktabsharif.files.todo.ui;

import ir.maktabsharif.files.todo.dto.CategoryView;
import ir.maktabsharif.files.todo.service.CategoryService;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CategoryDialog extends JDialog {
    private final CategoryService service;
    private final CategoryView existing;
    private final JTextField nameField = new JTextField(20);
    @Getter
    private boolean saved = false;
    private Point initialClick;

    public CategoryDialog(Window owner, CategoryService service, CategoryView existing) {
        super(owner, ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.existing = existing;
        setUndecorated(true);
        setBackground(Theme.OUTER_FILL);
        buildUi();
        if (existing != null) nameField.setText(existing.name());
        installEscapeToClose();
        pack();
        setLocationRelativeTo(owner);
        SwingUtilities.invokeLater(nameField::requestFocusInWindow);
    }

    private void installEscapeToClose() {
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void buildUi() {
        RoundedPanel main = new RoundedPanel(24, Color.WHITE, true, Theme.BORDER_SOFT);
        main.setLayout(new BorderLayout());

        var titleBar = createTitleBar();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel lbl = new JLabel("Name:");
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.TEXT_DARK);
        form.add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        nameField.setFont(Theme.FONT_TABLE);
        nameField.putClientProperty("JTextField.placeholderText", "e.g. Work");
        form.add(nameField, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        buttons.add(ModernButton.secondary("Cancel", this::dispose));
        JButton saveBtn = ModernButton.primary("Save", this::onSave);
        buttons.add(saveBtn);
        getRootPane().setDefaultButton(saveBtn);

        main.add(titleBar, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);
        main.add(buttons, BorderLayout.SOUTH);
        setContentPane(main);
    }

    private JPanel createTitleBar() {
        var titleBar = createBar();

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

    private JPanel createBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 18));
        JLabel title = new JLabel(existing == null ? "New Category" : "Edit Category");
        title.setFont(Theme.FONT_H2);
        title.setForeground(Theme.TEXT_DARK);
        JButton close = new JButton("✕");
        close.setContentAreaFilled(false);
        close.setBorderPainted(false);
        close.setFocusPainted(false);
        close.setForeground(Theme.DANGER);
        close.setFont(Theme.FONT_ICON_SM);
        close.setToolTipText("Close (Esc)");
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.addActionListener(e -> dispose());
        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(close, BorderLayout.EAST);
        return titleBar;
    }

    private void onSave() {
        try {
            String name = nameField.getText().trim();
            if (existing == null) service.save(name);
            else service.update(existing.id(), name);
            saved = true;
            dispose();
        } catch (RuntimeException ex) {
            ModernDialogs.error(this, "Error", ex.getMessage());
        }
    }
}