module ursug.benchmarkssc {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.incubator.vector;
    requires com.google.gson;
    requires jdk.compiler;
    requires com.sun.jna;

    exports ursug.benchmarkssc; // Export the root package

    opens ursug.benchmarkssc.Controller to javafx.fxml;
    exports ursug.benchmarkssc.Controller;
    exports ursug.benchmarkssc.Enum;
    exports ursug.benchmarkssc.Model to com.google.gson;
}