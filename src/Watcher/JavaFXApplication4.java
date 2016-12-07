/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Watcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author BB3605
 */
public class JavaFXApplication4 extends Application {
    
    private static FXMLLoader loader;
    private static Stage mainStage;
    
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
            if(t1.booleanValue()){
                hide();
            }
        });
        mainStage.widthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            System.out.println("Old width value: " + t + "; new value: " + t1);
        });
        mainStage.heightProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            System.out.println("Old height value: " + t + "; new value: " + t1);
        });
        mainStage.show();
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
        launch(args);
    }
    
    public static FXMLLoader getLoader(){
        return JavaFXApplication4.loader;
    }
    
    public static Stage getStage(){
        return mainStage;
    }
    
    public static void hide(){
        mainStage.hide();
    }
    
    public static void show(){
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                mainStage.show();
            }
        });
        
    }
}
