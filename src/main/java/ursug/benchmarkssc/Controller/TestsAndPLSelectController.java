package ursug.benchmarkssc.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ursug.benchmarkssc.CodeController.CPLUSPLUS;
import ursug.benchmarkssc.CodeController.TestCase;
import ursug.benchmarkssc.Enum.TestPL;
import ursug.benchmarkssc.Enum.TestType;

import java.util.ArrayList;
import java.util.List;

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
            }
        });
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
                if (textfield_memalloc_from.getText().isEmpty() || textfield_memalloc_to.getText().isEmpty() ||
                        Integer.parseInt(textfield_memalloc_from.getText()) > Integer.parseInt(textfield_memalloc_to.getText())) {
                    return false;
                }
            }
            if (checkbox_memaccess.isSelected()) {
                if (textfield_memaccess_from.getText().isEmpty() || textfield_memacces_to.getText().isEmpty() ||
                        Integer.parseInt(textfield_memaccess_from.getText()) > Integer.parseInt(textfield_memacces_to.getText())) {
                    return false;
                }
            }
            if (checkbox_thcreate.isSelected()) {
                if (textfield_thcreate_from.getText().isEmpty() || textfield_thcreate_to.getText().isEmpty() ||
                        Integer.parseInt(textfield_thcreate_from.getText()) > Integer.parseInt(textfield_thcreate_to.getText())) {
                    return false;
                }
            }
            if (checkbox_thmigration.isSelected()) {
                if (textfield_thmigration_from.getText().isEmpty() || textfield_thmigration_to.getText().isEmpty() ||
                        Integer.parseInt(textfield_thmigration_from.getText()) > Integer.parseInt(textfield_thmigration_to.getText())) {
                    return false;
                }
            }
            if (checkbox_thconswitch.isSelected()) {
                if (textfield_thconswitch_from.getText().isEmpty() || textfield_thconswitch_to.getText().isEmpty() ||
                        Integer.parseInt(textfield_thconswitch_from.getText()) > Integer.parseInt(textfield_thconswitch_to.getText())) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public void createTestsBasedOnProgrammingLanguage() {
        if (checkbox_cpp.isSelected()) {
            //create tests for c++
            List<TestCase> testCases = createTestsBasedOnTestType(TestPL.CPP);
            CPLUSPLUS cplusplus = new CPLUSPLUS(testCases);
            cplusplus.runTests();
        }
    }

    public List<TestCase> createTestsBasedOnTestType(TestPL testPL) {
        //create all tests basd on right textfrields
        List<TestCase> testCases = new ArrayList<>();
        if (checkbox_memalloc.isSelected()) {
            testCases.add(new TestCase(Integer.parseInt(textfield_memalloc_from.getText().toString()),
                    Integer.parseInt(textfield_memalloc_to.getText().toString()), 1000, 10,
                    TestType.MEMORY_ALLOCATION_STATIC, testPL));
            testCases.add(new TestCase(Integer.parseInt(textfield_memalloc_from.getText().toString()),
                    Integer.parseInt(textfield_memalloc_to.getText().toString()), 1000, 10,
                    TestType.MEMORY_ALLOCATION_DYNAMIC, testPL));
        }
        if (checkbox_memaccess.isSelected()) {
            testCases.add(new TestCase(Integer.parseInt(textfield_memaccess_from.getText().toString()),
                    Integer.parseInt(textfield_memacces_to.getText().toString()), 100, 5,
                    TestType.MEMORY_ACCESS_STATIC, testPL));
            testCases.add(new TestCase(Integer.parseInt(textfield_memaccess_from.getText().toString()),
                    Integer.parseInt(textfield_memacces_to.getText().toString()), 100, 5,
                    TestType.MEMORY_ACCESS_DYNAMIC, testPL));
        }
        if (checkbox_thcreate.isSelected()) {
            testCases.add(new TestCase(Integer.parseInt(textfield_thcreate_from.getText().toString()),
                    Integer.parseInt(textfield_thcreate_to.getText().toString()), 100, 5,
                    TestType.THREAD_CREATION, testPL));
        }
        if (checkbox_thmigration.isSelected()) {
            testCases.add(new TestCase(Integer.parseInt(textfield_thmigration_from.getText().toString()),
                    Integer.parseInt(textfield_thmigration_to.getText().toString()), 100, 5,
                    TestType.THREAD_MIGRATION, testPL));
        }
        if (checkbox_thconswitch.isSelected()) {
            testCases.add(new TestCase(Integer.parseInt(textfield_thconswitch_from.getText().toString()),
                    Integer.parseInt(textfield_thconswitch_to.getText().toString()), 100, 5,
                    TestType.THREAD_CONTEXT_SWITCH, testPL));
        }
        return testCases;
    }
}
