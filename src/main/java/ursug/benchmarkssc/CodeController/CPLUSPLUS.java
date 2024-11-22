package ursug.benchmarkssc.CodeController;

import ursug.benchmarkssc.Enum.TestType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CPLUSPLUS {
    private static final Integer MAX_CODE_FAILURES = 3;
    private List<TestCase> testCases;
    public final String exeFile = "src/main/java/ursug/benchmarkssc/TestCodes/Cplusplus";
    public List<TestResults> testResults;

    public CPLUSPLUS(List<TestCase> testTypes) {
        this.testCases = testTypes;
    }

    public void runTests() {
        this.testResults = new ArrayList<>();
        for (TestCase testCase : testCases) {
            TestResults testResult = new TestResults(testCase, null, null);
            switch (testCase.testType) {
                case MEMORY_ALLOCATION_STATIC:
                    runMemoryAllocation(testCase, testResult);
                    break;
                case MEMORY_ALLOCATION_DYNAMIC:
                    runMemoryAllocation(testCase, testResult);
                    break;
                case MEMORY_ACCESS_STATIC:
                    runMemoryAccess(testCase, testResult);
                    break;
                case MEMORY_ACCESS_DYNAMIC:
                    runMemoryAccess(testCase, testResult);
                    break;
                case THREAD_CREATION:
                    runThreadCreation(testCase, testResult);
                    break;
                case THREAD_CONTEXT_SWITCH:
                    runThreadContextSwitch(testCase, testResult);
                    break;
                case THREAD_MIGRATION:
                    runThreadMigration(testCase, testResult);
                    break;
            }
            testResults.add(testResult);
        }
    }

    private void runMemoryAllocation(TestCase testCase, TestResults testResult) {
        // Run memory allocation
        int codeFailures = 0;
        for (int i = testCase.startInterval; i <= testCase.endInterval; i += testCase.step) {
            // Run memory allocation test

            int result = 0;
            for (int j = 1; j <= testCase.numberOfTests; j++) {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(exeFile, Integer.toString(i), testCase.testType.toString());
                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result += Integer.parseInt(line);
                    }
                    // System.out.println("Memory Allocation Test: " + i + " " + result / testCase.numberOfTests);
                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        codeFailures++;
                        j--;
                    }
                    if (codeFailures >= MAX_CODE_FAILURES) {
                        testResult.setErrorIndex(i);
                        System.out.println("failed at index " + i);
                        return;
                    }
                    // System.out.print("   C++ program exited with code: " + exitCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(testCase.testPL + " " + testCase.testType + " " + i + " " + result / testCase.numberOfTests);
            testResult.appendGraphPoint(new GraphPoint(i, result / testCase.numberOfTests));
        }

    }


    private void runMemoryAccess(TestCase testCase, TestResults testResult) {
        int codeFailures = 0;
        for (int i = testCase.startInterval; i <= testCase.endInterval; i += testCase.step) {
            // Run memory allocation test

            int result = 0;
            for (int j = 1; j <= testCase.numberOfTests; j++) {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(exeFile, Integer.toString(i), testCase.testType.toString());
                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result += Integer.parseInt(line);
                    }
                    // System.out.println("Memory Access Test: " + i + " " + result / testCase.numberOfTests);
                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        codeFailures++;
                        j--;
                    }
                    if (codeFailures >= MAX_CODE_FAILURES) {
                        testResult.setErrorIndex(i);
                        System.out.println("failed at index " + i);
                        return;
                    }
                    // System.out.print("   C++ program exited with code: " + exitCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(testCase.testPL + " " + testCase.testType + " " + i + " " + result / testCase.numberOfTests);
            testResult.appendGraphPoint(new GraphPoint(i, result / testCase.numberOfTests));
        }

    }


    private void runThreadCreation(TestCase testCase, TestResults testResult) {
        // Run thread creation
    }

    private void runThreadContextSwitch(TestCase testCase, TestResults testResult) {
        // Run thread context switch
    }

    private void runThreadMigration(TestCase testCase, TestResults testResult) {
        // Run thread migration
    }
}
