package geneticalgorithm;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.util.List;

public class ChartBuilder {

    public static LineChart<Number, Number> createChart(List<Double> fitnessHistory) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Поколение");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(fitnessHistory.size() > 0 ? fitnessHistory.size() - 1 : 1);
        xAxis.setTickUnit(Math.max(1, fitnessHistory.size() / 10));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Приспособленность f(x)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setTitle("График сходимости алгоритма");
        chart.setCreateSymbols(true); // Показывать точки

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (int i = 0; i < fitnessHistory.size(); i++) {
            XYChart.Data<Number, Number> data = new XYChart.Data<>(i, fitnessHistory.get(i));

            final int index = i;
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    if (index == fitnessHistory.size() - 1) {
                        // ФИНАЛЬНАЯ ТОЧКА: Красная, крупная
                        newNode.setStyle("-fx-background-color: red, white; " +
                                "-fx-background-radius: 8px; " +
                                "-fx-padding: 8px; " +
                                "-fx-background-insets: 0, 2;");
                    } else {
                        // ОБЫЧНЫЕ ТОЧКИ: Синие
                        newNode.setStyle("-fx-background-color: blue, white; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-padding: 5px; " +
                                "-fx-background-insets: 0, 2;");
                    }
                }
            });
            series.getData().add(data);
        }

        chart.getData().add(series);

        // Стиль для линии (синяя)
        chart.lookup(".chart-series-line").setStyle(
                "-fx-stroke: blue; -fx-stroke-width: 2px;"
        );

        return chart;
    }
}