package ir.maktabsharif.files.todo.repository.inmemory;

import ir.maktabsharif.files.todo.entity.Category;
import ir.maktabsharif.files.todo.repository.CategoryRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryCategoryRepository implements CategoryRepository {
    private final Map<Integer, Category> store = new LinkedHashMap<>();
    private int seq = 1;

    @Override
    public void save(Category c) {
        c.setId(seq++);
        store.put(c.getId(), copy(c));
    }

    @Override
    public void update(Category c) {
        store.put(c.getId(), copy(c));
    }

    @Override
    public void delete(int id) {
        store.remove(id);
    }

    @Override
    public Optional<Category> findById(int id) {
        return Optional.ofNullable(store.get(id)).map(this::copy);
    }

    @Override
    public List<Category> findAll() {
        return store.values().stream().map(this::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findByName(String name) {
        return store.values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst().map(this::copy);
    }

    private Category copy(Category c) {
        return new Category(c.getId(), c.getName());
    }
}