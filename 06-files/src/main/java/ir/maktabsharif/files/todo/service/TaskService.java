package ir.maktabsharif.files.todo.service;

import ir.maktabsharif.files.todo.dto.TaskDetailView;
import ir.maktabsharif.files.todo.dto.TaskView;
import ir.maktabsharif.files.todo.entity.Category;
import ir.maktabsharif.files.todo.entity.Task;
import ir.maktabsharif.files.todo.entity.TaskStatus;
import ir.maktabsharif.files.todo.repository.CategoryRepository;
import ir.maktabsharif.files.todo.repository.TaskRepository;
import ir.maktabsharif.files.todo.service.exception.NotFoundException;
import ir.maktabsharif.files.todo.service.exception.ValidationException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final CategoryRepository categoryRepository;

    public void create(String title, String description, LocalDateTime dueDate, boolean isDraft, int categoryId) {
        validateTitle(title);
        validateDueDate(dueDate);
        validateCategory(categoryId);
        TaskStatus status = isDraft ? TaskStatus.DRAFT : TaskStatus.ACTIVE;
        if (status == TaskStatus.ACTIVE && dueDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Active tasks need a future due date. Save as draft to keep a past date.");
        }
        var task = new Task(title, description, dueDate, status, categoryId);
        repository.save(task);
    }

    private static void validateDueDate(LocalDateTime dueDate) {
        if (dueDate == null) throw new ValidationException("Due date must not be null");
    }

    public List<TaskView> findAll() {
        return repository.findAll().stream().map(this::toView).toList();
    }

    public TaskDetailView findById(int id) {
        return repository.findById(id)
                .map(task -> new TaskDetailView(
                        task.getId(),
                        task.getTitle(),
                        task.getDueDate(),
                        task.getStatus(),
                        task.getDescription(),
                        task.getCategoryId(),
                        categoryRepository.findById(task.getCategoryId())
                                .map(Category::getName)
                                .orElse("—")
                ))
                .orElseThrow(() -> new NotFoundException("Task not found: " + id));
    }

    public List<TaskView> findByStatus(TaskStatus status) {
        if (status == null) throw new ValidationException("Status must not be null");
        return repository.findByStatus(status).stream().map(this::toView).toList();
    }

    public List<TaskView> findByCategory(int categoryId) {
        validateCategory(categoryId);
        return repository.findByCategoryId(categoryId).stream().map(this::toView).toList();
    }

    public void update(int id, String title, String description, LocalDateTime dueDate, int categoryId, TaskStatus status) {
        validateTitle(title);
        validateDueDate(dueDate);
        validateCategory(categoryId);
        Task task = repository.findById(id).orElseThrow(() -> new NotFoundException("Task not found: " + id));
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setCategoryId(categoryId);
        task.setStatus(status);
        repository.update(task);
    }

    public void markDone(int id) {
        Task task = repository.findById(id).orElseThrow(() -> new NotFoundException("Task not found: " + id));
        task.setStatus(TaskStatus.DONE);
        task.setDoneAt(LocalDateTime.now());
        repository.update(task);
    }

    public void delete(int id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Task not found: " + id);
        }
        repository.delete(id);
    }

    private TaskView toView(Task task) {
        String categoryName = categoryRepository.findById(task.getCategoryId())
                .map(Category::getName)
                .orElse("—");
        return new TaskView(task.getId(), task.getTitle(), task.getDueDate(),
                task.getStatus(), task.getCategoryId(), categoryName);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) throw new ValidationException("Title must not be blank.");
    }

    private void validateCategory(int categoryId) {
        if (categoryId <= 0) throw new ValidationException("Please pick a category.");
        if (categoryRepository.findById(categoryId).isEmpty())
            throw new NotFoundException("Category not found: " + categoryId);
    }
}