package ursug.benchmarkssc.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import ursug.benchmarkssc.Enum.TestPL;
import ursug.benchmarkssc.Enum.TestType;
import ursug.benchmarkssc.MainApp;
import ursug.benchmarkssc.Model.GraphPoint;
import ursug.benchmarkssc.Model.TestResults;
import ursug.benchmarkssc.Utils.ShowAlert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ursug.benchmarkssc.Utils.ShowAlert.setAlert;

public class TestResultsController {
    public LineChart<Number, Number> linechart_result;
    public NumberAxis axisX;
    public NumberAxis axisY;

    public List<List<TestResults>> allTestResults;
    public ChoiceBox<String> choicebox_selectTest;
    public CheckBox checkbox_cpp;
    public CheckBox checkbox_csharp;
    public CheckBox checkbox_java;
    public Button button_goback;
    public Button button_saveresults;
    public Label label_erorr_index;

    Alert a = new Alert(Alert.AlertType.NONE);


    public void initialize() {
        axisX.setLabel("Size");
        axisY.setLabel("Time(ms)");
        label_erorr_index.setVisible(false);

        choicebox_selectTest.setOnAction(event -> viewGraph());
        checkbox_cpp.setOnAction(event -> viewGraph());
        checkbox_csharp.setOnAction(event -> viewGraph());
        checkbox_java.setOnAction(event -> viewGraph());
        button_saveresults.setOnAction(actionEvent -> {
            if (saveResultsToJson(allTestResults)) {
                setAlert(Alert.AlertType.CONFIRMATION, a);
            } else {
                setAlert(Alert.AlertType.ERROR, a);
            }
        });
    }

    public void initializeWithData(List<List<TestResults>> testResults) {
        this.allTestResults = testResults;

        ObservableList<String> choicebox_values = FXCollections.observableArrayList();
        choicebox_values.add(TestType.MEMORY_ALLOCATION.toString());
        choicebox_values.add(TestType.MEMORY_ACCESS.toString());
        for (TestResults testResult : testResults.get(0)) {
            choicebox_values.add(testResult.testCase.testType.name());
        }
        choicebox_selectTest.setItems(choicebox_values);
        button_goback.setOnAction(event -> goBack());
    }

    private void goBack() {

        try {
            MainApp.switchToMain();
        } catch (Exception e) {
            e.printStackTrace();
            setAlert(Alert.AlertType.ERROR, a);
        }
    }


    public void viewGraph() {
        if (validateInput()) {
            clearLineChart();
            List<List<TestResults>> filteredResults = filterResults();
            drawGraph(filteredResults);
        }
    }

    public void clearLineChart() {
        linechart_result.getData().clear();
    }

    public List<List<TestResults>> filterResults() {
        List<List<TestResults>> filteredResults = new ArrayList<>();

        if (checkbox_cpp.isSelected()) {
            filteredResults.add(filterResultsSelectPl(allTestResults, TestPL.CPP));
        }
        if (checkbox_csharp.isSelected()) {
            filteredResults.add(filterResultsSelectPl(allTestResults, TestPL.CSHARP));
        }
        if (checkbox_java.isSelected()) {
            filteredResults.add(filterResultsSelectPl(allTestResults, TestPL.JAVA));
        }
        return filterResultsSelectTestType(filteredResults, choicebox_selectTest.getValue());
    }

    public List<List<TestResults>> filterResultsSelectTestType(List<List<TestResults>> testResults, String testType) {
        List<List<TestResults>> filteredResults = new ArrayList<>();

        for (List<TestResults> testResultList : testResults) {
            List<TestResults> filteredSubList = new ArrayList<>();
            for (TestResults testResult : testResultList) {
                if (testType.equals(testResult.testCase.testType.toString()) ||
                        (testType.equals(TestType.MEMORY_ALLOCATION.toString()) &&
                                (testResult.testCase.testType == TestType.MEMORY_ALLOCATION_STATIC ||
                                        testResult.testCase.testType == TestType.MEMORY_ALLOCATION_DYNAMIC)) ||
                        (testType.equals(TestType.MEMORY_ACCESS.toString()) &&
                                (testResult.testCase.testType == TestType.MEMORY_ACCESS_STATIC ||
                                        testResult.testCase.testType == TestType.MEMORY_ACCESS_DYNAMIC))) {
                    filteredSubList.add(testResult);
                }
            }
            if (!filteredSubList.isEmpty()) {
                filteredResults.add(filteredSubList);
            }
        }
        return filteredResults;
    }

    public List<TestResults> filterResultsSelectPl(List<List<TestResults>> testResults, TestPL testPL) {
        List<TestResults> filtered = new ArrayList<>();
        for (List<TestResults> testResultList : testResults) {
            for (TestResults testResult : testResultList) {
                if (testResult.testCase.testPL == testPL) {
                    filtered.add(testResult);
                }
            }
        }
        return filtered;
    }

    public Boolean validateInput() {
        if (!checkbox_cpp.isSelected() && !checkbox_csharp.isSelected() && !checkbox_java.isSelected()) {
            return false;
        }
        if (choicebox_selectTest.getValue() == null) {
            return false;
        }
        return true;
    }

    public void drawGraph(List<List<TestResults>> testResults) {
        List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();

        axisX.setAutoRanging(false);
        label_erorr_index.setVisible(false);
        label_erorr_index.setText("Error");

        for (List<TestResults> resultsList : testResults) {
            for (TestResults testResult : resultsList) {
                if (testResult.errorIndex != null) {
                    ShowAlert.setAlert(Alert.AlertType.ERROR, a);
                    a.setContentText("Test" + testResult.testCase.testPL + " " + testResult.testCase.testType
                            + " failed at index " + testResult.errorIndex);
                    label_erorr_index.setText(label_erorr_index.getText().toString() + " Test "
                            + testResult.testCase.testPL + " " + testResult.testCase.testType + " failed at index "
                            + testResult.errorIndex + "\n");
                    label_erorr_index.setVisible(true);
                }

                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(testResult.testCase.testPL.toString() + " " + testResult.testCase.testType + " avg time: " + String.format("%.2f", testResult.averageTime));

                int start = testResult.testCase.startInterval;
                int end = testResult.testCase.endInterval;

                axisX.setLowerBound(start);
                axisX.setUpperBound(end);
                axisX.setTickUnit((end - start) / 10.0);

                List<Integer> xValues = new ArrayList<>();
                List<Long> yValues = new ArrayList<>();

                for (GraphPoint graphPoint : testResult.graphPoints) {
                    xValues.add(graphPoint.index);
                    yValues.add(graphPoint.value);
                }

                int windowSize = 5;
                //List<Integer> smoothedYValues = movingAverage(yValues, windowSize);

                for (int i = 0; i < xValues.size(); i++) {
                    series.getData().add(new XYChart.Data<>(xValues.get(i), yValues.get(i)));
                }

                seriesList.add(series);
            }
        }
        linechart_result.getData().addAll(seriesList);
    }

    private boolean saveResultsToJson(List<List<TestResults>> testResults) {
        JsonObject results = new JsonObject();
        for (List<TestResults> testResultList : testResults) {

            for (TestResults testResult : testResultList) {
                JsonObject metrics = new JsonObject();
                metrics.addProperty("avg_execution_time", testResult.averageTime);
                metrics.addProperty("err_index", testResult.errorIndex);

                JsonObject testObject = new JsonObject();
                testObject.addProperty("time_stamp", testResult.date.toString());
                testObject.addProperty("test_type", testResult.testCase.testType.toString());
                testObject.addProperty("language", testResult.testCase.testPL.toString());
                testObject.add("metrics", metrics);
                results.add(testResult.testCase.testPL.toString(), testObject);
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String folderPath = "./Results/";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                System.err.println("Failed to create directory: " + folderPath);
                return false;
            }
        }

        String timeStamp = LocalDateTime.now().toString().replace(":", "-");
        String fileName = "test_results_" + timeStamp + ".json";

        try (FileWriter file = new FileWriter(new File(folder, fileName))) {
            file.write(gson.toJson(allTestResults));
            System.out.println("File saved as: " + new File(folder, fileName).getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private List<Integer> movingAverage(List<Long> values, int windowSize) {
        List<Integer> smoothedValues = new ArrayList<>();
        int halfWindow = windowSize / 2;

        for (int i = 0; i < values.size(); i++) {
            int start = Math.max(0, i - halfWindow);
            int end = Math.min(values.size(), i + halfWindow + 1);

            int sum = 0;
            for (int j = start; j < end; j++) {
                sum += values.get(j);
            }
            smoothedValues.add(sum / (end - start));
        }
        return smoothedValues;
    }
}
