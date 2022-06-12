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
 * Dataset Name Search Controller
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0, 04/10/2020
 */
public class DatasetNameSearchController implements Initializable {

    @FXML
    private TextField searchDatasetName;

    @FXML
    private ListView<Object> viewDatasetName;

    private List<Object> datasetNameAux;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    /**
     * Evento mouse clicked para o list view dataset name
     *
     * @param mouseEvent
     */
    public void datasetNameMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() >= 2) {

            int indice = viewDatasetName.getSelectionModel().getSelectedIndex();
            Object datasetName = viewDatasetName.getItems().get(indice);

            searchDatasetName.setText(datasetName.toString());

            Stage stage = (Stage) searchDatasetName.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Evento key released para pesquisar na lista de dataset
     *
     * @param event
     */
    public void searchDatasetKeyReleased(KeyEvent event) {
        find(searchDatasetName.getText());
    }

    /**
     * Procurar o dataset na lista datasetNameAux
     * 
     * @param value 
     */
    public void find(String value) {

        if (datasetNameAux == null) {
            datasetNameAux = viewDatasetName.getItems();
        }

        if (value.isEmpty()) {
            this.viewDatasetName.setItems(FXCollections.observableArrayList(datasetNameAux));
        }
        else {
            List<Object> datasetsName = new ArrayList<>();

            for (Object datasetName : datasetNameAux) {
                if (datasetName.toString().toUpperCase().contains(value.toUpperCase())) {
                    datasetsName.add(datasetName);
                }
            }

            this.viewDatasetName.setItems(FXCollections.observableArrayList(datasetsName));
        }
    }
    
    public void setSearchDatasetNome(String searchDatasetNome) {
        find(searchDatasetNome);
        this.searchDatasetName.setText(searchDatasetNome);
    }

    public String getSearchDatasetNome() {
        return this.searchDatasetName.getText();
    }

    public void setDatasetsName(List<Object> datasetsName) {
        this.viewDatasetName.setItems(FXCollections.observableArrayList(datasetsName));
    }
}
