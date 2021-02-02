import app.item.Asset;
import app.item.AssetAttribute;
import app.item.Measurement;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import local.AssetDAOImpl;
import local.AssetTypeDAOImpl;
import local.AttributeDAOImpl;
import local.ModelDAOImpl;
import rul.assessment.AssessmentController;

import java.net.URL;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class SystemInfoController implements Initializable {

    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button systemTypeMenuBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Text systemName;
    @FXML
    private Text systemNameOutput;
    @FXML
    private FlowPane sensorFlowPane;
    @FXML
    private Text systemTypeOutput;
    @FXML
    private Text serialNumberOutput;
    @FXML
    private Text manufacturerOutput;
    @FXML
    private Text locationOutput;
    @FXML
    private Text siteOutput;
    @FXML
    private Text modelOutput;
    @FXML
    private Text rulOutput;
    @FXML
    private Text recommendationOutput;
    @FXML
    private Text categoryOutput;
    @FXML
    private Text descriptionOutput;
    @FXML
    private Tab rawDataTab;
    @FXML
    private AnchorPane rawDataListPane;

    private Asset system;
    private AssetDAOImpl assetDAOImpl;
    private AssetTypeDAOImpl assetTypeDAOImpl;
    private AttributeDAOImpl attributeDAOImpl;
    private ModelDAOImpl modelDAO;
    private UIUtilities uiUtilities;

    private Timeline rawDataTimeline;

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url            url to be used
     * @param resourceBundle url to be used
     * @author Jeff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assetDAOImpl = new AssetDAOImpl();
        assetTypeDAOImpl = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();
        attributeDAOImpl = new AttributeDAOImpl();
        uiUtilities = new UIUtilities();
        attachEvents();
    }

    /**
     * initData receives the Engine data that was selected from System.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param system is an asset object that will get initialized
     * @author Jeff
     */
    void initData(Asset system) {
        this.system = system;
        String systemTypeName = assetTypeDAOImpl.getNameFromID(system.getAssetTypeID());
        systemName.setText(systemTypeName + " - " + system.getSerialNo());
        systemNameOutput.setText(system.getName());
        systemTypeOutput.setText(systemTypeName);
        serialNumberOutput.setText(system.getSerialNo());
        manufacturerOutput.setText(system.getManufacturer());
        locationOutput.setText(system.getLocation());
        siteOutput.setText(system.getSite());
        modelOutput.setText(modelDAO.getModelNameFromModelID(modelDAO.getModelsByAssetTypeID(system.getAssetTypeID()).getModelID()));
        categoryOutput.setText(system.getCategory());

        rulOutput.setText(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(system.getId())));
        recommendationOutput.setText(system.getRecommendation());

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> rulOutput.setText(String.valueOf(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(system.getId()))))));

        timeline.setCycleCount(Animation.INDEFINITE); // loop forever
        timeline.play();


        descriptionOutput.setText(system.getDescription());
        constructSensorPanes();
    }

    /**
     * Constructs the sensor panes to be able to display data in a nice format.
     *
     * @author Jeff
     */
    public void constructSensorPanes() {
        for (AssetAttribute sensor : system.getAssetInfo().getAssetAttributes()) {
            Pane pane = new Pane();
            pane.getStyleClass().add("sensorPane");
            final CategoryAxis xAxis = new CategoryAxis();
            final CategoryAxis yAxis = new CategoryAxis();
            // UI String constants
            String CYCLE = "Cycle";
            xAxis.setLabel(CYCLE);
            xAxis.setAnimated(false);
            final LineChart<String, String> sensorChart =
                    new LineChart<>(xAxis, yAxis);
            String SENSOR_VALUES = "Sensor Values";
            sensorChart.setTitle(SENSOR_VALUES);
            XYChart.Series<String, String> series = new XYChart.Series<>();
            sensorChart.setAnimated(false);

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
                ArrayList<Measurement> measurements = attributeDAOImpl.getLastXMeasurementsByAssetIDAndAttributeID(Integer.toString(system.getId()), Integer.toString(sensor.getId()), 5);
                if (!sensorChart.getXAxis().isValueOnAxis(Integer.toString(measurements.get(0).getTime())))
                    series.getData().add(new XYChart.Data<>(Integer.toString(measurements.get(0).getTime()), Double.toString(measurements.get(0).getValue())));
                else if (series.getData().size() != measurements.size()) {
                    for (int i = measurements.size() - 1; i >= 0; i--) {
                        series.getData().add(new XYChart.Data<>(Integer.toString(measurements.get(i).getTime()), Double.toString(measurements.get(i).getValue())));
                    }
                }
                if (series.getData().size() > 5)
                    series.getData().remove(0);
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            sensorChart.getData().add(series);
            sensorChart.setPrefWidth(275.0);
            sensorChart.setPrefHeight(163.0);
            sensorChart.setLayoutX(12.0);
            sensorChart.setLayoutY(50.0);
            pane.getChildren().add(sensorChart);
            Text sensorName = new Text(sensor.getName());
            sensorName.setId("sensorType");
            sensorName.setLayoutX(35.0);
            sensorName.setLayoutY(30.0);
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
        systemMenuBtn.setOnMouseClicked(mouseEvent -> {
            rawDataTimeline.stop();
            uiUtilities.changeScene(mouseEvent, "/Systems");
        });
        //Attach link to systemTypeMenuBtn to go to SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeList"));
        deleteBtn.setOnMouseClicked(this::deleteDialog);

        rawDataTab.setOnSelectionChanged(event ->  {
                rawDataListPane.getChildren().clear();
                generateRawDataTable();
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
     * @param mouseEvent is an event trigger for this delete dialog
     */
    void deleteDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        String ALERT_TITLE = "Confirmation Dialog";
        alert.setTitle(ALERT_TITLE);
        String ALERT_HEADER = "Confirmation of system deletion";
        alert.setHeaderText(ALERT_HEADER);
        String ALERT_CONTENT = "Are you sure you want to delete this system?";
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteAsset();
            uiUtilities.changeScene(mouseEvent, "/Systems");
        }
    }

    public void generateRawDataTable() {
        TableView<ObservableList> tableview = new TableView<>();
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        AtomicInteger lastCycle = new AtomicInteger();
        ResultSet resultSet = assetDAOImpl.createMeasurementsFromAssetIdAndTime(system.getId(), lastCycle.get());
        try {
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn<ObservableList, String> col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                tableview.getColumns().addAll(col);
            }

            updateRawTableView(data,lastCycle);
            if (rawDataTimeline == null)
                rawDataTimeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> updateRawTableView(data,lastCycle)));

            rawDataTimeline.setCycleCount(Animation.INDEFINITE); // loop forever
            rawDataTimeline.play();


            tableview.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }


        tableview.setId("RawDataTable");
        AnchorPane.setBottomAnchor(tableview, 0.0);
        AnchorPane.setTopAnchor(tableview, 5.0);
        AnchorPane.setRightAnchor(tableview, 0.0);
        AnchorPane.setLeftAnchor(tableview, 0.0);
        rawDataListPane.getChildren().addAll(tableview);
    }

    private void updateRawTableView(ObservableList<ObservableList> data, AtomicInteger lastCycle ){
        ResultSet rs = assetDAOImpl.createMeasurementsFromAssetIdAndTime(system.getId(), lastCycle.get());
        try {
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(0, row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error on Building Data");
        }
        lastCycle.set(data.size());
    }
}
