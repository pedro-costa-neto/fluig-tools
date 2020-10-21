package br.com.softdigital.fluig.controllers;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.totvs.technology.ecm.dataservice.ws.SearchConstraintDto;
import com.totvs.technology.ecm.dataservice.ws.SearchConstraintDtoArray;
import com.totvs.technology.ecm.dataservice.ws.StringArray;
import com.totvs.technology.ecm.dataservice.ws.ValuesDto;

import br.com.softdigital.fluig.domains.Credential;
import br.com.softdigital.fluig.services.CredentialService;
import br.com.softdigital.fluig.dto.DatasetSimpleDto;
import br.com.softdigital.fluig.services.DatasetWsService;
import br.com.softdigital.fluig.services.exception.ExceptionDialog;
import com.opencsv.CSVWriterBuilder;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Dataset Controller
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0, 06/06/2020
 */
public class DatasetController implements Initializable {

    private final static Logger LOG = Logger.getLogger(DatasetController.class);

    @FXML
    private TextField tfConstraintCampo;

    @FXML
    private TextField tfConstraintInicial;

    @FXML
    private TextField tfConstraintFinal;

    @FXML
    private TextField tfDatasetNome;

    @FXML
    private CheckBox cbContraintLike;

    @FXML
    private ComboBox<String> cbConstraintTipo;

    @FXML
    private ComboBox<Credential> cbServidor;

    @FXML
    private TableView<ValuesDto> tvDados;

    @FXML
    private TableView<SearchConstraintDto> tvContraints;

    @FXML
    private ListView<String> lvCampos;

    @FXML
    private ListView<String> lvCamposSelecionados;

    @FXML
    private ListView<String> lvSortCampos;

    @FXML
    private ListView<String> lvSortCamposSelecionados;

    private CredentialService credentialService = new CredentialService();
    private DatasetWsService datasetWsService = new DatasetWsService();
    private SearchConstraintDtoArray contraints = new SearchConstraintDtoArray();
    private StringArray fields = new StringArray();
    private StringArray sortFields = new StringArray();
    private List<String> datasetFields = new ArrayList<>();

    private boolean isUpdateData = false;
    private boolean isUpdateDatasetName = false;
    private boolean isShowSearchDataset = false;
    private boolean isShowConstraintFieldSearch = false;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        try {
            cbServidor.setItems(credentialService.getAllCredential());
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DatasetController.class.getName()).log(Level.SEVERE, null, ex);
        }

        cbConstraintTipo.setItems(datasetWsService.getSearchsType());
        cbConstraintTipo.setValue("MUST");

        addContraint("sqlLimit", "100", "100", false, "MUST");

        String[][] collumns = {
            {"Campo", "fieldName"},
            {"Valor Inicial", "initialValue"},
            {"Valor Final", "finalValue"},
            {"Tipo", "contraintType"},
            {"Like", "likeSearch"}
        };

        ObservableList<TableColumn<SearchConstraintDto, String>> collumnList = FXCollections.observableArrayList();
        for (String[] column : collumns) {
            TableColumn<SearchConstraintDto, String> tableColumn = new TableColumn<>(column[0]);
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(column[1]));
            collumnList.add(tableColumn);
        }

        tvContraints.getColumns().addAll(collumnList);
    }

    public void showSearchDatasetAction() {
        try {

            if (isShowSearchDataset) {
                throw new ExceptionDialog("A janela de busca ja esta aberta", "Mais detalhes");
            }

            isShowSearchDataset = true;

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/layouts/FrmDatasetNameSearch.fxml"));
            Parent root = loader.load();
            DatasetNameSearchController controller = loader.getController();

            if (cbServidor.getValue() == null) {
                LOG.error("Buscar datasets sem informar o servidor;");
                throw new ExceptionDialog(
                        "Folha ao buscar os datasets",
                        "Por favor, informar um servidor para buscar os datasets."
                );
            }

            List<Object> datasetsName = datasetWsService.getDatasets(cbServidor.getValue());
            controller.setDatasetsName(datasetsName);

            if (!tfDatasetNome.getText().isEmpty()) {
                controller.setSearchDatasetNome(tfDatasetNome.getText());
            }

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Buscar Dataset");
            stage.getIcons().add(new Image("/images/fluig-icon-brand.png"));
            stage.setScene(scene);
            stage.showAndWait();

            String datasetName = controller.getSearchDatasetNome();

            if (!datasetName.isEmpty()) {
                tfDatasetNome.setText(datasetName);
                updateDatasetInformationAction();
            }
        } catch (IOException ex) {
            LOG.error("Falha ao abrir a tela >>> " + ex.toString());
            new ExceptionDialog(
                    "Folha ao abrir a tela",
                    ex.getMessage()
            ).showMessage();
        } catch (ExceptionDialog ex) {
            ex.showMessage();
        } finally {
            isShowSearchDataset = false;
            isUpdateDatasetName = true;
        }
    }

    /**
     * Metodo que realiza a atualizacao de dados do dataset nos grids da tela.
     */
    public void updateDatasetInformationAction() {
        try {
            if (cbServidor.getValue() == null) {
                LOG.info("Atualizacao da TableView \"Dados\" sem informar o servidor;");
                throw new ExceptionDialog(
                        "Folha ao atualizar os dados",
                        "Por favor, informar um servidor para consultar os dados."
                );
            }

            DatasetSimpleDto datasetSimpleDto = datasetWsService.getDataset(
                    cbServidor.getValue(),
                    tfDatasetNome.getText(),
                    fields,
                    contraints,
                    sortFields
            );

            if (isUpdateDatasetName || lvCampos.getItems().isEmpty()) {
                lvCampos.setItems(datasetSimpleDto.getObservableColumns());
            }

            if (isUpdateDatasetName || lvSortCampos.getItems().isEmpty()) {
                lvSortCampos.setItems(datasetSimpleDto.getObservableColumns());
            }

            if (isUpdateDatasetName || datasetFields.isEmpty()) {
                datasetFields = datasetSimpleDto.getColumns();
            }

            ObservableList<TableColumn<ValuesDto, String>> columns = FXCollections.observableArrayList();
            for (Integer index = 0; index < datasetSimpleDto.getColumns().size(); index++) {
                TableColumn<ValuesDto, String> column;
                column = new TableColumn<>(datasetSimpleDto.getColumns().get(index));
                column.setId(index.toString());
                column.setCellValueFactory(cellValue -> {
                    TableColumn<ValuesDto, String> cellColumn = cellValue.getTableColumn();
                    ValuesDto valuesDto = cellValue.getValue();

                    String value = "";
                    if (valuesDto != null && valuesDto.getValue().size() > 0) {
                        int indexColumn = Integer.parseInt(cellColumn.getId());
                        Object obj = valuesDto.getValue().get(indexColumn);
                        value = obj != null ? obj.toString() : "";
                    }

                    return new SimpleStringProperty(value);
                });
                columns.add(column);
            }
            tvDados.getColumns().setAll(columns);
            tvDados.setItems(datasetSimpleDto.getObservableItems());

        } catch (ExceptionDialog ex) {
            ex.showMessage();
        } finally {
            isUpdateDatasetName = false;
        }
    }

    //==========================================================================
    // METODOS REFERENTE A ABA DADOS (VIEW)
    //==========================================================================
    /**
     * Metodo para utilizacao do evento selection changed na aba dados (View)
     *
     * @param event
     */
    public void viewSelectionChanged(Event event) {
        if (isUpdateData) {
            isUpdateData = false;
            updateDatasetInformationAction();
        }
    }

    /**
     * Recupera as informações da tabela "dados" e gera um arquivo CSV
     */
    public void exportDatasetInCSVAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar os registros do dataset " + tfDatasetNome.getText());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Arquivo CSV", "*.csv"),
                new FileChooser.ExtensionFilter("Arquivo de texto", "*.txt")
        );
        fileChooser.setInitialFileName(tfDatasetNome.getText());
        File file = fileChooser.showSaveDialog(null);

        if (file == null) {
            return;
        }

        int columnsSize = tvDados.getColumns().size();
        int itemsSize = tvDados.getItems().size();

        String[] columns = new String[columnsSize];
        List<String[]> items = new ArrayList<>();

        for (int index = 0; index < columnsSize; index++) {
            TableColumn<ValuesDto, ?> item = tvDados.getColumns().get(index);
            columns[index] = item.getText();
        }

        for (int indexLine = 0; indexLine < itemsSize; indexLine++) {
            ValuesDto item = tvDados.getItems().get(indexLine);
            List<Object> itemObjects = item.getValue();
            String[] line = new String[columnsSize];

            for (int indexColumn = 0; indexColumn < columnsSize; indexColumn++) {
                Object itemObject = itemObjects.get(indexColumn);
                String itemValue = itemObject != null ? itemObject.toString() : "";
                line[indexColumn] = itemValue;
            }

            items.add(line);
        }

        try {
            Writer writer = Files.newBufferedWriter(file.toPath());
            CSVWriterBuilder csvWriter = new CSVWriterBuilder(writer);

            csvWriter.withSeparator(';');
            csvWriter.build().writeNext(columns);
            csvWriter.build().writeAll(items);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOG.error("Ocorreu uma falha ao gerar o arquivo " + file.getPath() + "\nTHROW " + e);
            new ExceptionDialog(
                    "Ocorreu uma falha ao gerar o arquivo " + file.getPath(), 
                    e.getMessage()
            ).showMessage();
        }

    }

    //==========================================================================
    // METODOS REFERENTE A CONSTRAINTS
    //==========================================================================
    /**
     * Metodo para adicionar uma constraint, atualizando os objetos de lista
     * para o webservice e grid da tela
     *
     * @param fieldName
     * @param initialValue
     * @param finalValue
     * @param likeSearch
     * @param contraintType
     */
    private void addContraint(String fieldName, String initialValue, String finalValue,
            boolean likeSearch, String contraintType) {

        SearchConstraintDto contraintDto = new SearchConstraintDto();
        contraintDto.setFieldName(fieldName);
        contraintDto.setInitialValue(initialValue);
        contraintDto.setFinalValue(finalValue);
        contraintDto.setLikeSearch(likeSearch);
        contraintDto.setContraintType(contraintType);

        ObservableList<SearchConstraintDto> items = tvContraints.getItems();
        items.addAll(Arrays.asList(contraintDto));
        tvContraints.setItems(items);
        contraints.getItem().add(contraintDto);
    }

    /**
     * Adicionar contraint na pesquisa
     */
    public void addContraintAction() {
        isUpdateData = true;

        addContraint(
                tfConstraintCampo.getText(),
                tfConstraintInicial.getText(),
                tfConstraintFinal.getText(),
                cbContraintLike.isSelected(),
                cbConstraintTipo.getValue()
        );

        tfConstraintCampo.setText("");
        tfConstraintInicial.setText("");
        tfConstraintFinal.setText("");
        cbContraintLike.setSelected(false);
        cbConstraintTipo.setValue("MUST");
    }

    /**
     * Abrir janela para consultar os campos do dataset
     */
    public void showConstraintFieldSearchAction() {
        try {

            if (isShowConstraintFieldSearch) {
                throw new ExceptionDialog("A janela de busca ja esta aberta", "Mais detalhes");
            }

            isShowConstraintFieldSearch = true;

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/layouts/FrmDatasetFieldsSearch.fxml"));
            Parent root = loader.load();
            DatasetFieldSearchController controller = loader.getController();

            if (tfDatasetNome.getText().isEmpty()) {
                LOG.error("Buscar campos sem informar o dataset;");
                throw new ExceptionDialog(
                        "Falha ao buscar campos",
                        "Por favor, informar um dataset para buscar os campos."
                );
            }

            controller.setViewFields(datasetFields);

            if (!tfConstraintCampo.getText().isEmpty()) {
                controller.setSearchField(tfConstraintCampo.getText());
            }

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Buscar Campos (Dataset)");
            stage.getIcons().add(new Image("/images/fluig-icon-brand.png"));
            stage.setScene(scene);
            stage.showAndWait();

            String datasetField = controller.getSearchField();

            if (!datasetField.isEmpty()) {
                tfConstraintCampo.setText(datasetField);
            }
        } catch (IOException ex) {
            LOG.error("Falha ao abrir a tela >>> " + ex.toString());
            new ExceptionDialog(
                    "Folha ao abrir a tela",
                    ex.getMessage()
            ).showMessage();
        } catch (ExceptionDialog ex) {
            ex.showMessage();
        } finally {
            isShowConstraintFieldSearch = false;
        }
    }

    /**
     * Metodo key pressed para o table view constraints
     *
     * @param event
     */
    public void contraintKeyPressed(KeyEvent event) {
        int indice = tvContraints.getSelectionModel().getSelectedIndex();
        KeyCode key = event.getCode();

        if (key == KeyCode.DELETE) {
            isUpdateData = true;

            tvContraints.getItems().remove(indice);
            contraints.getItem().remove(indice);
        }
    }

    //==========================================================================
    // METODOS REFERENTE A FIELDS
    //==========================================================================
    /**
     * Metodo mouse clicked para o list view campos (fields)
     *
     * @param mouseEvent
     */
    public void fieldsMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() >= 2) {
            isUpdateData = true;

            int indice = lvCampos.getSelectionModel().getSelectedIndex();
            String campo = lvCampos.getItems().get(indice);

            ObservableList<String> camposSelecionados = lvCamposSelecionados.getItems();
            camposSelecionados.addAll(Arrays.asList(campo));
            lvCamposSelecionados.setItems(camposSelecionados);
            fields.getItem().add(campo);
        }
    }

    /**
     * Metodo key pressed para o list view campos selecionados (fields selected)
     *
     * @param event
     */
    public void fieldsSelectedKeyPressed(KeyEvent event) {
        int indice = lvCamposSelecionados.getSelectionModel().getSelectedIndex();
        KeyCode key = event.getCode();

        if (key == KeyCode.DELETE) {
            isUpdateData = true;

            lvCamposSelecionados.getItems().remove(indice);
            fields.getItem().remove(indice);
        }
    }

    //==========================================================================
    // METODOS REFERENTE A SORTFIELDS
    //==========================================================================
    /**
     * Metodo mouse clicked para o list view ordenacao de campos (sort fields)
     *
     * @param mouseEvent
     */
    public void sortFieldsMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() >= 2) {
            isUpdateData = true;

            int indice = lvSortCampos.getSelectionModel().getSelectedIndex();
            String campo = lvSortCampos.getItems().get(indice);

            ObservableList<String> camposSelecionados = lvSortCamposSelecionados.getItems();
            camposSelecionados.addAll(Arrays.asList(campo));
            lvSortCamposSelecionados.setItems(camposSelecionados);
            sortFields.getItem().add(campo);
        }
    }

    /**
     * Metodo key pressed para o list view ordenacao de campos selecionados
     * (sort fields selected)
     *
     * @param event
     */
    public void sortFieldsSelectedKeyPressed(KeyEvent event) {
        int indice = lvSortCamposSelecionados.getSelectionModel().getSelectedIndex();
        KeyCode key = event.getCode();

        if (key == KeyCode.DELETE) {
            isUpdateData = true;

            lvSortCamposSelecionados.getItems().remove(indice);
            sortFields.getItem().remove(indice);
        }
    }
}
