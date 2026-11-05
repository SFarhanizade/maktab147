package ir.maktabsharif.files.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BlockingIo {
    static void main() {
//        doIoForText();
        doIoForObj();
    }

    private static void doIoForText() {
        String textA = "this is content for file A.txt";
        try (var fileOutputStream = new FileOutputStream("A.txt")) {
            byte[] bytes = textA.getBytes(StandardCharsets.UTF_8);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (var in = new FileInputStream("A.txt")) {
            byte[] bytes = in.readAllBytes();
            IO.println(new String(bytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    record Student(int id, String name) {
    }

    private static void doIoForObj() {
        var studentA = new Student(1, "ali");
        try (var fileOutputStream = new FileOutputStream("A.stu")) {
            var nameBytes = studentA.name().getBytes(StandardCharsets.UTF_8);
            var bytes = new byte[4 + nameBytes.length];
            bytes[0] = (byte) (studentA.id() >> 24);
            bytes[1] = (byte) (studentA.id() >> 16);
            bytes[2] = (byte) (studentA.id() >> 8);
            bytes[3] = (byte) studentA.id();
            System.arraycopy(nameBytes, 0, bytes, 4, nameBytes.length);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (var in = new FileInputStream("A.stu")) {
            byte[] bytes = in.readAllBytes();
            int id = ((bytes[0] & 0xFF) << 24) |
                     ((bytes[1] & 0xFF) << 16) |
                     ((bytes[2] & 0xFF) << 8) |
                     ((bytes[3] & 0xFF));
            String name = new String(Arrays.copyOfRange(bytes, 4, bytes.length), StandardCharsets.UTF_8);
            IO.println(new Student(id, name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
