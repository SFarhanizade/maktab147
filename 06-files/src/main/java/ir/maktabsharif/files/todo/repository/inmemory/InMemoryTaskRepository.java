package ir.maktabsharif.files.todo.repository.inmemory;

import ir.maktabsharif.files.todo.entity.Task;
import ir.maktabsharif.files.todo.entity.TaskStatus;
import ir.maktabsharif.files.todo.repository.TaskRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryTaskRepository implements TaskRepository {
    private final Map<Integer, Task> store = new LinkedHashMap<>();
    private int seq = 1;

    @Override
    public void save(Task task) {
        task.setId(seq++);
        store.put(task.getId(), copy(task));
    }

    @Override
    public void update(Task task) {
        store.put(task.getId(), copy(task));
    }

    @Override
    public void delete(int id) {
        store.remove(id);
    }

    @Override
    public Optional<Task> findById(int id) {
        return Optional.ofNullable(store.get(id)).map(this::copy);
    }

    @Override
    public List<Task> findAll() {
        return store.values().stream().map(this::copy).collect(Collectors.toList());
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return findAll().stream().filter(t -> t.getStatus() == status).collect(Collectors.toList());
    }

    @Override
    public List<Task> findByCategoryId(int categoryId) {
        return findAll().stream().filter(t -> t.getCategoryId() == categoryId).collect(Collectors.toList());
    }

    private Task copy(Task t) {
        return new Task(t.getId(), t.getTitle(), t.getDescription(), t.getDueDate(),
                t.getDoneAt(), t.getStatus(), t.getCategoryId(), t.getCreatedAt());
    }
}