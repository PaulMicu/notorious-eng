package com.cbms.ui.controller;

import com.cbms.app.ModelController;
import com.cbms.app.item.Asset;
import com.cbms.app.item.AssetInfo;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class SystemsController implements Initializable {

    @FXML
    private Button systemMenuButton;
    @FXML
    private Button addSystemButton;
    @FXML
    private FlowPane systemsThumbPane;
    @FXML
    private FlowPane systemsListPane;
    @FXML
    private Tab thumbnailTab;
    @FXML
    private Tab listTab;

    private ObservableList<Pane> boxes = FXCollections.observableArrayList();
    private UIUtilities uiUtilities;
    private ObservableList<Asset> systems;

    public SystemsController() {

    }

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
        uiUtilities = new UIUtilities();

        try {
            systems = FXCollections.observableArrayList(ModelController.getInstance().estimate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        linkButtons();
        generateThumbnails();
    }

    public void linkButtons() {
        thumbnailTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                systemsThumbPane.getChildren().clear();
                generateThumbnails();
            }
        });

        listTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                systemsListPane.getChildren().clear();
                generateList();
            }
        });

        //Attach link to systemMenuButton to go to Systems.fxml
        systemMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });

        //Attach link to addSystemButton to go to AddSystem.fxml
        addSystemButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/AddSystem");
            }
        });
    }

    /**
     * Creates elements that are in the scene so the data can be displayed.
     *
     * @author Jeff
     */
    public void generateThumbnails() {
        for (Asset system: systems) {
            Pane pane = new Pane();

            //When clicked on a system, open SystemInfo.FXML for that system.
            pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Stage primaryStage = (Stage) pane.getScene().getWindow();
                    try {

                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/SystemInfo.fxml"));
                        Parent systemsParent = loader.load();
                        Scene systemInfo = new Scene(systemsParent);

                        Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                        window.setScene(systemInfo);
                        SystemInfoController controller = loader.getController();
                        controller.initData(system);
                        window.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            pane.getStyleClass().add("systemPane");
            Text systemName = new Text(system.getSerialNo());
            Text systemType = new Text(system.getAssetTypeID());
            Text linearLabel = new Text("Linear Regression RUL:");
            Text linearRUL = new Text(String.valueOf(new DecimalFormat("#.##").format(system.getAssetInfo().getRULMeasurement())));
            //Text lstmLabel = new Text("LSTM RUL:");
            //Text lstmRUL = new Text(String.valueOf(system.getLstmRUL()));

            systemName.setId("systemName");
            systemType.setId("systemType");
            linearLabel.setId("linearLabel");
            linearRUL.setId("linearRUL");
            //lstmLabel.setId(("lstmLabel"));
            //lstmRUL.setId("lstmRUL");

            systemName.setLayoutX(14.0);
            systemName.setLayoutY(28.0);
            systemType.setLayoutX(14.0);
            systemType.setLayoutY(60.0);
            linearLabel.setLayoutX(14.0);
            linearLabel.setLayoutY(121.0);
            linearRUL.setLayoutX(230.0);
            linearRUL.setLayoutY(120.0);
            //lstmLabel.setLayoutX(14.0);
            //lstmLabel.setLayoutY(190.0);
            //lstmRUL.setLayoutX(250.0);
            //lstmRUL.setLayoutY(190.0);

            pane.getChildren().add(systemName);
            pane.getChildren().add(systemType);
            pane.getChildren().add(linearLabel);
            pane.getChildren().add(linearRUL);
            //pane.getChildren().add(lstmLabel);
            //pane.getChildren().add(lstmRUL);

            boxes.add(pane);
        }

        systemsThumbPane.getChildren().addAll(boxes);
    }

    public void generateList() {
        TableView table = new TableView();
        TableColumn systemTypeCol = new TableColumn("Type");
        systemTypeCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("assetTypeID"));
        TableColumn serialNoCol = new TableColumn("Serial No.");
        serialNoCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("serialNo"));
        TableColumn<Asset, Double> linearRULCol = new TableColumn<>("Linear RUL");
        linearRULCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                Double.parseDouble(new DecimalFormat("#.##").format(cellData.getValue().getAssetInfo().getRULMeasurement()))).asObject());
        TableColumn locationCol = new TableColumn("Location");
        locationCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("location"));

        table.setItems(systems);
        table.getColumns().addAll(systemTypeCol, serialNoCol, linearRULCol, locationCol);
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10,0,0,10));
        vbox.getChildren().addAll(table);
        systemsListPane.getChildren().addAll(vbox);
    }
}
