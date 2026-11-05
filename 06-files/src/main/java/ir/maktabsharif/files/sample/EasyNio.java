
package ir.maktabsharif.files.sample;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class EasyNio {
    static void main() {
        doIoForText();
        doIoForObj();
    }

    private static void doIoForText() {
        String textA = "this is content for file A.txt";
        Path path = Path.of("A.txt");

        try {
            Files.writeString(path, textA, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            IO.println(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    record Student(int id, String name) {
    }

    private static void doIoForObj() {
        var studentA = new Student(1, "ali");
        Path path = Path.of("A.stu");

        try {
            var nameBytes = studentA.name().getBytes(StandardCharsets.UTF_8);
            var buffer = ByteBuffer.allocate(4 + nameBytes.length);
            buffer.putInt(studentA.id());
            buffer.put(nameBytes);
            Files.write(path, buffer.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            byte[] bytes = Files.readAllBytes(path);
            var buffer = ByteBuffer.wrap(bytes);
            int id = buffer.getInt();
            byte[] nameBytes = new byte[buffer.remaining()];
            buffer.get(nameBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);
            IO.println(new Student(id, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
