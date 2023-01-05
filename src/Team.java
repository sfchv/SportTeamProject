import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Team {

    private static final Map<String, Team> teams = new HashMap<>();
    private final String name;
    private List<Player> players = new ArrayList<>();

    public Team(String name) {
        this.name = name;
        teams.put(name, this);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Player[] getPlayers() {
        Player[] array = new Player[players.size()];
        players.toArray(array);
        return array;
    }

    public String getName() {
        return name;
    }

    public static Team getTeamByName(String name) {
        if (teams.containsKey(name)) {
            return teams.get(name);
        }
        return null;
    }
}
