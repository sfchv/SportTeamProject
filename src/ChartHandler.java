import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartHandler {
    public static void drawChart(String name, String labelCategory, String valueLabel,
        DefaultCategoryDataset dataset) throws IOException {
        JFreeChart chart = ChartFactory.createLineChart(
            name,
            labelCategory,
            valueLabel,
            dataset
        );

        ChartUtils.saveChartAsJPEG(
            new File("src\\chart.jpg"),
            chart,
            1920,
            1080
        );
    }
}
