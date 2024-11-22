package ursug.benchmarkssc.CodeController;

import ursug.benchmarkssc.Enum.TestPL;
import ursug.benchmarkssc.Enum.TestType;

public class TestCase {
    public int startInterval;
    public int endInterval;
    public int step;
    public int numberOfTests;
    public TestType testType;
    public TestPL testPL;

    public TestCase(int startInterval, int endInterval, int step, int numberOfTests, TestType testType, TestPL testPL) {
        this.startInterval = startInterval;
        this.endInterval = endInterval;
        this.step = step;
        this.numberOfTests = numberOfTests;
        this.testType = testType;
        this.testPL = testPL;
    }
}
