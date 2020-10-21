package br.com.softdigital.fluig.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.totvs.technology.ecm.foundation.ws.ColleagueDto;
import com.totvs.technology.ecm.foundation.ws.ColleagueDtoArray;

import br.com.softdigital.fluig.services.ColleagueWsService;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Principal Controller
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class PrincipalController implements Initializable {

    private final static Logger LOG = Logger.getLogger(PrincipalController.class);

    private ColleagueWsService colleagueWsService;

    @FXML
    private TableView<ColleagueDto> tbUsers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        try {
//            colleagueWsService = new ColleagueWsService(FluigApplication.getCREDENTIALS());
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        listarUsers();
    }

    public void listarUsers() {

        if (tbUsers.getColumns().size() == 0) {
            String[][] collumns = {
                {"Nome", "colleagueName"},
                {"Email", "mail"},
                {"Matrícula", "colleagueId"},
                {"Empresa", "companyId"},
                {"Grupo", "groupId"},
                {"GED User", "gedUser"},
                {"Admin", "adminUser"},
                {"Ativo", "active"},
                {"Idioma", "defaultLanguage"},
                {"Area 1 Id", "area1Id"},
                {"Area 2 Id", "area2Id"},
                {"Area 3 Id", "area3Id"},
                {"Area 4 Id", "area4Id"},
                {"Area 5 Id", "area5Id"}
            };

            ObservableList<TableColumn<ColleagueDto, String>> collumnList = FXCollections.observableArrayList();
            for (String[] column : collumns) {
                TableColumn<ColleagueDto, String> tableColumn = new TableColumn<ColleagueDto, String>(column[0]);
                tableColumn.setCellValueFactory(new PropertyValueFactory<ColleagueDto, String>(column[1]));
                collumnList.add(tableColumn);
            }

            tbUsers.getColumns().addAll(collumnList);

        }

        ObservableList<ColleagueDto> colleagueList = FXCollections.observableArrayList(colleagueWsService.getColleagues());
        tbUsers.setItems(colleagueList);
    }

    public void btnGerarCSVAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar lista de usuários");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Arquivo CSV", "*.csv"),
                new ExtensionFilter("Arquivo de texto", "*.txt")
        );
        fileChooser.setInitialFileName("usuarios");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                colleagueWsService.ColleaguesToCsv(file);
            } catch (IOException e) {
                LOG.error("Ocorreu uma falha ao gerar o arquivo " + file.getPath() + "\nTHROW " + e);
            }
        }
    }

    public void btnCadastrarAtualizarUserCSVAction() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buscar csv");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Arquivo CSV", "*.csv"),
                new ExtensionFilter("Arquivo de texto", "*.txt")
        );
        File file = fileChooser.showOpenDialog(null);

        ColleagueDtoArray colleagues = colleagueWsService.csvToColleagues(file);

        //colleagueWsService.createColleagues(colleagues);
    }

    public void showDatasetAction() {
        try {
            Parent parent = FXMLLoader.load(PrincipalController.class.getResource("/layouts/FrmDataset.fxml"));
            Scene scene = new Scene(parent);

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Dataset");
            stage.getIcons().add(new Image("/images/fluig-icon-brand.png"));
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showCredentialAction() {
        try {
            Parent parent = FXMLLoader.load(PrincipalController.class.getResource("/layouts/FrmCredential.fxml"));
            Scene scene = new Scene(parent);

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Cadastro de servidores");
            stage.getIcons().add(new Image("/images/fluig-icon-brand.png"));
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
