package ir.maktabsharif.files.todo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private LocalDateTime doneAt;
    private TaskStatus status;
    private int categoryId;
    private LocalDateTime createdAt;

    public Task(String title, String description, LocalDateTime dueDate, TaskStatus status, int categoryId) {
        this(0, title, description, dueDate, null, status, categoryId, LocalDateTime.now());
    }

    public Task(String title, String description, LocalDateTime dueDate, TaskStatus status) {
        this(title, description, dueDate, status, 0);
    }
}