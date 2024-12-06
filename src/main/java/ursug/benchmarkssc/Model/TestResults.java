package ursug.benchmarkssc.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestResults {
    public String date;
    public TestCase testCase;
    public Integer errorIndex;// get the index where is stack overflow
    public Double averageTime;
    public List<GraphPoint> graphPoints;


    public TestResults(TestCase testCase, List<GraphPoint> graphPoints, Integer errorIndex, Double averageTime) {
        this.date = LocalDateTime.now().toString();
        this.testCase = testCase;
        this.graphPoints = graphPoints;
        this.graphPoints = new ArrayList<>();
        this.errorIndex = errorIndex;
        this.averageTime = averageTime;
    }

    public void setErrorIndex(Integer errorIndex) {
        this.errorIndex = errorIndex;
    }
    public void appendGraphPoint(GraphPoint graphPoint) {
        this.graphPoints.add(graphPoint);
    }

}
