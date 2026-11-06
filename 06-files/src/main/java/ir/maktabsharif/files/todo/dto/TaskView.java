package ir.maktabsharif.files.todo.dto;

import ir.maktabsharif.files.todo.entity.TaskStatus;
import java.time.LocalDateTime;

public record TaskView(
        int id,
        String title,
        LocalDateTime dueDate,
        TaskStatus status,
        int categoryId,
        String categoryName
) {
}