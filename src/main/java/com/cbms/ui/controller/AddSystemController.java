package com.cbms.ui.controller;

import com.cbms.source.local.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.sql.*;
import java.util.*;

import java.sql.ResultSet;

public class AddSystemController implements Initializable {

    @FXML
    public Button systemMenuBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private TextArea systemDescriptionTextArea;
    @FXML
    private ChoiceBox<String> systemTypeChoiceBox;

    private static final String GET_ASSET_TYPES = "SELECT * FROM asset_type";
    private UIUtilities uiUtilities;

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
        attachEvents();
        initializeFieldValues();
        systemDescriptionTextArea.setWrapText(true);
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
        // Change scenes to Systems.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
    }

    /**
     * Initializes the default and possible values for all fields that can accept user input. For example,
     * it establishes the possible dropdown values for the system type selection.
     */
    public void initializeFieldValues() {
        // Establishes the asset types available for selection in the dropdown
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPES);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                ObservableList<String> assetTypeNames = FXCollections.observableArrayList(rs.getString("name"));
                systemTypeChoiceBox.setItems(assetTypeNames);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return DatabaseConnection.start().getConnection();
    }
}
