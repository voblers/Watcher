/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import Services.ChartUpdateService;
import Services.HSQL_Manager;
import Watcher.FXMLDocumentController;
import Watcher.JavaFXApplication4;
import dao.Site;
import dao.StatisticsObj;
import dao.detailsObject;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author BB3605
 */
public final class processData {

    private ObservableList<PieChart.Data> pieChartData = null;
    private ObservableList<detailsObject> detailsData = null;
    private PieChart pieChart = null;
    private TableView details = null;

    private ChartUpdateService updateService;

    public processData(PieChart pieChart, TableView details) {
        this.pieChartData = FXCollections.observableArrayList();
        this.detailsData = FXCollections.observableArrayList();

        this.pieChart = pieChart;
        this.pieChart.setData(pieChartData);

        this.details = details;

        this.details.focusedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) {
                updateService.stop();
            } else {
                updateService.start();
            }
        });

        TableColumn urlCol = new TableColumn("URL");
        urlCol.prefWidthProperty().bind(this.details.widthProperty().divide(3));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("URL"));

        TableColumn timestampCol = new TableColumn("Timestamp");
        timestampCol.prefWidthProperty().bind(this.details.widthProperty().divide(3));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        TableColumn codeCol = new TableColumn("Response code");
        codeCol.prefWidthProperty().bind(this.details.widthProperty().divide(3));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        this.details.setRowFactory(tv -> new TableRow<detailsObject>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem(detailsObject det, boolean empty) {
                super.updateItem(det, empty);
                if (det == null) {
                    setTooltip(null);
                    setStyle("");
                } else {
                    tooltip.setText("Description: " + det.getCodeDet());
                    setTooltip(tooltip);

                    if (det.getCode() == 200) {
                        setStyle("-fx-background-color: lightgreen;");
                    } else {
                        setStyle("-fx-background-color: red;");
                    }
                }
            }
        });

        this.details.setItems(detailsData);
        this.details.getColumns().addAll(urlCol, timestampCol, codeCol);

        FXMLDocumentController control = JavaFXApplication4.getLoader().getController();
        renew(control.getSelectedItem());
        Runnable a = updateService = new ChartUpdateService(10);
        a.run();
    }

    public void renew(String site) {
        renewChart(site);
        renewDetails(site);
    }

    private void renewChart(String site) {

        if (pieChartData != null) {
            String status = "Unknown";
            ArrayList<StatisticsObj> statisList = HSQL_Manager.getStatistics(new Site(site));
            pieChartData.clear();

            for (StatisticsObj a : statisList) {
                switch (a.site.getStatus()) {
                    case Const.online:
                        status = "Online";
                        break;
                    case Const.offline:
                        status = "Offline";
                        break;
                    case Const.redirecting:
                        status = "Redirecting";
                        break;
                    case Const.unknown:
                        status = "Unknown";
                        break;
                    default:
                        status = "Offline";
                        break;
                }
                PieChart.Data data = new PieChart.Data(status, a.count);
                pieChartData.add(data);

                try {
                    data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent t) -> {
                        if (t.isPrimaryButtonDown()) {
                            FXMLDocumentController control = JavaFXApplication4.getLoader().getController();
                            control.showDetails(data.getName());

                            FilteredList<detailsObject> filteredData = new FilteredList<>(detailsData, p -> true);
                            filteredData.setPredicate((detailsObject t1) -> {
                                switch (data.getName().toUpperCase()) {
                                    case "ONLINE":
                                        return t1.getCode() == 200;
                                    default:
                                        return t1.getCode() != 200;
                                }
                            });

                            SortedList<detailsObject> sortedData = new SortedList<>(filteredData);
                            //sortedData.comparatorProperty().bind(details.comparatorProperty());

                            if (details != null) {
                                details.setItems(sortedData);
                            }
                        }
                    });
                } catch (NullPointerException ex) {
                }
            }
        }
    }

    private void renewDetails(String site) {
        if (detailsData != null) {
            ArrayList<detailsObject> data = HSQL_Manager.getDetails(site);

            detailsData.clear();
            data.stream().forEach((a) -> {
                detailsData.add(new detailsObject(a));
            });
        }
    }

    public void cancelFilter(String site) {
        if (detailsData != null) {
            ArrayList<detailsObject> data = HSQL_Manager.getDetails(site);

            detailsData.clear();

            for (detailsObject rec : data) {
                detailsData.add(rec);

                FXMLDocumentController control = JavaFXApplication4.getLoader().getController();
                FilteredList<detailsObject> filteredData = new FilteredList<>(detailsData, p -> true);
                filteredData.setPredicate((detailsObject t1) -> {
                    return true;
                });

                SortedList<detailsObject> sortedData = new SortedList<>(filteredData);
                //sortedData.comparatorProperty().bind(details.comparatorProperty());

                if (details != null) {
                    details.setItems(sortedData);
                }
            }
        }
    }
}
