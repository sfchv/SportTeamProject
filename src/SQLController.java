import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.sqlite.SQLiteConfig;

public class SQLController {

    private final Connection conn;

    public SQLController() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);

        conn = DriverManager.getConnection("jdbc:sqlite:Teams.db", config.toProperties());

        if (conn.createStatement().executeQuery(
            "SELECT count(*) FROM sqlite_master WHERE type = 'table'"
        ).getInt(1) != 0) {
            // чистим базу, что не создавать дубликаты
            conn.createStatement().execute("DELETE FROM players");
            conn.createStatement().execute("DELETE FROM teams");
            conn.createStatement().execute("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'teams'");
            conn.createStatement().execute("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'players'");
        }

        addPlayerTable();
        addTeamTable();
    }

    public void addTeamTable() throws SQLException {
        String sqlCreateTeamsTable = "CREATE TABLE IF NOT EXISTS teams(\n"
            + "id integer primary key AUTOINCREMENT,\n"
            + "name text not null UNIQUE\n"
            + ");";
        conn.createStatement().execute(sqlCreateTeamsTable);
    }

    public void addPlayerTable() throws SQLException {
        String sqlCreatePlayersTable = "CREATE TABLE IF NOT EXISTS players (\n"
            + "id integer not null PRIMARY KEY AUTOINCREMENT,\n"
            + "name text not null,\n"
            + "position text not null,\n"
            + "height integer not null,\n"
            + "weight integer not null,\n"
            + "age real not null,\n"
            + "team_id integer,\n"
            + "FOREIGN KEY (team_id) REFERENCES teams(id)\n"
            + ");";

        conn.createStatement().execute(sqlCreatePlayersTable);
    }

    public void showTeamTableData() throws SQLException {
        String selectAll = "SELECT * FROM teams";
        printResultSet(conn.createStatement().executeQuery(selectAll));
    }

    public void showPlayersTableData() throws SQLException {
        String selectAll = "SELECT * FROM players";
        printResultSet(conn.createStatement().executeQuery(selectAll));
    }

    private void printResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            for (var i = 0; i < rs.getMetaData().getColumnCount(); i++) {
               System.out.print(rs.getString(i + 1) + " ");
            }
            System.out.println();
        }
    }

    public void addTeam(Team team) throws SQLException {
        Player[] players = team.getPlayers();
        if (!getTeam(team.getName()).next()) {
            String sqlInsertTeam = "INSERT INTO teams(name) VALUES(?)";

            PreparedStatement preparedStatement = conn.prepareStatement(sqlInsertTeam);
            preparedStatement.setString(1, team.getName());
            preparedStatement.executeUpdate();
        }

        for (Player player : players) {
            addPlayer(player, getTeam(team.getName()).getInt(1));
        }
    }

    public void addPlayer(Player player, int teamId) throws SQLException {
        String sqlInsertPlayer =
            "INSERT INTO players(name, position, height, weight, age, team_id)\n"
                + "VALUES (?,?,?,?,?,?);";

        PreparedStatement preparedStatement = conn.prepareStatement(sqlInsertPlayer);
        preparedStatement.setString(1, player.getName());
        preparedStatement.setString(2, player.getPosition());
        preparedStatement.setInt(3, player.getHeight());
        preparedStatement.setInt(4, player.getWeight());
        preparedStatement.setDouble(5, player.getAge());
        preparedStatement.setInt(6, teamId);
        preparedStatement.executeUpdate();
    }

    public ResultSet getTeam(String name) throws SQLException {
        String sqlGetTeam = "SELECT * FROM teams WHERE name = '" + name + "'";
        return conn.createStatement().executeQuery(sqlGetTeam);
    }

    public double getAverageAgeByTeam(String teamName) throws SQLException {
        int getTeamId = getTeam(teamName).getInt(1);
        String avgAgeByTeamSql = "SELECT AVG(age) FROM players WHERE team_id = '" + getTeamId + "'";
        ResultSet averageHeight = conn.createStatement().executeQuery(avgAgeByTeamSql);
        return averageHeight.getDouble(1);
    }

    public double getAverageHeightByTeam(String teamName) throws SQLException {
        int getTeamId = getTeam(teamName).getInt(1);
        String avgAgeByTeamSql =
            "SELECT AVG(height) FROM players WHERE team_id = '" + getTeamId + "'";
        ResultSet averageHeight = conn.createStatement().executeQuery(avgAgeByTeamSql);
        return averageHeight.getDouble(1);
    }

    public double getAverageWeightByTeam(String teamName) throws SQLException {
        int getTeamId = getTeam(teamName).getInt(1);
        String avgAgeByTeamSql =
            "SELECT AVG(weight) FROM players WHERE team_id = '" + getTeamId + "'";
        ResultSet averageHeight = conn.createStatement().executeQuery(avgAgeByTeamSql);
        return averageHeight.getDouble(1);
    }

    public List<String> getAllTeamName() throws SQLException {
        List<String> allTeamNames = new ArrayList<>();
        String selectAllTeamSql = "SELECT name FROM teams";
        ResultSet data = conn.createStatement().executeQuery(selectAllTeamSql);
        while (data.next()) {
            allTeamNames.add(data.getString(1));
        }
        return allTeamNames;
    }

    public Entry<String, Double> getTeamWithHigherAvgHeight(List<String> teamNames)
        throws SQLException {
        Map<String, Double> avgHeightByTeam = new HashMap<>();

        for (String name : teamNames) {
            avgHeightByTeam.put(name, getAverageHeightByTeam(name));
        }

        return avgHeightByTeam.entrySet().stream().max(
            Comparator.comparing(Entry::getValue)).get();
    }

    public String getTeamWithOlderPlayers(List<String> teamNames) throws SQLException {
        Map<String, Double> s = new HashMap<>();
        for (String name : teamNames) {
            s.put(name, getAverageAgeByTeam(name));
        }
        return s.entrySet().stream().max(Comparator.comparing(Entry::getValue)).get().getKey();
    }

    public String getTeamWithAvgByHeightAndWeight(List<String> teamNames) throws SQLException {
        List<String> teams = new ArrayList<>();
        for (String name : teamNames) {
            Double avgHeightByTeam = getAverageHeightByTeam(name);
            Double avgWeightByTeam = getAverageWeightByTeam(name);
            if (74 <= avgHeightByTeam && avgHeightByTeam <= 78 && 190 <= avgWeightByTeam
                && avgWeightByTeam <= 210) {
                teams.add(name);
            }
        }

        return getTeamWithOlderPlayers(teams);
    }
}
