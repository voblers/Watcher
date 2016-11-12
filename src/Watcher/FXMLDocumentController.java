 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Watcher;

import Data.Const;
import Data.processData;
import ErrorHandling.InternetWatcherServiceAlreadyStartedException;
import ErrorHandling.SiteAlreadyAddedException;
import ErrorHandling.StatusUpdaterServiceAlreadyStartedException;
import ErrorHandling.WatchDogNotStartedException;
import ErrorHandling.WatchDogTimerServiceAlreadyStartedException;
import ErrorHandling.errDialog;
import Listeners.listViewChangeListener;
import Services.HSQL_Manager;
import Services.InternetWatcher;
import Services.ServiceHandler;
import Services.StatusUpdater;
import Notifications.notificationService;
import Services.GlobalServiceControlVariables;
import dao.Site;
import dao.WatchObj;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.commons.validator.UrlValidator;

/**
 *
 * @author BB3605
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ListView siteList;

    private ObservableList<String> items = FXCollections.observableArrayList();
    private processData processor = null;

    @FXML
    private PieChart uptime;

    @FXML
    private TextField siteUrl;

    @FXML
    private ToggleButton powerSwitch;

    @FXML
    private BorderPane SuspendPane;

    @FXML
    private BorderPane addSitePane;

    @FXML
    private Pane fadeOutPane;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView detailedData;

    @FXML
    private Accordion sections;

    @FXML
    private TitledPane detailsPane;

    @FXML
    private TitledPane chartPane;

    @FXML
    private Button removeFilter;

    public FXMLDocumentController() {
    }

    @FXML
    protected void addSite(ActionEvent event) {
        if (powerSwitch.isSelected()) {
            addSitePane.setVisible(true);
            RotateTransition rt = new RotateTransition(Duration.millis(200), addSitePane);
            rt.setFromAngle(180);
            rt.setToAngle(0);
            rt.setCycleCount(1);
            rt.setInterpolator(Interpolator.EASE_IN);
            fadeout(true);
            rt.play();
        } else {
            new errDialog().showError(new WatchDogNotStartedException().toString());
        }
    }

    @FXML
    protected void startWatchDog(ActionEvent event) {
        if (powerSwitch.isSelected()) {
            watcherManager.start();
            changeStatusLabel("DETECTING...");
            changeStatusPaneStyle(Const.detectingStyle);
        } else {
            watcherManager.stop();
            changeStatusLabel("UNKNOWN");
            changeStatusPaneStyle(Const.unknownStyle);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ServiceHandler.start(new notificationService());
        
        try {
            HSQL_Manager.init("jdbc:hsqldb:file:src/dbEnv/", "SA", "");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        int count = 0;
        for (Site a : HSQL_Manager.getSites()) {
            items.add(a.getAddress());

            try {
                //sites.add(new Site(siteList.getItems().get(i).toString()));
                watcherManager.addSite(a);
            } catch (WatchDogNotStartedException | SiteAlreadyAddedException ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                new errDialog().showError(sw.toString());
                break;
            }
            count++;
        }

        MenuItem deleteItem = new MenuItem("Delete");
        final ContextMenu contextMenu = new ContextMenu(deleteItem);
        contextMenu.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                removeItem(new Site(siteList.getSelectionModel().getSelectedItem().toString()));
            }
        });
        siteList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        //textProperty().unbind();
                        setContextMenu(null);
                        if (!empty && item != null) {
                            setContextMenu(contextMenu);
                            setText(item);
                        }
                        if(item!=null){}
                        else{
                            setGraphic(null);
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });

        siteList.getSelectionModel().selectedItemProperty().addListener(new listViewChangeListener());
        siteList.setItems(items);
        siteList.autosize();

        if (count > 0) {
            siteList.getSelectionModel().select(0);
        }
        uptime.setAnimated(false);
        //uptime.setStyle(null);
        processor = new processData(uptime, detailedData);

        try {
            ServiceHandler.startService(new InternetWatcher("http://www.google.com", 3));
            ServiceHandler.startService(new StatusUpdater(1, (FXMLDocumentController) JavaFXApplication4.getLoader().getController()));
        } catch (InternetWatcherServiceAlreadyStartedException | WatchDogTimerServiceAlreadyStartedException | StatusUpdaterServiceAlreadyStartedException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            new errDialog().showError(sw.toString());
        }

        removeFilter.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if (t.isPrimaryButtonDown()) {
                    processor.cancelFilter(getSelectedItem());
                }
            }
        });
    }

    public void isInternet(boolean inpB) {
        if (inpB) {
            if(powerSwitch.isSelected()){
                watcherManager.start();
            }
            SuspendPane.setVisible(false);
        } else if (!inpB && powerSwitch.isSelected()){
            if(!GlobalServiceControlVariables.InternetWatcher){
                if(powerSwitch.isSelected()){
                    watcherManager.stop();
                }
                SuspendPane.setVisible(true);
                ServiceHandler.getNotificationService().showNotification("Internet connection lost", Const.notificationError, 0);
            }
        }
    }

    public synchronized void updateStatus(WatchObj obj) {
        switch (obj.getStatus()) {
            case Const.online:
                changeStatusLabel("ONLINE");
                changeStatusPaneStyle(Const.onlineStyle);
                break;
            case Const.offline:
                changeStatusLabel("OFFLINE");
                changeStatusPaneStyle(Const.offlineStyle);
                break;
            case Const.redirecting:
                changeStatusLabel("REDIRECTING");
                changeStatusPaneStyle(Const.redirectStyle);
                break;
            case Const.unknown:
                changeStatusLabel("UNKNOWN");
                changeStatusPaneStyle(Const.unknownStyle);
                break;
            default:
                changeStatusLabel("OFFLINE");
                changeStatusPaneStyle(Const.offlineStyle);
                break;
        }
    }

    public synchronized void setChartAnimation(boolean animation) {
        //uptime.setAnimated(animation);
    }

    public synchronized void changeStatusLabel(String inpString) {
        statusLabel.setText(inpString);
    }

    public synchronized void changeStatusPaneStyle(String inpString) {
        statusLabel.setStyle(inpString);
    }

    public synchronized String getSelectedItem() {
        return siteList.getSelectionModel().getSelectedItem().toString();
    }

    public void shuttingDown() {
        HSQL_Manager.exit();
    }

    @FXML
    protected void doAdd(MouseEvent event) {
        UrlValidator urlValidator;
        urlValidator = new UrlValidator();
        if (urlValidator.isValid(siteUrl.getText())) {
            siteUrl.setBlendMode(null);
            addItem(new Site(siteUrl.getText()));
            cancelSiteAdd(event);
        } else {
            siteUrl.setBlendMode(BlendMode.RED);
        }
    }

    @FXML
    protected void cancelSiteAdd(MouseEvent event) {
        RotateTransition rt = new RotateTransition(Duration.millis(200), addSitePane);
        rt.setFromAngle(0);
        rt.setToAngle(180);
        rt.setCycleCount(1);
        rt.setInterpolator(Interpolator.EASE_OUT);
        rt.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                addSitePane.setVisible(false);
                fadeout(false);
            }
        });

        rt.play();
    }

    @FXML
    protected void clear(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("Deleting statistics");
            alert.setContentText("Are you sure you want to delete statistics? It's not reversable!");
            alert.initOwner(JavaFXApplication4.getStage());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (HSQL_Manager.clearStat(getSelectedItem())) {
                    processor.renew(getSelectedItem());
                }
            }
        }
    }

    private void fadeout(boolean inpB) {
        if (inpB) {
            javafx.animation.FadeTransition ft = new FadeTransition(Duration.millis(200), fadeOutPane);
            ft.setCycleCount(1);
            ft.setFromValue(0);
            ft.setToValue(0.7);
            fadeOutPane.setVisible(true);
            ft.play();
        } else {
            javafx.animation.FadeTransition ft = new FadeTransition(Duration.millis(200), fadeOutPane);
            ft.setCycleCount(1);
            ft.setFromValue(0.7);
            ft.setToValue(0);
            ft.play();
            fadeOutPane.setVisible(false);
        }
    }

    private void addItem(Site inpSite) {
        boolean flag = HSQL_Manager.addSite(inpSite);

        if (flag) {
            items.add(siteUrl.getText());
            siteList.getSelectionModel().select(siteUrl.getText());
            try {
                watcherManager.addSite(new Site(inpSite));
            } catch (WatchDogNotStartedException | SiteAlreadyAddedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void removeItem(Site inpSite) {
        boolean flag = HSQL_Manager.removeSite(inpSite);
        if (flag) {
            items.remove(inpSite.getAddress());
            watcherManager.removeSite(inpSite);
        }
    }

    public void showDetails(String status) {
        sections.setExpandedPane(detailsPane);
        detailsPane.requestFocus();
    }

    public synchronized processData getProcessor() {
        return processor;
    }
    
    public synchronized void showSelectedSite(String site){
        System.out.println("Should go to site: " + site);
    }
}
