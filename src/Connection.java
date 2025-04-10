import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Runnable {
    private Server server;
    private Socket socket;
    private int id;
    private String userName;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public Connection(Socket socket,String userName,int id) {
        this.socket = socket;
        this.userName = userName;
        this.id = id;
    }

    public Connection(Socket socket,Server server) {
        this.socket = socket;
        this.server = server;

        try {
            socket.setSoTimeout(20000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String data = (String) inputStream.readObject();
                checkPacket(data);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Verbindung verloren zu: " + this);
        } finally {
            close();
            server.removeConnection(this);
        }
    }

    public void sendData(String data) {
        try {
            outputStream.writeObject(data);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkPacket(String data) {
        switch (data) {
            case "join":
                System.out.println(server.connectedUsers());
                sendData("test");
                break;
        }
    }

    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "socket=" + socket +
                ", id=" + id +
                ", userName='" + userName + '\'' +
                ", inputStream=" + inputStream +
                ", outputStream=" + outputStream +
                '}';
    }
}
