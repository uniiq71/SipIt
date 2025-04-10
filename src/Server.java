import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Server implements Runnable {
    private int port;
    private ServerSocket serverSocket;
    private boolean running = false;
    public List<Connection> connections = new CopyOnWriteArrayList<>();

    public Server(int port) {
        this.port = port;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        startConnectionMonitor();
        new Thread(this).start();
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                initializeSocket(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        shutdown();
    }

    private void initializeSocket(Socket socket) {
        Connection connection = new Connection(socket, this);
        connections.add(connection);
        new Thread(connection).start();
    }

    public void startConnectionMonitor() {
        new Thread(() -> {
            while (true) {
                if (!connections.isEmpty()) {
                    System.out.println("Aktive Verbindungen: " + connections.size());
                    for (Connection conn : connections) {
                        System.out.println(" - " + conn);
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String connectedUsers() {
        return connections.stream().map(Connection::getUserName).collect(Collectors.joining(";"));
    }

    public void shutdown() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeConnection(Connection connection) {
        connections.remove(connection);
    }
}