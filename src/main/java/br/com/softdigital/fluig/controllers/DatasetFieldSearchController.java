package br.com.softdigital.fluig.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Dataset Field Search Controller
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0, 08/10/2020
 */
public class DatasetFieldSearchController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> viewFields;

    private List<String> fields;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    /**
     * Evento mouse clicked para o list view fields
     *
     * @param mouseEvent
     */
    public void fieldsMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() >= 2) {

            int indice = viewFields.getSelectionModel().getSelectedIndex();
            Object datasetName = viewFields.getItems().get(indice);

            searchField.setText(datasetName.toString());

            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Evento key released para pesquisar na lista de fields
     *
     * @param event
     */
    public void searchFieldKeyReleased(KeyEvent event) {
        find(searchField.getText());
    }

    /**
     * Procurar o campo na lista fields
     * 
     * @param value 
     */
    public void find(String value) {

        if (fields == null) {
            fields = viewFields.getItems();
        }

        if (value.isEmpty()) {
            this.viewFields.setItems(FXCollections.observableArrayList(fields));
        }
        else {
            List<String> datasetFields = new ArrayList<>();

            for (String datasetName : fields) {
                if (datasetName.toUpperCase().contains(value.toUpperCase())) {
                    datasetFields.add(datasetName);
                }
            }

            this.viewFields.setItems(FXCollections.observableArrayList(datasetFields));
        }
    }
    
    public void setSearchField(String searchField) {
        find(searchField);
        this.searchField.setText(searchField);
    }

    public String getSearchField() {
        return this.searchField.getText();
    }

    public void setViewFields(List<String> viewFields) {
        this.viewFields.setItems(FXCollections.observableArrayList(viewFields));
    }
}
