package ursug.benchmarkssc;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ursug.benchmarkssc.Controller.TestResultsController;
import ursug.benchmarkssc.Model.TestResults;

import java.util.List;

public class MainApp extends Application {

    private static Stage primaryStage;
    public static String TESTS_AND_PL_SELECT = "tests_and_PL_select.fxml";
    public static String TEST_RESULTS = "test_results.fxml";
    public static String ALL_RESULTS = "all_results.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Benchmark");
        switchToMain();
    }


    public static void switchToMain() throws Exception {
        Parent root = FXMLLoader.load(MainApp.class.getResource(TESTS_AND_PL_SELECT));
        primaryStage.setScene(new Scene(root, 910, 400));
        primaryStage.show();
    }

    public static void switchToResults(List<List<TestResults>> testResults, Stage currentStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(TEST_RESULTS));
            Parent root = loader.load();

            TestResultsController controller = loader.getController();
            controller.initializeWithData(testResults);

            Platform.runLater(() -> {
                Stage stage = currentStage;
                stage.setScene(new Scene(root));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchToAllResults(Stage currentStage) throws Exception {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource(ALL_RESULTS));
            currentStage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
