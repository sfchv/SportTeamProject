import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVParser {

    public static List<Team> parse(String url) throws IOException {
        Map<String, Team> teams = new HashMap<>();

        FileReader fileReader = new FileReader(url);
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String str;
            bufferedReader.readLine();

            while ((str = bufferedReader.readLine()) != null) {
                String[] csvFields = str.replace("\"", "").split(",");

                Player player = new Player
                    (
                        csvFields[0], csvFields[2],
                        Integer.parseInt(csvFields[3]),
                        Integer.parseInt(csvFields[4]),
                        Double.parseDouble(csvFields[5])
                    );

                if (teams.containsKey(csvFields[1])) {
                    Team team = teams.get(csvFields[1]);
                    team.addPlayer(player);
                } else {
                    Team team = new Team(csvFields[1]);
                    team.addPlayer(player);
                    teams.put(team.getName(), team);
                }
            }
        }

        return teams.values().stream().toList();
    }
}
