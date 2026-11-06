package ir.maktabsharif.files.todo.dto;

import ir.maktabsharif.files.todo.entity.TaskStatus;
import java.time.LocalDateTime;

public record TaskDetailView(
        int id,
        String title,
        LocalDateTime dueDate,
        TaskStatus status,
        String description,
        int categoryId,
        String categoryName
) {
}