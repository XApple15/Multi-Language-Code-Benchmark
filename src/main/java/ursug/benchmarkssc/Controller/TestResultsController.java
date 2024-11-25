package ursug.benchmarkssc.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import ursug.benchmarkssc.Enum.TestPL;
import ursug.benchmarkssc.Enum.TestType;
import ursug.benchmarkssc.Model.GraphPoint;
import ursug.benchmarkssc.Model.TestResults;

import java.util.ArrayList;
import java.util.List;

public class TestResultsController {
    public LineChart<Number, Number> linechart_result;
    public NumberAxis axisX;
    public NumberAxis axisY;

    public List<List<TestResults>> allTestResults;
    public ChoiceBox<String> choicebox_selectTest;
    public CheckBox checkbox_cpp;
    public CheckBox checkbox_csharp;
    public CheckBox checkbox_java;

    public void initialize() {
        axisX.setLabel("Size");
        axisY.setLabel("Time(ms)");

        choicebox_selectTest.setOnAction(event -> viewGraph());
        checkbox_cpp.setOnAction(event -> viewGraph());
        checkbox_csharp.setOnAction(event -> viewGraph());
        checkbox_java.setOnAction(event -> viewGraph());
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

        for (List<TestResults> resultsList : testResults) {
            for (TestResults testResult : resultsList) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("Test Result " + testResult.testCase.testType);

                int start = testResult.testCase.startInterval;
                int end = testResult.testCase.endInterval;

                axisX.setLowerBound(start);
                axisX.setUpperBound(end);
                axisX.setTickUnit((end - start) / 10.0);

                List<Integer> xValues = new ArrayList<>();
                List<Integer> yValues = new ArrayList<>();

                for (GraphPoint graphPoint : testResult.graphPoints) {
                    xValues.add(graphPoint.x);
                    yValues.add(graphPoint.y);
                }

                int windowSize = 5;
                List<Integer> smoothedYValues = movingAverage(yValues, windowSize);

                for (int i = 0; i < xValues.size(); i++) {
                    series.getData().add(new XYChart.Data<>(xValues.get(i), smoothedYValues.get(i)));
                }
                seriesList.add(series);
            }
        }
        linechart_result.getData().addAll(seriesList);
    }

    private List<Integer> movingAverage(List<Integer> values, int windowSize) {
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
