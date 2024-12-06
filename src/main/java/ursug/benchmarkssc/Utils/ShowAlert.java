package ursug.benchmarkssc.Utils;

import javafx.scene.control.Alert;

public class ShowAlert {

    public static void setAlert(Alert.AlertType type, Alert alert) {
        alert.setAlertType(type);
        alert.show();
    }
}
