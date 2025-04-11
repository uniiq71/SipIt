import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private int gameCode;
    private List<Connection> users = new ArrayList<Connection>();

    public Game(int gameCode, Connection user) {
        this.gameCode = gameCode;
        users.add(user);
    }

    public int getGameCode() {
        return gameCode;
    }

    public List<Connection> getUsers() {
        return users;
    }

    public String connectedUsers() {
        return users.stream().map(Connection::getUserName).collect(Collectors.joining(";"));
    }

    public List<String> connectedUserNames() {
        return users.stream().map(Connection::getUserName).collect(Collectors.toList());
    }

    public void addUser(Connection user) {
        users.add(user);
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameCode=" + gameCode +
                ", users=" + users +
                '}';
    }
}
