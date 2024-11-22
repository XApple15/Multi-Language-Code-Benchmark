package ursug.benchmarkssc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ursug.benchmarkssc.Enum.TestType;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        switchToSceneOne();
    }

    public void Test() {

        for (int i = 10000; i <= 100000; i = i + 1000) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("src/main/java/ursug/benchmarkssc/TestCodes/bin/Debug/net8.0/TestCodes.exe", Integer.toString(i), TestType.MEMORY_ALLOCATION_STATIC.toString());
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                int exitCode = process.waitFor();
                if(exitCode != 0){
                    System.out.println("C++ program exited with code: " + exitCode);
                    break;
                }
                System.out.println("C++ program exited with code: " + exitCode);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Static methods to switch scenes
    public static void switchToSceneOne() throws Exception {
        Parent root = FXMLLoader.load(MainApp.class.getResource("tests_and_PL_select.fxml"));
        primaryStage.setScene(new Scene(root, 760, 400));
        primaryStage.setTitle("Scene 1");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
