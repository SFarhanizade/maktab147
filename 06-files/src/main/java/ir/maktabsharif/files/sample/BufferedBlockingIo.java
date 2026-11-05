package ir.maktabsharif.files.sample;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BufferedBlockingIo {
    static void main() {
//        doIoForText();
        doIoForObj();
    }

    private static void doIoForText() {
        String textA = "this is content for file A.txt";
        try (var os = new BufferedOutputStream(new FileOutputStream("A.txt"))) {
            var bytes = textA.getBytes(StandardCharsets.UTF_8);
            for (var i = 0; i < bytes.length; i += 4) {
                os.write(bytes, i, Math.min(4, bytes.length - i));
            }
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        try (var is = new BufferedInputStream(new FileInputStream("A.txt"))) {
            byte[] buf = new byte[4];
            var out = new ByteArrayOutputStream();
            int n;
            while ((n = is.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            IO.println(out.toString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    record Student(int id, String name) {
    }

    private static void doIoForObj() {
        var studentA = new Student(1, "ali");
        try (var os = new BufferedOutputStream(new FileOutputStream("A.stu"))) {
            var nameBytes = studentA.name().getBytes(StandardCharsets.UTF_8);
            var bytes = new byte[4 + nameBytes.length];
            bytes[0] = (byte) (studentA.id() >> 24);
            bytes[1] = (byte) (studentA.id() >> 16);
            bytes[2] = (byte) (studentA.id() >> 8);
            bytes[3] = (byte) studentA.id();
            System.arraycopy(nameBytes, 0, bytes, 4, nameBytes.length);

            for (var i = 0; i < bytes.length; i += 4) {
                os.write(bytes, i, Math.min(4, bytes.length - i));
            }
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (var is = new BufferedInputStream(new FileInputStream("A.stu"))) {
            byte[] buf = new byte[4];
            var out = new ByteArrayOutputStream();
            int n;
            while ((n = is.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            byte[] bytes = out.toByteArray();

            int id = ((bytes[0] & 0xFF) << 24) |
                     ((bytes[1] & 0xFF) << 16) |
                     ((bytes[2] & 0xFF) << 8) |
                     ((bytes[3] & 0xFF));
            String name = new String(bytes, 4, bytes.length - 4, StandardCharsets.UTF_8);
            IO.println(new Student(id, name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
