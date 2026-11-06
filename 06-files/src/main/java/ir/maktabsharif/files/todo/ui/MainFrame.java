package ir.maktabsharif.files.todo.ui;

import ir.maktabsharif.files.todo.service.CategoryService;
import ir.maktabsharif.files.todo.service.TaskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends ModernFrame {
    private final JPanel content;
    private final List<JButton> navButtons = new ArrayList<>();

    public MainFrame(TaskService taskService, CategoryService categoryService) {
        super("Maktab Todo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);

        var gradient = createGradient();

        RoundedPanel whiteCard = new RoundedPanel(24, Theme.CARD_BG, false);
        whiteCard.setLayout(new BorderLayout());
        whiteCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        content = new JPanel(new CardLayout());
        content.setOpaque(false);
        TaskPanel taskPanel = new TaskPanel(taskService, categoryService);
        CategoryPanel categoryPanel = new CategoryPanel(categoryService, taskPanel::reload);
        content.add(taskPanel, "tasks");
        content.add(categoryPanel, "categories");

        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JLabel brand = new JLabel("TODO");
        brand.setFont(Theme.FONT_BRAND);
        brand.setForeground(Theme.PRIMARY);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(40));

        JButton tasksBtn = navButton("Tasks", "tasks");
        JButton catsBtn = navButton("Categories", "categories");
        navButtons.add(tasksBtn);
        navButtons.add(catsBtn);
        sidebar.add(tasksBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(catsBtn);

        selectNav(tasksBtn);
        whiteCard.add(sidebar, BorderLayout.WEST);
        whiteCard.add(content, BorderLayout.CENTER);
        gradient.add(whiteCard, BorderLayout.CENTER);

        JPanel shell = new JPanel(new BorderLayout());
        shell.setOpaque(false);
        shell.add(buildTitleBar(), BorderLayout.NORTH);
        shell.add(gradient, BorderLayout.CENTER);
        setContentPane(roundedShell(shell));
    }

    private static JPanel createGradient() {
        JPanel gradient = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, Theme.BG_TOP, 0, getHeight(), Theme.BG_BOTTOM));
                g2.fillRect(0, 0, getWidth(), getHeight());
                RadialGradientPaint glow = new RadialGradientPaint(
                        new Point(getWidth() - 120, 100), 420f,
                        new float[]{0f, 1f},
                        new Color[]{Theme.GLOW, new Color(255, 255, 255, 0)});
                g2.setPaint(glow);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        gradient.setOpaque(false);
        gradient.setBorder(new EmptyBorder(30, 30, 30, 30));
        return gradient;
    }

    private JButton navButton(String text, String card) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (Boolean.TRUE.equals(getClientProperty("selected"))) {
                    g2.setColor(Theme.PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                } else if (getModel().isRollover()) {
                    g2.setColor(Theme.SURFACE_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.setPreferredSize(new Dimension(160, 46));
        b.setFont(Theme.FONT_LABEL);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.addActionListener(e -> {
            ((CardLayout) content.getLayout()).show(content, card);
            selectNav(b);
        });
        return b;
    }

    private void selectNav(JButton selected) {
        for (JButton btn : navButtons) {
            boolean isSel = btn == selected;
            btn.putClientProperty("selected", isSel);
            btn.setForeground(isSel ? Color.WHITE : Theme.TEXT_MUTED);
            btn.repaint();
        }
    }
}