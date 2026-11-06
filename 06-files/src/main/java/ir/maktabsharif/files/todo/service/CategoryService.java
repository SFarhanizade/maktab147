package ir.maktabsharif.files.todo.service;

import ir.maktabsharif.files.todo.dto.CategoryView;
import ir.maktabsharif.files.todo.entity.Category;
import ir.maktabsharif.files.todo.repository.CategoryRepository;
import ir.maktabsharif.files.todo.repository.TaskRepository;
import ir.maktabsharif.files.todo.service.exception.NotFoundException;
import ir.maktabsharif.files.todo.service.exception.ValidationException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private final TaskRepository taskRepository;

    public void save(String name) {
        validateName(name);
        if (repository.existsByName(name)) {
            throw new ValidationException("Category already exists: " + name);
        }
        repository.save(new Category(name));
    }

    public List<CategoryView> findAll() {
        return repository.findAll()
                .stream()
                .map(c -> new CategoryView(c.getId(), c.getName()))
                .toList();
    }

    public CategoryView findById(int id) {
        return repository.findById(id)
                .map(c -> new CategoryView(c.getId(), c.getName()))
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }

    public void update(int id, String newName) {
        validateName(newName);

        if (repository.existsByName(newName)) {
            throw new ValidationException("Category already exists: " + newName);
        }

        Category category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));

        category.setName(newName);
        repository.update(category);
    }

    public void delete(int id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Category not found: " + id);
        }

        if (taskRepository.existsByCategoryId(id)) {
            throw new ValidationException(
                    "Cannot delete category " + id + ": it still has tasks");
        }
        repository.delete(id);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Category name must not be blank");
        }
    }
}