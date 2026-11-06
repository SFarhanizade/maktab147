package ir.maktabsharif.files.todo;

import ir.maktabsharif.files.todo.service.CategoryService;
import ir.maktabsharif.files.todo.service.TaskService;
import ir.maktabsharif.files.todo.ui.MainFrame;
import ir.maktabsharif.files.todo.ui.Theme;

import javax.swing.*;

public class Main {

    void main() {
        var context = ApplicationContext.getInstance();

        TaskService taskService = context.getTaskService();
        CategoryService categorySvc = context.getCategoryService();

        SwingUtilities.invokeLater(() -> {
            Theme.install();

            if (categorySvc.findAll().isEmpty()) {
                categorySvc.save("Work");
                categorySvc.save("Personal");
            }

            new MainFrame(taskService, categorySvc).setVisible(true);
        });
    }
}