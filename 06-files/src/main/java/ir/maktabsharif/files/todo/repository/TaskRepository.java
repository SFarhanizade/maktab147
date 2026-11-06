package ir.maktabsharif.files.todo.repository;

import ir.maktabsharif.files.todo.entity.Task;
import ir.maktabsharif.files.todo.entity.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    void save(Task task);

    void update(Task task);

    void delete(int id);

    Optional<Task> findById(int id);

    List<Task> findAll();

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByCategoryId(int categoryId);

    default boolean existsByCategoryId(int id){
        return false;
    }

    default boolean existsById(int id){
        return false;
    }
}