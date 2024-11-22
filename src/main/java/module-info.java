module ursug.benchmarkssc {
    requires javafx.controls;
    requires javafx.fxml;

    exports ursug.benchmarkssc; // Export the root package

    opens ursug.benchmarkssc.Controller to javafx.fxml;
    exports ursug.benchmarkssc.Controller;
    exports ursug.benchmarkssc.Enum;
}