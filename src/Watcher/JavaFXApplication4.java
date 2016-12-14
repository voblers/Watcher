/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Watcher;

import Services.HSQL_Manager;
import static Services.HSQL_Manager.getSettingDouble;
import static Services.HSQL_Manager.getSettingInt;
import static Services.HSQL_Manager.setSetting;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.w3c.dom.events.MouseEvent;

/**
 *
 * @author BB3605
 */
public class JavaFXApplication4 extends Application {

    private static FXMLLoader loader;
    private static Stage mainStage;
    private boolean minimized = false;

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(false);
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FXMLDocument.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);

        mainStage = stage;
        mainStage.setTitle("Watcher");
        mainStage.setScene(scene);
        mainStage.setOnCloseRequest((WindowEvent t) -> {
            FXMLDocumentController control = loader.getController();
            control.shuttingDown();
            System.exit(0);
        });
        mainStage.iconifiedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) {
                minimized = true;
                //mainStage.hide();
                mainStage.getScene().getWindow().hide();
            } else {
                minimized = false;
            }
        });
        mainStage.maximizedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) {
                setSetting("windowMaximized", 1);
            } else {
                setSetting("windowMaximized", 0);
            }
        });
        mainStage.widthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            if (!minimized) {
                setSetting("windowWidth", t1);
            }

        });
        mainStage.heightProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            if (!minimized) {
                setSetting("windowHeight", t1);
            }
        });

        if (getSettingDouble("windowWidth") > Screen.getPrimary().getVisualBounds().getWidth()) {
            setSetting("windowWidth", Screen.getPrimary().getVisualBounds().getWidth() - 500);
        }
        if (getSettingDouble("windowHeight") > Screen.getPrimary().getVisualBounds().getHeight()) {
            setSetting("windowHeight", Screen.getPrimary().getVisualBounds().getHeight() - 500);
        }

        mainStage.setWidth(getSettingDouble("windowWidth"));
        mainStage.setHeight(getSettingDouble("windowHeight"));

        mainStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - mainStage.getWidth()) / 2);
        mainStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - mainStage.getHeight()) / 2);

        if (getSettingInt("windowMaximized") == 1) {
            mainStage.setMaximized(true);
        } else {
            mainStage.setMaximized(false);
        }

        if (getSettingInt("runBackground") == 0) {
            mainStage.show();
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            HSQL_Manager.init("jdbc:hsqldb:file:src/dbEnv/", "SA", "");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        launch(args);
    }

    public static FXMLLoader getLoader() {
        return JavaFXApplication4.loader;
    }

    public static Stage getStage() {
        return mainStage;
    }

    public static void hide() {
        mainStage.hide();
    }

    public static void show() {
        Platform.runLater(() -> {
            mainStage.setWidth(getSettingDouble("windowWidth"));
            mainStage.setHeight(getSettingDouble("windowHeight"));

            mainStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - mainStage.getWidth()) / 2);
            mainStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - mainStage.getHeight()) / 2);

            mainStage.show();
            mainStage.toFront();
        });

    }

    private int currentScreenIndex() {
        double scX = mainStage.getX();
        int number = 0;

        for (Screen src : Screen.getScreens()) {
            if (scX < src.getVisualBounds().getMaxX()) {
                break;
            } else {
                number++;
            }
        }
        return number;
    }
}
