package br.com.softdigital.fluig.services;

import java.sql.SQLException;
import java.util.List;

import br.com.softdigital.fluig.domains.Credential;
import br.com.softdigital.fluig.repositories.CredentialsRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Credential Service
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0, 06/06/2020
 */
public class CredentialService {

    private CredentialsRepository repository;

    public CredentialService() {
        repository = new CredentialsRepository();
    }

    /**
     * Return all credentials of data base
     * @return
     * @throws SQLException 
     */
    public ObservableList<Credential> getAllCredential() throws SQLException {
        ObservableList<Credential> creds = FXCollections.observableArrayList();
        creds.setAll(repository.findAll());
        return creds;
    }

    public Credential saveOrUpdate(Credential obj) throws SQLException {
        return repository.saveOrUpdate(obj);
    }

}
