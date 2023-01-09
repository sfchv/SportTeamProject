import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jfree.data.category.DefaultCategoryDataset;

public class Main {

    private static SQLController SQL_CONTROLLER;

    public static void main(String[] args) throws SQLException, IOException {
        // получаем список команда, после парсинга csv
        List<Team> teams = CSVParser.parse("src\\Показатели спортивных команд.csv");
        // создаем контроллер SQL который будет выполнять все наши запросы
        SQL_CONTROLLER = new SQLController();
        // заполняем таблицу командами
        fillDB(teams);

        Task1();
        Task2();
        Task3();
    }

    public static void fillDB(List<Team> teams) throws SQLException {
        for (Team team : teams) {
            SQL_CONTROLLER.addTeam(team);
        }
    }


    public static void Task1() throws SQLException, IOException {
        //Постройте график по среднему возрасту во всех командах.
        Map<String, Double> teamNameByAvgAge = new HashMap<>();
        for (String name : SQL_CONTROLLER.getAllTeamName()) {
            teamNameByAvgAge.put(name, SQL_CONTROLLER.getAverageAgeByTeam(name));
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String key : teamNameByAvgAge.keySet()) {
            dataset.addValue(teamNameByAvgAge.get(key), "age", key);
        }

        ChartHandler.drawChart(
            "График среднего возраста команд", "Возраст", "Команды", dataset
        );
        System.out.println("График среднего возраста команд был успешно создан!");
    }

    public static void Task2() throws SQLException {
        //Найдите команду с самым высоким средним ростом. Выведите в консоль 5 самых высоких игроков команды.
        // получаем название команды и самый высокий средний рост
        var teamAvgHigherHeight =
            SQL_CONTROLLER.getTeamWithHigherAvgHeight(SQL_CONTROLLER.getAllTeamName());

        System.out.println("Cостав данный команды с самым высоким средним ростом: "
                           + String.join(", ", teamAvgHigherHeight)
        );
    }

    public static void Task3() throws SQLException {
        //Найдите команду, с средним ростом равным от 74 до 78 inches и средним весом от 190 до 210 lbs, с самым высоким средним возрастом.
        System.out.println(
            "Команда, с средним ростом равным от 74 до 78 inches"
            + " и средним весом от 190 до 210 lbs, с самым высоким средним возрастом - "
            + SQL_CONTROLLER.getTeamWithAvgByHeightAndWeight(SQL_CONTROLLER.getAllTeamName())
        );
    }
}
