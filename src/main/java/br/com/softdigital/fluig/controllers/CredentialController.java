package br.com.softdigital.fluig.controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import com.totvs.technology.ecm.foundation.ws.ColleagueDto;

import br.com.softdigital.fluig.domains.Credential;
import br.com.softdigital.fluig.services.ColleagueWsService;
import br.com.softdigital.fluig.services.CredentialService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Credential Controller
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class CredentialController implements Initializable {

    private final static Logger LOG = Logger.getLogger(CredentialController.class);

    @FXML
    private TextField urlFluig;

    @FXML
    private TextField companyDescription;

    @FXML
    private TextField userLogin;

    @FXML
    private TextField userPassword;

    @FXML
    private CheckBox production;

    @FXML
    private ListView<Credential> credentials;

    private CredentialService credentialService = new CredentialService();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        credentialsUpdate();
    }

    public void btnSaveAction() {
        Credential obj = validateDataAuthentication();
        if (obj == null) {
            return;
        }

        try {
            credentialService.saveOrUpdate(obj);
        } catch (SQLException e) {
            LOG.error("Falha ao gravar/atualizar registros de credenciais: " + e);

            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Falha");
            alert.setHeaderText("Falha ao gravar/atualizar registros de credenciais");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        clearFields();
        credentialsUpdate();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() >= 2) {
            int indice = credentials.getSelectionModel().getSelectedIndex();
            Credential credential = credentials.getItems().get(indice);

            urlFluig.setText(credential.getUrl());
            userLogin.setText(credential.getUserName());
            userPassword.setText(credential.getPassword());
            companyDescription.setText(credential.getCompanyName());
            production.setSelected(credential.isEnvironment());
        }
    }

    private Credential validateDataAuthentication() {
        Credential obj = null;

        try {
            ColleagueWsService colleagueWsService = new ColleagueWsService(urlFluig.getText());
            ColleagueDto colleagueDto = colleagueWsService.getColleague(userLogin.getText(), userPassword.getText());

            obj = new Credential();
            obj.setUrl(urlFluig.getText());
            obj.setUserName(userLogin.getText());
            obj.setPassword(userPassword.getText());
            obj.setCompanyName(companyDescription.getText());
            obj.setEnvironment(production.isSelected());
            obj.setUserCode(colleagueDto.getColleagueId());
            obj.setCompany(colleagueDto.getCompanyId());

        } catch (Exception e) {
            LOG.error("Falha ao alterar a rota: " + e);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(e.toString());
            alert.showAndWait();
        }

        return obj;
    }

    private void credentialsUpdate() {
        try {
            credentials.setItems(credentialService.getAllCredential());
        } catch (SQLException e) {

            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Falha");
            alert.setHeaderText("Ocorreu uma falha ao listar as empresas!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void clearFields() {
        urlFluig.setText("");
        companyDescription.setText("");
        userLogin.setText("");
        userPassword.setText("");
        production.setSelected(false);
    }

    public void saveAction() {

    }
}
