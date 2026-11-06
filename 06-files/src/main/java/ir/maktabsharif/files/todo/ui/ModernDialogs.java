package ir.maktabsharif.files.todo.ui;

import javax.swing.*;
import java.awt.*;

public final class ModernDialogs {
    private ModernDialogs() {
    }

    public static void info(Component parent, String title, String message) {
        show(parent, title, message, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warning(Component parent, String title, String message) {
        show(parent, title, message, JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String title, String message) {
        show(parent, title, message, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String title, String message) {
        int r = JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return r == JOptionPane.OK_OPTION;
    }

    private static void show(Component parent, String title, String message, int type) {
        JOptionPane.showMessageDialog(parent, message, title, type);
    }
}