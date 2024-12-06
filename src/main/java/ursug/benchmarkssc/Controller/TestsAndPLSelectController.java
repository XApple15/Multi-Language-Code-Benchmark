package ursug.benchmarkssc.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ursug.benchmarkssc.Logic.CPLUSPLUS;
import ursug.benchmarkssc.Logic.CSHARP;
import ursug.benchmarkssc.Logic.JAVA;
import ursug.benchmarkssc.MainApp;
import ursug.benchmarkssc.Model.TestCase;
import ursug.benchmarkssc.Model.TestResults;
import ursug.benchmarkssc.Enum.TestPL;
import ursug.benchmarkssc.Enum.TestType;

import java.util.ArrayList;
import java.util.List;

import static ursug.benchmarkssc.Utils.ShowAlert.setAlert;

public class TestsAndPLSelectController {
    public CheckBox checkbox_cpp;
    public CheckBox checkbox_csharp;
    public CheckBox checkbox_java;
    public CheckBox checkbox_memalloc;
    public CheckBox checkbox_memaccess;
    public CheckBox checkbox_thcreate;
    public CheckBox checkbox_thmigration;
    public CheckBox checkbox_thconswitch;
    public TextField textfield_memalloc_from;
    public TextField textfield_memalloc_to;
    public TextField textfield_memacces_to;
    public TextField textfield_memaccess_from;
    public TextField textfield_thcreate_from;
    public TextField textfield_thcreate_to;
    public TextField textfield_thmigration_from;
    public TextField textfield_thmigration_to;
    public TextField textfield_thconswitch_from;
    public TextField textfield_thconswitch_to;
    public Button button_starttest;
    public AnchorPane anchorpane_selectPL;
    public AnchorPane anchorpane_tests_parameters;
    public ProgressIndicator progressindicator_loading_tests;
    public TextField textfield_memalloc_step;
    public TextField textfield_memacces_step;
    public TextField textfield_thcreate_step;
    public TextField textfield_thconswitch_step;
    public TextField textfield_thmigration_step;
    public Button button_previoustests;

    Alert a = new Alert(Alert.AlertType.NONE);

    @FXML
    public void initialize() {
        progressindicator_loading_tests.setVisible(false);
        button_starttest.setOnAction(event -> {
            if (areFieldsValid()) {
                button_starttest.setVisible(false);
                anchorpane_tests_parameters.setOpacity(0.7);
                anchorpane_tests_parameters.setDisable(true);
                anchorpane_selectPL.setOpacity(0.7);
                anchorpane_selectPL.setDisable(true);
                progressindicator_loading_tests.setVisible(true);

                new Thread(() -> {
                    createTestsBasedOnProgrammingLanguage();
                    Platform.runLater(() -> {
                        button_starttest.setVisible(true);
                        anchorpane_tests_parameters.setOpacity(1.0);
                        anchorpane_tests_parameters.setDisable(false);
                        anchorpane_selectPL.setOpacity(1.0);
                        anchorpane_selectPL.setDisable(false);
                        button_starttest.setVisible(true);
                        progressindicator_loading_tests.setVisible(false);
                    });
                }).start();
            } else {
                System.out.println("Fields are not valid");
                setAlert(Alert.AlertType.ERROR, a);
            }
        });

        button_previoustests.setOnAction(this::switchToAllResults);
    }

    public Boolean areFieldsValid() {
        try {
            if (!checkbox_cpp.isSelected() && !checkbox_csharp.isSelected() && !checkbox_java.isSelected()) {
                return false;
            }
            if (!checkbox_memalloc.isSelected() && !checkbox_memaccess.isSelected() && !checkbox_thcreate.isSelected() &&
                    !checkbox_thmigration.isSelected() && !checkbox_thconswitch.isSelected()) {
                return false;
            }
            if (checkbox_memalloc.isSelected()) {
                if (textfield_memalloc_step.getText().isEmpty() || textfield_memalloc_from.getText().isEmpty() ||
                        textfield_memalloc_to.getText().isEmpty() ||
                        Integer.parseInt(textfield_memalloc_from.getText()) > Integer.parseInt(textfield_memalloc_to.getText()) ||
                        Integer.parseInt(textfield_memalloc_to.getText()) - Integer.parseInt(textfield_memalloc_from.getText())
                                < Integer.parseInt(textfield_memalloc_step.getText())
                ) {
                    return false;
                }
            }
            if (checkbox_memaccess.isSelected()) {
                if (textfield_memaccess_from.getText().isEmpty() || textfield_memacces_to.getText().isEmpty() ||
                        textfield_memacces_step.getText().isEmpty() ||
                        Integer.parseInt(textfield_memaccess_from.getText()) > Integer.parseInt(textfield_memacces_to.getText()) ||
                        Integer.parseInt(textfield_memacces_to.getText()) - Integer.parseInt(textfield_memaccess_from.getText())
                                < Integer.parseInt(textfield_memacces_step.getText())
                ) {
                    return false;
                }
            }
            if (checkbox_thcreate.isSelected()) {
                if (textfield_thcreate_from.getText().isEmpty() || textfield_thcreate_to.getText().isEmpty() ||
                        textfield_thcreate_step.getText().isEmpty() ||
                        Integer.parseInt(textfield_thcreate_from.getText()) > Integer.parseInt(textfield_thcreate_to.getText()) ||
                        Integer.parseInt(textfield_thcreate_to.getText()) - Integer.parseInt(textfield_thcreate_from.getText())
                                < Integer.parseInt(textfield_thcreate_step.getText())
                ) {
                    return false;
                }
            }
            if (checkbox_thmigration.isSelected()) {
                if (textfield_thmigration_from.getText().isEmpty() || textfield_thmigration_to.getText().isEmpty() ||
                        textfield_thmigration_step.getText().isEmpty() ||
                        Integer.parseInt(textfield_thmigration_from.getText()) > Integer.parseInt(textfield_thmigration_to.getText()) ||
                        Integer.parseInt(textfield_thmigration_to.getText()) - Integer.parseInt(textfield_thmigration_from.getText())
                                < Integer.parseInt(textfield_thmigration_step.getText())
                ) {
                    return false;
                }
            }
            if (checkbox_thconswitch.isSelected()) {
                if (textfield_thconswitch_from.getText().isEmpty() || textfield_thconswitch_to.getText().isEmpty() ||
                        textfield_thconswitch_step.getText().isEmpty() ||
                        Integer.parseInt(textfield_thconswitch_from.getText()) > Integer.parseInt(textfield_thconswitch_to.getText()) ||
                        Integer.parseInt(textfield_thconswitch_to.getText()) - Integer.parseInt(textfield_thconswitch_from.getText())
                                < Integer.parseInt(textfield_thconswitch_step.getText())
                ) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @FXML
    public void createTestsBasedOnProgrammingLanguage() {
        List<List<TestResults>> testResults = new ArrayList<>();

        Runnable test1 = () -> {
            if (checkbox_cpp.isSelected()) {
                List<TestCase> testCases = createTestsBasedOnTestType(TestPL.CPP);
                CPLUSPLUS cplusplus = new CPLUSPLUS(testCases);
                List<TestResults> results = cplusplus.runTests();
                testResults.add(results);
            }
        };
        Runnable test2 = () -> {
            if (checkbox_csharp.isSelected()) {
                List<TestCase> testCases = createTestsBasedOnTestType(TestPL.CSHARP);
                CSHARP csharp = new CSHARP(testCases);
                List<TestResults> results = csharp.runTests();
                testResults.add(results);
            }
        };

        Runnable test3 = () -> {
            if (checkbox_java.isSelected()) {
                List<TestCase> testCases = createTestsBasedOnTestType(TestPL.JAVA);
                JAVA java = new JAVA(testCases);
                List<TestResults> results = java.runTests();
                testResults.add(results);
            }
        };
        Thread thread1 = new Thread(test1);
        Thread thread2 = new Thread(test2);
        Thread thread3 = new Thread(test3);

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            try {
                MainApp.switchToResults(testResults, (Stage) button_starttest.getScene().getWindow());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void switchToAllResults(ActionEvent actionEvent) {
        try {
            MainApp.switchToAllResults((Stage) ((Node) actionEvent.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<TestCase> createTestsBasedOnTestType(TestPL testPL) {
        List<TestCase> testCases = new ArrayList<>();
        if (checkbox_memalloc.isSelected()) {
            testCases.add(new TestCase(Integer.parseInt(textfield_memalloc_from.getText().toString()),
                    Integer.parseInt(textfield_memalloc_to.getText().toString()), Integer.parseInt(textfield_memalloc_step.getText().toString()), 10,
                    TestType.MEMORY_ALLOCATION_STATIC, testPL));
            testCases.add(new TestCase(Integer.parseInt(textfield_memalloc_from.getText().toString()),
                    Integer.parseInt(textfield_memalloc_to.getText().toString()), Integer.parseInt(textfield_memalloc_step.getText().toString()), 10,
                    TestType.MEMORY_ALLOCATION_DYNAMIC, testPL));
        }
        if (checkbox_memaccess.isSelected()) {
            testCases.add(new TestCase(Integer.parseInt(textfield_memaccess_from.getText().toString()),
                    Integer.parseInt(textfield_memacces_to.getText().toString()), Integer.parseInt(textfield_memacces_step.getText().toString()), 5,
                    TestType.MEMORY_ACCESS_STATIC, testPL));
            testCases.add(new TestCase(Integer.parseInt(textfield_memaccess_from.getText().toString()),
                    Integer.parseInt(textfield_memacces_to.getText().toString()), Integer.parseInt(textfield_memacces_step.getText().toString()), 5,
                    TestType.MEMORY_ACCESS_DYNAMIC, testPL));
        }
        if (checkbox_thcreate.isSelected()) {
            testCases.add(new TestCase(Integer.parseInt(textfield_thcreate_from.getText().toString()),
                    Integer.parseInt(textfield_thcreate_to.getText().toString()), Integer.parseInt(textfield_thcreate_step.getText().toString()), 5,
                    TestType.THREAD_CREATION, testPL));
        }
        if (checkbox_thmigration.isSelected()) {
            testCases.add(new TestCase(Integer.parseInt(textfield_thmigration_from.getText().toString()),
                    Integer.parseInt(textfield_thmigration_to.getText().toString()), Integer.parseInt(textfield_thmigration_step.getText().toString()), 5,
                    TestType.THREAD_MIGRATION, testPL));
        }
        if (checkbox_thconswitch.isSelected()) {
            testCases.add(new TestCase(Integer.parseInt(textfield_thconswitch_from.getText().toString()),
                    Integer.parseInt(textfield_thconswitch_to.getText().toString()), Integer.parseInt(textfield_thconswitch_step.getText().toString()), 5,
                    TestType.THREAD_CONTEXT_SWITCH, testPL));
        }
        return testCases;
    }
}
