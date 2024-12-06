package ursug.benchmarkssc.Logic;

import ursug.benchmarkssc.Model.GraphPoint;
import ursug.benchmarkssc.Model.TestCase;
import ursug.benchmarkssc.Model.TestResults;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Code {
    static final Integer MAX_CODE_FAILURES = 3;
    static final long MAX_EXECUTION_TIME_MS = 5000;
    final String exeFileCPP = "src/main/java/ursug/benchmarkssc/TestCodes/Cplusplus";
    final String exefileCSHARP = "src/main/java/ursug/benchmarkssc/TestCodes/CsharpTest/bin/Debug/net8.0/CsharpTest.exe";
    final String exeFileJAVA = "./src/main/java;./src/main/java/ursug/benchmarkssc/TestCodes/jna-5.15.0.jar";

    public TestResults executeTest(TestCase testCase) {
        TestResults testResult = new TestResults(testCase, new ArrayList<>(), null, null);

        int codeFailures = 0;
        for (int i = testCase.startInterval; i <= testCase.endInterval; i += testCase.step) {

            long result = 0;
            for (int j = 1; j <= testCase.numberOfTests; j++) {
                try {
                    if (codeFailures >= MAX_CODE_FAILURES) {
                        testResult.setErrorIndex(i);
                        System.out.println("failed at index " + i);
                        return testResult;
                    }
                    ProcessBuilder processBuilder = null;
                    if (this.getClass() == CPLUSPLUS.class) {
                        processBuilder = new ProcessBuilder(exeFileCPP, Integer.toString(i), testCase.testType.toString());
                    }
                    if (this.getClass() == CSHARP.class) {
                        processBuilder = new ProcessBuilder(exefileCSHARP, Integer.toString(i), testCase.testType.toString());

                    } else if (this.getClass() == JAVA.class) {
                        processBuilder = new ProcessBuilder("java", "-cp", exeFileJAVA, "ursug.benchmarkssc.TestCodes.Java", Integer.toString(i), testCase.testType.toString());
                    }

                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();

                    boolean finished = process.waitFor(MAX_EXECUTION_TIME_MS, TimeUnit.MILLISECONDS);

                    if (!finished) {
                        process.destroy();
                        codeFailures++;
                        System.out.println("Script timed out for interval " + i + ", restarting test...");
                        j--;
                        continue;
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result += Long.parseLong(line);
                    }
                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        codeFailures++;
                        j--;
                    }

                    // System.out.print("   C++ program exited with code: " + exitCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(testCase.testPL + " " + testCase.testType + " " + i + " " + result / testCase.numberOfTests);
            testResult.appendGraphPoint(new GraphPoint(i, result / testCase.numberOfTests));
        }

        return testResult;
    }

    public void computeAverageTime(List<TestResults> testResults) {
        for (TestResults testResult : testResults) {
            double sum = 0;
            for (GraphPoint graphPoint : testResult.graphPoints) {
                sum += graphPoint.value;
            }
            double value = sum / testResult.graphPoints.size();
            testResult.averageTime = Math.round(value * 100.0) / 100.0;
        }
    }
}
