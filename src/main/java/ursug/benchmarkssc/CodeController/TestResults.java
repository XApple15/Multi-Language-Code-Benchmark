package ursug.benchmarkssc.CodeController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestResults {
    public LocalDateTime date;
    public TestCase testCase;
    public List<GraphPoint> graphPoints;
    public Integer errorIndex;// get the index where is stack overflow

    public TestResults(TestCase testCase, List<GraphPoint> graphPoints, Integer errorIndex) {
        this.date = LocalDateTime.now();
        this.testCase = testCase;
        this.graphPoints = graphPoints;
        this.graphPoints = new ArrayList<>();
        this.errorIndex = errorIndex;
    }

    public void setErrorIndex(Integer errorIndex) {
        this.errorIndex = errorIndex;
    }
    public void appendGraphPoint(GraphPoint graphPoint) {
        this.graphPoints.add(graphPoint);
    }

}
