package ir.maktabsharif.bank;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApplicationProperties {
    private final Map<String, String> properties;

    public ApplicationProperties() {
        this("config.properties");
    }

    public ApplicationProperties(String filename) {
        var resource = ApplicationProperties.class.getClassLoader().getResource(filename);
        try {
            properties = Files.lines(Path.of(resource.getPath()))
                    .filter(line -> !line.isBlank())
                    .map(line -> line.split("=", 2))
                    .collect(Collectors.toMap(
                            parts -> parts[0],
                            parts -> parts[1]
                    ));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String get(String key) {
        return Optional.ofNullable(System.getenv(key))
                .or(() -> Optional.ofNullable(properties.get(key)))
                .orElseThrow();
    }

    public Map<String, String> getProperties(String keyPrefix) {
        return properties.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(keyPrefix))
                .collect(Collectors.toMap(
                        entry -> entry.getKey().replace(keyPrefix + ".", ""),
                        Map.Entry::getValue
                ));
    }
}
