package ir.maktabsharif.files.todo.repository.textfile;

import ir.maktabsharif.files.todo.entity.Task;
import ir.maktabsharif.files.todo.entity.TaskStatus;
import ir.maktabsharif.files.todo.repository.TaskRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

public class TextFileTaskRepository implements TaskRepository {

    private final Path baseDir;
    private final Path tasksInfo;

    public TextFileTaskRepository(String baseDirectory) {
        baseDir = Path.of(baseDirectory);
        tasksInfo = baseDir.resolve("task-index");
        try {
            Files.createDirectories(baseDir);
            if (!Files.exists(tasksInfo)) {
                Files.writeString(tasksInfo, "1");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Task task) {
        try {
            Path categoryDir = baseDir.resolve(String.valueOf(task.getCategoryId()));

            var lines = Files.readAllLines(tasksInfo);
            var nextId = Integer.parseInt(lines.getLast());
            task.setId(nextId);

            Path taskFile = categoryDir.resolve(String.valueOf(nextId));
            writeTaskData(taskFile, task);

            lines.set(lines.size() - 1, nextId + "-" + task.getCategoryId());
            lines.add(String.valueOf(nextId + 1));
            Files.write(tasksInfo, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Task task) {
        try {
            Path categoryDir = baseDir.resolve(String.valueOf(task.getCategoryId()));
            Path taskFile = categoryDir.resolve(String.valueOf(task.getId()));

            if (!Files.exists(taskFile)) {
                var currentTask = findById(task.getId()).orElseThrow();

                Path oldPath = baseDir.resolve(String.valueOf(currentTask.getCategoryId()))
                        .resolve(String.valueOf(currentTask.getId()));

                Files.move(oldPath, taskFile);

                var currentLine = currentTask.getId() + "-" + currentTask.getCategoryId();
                var lines = Files.readAllLines(tasksInfo);
                for (var i = 0; i < lines.size(); i++) {
                    var line = lines.get(i);
                    if (line.equals(currentLine)) {
                        lines.set(i, task.getId() + "-" + task.getCategoryId());
                        break;
                    }
                }

                Files.write(tasksInfo, lines);

            } else writeTaskData(taskFile, task);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        try {
            var lines = Files.readAllLines(tasksInfo);
            var prefix = id + "-";

            Integer categoryId = null;
            var iterator = lines.iterator();
            while (iterator.hasNext()) {
                var line = iterator.next();
                if (line.startsWith(prefix)) {
                    categoryId = Integer.parseInt(line.split("-", 2)[1]);
                    iterator.remove();
                    break;
                }
            }

            if (categoryId == null)
                throw new IOException("task[%d] not found".formatted(id));

            Files.deleteIfExists(baseDir.resolve(String.valueOf(categoryId)).resolve(String.valueOf(id)));
            Files.write(tasksInfo, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByCategoryId(int id) {
        try {
            return Files.lines(tasksInfo)
                    .mapToInt(toCategoryId())
                    .anyMatch(foundId -> foundId == id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(int id) {
        try {
            return Files.lines(tasksInfo)
                    .mapToInt(line -> {
                        var parts = line.split("-");
                        return parts.length > 1 ? Integer.parseInt(parts[0]) : -1;
                    })
                    .anyMatch(foundId -> foundId == id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ToIntFunction<String> toCategoryId() {
        return line -> {
            var parts = line.split("-", 2);
            return parts.length > 1 ?
                    Integer.parseInt(parts[1]) : -1;
        };
    }

    @Override
    public Optional<Task> findById(int id) {
        try {
            var prefix = id + "-";
            var categoryId = Files.lines(tasksInfo)
                    .filter(line -> line.startsWith(prefix))
                    .findFirst()
                    .map(line -> line.split("-", 2)[1])
                    .orElseThrow();

            var taskPath = baseDir.resolve(categoryId).resolve(String.valueOf(id));

            return Optional.of(readTaskData(taskPath));

        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Task> findAll() {
        try {
            return listCategoryDirs().stream()
                    .flatMap(dir -> {
                        try {
                            return Files.list(dir)
                                    .filter(path -> !path.toFile().getName().equals("nextId"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(this::readTaskData)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return findAll().stream().filter(t -> t.getStatus() == status).toList();
    }

    @Override
    public List<Task> findByCategoryId(int categoryId) {
        try {
            Path categoryDir = baseDir.resolve(String.valueOf(categoryId));
            if (!Files.exists(categoryDir)) return List.of();

            return Files.list(categoryDir)
//                    .filter(path -> !path.toFile().getName().equals("nextId"))
                    .map(this::readTaskData)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Path> listCategoryDirs() throws IOException {
        try (Stream<Path> s = Files.list(baseDir)) {
            return s.filter(Files::isDirectory)
                    .sorted(Comparator.comparing(p -> Integer.parseInt(p.getFileName().toString())))
                    .toList();
        }
    }

    private static String sanitize(String title) {
        return title == null ? "" : title.replace("\\", "\\\\")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private static String normalize(String title) {
        return title == null ? "" : title.replace("\\\\", "\\")
                .replace("\\r", "\r")
                .replace("\\n", "\n")
                .replace("\\t", "\t");
    }

    private void writeTaskData(Path taskDir, Task t) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("id=" + nullSafe(t.getId()));
        lines.add("title=" + nullSafe(t.getTitle()));
        lines.add("description=" + nullSafe(sanitize(t.getDescription())));
        lines.add("dueDate=" + nullSafe(t.getDueDate()));
        lines.add("doneAt=" + nullSafe(t.getDoneAt()));
        lines.add("status=" + (t.getStatus() == null ? "" : t.getStatus().name()));
        lines.add("createdAt=" + nullSafe(t.getCreatedAt()));
        Files.write(taskDir, lines);
    }

    private Task readTaskData(Path taskPath) {
        var map = new HashMap<String, String>();
        try {
            for (String line : Files.readAllLines(taskPath)) {
                int eq = line.indexOf('=');
                if (eq > 0) map.put(line.substring(0, eq), line.substring(eq + 1));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Task t = new Task();
        t.setId(Integer.parseInt(map.getOrDefault("id", "0")));
        t.setCategoryId(Integer.parseInt(taskPath.getParent().toFile().getName()));
        t.setTitle(map.getOrDefault("title", ""));
        t.setDescription(normalize(map.getOrDefault("description", "")));
        t.setDueDate(parseDateTime(map.get("dueDate")));
        t.setDoneAt(parseDateTime(map.get("doneAt")));
        String status = map.getOrDefault("status", "");
        if (!status.isEmpty()) t.setStatus(TaskStatus.valueOf(status));
        t.setCreatedAt(parseDateTime(map.get("createdAt")));
        return t;
    }

    private static String nullSafe(Object o) {
        return o == null ? "" : o.toString();
    }

    private static LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank() || "null".equals(s)) return null;
        return LocalDateTime.parse(s);
    }
}