/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ErrorHandling;

import Data.Const;
import Watcher.FXMLExceptionController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author BB3605
 */
public class errDialog {

    public void showError(String exc) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Const.exceptionPath));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(errDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Exception!");
        stage.setScene(new Scene(root1));
        
        FXMLExceptionController expControl = fxmlLoader.getController();
        expControl.setException(exc);
        
        stage.showAndWait();
    }
}
