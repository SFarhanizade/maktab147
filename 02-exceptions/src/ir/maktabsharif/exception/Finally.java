package ir.maktabsharif.exception;

public class Finally {
    void main() {
        var database = new Database();
        try {
            var result = database.query("select * from X");
            IO.println("result: " + result);
        } finally {
            database.disconnect();
        }
    }


}

class Database {
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
        isConnected = false;
    }
}
