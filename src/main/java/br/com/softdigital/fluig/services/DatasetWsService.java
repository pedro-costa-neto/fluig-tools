package br.com.softdigital.fluig.services;

import br.com.softdigital.fluig.dto.DatasetSimpleDto;
import br.com.softdigital.fluig.services.exception.ExceptionDialog;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.totvs.technology.ecm.dataservice.ws.AnyTypeArray;
import com.totvs.technology.ecm.dataservice.ws.DatasetDto;
import com.totvs.technology.ecm.dataservice.ws.DatasetService;
import com.totvs.technology.ecm.dataservice.ws.ECMDatasetServiceService;
import com.totvs.technology.ecm.dataservice.ws.Exception_Exception;
import com.totvs.technology.ecm.dataservice.ws.SearchConstraintDtoArray;
import com.totvs.technology.ecm.dataservice.ws.StringArray;

import br.com.softdigital.fluig.domains.Credential;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

/**
 * Dataset Controller
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0, 06/06/2020
 */
public class DatasetWsService {

    private final static Logger LOG = Logger.getLogger(DatasetWsService.class);

    /**
     * Conexao com o webservice retornando um objeto DatasetService
     *
     * @param url
     * @return
     * @throws ExceptionDialog
     */
    private DatasetService getDatasetService(String url) throws ExceptionDialog {
        LOG.info("getDatasetService " + url);
        DatasetService datasetService = null;

        try {
            URL urlWsdl = new URL(url + "/webdesk/ECMDatasetService?wsdl");
            ECMDatasetServiceService ecmDatasetService = new ECMDatasetServiceService(urlWsdl);
            datasetService = ecmDatasetService.getDatasetServicePort();
        } catch (MalformedURLException | RuntimeException ex) {
            LOG.error("Falho ao iniciar a conexao com o webservice >>> " + ex.toString());
            throw new ExceptionDialog("Falho ao iniciar a conexao com o webservice ", ex.getMessage());
        }
        return datasetService;
    }

    /**
     * Retorna dados de um dataset
     *
     * @param credential
     * @param nameDataset
     * @param fields
     * @param constraints
     * @param order
     * @return
     * @throws ExceptionDialog
     */
    public DatasetSimpleDto getDataset(Credential credential, String nameDataset, StringArray fields,
            SearchConstraintDtoArray constraints, StringArray order) throws ExceptionDialog {
        DatasetSimpleDto datasetSimpleDto = null;
        try {
            DatasetService datasetService = getDatasetService(credential.getUrl());
            DatasetDto datasetDto = datasetService.getDataset(
                    credential.getCompany(),
                    credential.getUserName(),
                    credential.getPassword(),
                    nameDataset,
                    fields,
                    constraints,
                    order
            );

            datasetSimpleDto = new DatasetSimpleDto();
            datasetSimpleDto.setColumns(datasetDto.getColumns());
            datasetSimpleDto.setItems(datasetDto.getValues());

        } catch (Exception_Exception ex) {
            LOG.error("Falho ao carregar o dataset >>> " + ex.toString());
            throw new ExceptionDialog("Falho ao carregar o dataset ", ex.getMessage());
        }

        return datasetSimpleDto;
    }

    /**
     * Retorna uma lista com o nome dos datasets
     * @param credential
     * @return
     * @throws ExceptionDialog 
     */
    public List<Object> getDatasets(Credential credential) throws ExceptionDialog {
        DatasetService datasetService = getDatasetService(credential.getUrl());
        List<Object> nameDatasets = new ArrayList<>();
        try {
            AnyTypeArray anyTypeArray = datasetService.getAvailableDatasets(
                    credential.getCompany(),
                    credential.getUserName(),
                    credential.getPassword()
            );

            nameDatasets = anyTypeArray.getItem();
        } catch (Exception_Exception ex) {
            LOG.error("Falha ao listar os datasets >>> " + ex.toString());
            throw new ExceptionDialog("Falha ao listar os datasets", ex.getMessage());
        }

        return nameDatasets;
    }

    public DatasetDto getDadosDataset(Credential credential, String nameDataset, StringArray fields,
            SearchConstraintDtoArray constraints, StringArray sortFields) throws ExceptionDialog {

        DatasetDto datasetDto = null;
        try {
            DatasetService datasetService = getDatasetService(credential.getUrl());
            datasetDto = datasetService.getDataset(
                    credential.getCompany(),
                    credential.getUserName(),
                    credential.getPassword(),
                    nameDataset,
                    fields,
                    constraints,
                    sortFields
            );
        } catch (Exception_Exception ex) {
            LOG.error("Falho ao carregar o dataset " + ex.getMessage());
            throw new ExceptionDialog("Falho ao carregar o dataset ", ex.getMessage());
        }

        return datasetDto;
    }

    public ObservableList<String> getSearchsType() {
        ObservableList<String> searchsType = FXCollections.observableArrayList();
        searchsType.setAll(Arrays.asList("MUST", "SHOULD", "MUST_NOT"));
        return searchsType;
    }
}
