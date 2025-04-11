import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Server implements Runnable {
    private int port;
    private ServerSocket serverSocket;
    private boolean running = false;
    private List<Connection> connections = new CopyOnWriteArrayList<>();
    private List<Integer> gameCodes = new CopyOnWriteArrayList<>();
    private List<Game> games = new CopyOnWriteArrayList<>();

    public Server(int port) {
        this.port = port;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getGameCodes() {
        return gameCodes;
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
                    System.out.println(games);
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

    public void createGame(Connection connection) {
        Random rnd = new Random();
        int gameCode;

        do {
            gameCode = 100000 + rnd.nextInt(999999);
        } while (gameCodes.contains(gameCode));

        gameCodes.add(gameCode);
        games.add(new Game(gameCode,connection));
        connection.setGameCode(gameCode);
    }

    public void joinGame(Connection connection, int gameCode) {
        Game joinedGame = findGameByCode(gameCode);
        if (joinedGame != null) {
            joinedGame.addUser(connection);
            connection.setGameCode(gameCode);
        }
    }

    public Game findGameByCode(int gameCode) {
        for (Game game : games) {
            if (game.getGameCode() == gameCode) {
                return game;
            }
        }
        return null;
    }

    public void broadcastGame(Game game, String data) {
        game.getUsers().forEach(user -> user.sendData(data));
    }

    public void broadcastUsernames(int gameCode) {
        Game game = findGameByCode(gameCode);
        broadcastGame(game, game.connectedUsers());
    }

    public boolean checkIfUsernameIsValid(String userName, int gameCode) {
        Game game = findGameByCode(gameCode);
        return !game.connectedUserNames().contains(userName);
    }
}