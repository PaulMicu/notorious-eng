package com.cbms.ui.controller;

import com.cbms.app.item.Asset;
import com.cbms.app.item.AssetAttribute;
import com.cbms.rul.assessment.AssessmentController;
import com.cbms.source.local.AssetDAOImpl;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class SystemInfoController implements Initializable {
    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Text systemName;
    @FXML
    private Text systemType;
    @FXML
    private Text serialNumber;
    @FXML
    private Text manufacturer;
    @FXML
    private Text systemLocation;
    @FXML
    private Text linearRUL;
    @FXML
    private Text lstmRUL;
    @FXML
    private FlowPane sensorFlowPane;

    private Asset system;
    private AssetDAOImpl assetDAOImpl;
    private UIUtilities uiUtilities;

    // UI String constants
    private final String MANUFACTURER = "";
    private final String LOCATION = "Location: ";
    private final String LINEAR_RUL = "Linear RUL: ";
    private final String DESCRIPTION = "Description: ";
    private final String CYCLE = "Cycle";
    private final String SENSOR_VALUES = "Sensor Values";
    private final String ALERT_TITLE = "Confirmation Dialog";
    private final String ALERT_HEADER = "Confirmation of system deletion";
    private final String ALERT_CONTENT = "Are you sure you want to delete this system?";

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url
     * @param resourceBundle
     *
     * @author Jeff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assetDAOImpl = new AssetDAOImpl();
        uiUtilities = new UIUtilities();
        attachEvents();
    }

    /**
     * initData receives the Engine data that was selected from System.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param system
     *
     * @author Jeff
     */
    void initData(Asset system) {
        this.system = system;
        systemName.setText(system.getName() + " " + system.getAssetTypeName() + " - " + system.getSerialNo());
        systemType.setText(system.getAssetTypeID());
        serialNumber.setText(system.getSerialNo());
        manufacturer.setText(MANUFACTURER);
        systemLocation.setText(LOCATION);
        linearRUL.setText(LINEAR_RUL + new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(system.getId())));
        lstmRUL.setText(DESCRIPTION);
        constructSensorPanes();
    }

    /**
     * Constructs the sensor panes to be able to display data in a nice format.
     *
     * @author Jeff
     */
    public void constructSensorPanes() {
        for (AssetAttribute sensor: system.getAssetInfo().getAssetAttributes()) {
            Pane pane = new Pane();
            pane.getStyleClass().add("sensorPane");
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel(CYCLE);
            final LineChart<Number, Number> sensorChart =
                    new LineChart<>(xAxis, yAxis);
            sensorChart.setTitle(SENSOR_VALUES);
            XYChart.Series series = new XYChart.Series();
            Map<Integer, Double> measurements = sensor.getMeasurements();
            series.getData().add(new XYChart.Data(1, measurements.get(1)));
            series.getData().add(new XYChart.Data(2, measurements.get(2)));
            series.getData().add(new XYChart.Data(3, measurements.get(3)));
            series.getData().add(new XYChart.Data(4, measurements.get(4)));
            series.getData().add(new XYChart.Data(5, measurements.get(5)));
            sensorChart.getData().add(series);
            sensorChart.setPrefWidth(275.0);
            sensorChart.setPrefHeight(163.0);
            sensorChart.setLayoutX(12.0);
            sensorChart.setLayoutY(12.0);
            pane.getChildren().add(sensorChart);
            Text sensorName = new Text(sensor.getName());
            sensorName.setId("sensorType");
            sensorName.setLayoutX(35.0);
            sensorName.setLayoutY(191.0);
            pane.getChildren().add(sensorName);

            sensorFlowPane.getChildren().add(pane);
        }
    }


    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        systemMenuBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });

        deleteBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                deleteDialog(mouseEvent);
            }
        });
    }

    /**
     * Send the asset ID to the Database class in order for it to be deleted.
     *
     * @author Jeff
     */
    public void deleteAsset() {
        assetDAOImpl.deleteAssetByID(system.getId());
    }

    /**
     * Creates a dialog box that asks user if they want to delete an asset.
     *
     * @param mouseEvent
     */
    void deleteDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(ALERT_TITLE);
        alert.setHeaderText(ALERT_HEADER);
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            deleteAsset();
            uiUtilities.changeScene(mouseEvent, "/Systems");
        }
    }
}
