package geneticalgorithm;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GeneticAlgorithmApp extends Application {

    private TextArea logArea;
    private BorderPane chartPane;
    private Label resultLabel;

    private ComboBox<String> strategyCombo;
    private ComboBox<String> selectionCombo;
    private ComboBox<String> crossoverCombo;
    private ComboBox<String> mutationCombo;
    private TextField popSizeField;
    private TextField generationsField;
    private TextField pCrossField;
    private TextField pMutField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Генетический алгоритм: Вариант 14");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Панель настроек
        GridPane settings = new GridPane();
        settings.setHgap(10);
        settings.setVgap(8);
        settings.setPadding(new Insets(10));
        settings.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");

        settings.add(new Label("Стратегия (II):"), 0, 0);
        strategyCombo = new ComboBox<>();
        strategyCombo.getItems().addAll("Одеяло (A)", "Фокусировка (C)");
        strategyCombo.setValue("Одеяло (A)");
        settings.add(strategyCombo, 1, 0);

        settings.add(new Label("Селекция (III):"), 0, 1);
        selectionCombo = new ComboBox<>();
        selectionCombo.getItems().addAll("Случайная (A)", "По шкале (B)");
        selectionCombo.setValue("По шкале (B)");
        settings.add(selectionCombo, 1, 1);

        settings.add(new Label("Кроссинговер (IV):"), 0, 2);
        crossoverCombo = new ComboBox<>();
        crossoverCombo.getItems().addAll("Одноточечный (A)", "Двухточечный (B)", "Циклический (I)");
        crossoverCombo.setValue("Одноточечный (A)");
        settings.add(crossoverCombo, 1, 2);

        settings.add(new Label("Мутация (V):"), 0, 3);
        mutationCombo = new ComboBox<>();
        mutationCombo.getItems().addAll("Простая (A)", "Инверсия (F)");
        mutationCombo.setValue("Простая (A)");
        settings.add(mutationCombo, 1, 3);

        // ИСПРАВЛЕНО: Размер популяции по умолчанию 10 (по методичке)
        settings.add(new Label("Популяция:"), 0, 4);
        popSizeField = new TextField("10");
        settings.add(popSizeField, 1, 4);

        settings.add(new Label("Поколений:"), 0, 5);
        generationsField = new TextField("50");
        settings.add(generationsField, 1, 5);

        settings.add(new Label("Вер. Кросс. (%):"), 0, 6);
        pCrossField = new TextField("70");
        settings.add(pCrossField, 1, 6);

        settings.add(new Label("Вер. Мут. (%):"), 0, 7);
        pMutField = new TextField("20");
        settings.add(pMutField, 1, 7);

        // Кнопка запуска
        HBox buttonBox = new HBox(10);
        Button runBtn = new Button("▶ ЗАПУСТИТЬ ЭКСПЕРИМЕНТ");
        runBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        runBtn.setOnAction(e -> runExperiment());
        buttonBox.getChildren().add(runBtn);

        // Лог
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(100);
        logArea.setText("Лог выполнения...\n");

        // Область для графика
        chartPane = new BorderPane();
        chartPane.setPrefHeight(350);
        chartPane.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");
        chartPane.setCenter(new Label("График появится здесь после запуска"));

        // Результат
        resultLabel = new Label("Результат: ");
        resultLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        root.getChildren().addAll(settings, buttonBox, resultLabel, logArea, chartPane);

        Scene scene = new Scene(root, 600, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void runExperiment() {
        try {
            int popSize = Integer.parseInt(popSizeField.getText());
            int gens = Integer.parseInt(generationsField.getText());
            double pCross = Double.parseDouble(pCrossField.getText()) / 100.0;
            double pMut = Double.parseDouble(pMutField.getText()) / 100.0;

            boolean isBlanket = strategyCombo.getValue().contains("Одеяло");
            boolean isRandom = selectionCombo.getValue().contains("Случайная");
            int crossType = crossoverCombo.getSelectionModel().getSelectedIndex() + 1;
            int mutType = mutationCombo.getSelectionModel().getSelectedIndex() + 1;

            logArea.appendText(String.format("Запуск: Pop=%d, Gens=%d, Pc=%.1f, Pm=%.1f\n",
                    popSize, gens, pCross, pMut));

            GeneticAlgorithm ga = new GeneticAlgorithm(
                    popSize, gens, pCross, pMut,
                    isBlanket, isRandom, crossType, mutType
            );

            Chromosome best = ga.run();

            String resText = String.format("Найдено: x=%.4f, f(x)=%.4f (Поколение %d)",
                    best.getX(), best.getFitness(), best.getGeneration());
            resultLabel.setText("✅ " + resText);
            logArea.appendText("Результат: " + resText + "\n");

            // Построение графика (LineChart)
            LineChart<Number, Number> chart = ChartBuilder.createChart(ga.getHistory());
            chartPane.setCenter(chart);

        } catch (Exception ex) {
            logArea.appendText("Ошибка: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}