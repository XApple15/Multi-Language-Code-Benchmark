package ursug.benchmarkssc.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import ursug.benchmarkssc.Enum.TestType;
import ursug.benchmarkssc.MainApp;
import ursug.benchmarkssc.Model.TestResults;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AllResultsController {
    public ChoiceBox choicebox_selecttest;

    private final String folderPath = "./Results/";
    public Button button_goback;


    public void initialize() {
        getAllFiles();
        choicebox_selecttest.setOnAction(event -> switchToGraph());
        button_goback.setOnAction(actionEvent -> {
            try {
                MainApp.switchToMain();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void getAllFiles() {

        File folder = new File(folderPath);
        System.out.println(folder.getAbsolutePath());

        ObservableList<String> choicebox_values = FXCollections.observableArrayList();
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        System.out.println("File: " + file.getName());

                        choicebox_values.add(file.getName());
                    }
                }
            }
        }
        choicebox_selecttest.setItems(choicebox_values);
    }

    private void switchToGraph() {

        choicebox_selecttest.getValue().toString();

        Gson gson = new Gson();
        List<List<TestResults>> testResultsList = null;
        try (FileReader reader = new FileReader(folderPath + choicebox_selecttest.getValue().toString())) {
            Type testResultsListType = new TypeToken<List<List<TestResults>>>() {
            }.getType();
            testResultsList = gson.fromJson(reader, testResultsListType);

        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ursug/benchmarkssc/test_results.fxml"));
            Parent root = loader.load();

            TestResultsController controller = loader.getController();
            controller.initializeWithData(testResultsList);

            Platform.runLater(() -> {
                Stage stage = (Stage) choicebox_selecttest.getScene().getWindow();
                stage.setScene(new Scene(root));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
