package ir.maktabsharif.files.todo.repository.textfile;

import ir.maktabsharif.files.todo.entity.Category;
import ir.maktabsharif.files.todo.repository.CategoryRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class TextFileCategoryRepository implements CategoryRepository {
    private final Path dir;
    private final Path index;

    public TextFileCategoryRepository(String baseDirectory) {
        dir = Path.of(baseDirectory);
        index = dir.resolve("index");
        try {
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
            }

            if (!Files.exists(index)) {
                Files.writeString(index, "1");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByName(String name) {
        try {
            return Files.lines(index)
                    .map(toName())
                    .anyMatch(foundName -> foundName.equals(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Function<String, String> toName() {
        return line -> {
            var parts = line.split("-", 2);
            return parts.length > 1 ? parts[1] : "";
        };
    }

    @Override
    public void save(Category c) {
        try {
            var lines = Files.readAllLines(index);
            var last = lines.getLast();
            var nextId = Integer.parseInt(last);
            c.setId(nextId);
            Files.createDirectory(dir.resolve(String.valueOf(nextId)));
            lines.set(lines.size() - 1, nextId + "-" + c.getName());
            lines.add(String.valueOf(nextId + 1));
            Files.write(index, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Category c) {
        try {
            var lines = Files.readAllLines(index);
            for (int i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                if (line.startsWith(c.getId() + "-")) {
                    lines.set(i, c.getId() + "-" + c.getName());
                    break;
                }
            }
            Files.write(index, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        try {
            var lines = Files.readAllLines(index);
            var prefix = id + "-";
            lines = lines.stream()
                    .filter(line -> !line.startsWith(prefix))
                    .toList();

            Files.deleteIfExists(dir.resolve(String.valueOf(id)));
            Files.write(index, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Category> findById(int id) {
        try {
            var prefix = id + "-";
            return Files.lines(index)
                    .filter(line -> line.startsWith(prefix))
                    .findFirst()
                    .map(toCategory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(int id) {
        try {
            return Files.lines(index)
                    .mapToInt(toId())
                    .anyMatch(foundId -> foundId == id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ToIntFunction<String> toId() {
        return line -> {
            var parts = line.split("-", 2);
            return parts.length > 1 ?
                    Integer.parseInt(parts[0]) : -1;
        };
    }

    private static Function<String, Category> toCategory() {
        return line -> toCategory(line.split("-", 2));
    }

    @Override
    public List<Category> findAll() {
        try {
            return Files.lines(index)
                    .filter(line -> line.contains("-"))
                    .map(toCategory())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Category> findByName(String name) {
        try {
            return Files.lines(index)
                    .map(line -> line.split("-", 2))
                    .filter(parts -> parts.length > 1 && parts[1].equals(name))
                    .findFirst()
                    .map(TextFileCategoryRepository::toCategory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Category toCategory(String[] parts) {
        return new Category(Integer.parseInt(parts[0]), parts[1]);
    }
}