package ursug.benchmarkssc.Logic;

import ursug.benchmarkssc.Model.TestCase;
import ursug.benchmarkssc.Model.TestResults;

import java.util.ArrayList;
import java.util.List;

public class CPLUSPLUS extends Code {
    private List<TestCase> testCases;
    public List<TestResults> testResults;

    public CPLUSPLUS(List<TestCase> testTypes) {
        this.testCases = testTypes;
    }

    public List<TestResults> runTests() {
        this.testResults = new ArrayList<>();
        for (TestCase testCase : testCases) {
            testResults.add(executeTest(testCase));
        }
        computeAverageTime(testResults);
        return testResults;
    }
}
