package ir.maktabsharif.files.sample;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class RealNio {
    static void main() {
        doIoForText();
        doIoForObj();
    }

    private static void doIoForText() {
        String textA = "this is content for file A.txt";
        Path path = Path.of("A.txt");

        try (var channel = FileChannel.open(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            var buffer = ByteBuffer.wrap(textA.getBytes(StandardCharsets.UTF_8));
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
            var buffer = ByteBuffer.allocate((int) channel.size());
            while (buffer.hasRemaining()) {
                if (channel.read(buffer) == -1) break;
            }
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            IO.println(new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    record Student(int id, String name) {
    }

    private static void doIoForObj() {
        var studentA = new Student(1, "ali");
        Path path = Path.of("A.stu");

        try (var channel = FileChannel.open(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            var nameBytes = studentA.name().getBytes(StandardCharsets.UTF_8);
            var buffer = ByteBuffer.allocate(4 + nameBytes.length);
            buffer.putInt(studentA.id());
            buffer.put(nameBytes);
            buffer.flip();
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
            var buffer = ByteBuffer.allocate((int) channel.size());
            while (buffer.hasRemaining()) {
                if (channel.read(buffer) == -1) break;
            }
            buffer.flip();
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