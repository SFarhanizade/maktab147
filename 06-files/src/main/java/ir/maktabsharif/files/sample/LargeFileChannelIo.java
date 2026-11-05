package ir.maktabsharif.files.sample;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LargeFileChannelIo {

    private static final int CHUNK_SIZE = 64 * 1024;

    static void main() throws IOException {
        Path path = Path.of("big-file");

        writeLargeFile(path, 3L * 1024 * 1024 * 1024);
        long total = readLargeFile(path);
        IO.println("Read " + total + " bytes");
    }

    private static void writeLargeFile(Path path, long targetBytes) throws IOException {
        byte[] line = "some repeated line of content\n".getBytes(StandardCharsets.UTF_8);

        try (var channel = FileChannel.open(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            var buffer = ByteBuffer.allocate(CHUNK_SIZE);
            long written = 0;
            int offset = 0;

            while (written < targetBytes) {
                while (buffer.hasRemaining() && written + buffer.position() < targetBytes) {
                    int toCopy = Math.min(buffer.remaining(), line.length - offset);
                    buffer.put(line, offset, toCopy);
                    offset += toCopy;
                    if (offset == line.length) offset = 0;
                }

                buffer.flip();
                while (buffer.hasRemaining()) {
                    written += channel.write(buffer);
                }
                buffer.clear();
            }
        }
    }

    private static long readLargeFile(Path path) throws IOException {
        try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
            long size = channel.size();
            IO.println("File size: " + size + " bytes (" + (size / (1024 * 1024 * 1024)) + " GB)");

            var buffer = ByteBuffer.allocate(CHUNK_SIZE);
            long total = 0;
            int reads = 0;

            while (true) {
                int n = channel.read(buffer);
                if (n == -1) break;
                total += n;
                reads++;

                buffer.flip();
                buffer.clear();
            }

            IO.println("Read in " + reads + " chunks, total = " + total);
            return total;
        }
    }
}