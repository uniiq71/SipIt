import java.util.ArrayList;
import java.util.List;

public class Game {
    private int gameCode;
    private List<Connection> users = new ArrayList<Connection>();

    public Game(int gameCode, Connection user) {
        this.gameCode = gameCode;
        users.add(user);
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
