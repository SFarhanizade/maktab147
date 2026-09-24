package ir.maktabsharif.exception;

public class Finally {
    void main() throws Exception {
        try (var database = new Database();
             var database2 = new Database()) {
            database.disconnect();
            var result = database.query("select * from X");
            IO.println("result: " + result);
        } catch (Exception e) {
            IO.println("error while closing the connection");
            throw new RuntimeException(e);
        }
    }


}

class Database implements AutoCloseable {
    private boolean isConnected;

    public Database() {
        isConnected = true;
    }

    public Object query(String sql) {
        if (!isConnected)
            throw new IllegalStateException("database is not connected");
        return new Object();
    }

    public void disconnect() {
        IO.println("disconnect");
        isConnected = false;
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }
}
