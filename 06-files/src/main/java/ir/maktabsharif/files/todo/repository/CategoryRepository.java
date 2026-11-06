package ir.maktabsharif.files.todo.repository;

import ir.maktabsharif.files.todo.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    void save(Category category);

    void update(Category category);

    void delete(int id);

    Optional<Category> findById(int id);

    List<Category> findAll();

    Optional<Category> findByName(String name);

    default boolean existsByName(String name){
        return false;
    }

    default boolean existsById(int id){
        return false;
    }
}