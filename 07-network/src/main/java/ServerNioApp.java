import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class ServerNioApp {

    private static final int PORT = 8080;
    private static final int BUFFER_SIZE = 8192;

    private final Selector selector;
    private final ServerSocketChannel serverChannel;

    public ServerNioApp(int port) throws IOException {
        this.selector = Selector.open();
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.bind(new InetSocketAddress(port));
        this.serverChannel.configureBlocking(false);
        this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() throws IOException {
        System.out.println("Server started on port " + PORT);
        process();
    }

    private void process() throws IOException {
        while (true) {
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                try {
                    if (!key.isValid())
                        continue;

                    if (key.isAcceptable()) handleAccept(key);
                    else if (key.isReadable()) handleRead(key);
                    else if (key.isWritable()) handleWrite(key);

                } catch (IOException e) {
                    System.err.println("Connection error: " + e.getMessage());
                    closeChannel(key);
                }
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();

        if (client != null) {
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ, new ClientState());
            System.out.println("Accepted connection from " + client.getRemoteAddress());
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        int bytesRead = client.read(buffer);

        if (bytesRead == -1) {
            System.out.println("Client disconnected: " + client.getRemoteAddress());
            closeChannel(key);
            return;
        }

        if (bytesRead > 0) {
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            state.request.append(new String(data, StandardCharsets.UTF_8));

            String request = state.request.toString();
            if (request.contains("\r\n\r\n") || request.contains("\n\n")) {
                processRequest(client, state, request);
            } else {
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private void processRequest(SocketChannel client, ClientState state, String request) {
        String responseBody = buildResponse(request);
        String response = "HTTP/1.1 200 OK\r\n" +
                          "Content-Type: text/plain; charset=UTF-8\r\n" +
                          "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                          "Connection: close\r\n" +
                          "\r\n" +
                          responseBody;

        state.response = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
        state.request.setLength(0);

        try {
            SelectionKey key = client.keyFor(selector);
            key.interestOps(SelectionKey.OP_WRITE);
        } catch (Exception e) {
            System.err.println("Failed to switch to write: " + e.getMessage());
        }
    }

    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();

        if (state.response == null) {
            key.interestOps(SelectionKey.OP_READ);
            return;
        }

        client.write(state.response);

        if (!state.response.hasRemaining()) {
            System.out.println("Response sent to " + client.getRemoteAddress());
            closeChannel(key);
        }
    }

    private String buildResponse(String request) {
        String firstLine = request.lines().findFirst().orElse("");
        return "NIO Server received:\n" +
               firstLine + "\n\n" +
               "Request length: " + request.length() + " bytes\n" +
               "Timestamp: " + System.currentTimeMillis() + "\n";
    }

    private void closeChannel(SelectionKey key) {
        try {
            key.cancel();
            key.channel().close();
        } catch (IOException ignored) {
        }
    }

    private static class ClientState {
        final StringBuilder request = new StringBuilder();
        ByteBuffer response;
    }

    public static void main(String[] args) throws IOException {
        new ServerNioApp(PORT).start();
    }
}