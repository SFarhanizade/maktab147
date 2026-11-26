import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;

public class ServerIoApp {

    private static final int PORT = 8080;
    private static final int BUFFER_SIZE = 8192;

    void main() throws IOException {
        try (var socket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                var clientSocket = socket.accept();
                System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());

                try (clientSocket) {
                    var inputStream = clientSocket.getInputStream();
                    var outputStream = clientSocket.getOutputStream();
                    var buffer = new byte[BUFFER_SIZE];
                    var request = new StringBuilder();


                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        request.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));

                        String req = request.toString();
                        if (req.contains("\r\n\r\n") || req.contains("\n\n")) {
                            break;
                        }
                    }

                    if (request.length() > 0) {
                        String responseBody = buildResponse(request.toString());
                        byte[] bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);

                        String headers = "HTTP/1.1 200 OK\r\n" +
                                         "Content-Type: text/html; charset=UTF-8\r\n" +
//                                         "Content-Type: application/octet-stream\r\n" +
                                         "Content-Length: " + bodyBytes.length + "\r\n" +
                                         "Connection: close\r\n" +
                                         "X: test\r\n" +
                                         "\r\n";

                        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
                        outputStream.write(bodyBytes);
                        outputStream.flush();
                    }
                } catch (IOException e) {
                    System.err.println("Connection error: " + e.getMessage());
                }

                System.out.println("Connection closed");
            }
        }
    }

    private static String buildResponse(String request) {
        String firstLine = request.lines().findFirst().orElse("");
        return """
                <html>
                <body>
                <p>سلام بر شما</p>
                <p>لطفا نام خود را وارد کنید.</p>
                <input type='text'/>
                </body>
                </html>""";
    }
}