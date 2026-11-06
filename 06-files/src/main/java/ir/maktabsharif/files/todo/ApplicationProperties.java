package ir.maktabsharif.files.todo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class ApplicationProperties {
    public static String baseDirectory() {
        var key = "app.basedir";
        var baseDir = System.getenv(key);
        if (baseDir != null)
            return baseDir;

        var resource = ApplicationProperties.class.getClassLoader().getResource("config.properties");
        try {
            var config = Files.lines(Path.of(resource.getPath()))
                    .map(line -> line.split("=", 2))
                    .collect(Collectors.toMap(
                            parts -> parts[0],
                            parts -> parts[1]
                    ));
            baseDir = config.get(key);
            if (baseDir == null)
                throw new IllegalStateException("property {%s} not found".formatted(key));

            return baseDir;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
