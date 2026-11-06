package ir.maktabsharif.files.todo;

import ir.maktabsharif.files.todo.repository.CategoryRepository;
import ir.maktabsharif.files.todo.repository.TaskRepository;
import ir.maktabsharif.files.todo.repository.textfile.TextFileCategoryRepository;
import ir.maktabsharif.files.todo.repository.textfile.TextFileTaskRepository;
import ir.maktabsharif.files.todo.service.CategoryService;
import ir.maktabsharif.files.todo.service.TaskService;
import lombok.Getter;

public class ApplicationContext {
    private ApplicationContext() {
    }

    private CategoryRepository categoryRepo;
    private TaskRepository taskRepo;

    private TaskService taskService;
    private CategoryService categoryService;

    private static final ApplicationContext CTX = new ApplicationContext();

    public static ApplicationContext getInstance(){
        return CTX;
    }


    public CategoryRepository getCategoryRepo() {
        if (categoryRepo == null) {
            categoryRepo = new TextFileCategoryRepository(ApplicationProperties.baseDirectory());
        }
        return categoryRepo;
    }

    public TaskRepository getTaskRepo() {
        if (taskRepo == null) {
            taskRepo = new TextFileTaskRepository(ApplicationProperties.baseDirectory());
        }
        return taskRepo;
    }

    public TaskService getTaskService() {
        if (taskService == null) {
            taskService = new TaskService(getTaskRepo(), getCategoryRepo());
        }

        return taskService;
    }

    public CategoryService getCategoryService() {
        if (categoryService == null) {
            categoryService = new CategoryService(getCategoryRepo(), getTaskRepo());
        }

        return categoryService;
    }
}
